package com.ainq.caliphr.hqmf.service.impl;

import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.PATIENT_ID;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.SCHEMA;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.hqmf.model.DataCriteria;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.model.PopulationCriteria;
import com.ainq.caliphr.hqmf.model.Precondition;
import com.ainq.caliphr.hqmf.util.H2PopulateUtil;
import com.ainq.caliphr.hqmf.util.MeasureMetadataUtil;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Result;

import lombok.Data;
import lombok.val;

@Component
@Scope("prototype")
public class GeneratePopulationSqlStatements {

    static Logger logger = LoggerFactory.getLogger(GeneratePopulationSqlStatements.class);

    private HQMFDocument hqmfDoc;
    private HQMFGenerationHelper helper;

    @Autowired
    private H2PopulateUtil h2PopulateUtil;

    @Autowired
    private MeasureDao measureDao;
    
    @Autowired
    private MeasureMetadataUtil measureMetadataUtil;

    public GeneratePopulationSqlStatements(HQMFDocument hqmfDoc, HQMFGenerationHelper helper) {
        this.hqmfDoc = hqmfDoc;
        this.helper = helper;
    }

    public Map<String, PopulationGenerationContext> generatePopulationSqlStatements(Integer userId) {

        // if the sql for this measure has already been generated in a previous run since JVM start, use the cached sql
        helper.executeSqlIfCached();

        boolean allSuccess = true;
        val genCtxs = new HashMap<String, PopulationGenerationContext>();

        for (int i = 0; i < hqmfDoc.getPopulations().size(); i++) {

            Map<String, String> populationData = hqmfDoc.getPopulations().get(i);

            PopulationGenerationContext ippGenCxt = processPopulation("IPP", genCtxs, populationData, null, i, userId);
            allSuccess &= ippGenCxt.isSuccess();

            PopulationGenerationContext denomGenCxt = processPopulation("DENOM", genCtxs, populationData, ippGenCxt.getSpecificOccurrences(), i, userId);
            allSuccess &= denomGenCxt.isSuccess();

            PopulationGenerationContext denexGenCxt = processPopulation("DENEX", genCtxs, populationData, denomGenCxt.getSpecificOccurrences(), i, userId);
            if (denexGenCxt != null) {
                allSuccess &= denexGenCxt.isSuccess();
            }

            PopulationGenerationContext numerGenCxt = processPopulation("NUMER", genCtxs, populationData, denomGenCxt.getSpecificOccurrences(), i, userId);
            allSuccess &= numerGenCxt.isSuccess();

            PopulationGenerationContext denexcepGenCxt = processPopulation("DENEXCEP", genCtxs, populationData, denomGenCxt.getSpecificOccurrences(), i, userId);
            if (denexcepGenCxt != null) {
                allSuccess &= denexcepGenCxt.isSuccess();
            }

        }

        if (allSuccess) {
            logger.debug("** ALL SUCCESS!!!! ** {}", hqmfDoc.getCmsId());
        }

        return genCtxs;
    }

    private PopulationGenerationContext processPopulation(
            String popType,
            Map<String, PopulationGenerationContext> genCtxs,
            Map<String, String> populationData,
            Set<String> prevOccurrences,
            int index,
            Integer userId) {

        String popKey = populationData.get(popType);
        if (popKey == null) {
            return null;
        }
        
        String prevPopulationName = null;
        if ("IPP".equals(popType)) {
        	// ignore
        }
        else if ("DENOM".equals(popType)) {
        	prevPopulationName = populationData.get("IPP") + "_" + hqmfDoc.getCmsId();
        } 
        else if ("DENEX".equals(popType) || "NUMER".equals(popType) || "DENEXCEP".equals(popType)) {
        	prevPopulationName = populationData.get("DENOM") + "_" + hqmfDoc.getCmsId();
        }
        else {
        	throw new IllegalArgumentException("unrecognized population type: " + popType);
        }
        
        PopulationGenerationContext genCxt = genCtxs.get(popKey);
        if (genCxt == null) {
            PopulationCriteria popCrit = getPopulationCriteria(hqmfDoc, popKey);
            genCxt = processBasePopulation(popCrit, popType, prevOccurrences, populationData, prevPopulationName);
            genCtxs.put(popKey, genCxt);
        }

        String stratId = populationData.get("STRAT");
        if (stratId != null) {
            String stratKey = popKey + "_" + stratId;

            if (!genCtxs.containsKey(stratKey)) {
                genCxt = processStratification(getPopulationCriteria(hqmfDoc, stratId), popType, genCxt.getSpecificOccurrences(), populationData, prevPopulationName);
                genCtxs.put(stratKey, genCxt);
            } else {
                genCxt = genCtxs.get(stratKey);
            }
        }

        Result result = new Result();
        result.setResultValue(genCxt.getResults().size());
        result.setUserCreated(userId);
        result.setDateCreated(new Date());
        result.setUserUpdated(userId);
        result.setDateUpdated(new Date());

        List<Integer> patientIds = genCxt.getResults().stream()
                .map(m -> Integer.valueOf(m.get("patient_id").toString()))
                .collect(Collectors.toList());

        measureDao.saveResult(Long.valueOf(hqmfDoc.getId()), popType, index, result, patientIds, userId);

        return genCxt;
    }

    private PopulationGenerationContext processBasePopulation(
            PopulationCriteria popCrit, String populationType,
            Set<String> prevOccurrences,
            Map<String, String> populationData,
            String prevPopulationName) {

        PopulationGenerationContext genCxt = new PopulationGenerationContext();

        instantiateDependentViews(popCrit.getPreconditions(), null);
        Set<String> specificOccurrences = getSpecificOccurrences(popCrit.getPreconditions(), prevOccurrences);
        genCxt.setSpecificOccurrences(specificOccurrences);

        StringBuilder sql = new StringBuilder();

        String tableName = popCrit.getId() + "_" + hqmfDoc.getCmsId();
        logger.debug("********** {}", tableName);

        generateCreateTable(tableName, prevOccurrences, specificOccurrences);

        sql.append("INSERT INTO ").append(helper.field(SCHEMA, tableName)).append(" (");

        if (prevPopulationName != null) {
        	referencePreviousPopulation(sql, popCrit, prevPopulationName, prevOccurrences, specificOccurrences);
        }

        for (Precondition precondition : popCrit.getPreconditions()) {
            getPreconditionSQL(0, sql, precondition, null, specificOccurrences, prevPopulationName);
        }

        sql.append(")");

        h2PopulateUtil.testSQLUpdate(sql.toString(), tableName);

        if ("NUMER".equals(populationType) && populationData.containsKey("DENEX")) {
            removeFromPreviousPopulation(sql, tableName, populationData.get("DENEX") + "_" + hqmfDoc.getCmsId());
        } else if ("DENEXCEP".equals(populationType) && populationData.containsKey("NUMER")) {
            removeFromPreviousPopulation(sql, tableName, populationData.get("NUMER") + "_" + hqmfDoc.getCmsId());
        }

        return gatherResults(genCxt, tableName);
    }

    private void generateCreateTable(String tableName, Set<String> prevOccurrences, Set<String> specificOccurrences) {

        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE ").append(helper.field(SCHEMA, tableName)).append("(patient_id integer");
        if (prevOccurrences != null) {
            prevOccurrences.forEach(occName -> sql.append(", id")
                    .append(helper.getOccurrenceAlias(helper.findDataCriteria(occName)))
                    .append(" integer")
            );
        }
        if (specificOccurrences != null) {
            specificOccurrences.forEach(occName -> {
                if (prevOccurrences == null || !prevOccurrences.contains(occName)) {
                    sql.append(", id")
                            .append(helper.getOccurrenceAlias(helper.findDataCriteria(occName)))
                            .append(" integer");
                }
            });
        }
        sql.append(")");

        h2PopulateUtil.testSQLUpdate(sql.toString(), tableName + "_CREATE");
    }

    @SuppressWarnings("unchecked")
    private PopulationGenerationContext processStratification(
            PopulationCriteria popCrit,
            String basePopType,
            Set<String> baseOccurrences,
            Map<String, String> populationData,
            String prevPopulationName) {

        PopulationGenerationContext genCxt = new PopulationGenerationContext();
        genCxt.setSpecificOccurrences(baseOccurrences);

        instantiateDependentViews(popCrit.getPreconditions(), null);

        StringBuilder sql = new StringBuilder();
        String tableName = populationData.get(basePopType) + "_" + populationData.get("STRAT") + "_" + hqmfDoc.getCmsId();
        String basePopTableName = populationData.get(basePopType) + "_" + hqmfDoc.getCmsId();
        logger.debug("*********** {}", tableName);

        generateCreateTable(tableName, Collections.EMPTY_SET, Collections.EMPTY_SET);

        String alias = helper.getNewFromTableAlias();
        sql.append(String.format("INSERT INTO %s (SELECT %s FROM %s %s", 
        		helper.field(SCHEMA, tableName), 
        		helper.field(alias, "patient_id"), 
        		helper.field(SCHEMA, basePopTableName), 
        		alias)
        );
        if (popCrit.getPreconditions().size() > 0) {
            sql.append(" INTERSECT ");

            for (Precondition precondition : popCrit.getPreconditions()) {
                getPreconditionSQL(0, sql, precondition, null, Collections.EMPTY_SET, prevPopulationName);
            }
        }

        sql.append(")");

        h2PopulateUtil.testSQLUpdate(sql.toString(), tableName);

        return gatherResults(genCxt, tableName);
    }

    @SuppressWarnings("unchecked")
    private void referencePreviousPopulation(
            StringBuilder sql,
            PopulationCriteria popCrit,
            String prevPopulationTableName,
            Set<String> prevOccurrences,
            Set<String> specificOccurrences) {

        String alias = helper.getNewFromTableAlias();
        sql.append("SELECT ").append(helper.field(alias, "patient_id"));
        determineSelectFieldsForOccurrences(sql, alias, null, Collections.EMPTY_SET, specificOccurrences, prevOccurrences);
        sql.append(" FROM ").append(helper.field(SCHEMA, prevPopulationTableName)).append(" ").append(alias);
        determineFromJoinsForOccurrences(sql, alias, null, null, Collections.EMPTY_SET, specificOccurrences, prevOccurrences);
        if (popCrit.getPreconditions().size() > 0) {
            sql.append(" INTERSECT ");
        }
    }

    private void removeFromPreviousPopulation(StringBuilder sql, String tableName, String prevPopTableName) {
        sql.setLength(0);
        sql.append("DELETE FROM ").append(helper.field(SCHEMA, tableName)).append(" WHERE ");
        sql.append(PATIENT_ID).append(" IN (SELECT ").append(PATIENT_ID).append(" FROM ");
        sql.append(helper.field(SCHEMA, prevPopTableName)).append(")");
        h2PopulateUtil.testSQLUpdate(sql.toString(), tableName + "_STEP2");
    }

    public void getPreconditionSQL(
    		int index, StringBuilder sql, 
    		Precondition precondition, 
    		Precondition parent, 
    		Set<String> specificOccurrences, 
    		String prevPopulationName) {

        if (precondition.getReference() != null) {
            if (logger.isDebugEnabled()) {
                sql.append("/* ").append(precondition.getReference()).append(" */\n");
            }
            String selectClause = determineSelectClause(precondition, specificOccurrences, null);
            String refSql = selectClause;
            sql.append(refSql);
        }
        if (precondition.getPreconditions() != null) {
            if (parent != null && index > 0) {
                if (logger.isDebugEnabled()) {
                    sql.append("/* ").append(parent.getConjunctionCode()).append(" */");
                }
                getConjunctionOperator(sql, parent.getConjunctionCode());
            }
            
            if ("allFalse".equals(precondition.getConjunctionCode()) || "atLeastOneFalse".equals(precondition.getConjunctionCode())) {
            	
            	if ("atLeastOneFalse".equals(precondition.getConjunctionCode())) {
            		if (precondition.getPreconditions() != null && precondition.getPreconditions().size() != 1) {
                        throw new NotImplementedException("atLeastOneFalse only implemented where there is one precondition");
            		}
            		if (!CollectionUtils.isEmpty(precondition.getPreconditions().get(0).getPreconditions())) {
                        throw new NotImplementedException("atLeastOneFalse only implemented where there are no further child preconditions");
            		}
            	}
            	
            	String alias = helper.getNewFromTableAlias();
            	sql.append(String.format("/* %s */ (SELECT %s", precondition.getConjunctionCode(), helper.field(alias, PATIENT_ID)));
                determineSelectFieldsForOccurrences(sql, alias, null, Collections.emptySet(), specificOccurrences, null);
                prevPopulationName = prevPopulationName != null ? prevPopulationName : "PATIENT_INFO";
                sql.append(String.format(" FROM %s %s", helper.field(SCHEMA, prevPopulationName), alias));
                determineFromJoinsForOccurrences(sql, alias, precondition, null, null, specificOccurrences, null);
                sql.append(" MINUS ");
                
            }
            sql.append("(");
            for (int i = 0; i < precondition.getPreconditions().size(); i++) {
                Precondition nested = precondition.getPreconditions().get(i);
                if (i > 0 && nested.getReference() != null) {
                    if (logger.isDebugEnabled()) {
                        sql.append("/* ").append(precondition.getConjunctionCode()).append(" */");
                    }
                    getConjunctionOperator(sql, precondition.getConjunctionCode());
                }
                getPreconditionSQL(i, sql, nested, precondition, specificOccurrences, prevPopulationName);
            }
            sql.append(")");
            
            if ("allFalse".equals(precondition.getConjunctionCode()) || "atLeastOneFalse".equals(precondition.getConjunctionCode())) {
            	sql.append(")");
            }
            
        }

    }

    private void getConjunctionOperator(StringBuilder sql, String conjunctionCode) {
        if ("allTrue".equals(conjunctionCode)) {
            sql.append(" INTERSECT ");
        } else if ("allFalse".equals(conjunctionCode) || "atLeastOneTrue".equals(conjunctionCode)) {
            sql.append(" UNION ");
        } else if ("atLeastOneFalse".equals(conjunctionCode)) {
            throw new NotImplementedException("atLeastOneFalse not implemented yet at this level");
        } else {
            throw new RuntimeException("unknown conjunction code: " + conjunctionCode);
        }
    }

    private String determineSelectClause(
            Precondition precondition,
            Set<String> specificOccurrences,
            Set<String> prevOccurrences) {

        DataCriteria dataCrit = helper.findDataCriteria(precondition.getReference());
        Set<String> critOccs = helper.getAllOccurrences(dataCrit);
        String critOccAlias = helper.getOccurrenceAliasIfExists(dataCrit);

        String alias = critOccAlias;
        if (alias == null) {
            alias = helper.getNewFromTableAlias();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(helper.field(alias, PATIENT_ID));
        determineSelectFieldsForOccurrences(sql, alias, critOccAlias, critOccs, specificOccurrences, prevOccurrences);
        sql.append(" FROM ").append(helper.field(SCHEMA, helper.getViewName(precondition.getReference())));
        sql.append(" ").append(alias);
        determineFromJoinsForOccurrences(sql, alias, precondition, dataCrit, critOccs, specificOccurrences, prevOccurrences);
        return sql.toString();
    }

    private void determineSelectFieldsForOccurrences(
            StringBuilder sql,
            String baseAlias,
            String critOccAlias,
            Set<String> critOccurrences,
            Set<String> specificOccurrences,
            Set<String> previousOccurrences) {
        if (previousOccurrences != null) {
            for (String occ : previousOccurrences) {
                DataCriteria crit = helper.findDataCriteria(occ);
                sql.append(", ").append(helper.field(baseAlias, "id")).append(helper.getOccurrenceAlias(crit));
            }
        }
        Set<String> newOccurrences = new LinkedHashSet<String>(specificOccurrences);
        if (previousOccurrences != null) {
            newOccurrences.removeAll(previousOccurrences);
        }
        for (String occ : newOccurrences) {
            DataCriteria crit = helper.findDataCriteria(occ);
            String occAlias = helper.getOccurrenceAlias(crit);
            if (critOccurrences.contains(occAlias)) {
                String alias = critOccAlias != null ? critOccAlias : baseAlias;
                sql.append(", ").append(helper.field(alias, "id")).append(occAlias);
            } else {
                sql.append(", ").append(helper.field(occAlias, "id")).append(" id").append(occAlias);
            }
        }
    }

    private void determineFromJoinsForOccurrences(
            StringBuilder sql,
            String alias,
            Precondition precondition,
            DataCriteria dataCrit,
            Set<String> critOccs,
            Set<String> specificOccurrences,
            Set<String> prevOccurrences) {

        for (String occ : specificOccurrences) {
            if (prevOccurrences != null && prevOccurrences.contains(occ)) {
                continue;
            }
            DataCriteria occCrit = helper.findDataCriteria(occ);
            String curAlias = helper.getOccurrenceAlias(occCrit);
            if (critOccs != null && critOccs.contains(curAlias)) {
                continue;
            }
            String tableName = occ;
            if (dataCrit != null && occ.equals(dataCrit.getSourceDataCriteria())) {
                tableName = precondition.getReference();
            }
            String viewName = helper.getViewName(tableName);
            sql.append(" LEFT OUTER JOIN ").append(helper.field(SCHEMA, viewName)).append(" ").append(curAlias);
            sql.append(" ON ").append(helper.field(alias, PATIENT_ID)).append(" = ").append(helper.field(curAlias, PATIENT_ID));
        }
    }

    private PopulationCriteria getPopulationCriteria(HQMFDocument hqmfDoc, String id) {
        for (PopulationCriteria popCrit : hqmfDoc.getPopulationCriteria()) {
            if (id.equals(popCrit.getId())) {
                return popCrit;
            }
        }
        return null;
    }

    private void instantiateDependentViews(List<Precondition> preconditions, Precondition parent) {
        for (Precondition precond : preconditions) {
            String ref = precond.getReference();
            if (ref != null && !helper.isViewInstantiated(ref)) {
                instantiateDependentViews(helper.findDataCriteria(ref));
                helper.instantiateView(ref);
            }
            if (precond.getPreconditions() != null) {
                instantiateDependentViews(precond.getPreconditions(), precond);  // recursive call
            }
        }

    }

    private void instantiateDependentViews(DataCriteria dataCrit) {
        String srcDataCritName = dataCrit.getSourceDataCriteria();
        if (srcDataCritName != null && !dataCrit.getId().equals(srcDataCritName) && !helper.isViewInstantiated(srcDataCritName)) {
            helper.instantiateView(srcDataCritName);
        }
        List<String> temporalRefNames = helper.getTemporalRefNames(dataCrit.getTemporalReferences());
        if (temporalRefNames != null) {
			for (String temporalRefName : temporalRefNames) {
		        if (!helper.isViewInstantiated(temporalRefName)) {
		            helper.instantiateView(temporalRefName);
		        }
	        }
        }
    }

    private Set<String> getSpecificOccurrences(List<Precondition> preconditions, Set<String> previousOccurrences) {
        val result = new LinkedHashSet<String>();
        if (previousOccurrences != null) {
            result.addAll(previousOccurrences);
        }

        for (Precondition precond : preconditions) {
            if (precond.getReference() != null) {
                DataCriteria dataCrit = testForSpecificOccurrence(result, precond.getReference());
                List<String> temporalRefNames = helper.getTemporalRefNames(dataCrit.getTemporalReferences());
				if (temporalRefNames != null) {
					for (String temporalRefName : temporalRefNames) {
		                testForSpecificOccurrence(result, temporalRefName);
					}
                }
            }
            if (precond.getPreconditions() != null) {
                result.addAll(getSpecificOccurrences(precond.getPreconditions(), null));  // recursive call
            }
        }
        return result;
    }

    private DataCriteria testForSpecificOccurrence(Set<String> result, String refName) {
        DataCriteria dataCrit = helper.findDataCriteria(refName);
        if (dataCrit.getSpecificOccurrenceConst() != null) {
            result.add(dataCrit.getSourceDataCriteria());
        }
        return dataCrit;
    }
    
    private PopulationGenerationContext gatherResults(PopulationGenerationContext genCxt, String tableName) {
    	boolean episodeOfCareMeasure = measureMetadataUtil.isEpisodeOfCareMeasure(hqmfDoc.getCmsId());
		if (!episodeOfCareMeasure) {
			genCxt.setResults(processPatientBasedMeasure(genCxt, tableName));
		} else {
			genCxt.setResults(processEpisodeBasedMeasure(genCxt, tableName));
		}
        genCxt.setSuccess(true);
        return genCxt;
    }

    private List<Map<String, Object>> processPatientBasedMeasure(PopulationGenerationContext genCxt, String tableName) {
        String sqlString = "SELECT DISTINCT patient_id FROM " + helper.field(SCHEMA, tableName);
        return h2PopulateUtil.testSQLQueryWithResults(sqlString, tableName + "_RESULTS");
    }
    
    private List<Map<String, Object>> processEpisodeBasedMeasure(PopulationGenerationContext genCxt, String tableName) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT patient_id");
		
		List<String> episodeAliases = new ArrayList<>();
		for (val episodeIdObj : measureMetadataUtil.getEpisodeIds(hqmfDoc.getCmsId())) {
			String episodeId = episodeIdObj.getAsString();
			if (genCxt.getSpecificOccurrences().contains(episodeId)) {
				String episodeAlias = "id" + helper.getOccurrenceAliasIfExists(episodeId);
				episodeAliases.add(episodeAlias);
				sql.append(", ").append(episodeAlias);
			}
		}
			
		sql.append(" FROM ").append(helper.field(SCHEMA, tableName));
		
		val results = h2PopulateUtil.testSQLQueryWithResults(sql.toString(), tableName + "_RESULTS");
		
		// for episode based measures, retain only one row for a given episode id in the result set
		if (!results.isEmpty()) {
			Set<Integer> valuesSoFar = new HashSet<>();
			for (String episodeAlias : episodeAliases) {
				valuesSoFar.clear();
				for (Iterator<Map<String, Object>> it = results.iterator(); it.hasNext(); ) {
					Integer value = (Integer) it.next().get(episodeAlias);
					if (value != null && valuesSoFar.contains(value)) {
						it.remove();
					}
					else {
						valuesSoFar.add(value);
					}
				}
			}
		}
		
        return results;
    }

    public static
    @Data
    class PopulationGenerationContext {

        private Set<String> specificOccurrences;
        private boolean success;
        private List<Map<String, Object>> results;

    }


}
