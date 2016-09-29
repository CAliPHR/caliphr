package com.ainq.caliphr.hqmf.service.impl;

import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.BIRTH_TIME;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.CODE_ID;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.COMMON_SCHEMA;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.DEATH_DATE;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.EFFECTIVE_TIME_END;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.EFFECTIVE_TIME_START;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.GENDER;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.MEASURE_PERIOD_END_PLACEHOLDER;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.MEASURE_PERIOD_START_PLACEHOLDER;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.NEGATION_CODE_ID;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.PATIENT_ID;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.SCHEMA;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.STATUS;
import static com.ainq.caliphr.hqmf.service.impl.HQMFGenerationHelper.VALUE_SET_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.hqmf.model.DataCriteria;
import com.ainq.caliphr.hqmf.model.FromClause;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.model.FromClause.Join;
import com.ainq.caliphr.hqmf.model.type.HQMFAnyValue;
import com.ainq.caliphr.hqmf.model.type.HQMFCoded;
import com.ainq.caliphr.hqmf.model.type.HQMFRange;
import com.ainq.caliphr.hqmf.model.type.HQMFSubsetOperator;
import com.ainq.caliphr.hqmf.model.type.HQMFTemporalReference;
import com.ainq.caliphr.hqmf.model.type.HQMFValue;
import com.ainq.caliphr.hqmf.service.impl.GeneratePopulationSqlStatements.PopulationGenerationContext;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ValueSet;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ValueSetRepository;
import com.ainq.caliphr.persistence.util.predicate.code.ValueSetPredicate;

import lombok.val;

@Component
@Scope("prototype")
public class GenerateSQLsFromHQMF {
	
	static Logger logger = LoggerFactory.getLogger(GenerateSQLsFromHQMF.class);
	
	private HQMFDocument hqmfDoc;

	HQMFGenerationHelper helper;
	
	private int bundleId;

	@Autowired
	private ValueSetRepository valueSetRepository;

	@Autowired
	private ApplicationContext appCxt;

	public GenerateSQLsFromHQMF(HQMFDocument hqmf, int bundleId) {
		this.hqmfDoc = hqmf;
		this.bundleId = bundleId;
	}

	public Map<String, PopulationGenerationContext> generateSQL(Integer userId) {

		this.helper = appCxt.getBean(HQMFGenerationHelper.class, hqmfDoc, this);
		return appCxt.getBean(GeneratePopulationSqlStatements.class, hqmfDoc, helper).generatePopulationSqlStatements(userId);

	}

	public String generateSQLForDataCriteria(DataCriteria crit) {
		
		FromClause fromClause = null;
		StringBuilder where = new StringBuilder();
		String baseAlias = null;
		Map<String, String> var2Aliases = new HashMap<>();

		logger.debug("*********** {} - {} : {} : {}", hqmfDoc.getCmsId(), crit.getId(), crit.getTitle(), crit.getDescription());

		if ("derived".equals(crit.getType()) && crit.getChildrenCriteria() != null) {
			String result = processDerived(crit);
			if (result == null) {
				// if not successful, there was likely a reference that was not processed yet, so return false to signal 
				// the criteria should be retried on the next iteration
				return null;
			}
			helper.putSql(crit.getId(), result);
			return result;
		}

		boolean characteristic = false;
		boolean hasSource = !crit.getId().equals(crit.getSourceDataCriteria());

		if (!hasSource) {
			characteristic = "characteristic".equals(crit.getType());
			baseAlias = helper.getNewFromTableAlias();
			String viewName = crit.getPatientApiFunction();
			if (characteristic) {
				// special case for tobacco related characteristics
				viewName = isSpecialPatientCharacteristic(crit) ? "allProblems" : "PATIENT_INFO";
			}
			fromClause = new FromClause(baseAlias, viewName);

			if (!characteristic && !"derived".equals(crit.getType())) {
				if (crit.getCodeListId() != null) {
					processCodeList(crit, where, baseAlias);
				}
				if (crit.getStatus() != null) {
					processStatus(crit, where, baseAlias);
				}
			}
		} else {  // has source
			if (!helper.isViewInstantiated(crit.getSourceDataCriteria())) {
				helper.instantiateView(crit.getSourceDataCriteria());
			}
			baseAlias = helper.getNewFromTableAlias();
			fromClause = new FromClause(baseAlias, helper.getViewName(crit.getSourceDataCriteria()));
		}

		if (crit.getSubsetOperators() != null) {
			if (!hasSource) {
				throw new IllegalStateException();
			}
			//processSubset(crit.getSubsetOperators(), baseAlias, crit.getSourceDataCriteria(), where);
		}

		if (crit.getValue() != null) {
			processValue(crit, baseAlias, where);
		}
		
		if (!characteristic && hasSource) {
			if (!crit.isNegation()) {
				where.append(String.format(" AND %s IS NULL", helper.field(baseAlias, NEGATION_CODE_ID)));
			} else {
				where.append(String.format(" AND %s IN ", helper.field(baseAlias, NEGATION_CODE_ID)));
				getValueSetClause(where, crit.getNegationCodeListId());
			}
		}
		
		if (crit.getFieldValues() != null && crit.getFieldValues().size() > 0) {
			for (val fieldValueEntry : crit.getFieldValues().entrySet()) {
				if (fieldValueEntry.getValue() instanceof HQMFCoded) {
					HQMFCoded coded = (HQMFCoded)fieldValueEntry.getValue();
				
					if ("ORDINAL".equals(fieldValueEntry.getKey())) {
						where.append(String.format(" AND %s IN ", helper.field(baseAlias, "ordinality_code_id")));
						getValueSetClause(where, coded.getCodeListId());
					} else if ("REASON".equals(fieldValueEntry.getKey())) {
						where.append(String.format(" AND %s IN ", helper.field(baseAlias, "reason_code_id")));
						getValueSetClause(where, coded.getCodeListId());
					} else if ("SEVERITY".equals(fieldValueEntry.getKey())) {
						where.append(String.format(" AND %s IN ", helper.field(baseAlias, "severity_code_id")));
						getValueSetClause(where, coded.getCodeListId());
					} else if ("FACILITY_LOCATION".equals(fieldValueEntry.getKey())) {
						where.append(String.format(" AND %s IN ", helper.field(baseAlias, "facility_location_code_id")));
						getValueSetClause(where, coded.getCodeListId());
					} else {
						throw new NotImplementedException("criteria with field values not implemented yet: " + crit.getFieldValues().keySet());
					}
				} else {
					throw new NotImplementedException("criteria with field values not implemented yet: " + crit.getFieldValues().keySet());
				}
			}
		}
		
		if (crit.getTemporalReferences() != null) {
			for (HQMFTemporalReference temporalReference : crit.getTemporalReferences()) {
				String var2Alias = processTemporalReference(temporalReference, crit, baseAlias, fromClause, where);
				if (var2Alias == null) {
					// if not successful, there was likely a reference that was not processed yet, so return false to signal 
					// the criteria should be retried on the next iteration
					logger.debug("(deferred due to unresolved reference)");
					return null;
				}
				var2Aliases.put(temporalReference.getReference(), var2Alias);
			}
		}
		if (crit.getInlineCodeList() != null) {

			// the code list for birthtime and expired can be safely ignored, but still need to implement the rest
			if (!"birthtime".equals(crit.getProperty()) && !"expired".equals(crit.getProperty())) {
				throw new NotImplementedException("InlineCodeList not implemented yet");  // TODO
			}
		}

		StringBuilder sql = new StringBuilder();
		String viewName = helper.getViewName(crit.getId());
		sql.append("CREATE VIEW ");
		helper.output(sql, SCHEMA, viewName);
		sql.append(" AS ");
		if (crit.getSubsetOperators() != null) {
			sql.append(" SELECT * FROM ( ");
		}
		sql.append(" SELECT ");

		if (characteristic) {
			if ("birthtime".equals(crit.getProperty())) {
				sql.append(String.format("%s, %s as %s, %s as %s, null as %s", 
						helper.field(baseAlias, PATIENT_ID), 
						helper.field(baseAlias, BIRTH_TIME), EFFECTIVE_TIME_START,
						helper.field(baseAlias, BIRTH_TIME), EFFECTIVE_TIME_END,
						NEGATION_CODE_ID
				));
			} 
			else if ("expired".equals(crit.getProperty())) {
				sql.append(String.format("%s, %s as %s, %s as %s, null as %s", 
						helper.field(baseAlias, PATIENT_ID), 
						helper.field(baseAlias, DEATH_DATE), EFFECTIVE_TIME_START,
						helper.field(baseAlias, DEATH_DATE), EFFECTIVE_TIME_END,
						NEGATION_CODE_ID
				));
			} 
			else if ("gender".equals(crit.getProperty())) {
				sql.append(String.format("%s, %s, null as negation_code_id", helper.field(baseAlias, PATIENT_ID), helper.field(baseAlias, GENDER)));
			} 
			else if (isSpecialPatientCharacteristic(crit)) {
				helper.output(sql, baseAlias, "*");
				where.append(String.format(" AND %s IN ", helper.field(baseAlias, "code_id")));
				getValueSetClause(where, crit.getCodeListId());
			}
			else {
				String characteristicName = crit.getProperty() != null ? crit.getProperty() : crit.getId();
				throw new NotImplementedException("unimplemented characteristic: " + characteristicName);
			}
		} else {
			helper.output(sql, baseAlias, "*");
		}
		String currentCritOccAlias = helper.getOccurrenceAliasIfExists(crit);
		if (currentCritOccAlias != null) {

			// check if the source already has this field
			String srcOccAlias = null;
			if (!crit.getId().equals(crit.getSourceDataCriteria())) {
				srcOccAlias = helper.getOccurrenceAliasIfExists(crit.getSourceDataCriteria());
			}
			if (!currentCritOccAlias.equals(srcOccAlias)) {
				sql.append(", ");
				helper.output(sql, baseAlias, "id as id" + currentCritOccAlias);
			}
		}

		List<DataCriteria> temporalDataCriteria = helper.getTemporalDataCriteria(crit.getTemporalReferences());
		if (temporalDataCriteria != null) {
			for (DataCriteria temporalDataCrit : temporalDataCriteria) {
				Set<String> grpOccurrences = helper.getAllOccurrences(temporalDataCrit);
				for (String grpOcc : grpOccurrences) {
					sql.append(", ");
					helper.output(sql, var2Aliases.get(temporalDataCrit.getId()), "id" + grpOcc);
				}
			}
		}

		StringBuilder fromStringBuilder = new StringBuilder();
		generateFromClause(fromClause, fromStringBuilder);
		sql.append(fromStringBuilder);

		String whereString = where.toString().replaceFirst("\\s*AND\\s*", "");
		if (!StringUtils.isEmpty(whereString)) {
			sql.append(" WHERE ");
		}
		String sqlString = sql.toString() + whereString;

		if (crit.getSubsetOperators() != null) {
			//
			//	Subset logic.
			//
			//		first/most recent appends a inner join to the query
			//		group by record id if both sides of the comparison are Occurrences, otherwise group by patient id
			Boolean groupByPatient = true;
			if (crit.getSpecificOccurrence() != null && crit.getTemporalReferences() != null && crit.getTemporalReferences().size() > 0) {
				String temporalRef = crit.getTemporalReferences().get(0).getReference();
				if (!"MeasurePeriod".equals(temporalRef) && helper.findDataCriteria(temporalRef).getSpecificOccurrence() != null) {
					groupByPatient = false;
				}
			}
			
			sqlString = sqlString.concat(" ) ");
			sqlString = processSubset(crit.getSubsetOperators(), sqlString
					, fromStringBuilder.toString(), whereString, baseAlias, groupByPatient);
		}

		helper.putSql(crit.getId(), sqlString);

		return sqlString;
	}

	private void processCodeList(DataCriteria crit, StringBuilder where, String baseAlias) {
		where.append("AND ");
		boolean isMedication = "allMedications".equals(crit.getPatientApiFunction());
		if (!isMedication) {
			where.append(helper.field(baseAlias, CODE_ID));
		} else {
			where.append(helper.field(baseAlias, "product_code_id"));
		}
		where.append(" IN ");
		getValueSetClause(where, crit.getCodeListId());
	}

	private void processStatus(DataCriteria crit, StringBuilder where, String baseAlias) {

		if ("performed".equals(crit.getStatus())) {
			// special case for 'performed' status, also consider 'completed' as equivalent due to a mismatch between QDM and HQMF
			where.append("AND " + helper.field(baseAlias, STATUS) + " IN ('performed','completed')");
		} else if ("administered".equals(crit.getStatus())) {
			// special case for 'administered' status, also consider 'completed' as equivalent due to a mismatch between QDM and HQMF
			where.append("AND " + helper.field(baseAlias, STATUS) + " IN ('administered','completed')");
		} else if ("active".equals(crit.getStatus())) {
			// special case for 'active' status, also consider 'completed' as equivalent due to a mismatch between QDM and HQMF
			where.append("AND " + helper.field(baseAlias, STATUS) + " IN ('active','completed')");
		} else if ("ordered".equals(crit.getStatus())) {
			// special case for 'ordered' status, also consider 'new' as equivalent due to a mismatch between QDM and HQMF
			where.append("AND " + helper.field(baseAlias, STATUS) + " IN ('ordered','new')");
		} else if ("dispensed".equals(crit.getStatus())) {
			// special case for 'dispensed' status, also consider 'completed' as equivalent due to a mismatch between QDM and HQMF
			where.append("AND " + helper.field(baseAlias, STATUS) + " IN ('dispensed','completed')");
		} else {
			where.append("AND " + helper.field(baseAlias, STATUS) + " ='" + crit.getStatus() + "'");
		}
	}

	private String  processSubset(List<HQMFSubsetOperator> subsetOps, String baseSql, String fromString, String whereString, String baseAlias, Boolean groupByPatient) {
		boolean last = false;
		boolean first = false;
		boolean count = false;
		if (subsetOps != null) {
			if (subsetOps.size() != 1) {
				throw new NotImplementedException("multiple subsets not implemented yet");
			}
			HQMFSubsetOperator subOp = subsetOps.get(0); 
			if ("FIRST".equals(subOp.getType())) {
				first = true;
			} else if ("QDM_LAST".equals(subOp.getType())) {
				last = true;
			} else if ("SUM".equals(subOp.getType())) {
				count = true;
			} else if ("DATEDIFF".equals(subOp.getType())) {
				throw new NotImplementedException("DATEDIFF not implemented yet"); // TODO
			} else if ("TIMEDIFF".equals(subOp.getType())) {
				throw new NotImplementedException("TIMEDIFF not implemented yet"); // TODO
			} else {
				throw new RuntimeException("unknown subset operator: " + subOp.getType());
			}
			if (first || last) {
				StringBuilder sql = new StringBuilder(baseSql);
				String order = last ? "MAX" : "MIN";
				String subsetType = baseAlias;
				subsetType += last ? "_MR" : "_FR";
				String idColumn = (groupByPatient) ? "patient_id" : "id";
				sql.append(String.format(" AS %s_MAIN INNER JOIN ( ", baseAlias));
				sql.append(String.format(" SELECT %s(%s) AS ETS, %s as record_id ", order
						, helper.field(baseAlias, EFFECTIVE_TIME_START)
						, helper.field(baseAlias, idColumn)));
				if (fromString != null) {
					sql.append(fromString);
				}
				if (whereString != null) {
					if (!StringUtils.isEmpty(whereString)) {
						sql.append(" WHERE ");
					}
					sql.append(whereString);
				}
				sql.append(String.format(" GROUP BY %s) AS %s ON %s = %s AND %s = %s "
								, helper.field(baseAlias, idColumn)
								, subsetType
								, helper.field(String.format("%s_MAIN", baseAlias), idColumn)
								, helper.field(subsetType, "record_id")
								, helper.field(String.format("%s_MAIN", baseAlias), EFFECTIVE_TIME_START)
								, helper.field(subsetType, "ETS"))
				);
				return sql.toString();
			}
			
			// COUNT/SUM subset
			if (count) {
				
				HQMFRange range = (HQMFRange)subOp.getValue();
				StringBuilder sql = new StringBuilder(baseSql);
				boolean zeroCount = 
						range.getLow() != null && range.getLow().getValue().equals("0") &&
						range.getHigh() != null && range.getHigh().getValue().equals("0");
				
				if (zeroCount) {
					throw new NotImplementedException("COUNT=0 not supported yet");
				}

				String baseClauseAlias = baseAlias + "_CNTBASE";
				String cntAlias = baseAlias + "_CNT";
				sql.append(
					String.format("%s INNER JOIN ( SELECT patient_id AS cnt_patient_id, COUNT(*) AS CNT %s ", baseClauseAlias, fromString)
				);
				if (!StringUtils.isEmpty(whereString)) {
					sql.append("WHERE ").append(whereString);
				}
				sql.append(" GROUP BY patient_id ");
				sql.append(
					String.format(" ) AS %s ON %s.patient_id = %s.cnt_patient_id WHERE ", cntAlias, baseClauseAlias, cntAlias)
				);
				
				
				if (range.getLow() != null) {
					validateUnit(range.getLow().getUnit());
					String op = range.getLow().isInclusive() ? ">=" : ">"; 
					sql.append(String.format("%s_CNT.CNT %s %s ", baseAlias, op, range.getLow().getValue()));
				}
				if (range.getHigh() != null) {
					validateUnit(range.getHigh().getUnit());
					String op = range.getHigh().isInclusive() ? "<=" : "<";
					if (range.getLow() != null) {
						sql.append("AND ");
					}
					sql.append(String.format("%s_CNT.CNT %s %s ", baseAlias, op, range.getHigh().getValue()));
				}
				return sql.toString();
			}
		}
		
		return baseSql;
	}

	private String processDerived(DataCriteria crit) {

		// scan for all the occurrences in the group
		Set<String> occAliases = helper.getAllOccurrences(crit);
		String viewName = helper.getViewName(crit.getId());

		/*
			Create an inner view to hold the data from the derived criteria.
		 */
		StringBuilder sql = new StringBuilder();
		String derivedView = viewName + "_DERIVED";
		sql.append("CREATE VIEW ");
		helper.output(sql, SCHEMA, derivedView);
		sql.append(" AS ");
		for (int i = 0; i < crit.getChildrenCriteria().size(); i++) {
			String child = crit.getChildrenCriteria().get(i);
			DataCriteria grpCrit = helper.findDataCriteria(child);
			if (!helper.isViewInstantiated(child)) {
				helper.instantiateView(child);
			}
			//if (helper.hasSpecificOccurrence(child)) {
			//	throw new NotImplementedException("groups with specific occurrences not supported yet");
			//}
			if (i > 0) {
				if ("UNION".equals(crit.getDerivationOperator())) {
					sql.append(" UNION ");
				} 
				else if ("XPRODUCT".equals(crit.getDerivationOperator())) {
					sql.append(" INTERSECT ");
				} 
				else {
					throw new NotImplementedException("unrecognized derivation operator: " + crit.getDerivationOperator());
				}
			}
			//sql.append("/* grp child " + child + "*/ ");
			sql.append("select t.patient_id, t.effective_time_start, t.effective_time_end, t.id");
			val outerOccs = new ArrayList<String>();
			for (String occAlias : occAliases) {
				if (helper.hasLeftSideOccurrenceAlias(grpCrit, occAlias)) {
					sql.append(", t.id as id" + occAlias);
				} else if (helper.hasRightSideSpecificOccurrenceAlias(grpCrit, occAlias)) {
					sql.append(", t.id" + occAlias);
				} else {
					outerOccs.add(occAlias);
					sql.append(", " + occAlias + ".id as id" + occAlias);
				}
			}
			sql.append(" from " + helper.field(SCHEMA, helper.getViewName(child)) + " t");
			for (String outerOcc : outerOccs) {
				sql.append(String.format(" LEFT OUTER JOIN %s %s ON %s = %s", 
						helper.field(SCHEMA, helper.getOccurrenceViewName(outerOcc)), outerOcc,
						helper.field("t", PATIENT_ID), helper.field(outerOcc, PATIENT_ID)));
			}
			//} else {
			// the reference was not processed yet, so return false to signal the criteria should be retried on the next iteration
			//	System.out.println("(deferred due to unresolved reference)");
			//	return null;
			//}
		}

		String sqlString = sql.toString();

		// Instantiate the derived view.
		helper.instantiateViewSQL(derivedView, sqlString);
		helper.putSql(crit.getId() + "_DERIVED", sqlString);
		Boolean groupByPatient = (occAliases == null || occAliases.size() == 0);

		// If subset operators exist, create an aggregate off this derived view.
		//
		//	o.w. return all results in the derived view
		sql = new StringBuilder();
		if (crit.getSubsetOperators() != null) {
			//
			// throw new NotImplementedException("subsets not implemented yet for groups");
			sql.append(String.format("CREATE VIEW %s AS SELECT * FROM (SELECT * FROM %s) ", 
					helper.field(SCHEMA, viewName), helper.field(SCHEMA, derivedView)));
			sqlString = sql.toString();
			sqlString = processSubset(crit.getSubsetOperators(), sqlString
					, String.format("FROM %s", helper.field(SCHEMA,  derivedView)), null, derivedView, groupByPatient);
			sql.setLength(0);
			sql.append(sqlString);
		}
		else {
			sql.append(String.format("CREATE VIEW %s AS SELECT * FROM %s ", 
					helper.field(SCHEMA, viewName), helper.field(SCHEMA, derivedView)));
		}
		return sql.toString();
	}

	private void generateFromClause(FromClause fromClause, StringBuilder sql) {
		sql.append(" FROM ");
		helper.output(sql, SCHEMA, fromClause.getTableName());
		sql.append(" ");
		sql.append(fromClause.getAlias());
		fromClause.getJoins().forEach(join -> {
			sql.append(" JOIN ");
			helper.output(sql, SCHEMA, join.getTableName());
			sql.append(" ");
			sql.append(join.getAlias());
			sql.append(" ON ");
			sql.append(join.getOnClause());
		});
	}

	private void processValue(DataCriteria crit, String baseAlias, StringBuilder where) {
		String field = getValueField(crit, baseAlias);

		if (crit.getValue() instanceof HQMFRange) {
			HQMFRange range = (HQMFRange)crit.getValue();

			if (range.getLow() != null && range.getLow().getValue() != null) {
				validateUnit(range.getLow().getUnit());
				String op = range.getLow().isInclusive() ? ">=" : ">";
				where.append(" AND " + field + " " + op + " " + range.getLow().getValue());
			}
			if (range.getHigh() != null && range.getHigh().getValue() != null) {
				validateUnit(range.getHigh().getUnit());
				String op = range.getHigh().isInclusive() ? "<=" : "<";
				where.append(" AND " + field + " " + op + " " + range.getHigh().getValue());
			}

		} else if (crit.getValue() instanceof HQMFValue) {
			HQMFValue value = (HQMFValue)crit.getValue();
			validateUnit(value.getUnit());
			where.append(" AND " + field + " = " + value.getValue());

		} else if (crit.getValue() instanceof HQMFCoded) {
			HQMFCoded hqmfCoded = (HQMFCoded)crit.getValue();
			if (hqmfCoded.getCode() != null) {
				where.append(" AND " + field + " = '" + hqmfCoded.getCode() + "'");
			} else if (hqmfCoded.getCodeListId() != null) {

				// throw new NotImplementedException("coded list values not implemented yet");
				//
				// TODO: Not sure the following is correct, needs investigation.  Also, does codesystem itself be included?

				//	A code system was passed in.  Change the field to use the code system field in the views.
				field = getValueCodeSystemField(crit, baseAlias);
				where.append(" AND " + field + " IN ");
				getValueSetClause(where, hqmfCoded.getCodeListId());
			} else {
				throw new IllegalArgumentException("unknown HQMFCoded type: " + hqmfCoded);
			}

		} else if (crit.getValue() instanceof HQMFAnyValue) {
			HQMFAnyValue hqmfAnyValue = (HQMFAnyValue)crit.getValue();
			if ("ANYNonNull".equals(hqmfAnyValue.getType())) {
				String codeValueField = null;
				try {
					codeValueField = getValueCodeSystemField(crit, baseAlias);
				} catch (IllegalArgumentException e) {
					// ignore
				}
				if (codeValueField == null) {
					where.append(" AND " + field + " IS NOT NULL");
				} else {
					where.append(" AND (" + field + " IS NOT NULL OR " + codeValueField + " IS NOT NULL)");
				}
			}
			else throw new IllegalArgumentException("unknown HQMFAnyValue type: " + hqmfAnyValue.getType());

		} else {
			throw new IllegalArgumentException("unknown value type: " + crit.getValue());
		}

	}

	private String getValueField(DataCriteria crit, String baseAlias) {
		if ("characteristic".equals(crit.getType())) {
			return helper.field(baseAlias, crit.getProperty());
		}
		if ("laboratoryTests".equals(crit.getPatientApiFunction())) {
			if (crit.getValue() instanceof HQMFAnyValue &&
					"ANYNonNull".equals(((HQMFAnyValue)crit.getValue()).getType())) {
				return helper.field(baseAlias, "record_value");
			}
			return "CLEAN_CAST_TO_DOUBLE(" + helper.field(baseAlias, "record_value") + ")";
		} else if ("procedureResults".equals(crit.getPatientApiFunction())) {
			if (crit.getValue() instanceof HQMFAnyValue &&
					"ANYNonNull".equals(((HQMFAnyValue)crit.getValue()).getType())) {
				return helper.field(baseAlias, "result_value");
			}
			return "CLEAN_CAST_TO_DOUBLE(" + helper.field(baseAlias, "result_value") + ")";
		} else if ("procedures".equals(crit.getPatientApiFunction())) {
			if (crit.getValue() instanceof HQMFAnyValue &&
					"ANYNonNull".equals(((HQMFAnyValue)crit.getValue()).getType())) {
				return helper.field(baseAlias, "value_string");
			}
			return "CLEAN_CAST_TO_DOUBLE(" + helper.field(baseAlias, "value_string") + ")";
		} else {
			throw new IllegalArgumentException("unknown value field for: " + crit.getPatientApiFunction());
		}
	}

	private String getValueCodeSystemField(DataCriteria crit, String baseAlias) {
		if ("laboratoryTests".equals(crit.getPatientApiFunction()) 
				|| "procedures".equals(crit.getPatientApiFunction())
				|| "procedureResults".equals(crit.getPatientApiFunction())) {
			return helper.field(baseAlias, "value_code_id");
		} else {
			throw new IllegalArgumentException("unknown value code system field for: " + crit.getPatientApiFunction());
		}
	}

	private void validateUnit(String unit) {
		if (unit == null) {
			return;
		}
		switch (unit) {
			case "%":
			case "[copies]/mL":
			case "{H.B}/min":
			case "kg/m2":
			case "mg/dL":
			case "/mm3":
			case "mm[Hg]":
			case "ng/mL":
			case "s":
			case "wk":
				return;
		}
		throw new IllegalArgumentException("unknown unit: " + unit);
	}

	private String processTemporalReference(HQMFTemporalReference temporalRef, DataCriteria crit, String baseAlias, FromClause fromClause, StringBuilder where) {

		String start = EFFECTIVE_TIME_START;
		String end = EFFECTIVE_TIME_END;
		String innerView = null;
		String occurrenceAlias = null;

		if ("MeasurePeriod".equals(temporalRef.getReference())) {
			start = "'" + MEASURE_PERIOD_START_PLACEHOLDER + "'";
			end = "'" + MEASURE_PERIOD_END_PLACEHOLDER + "'";
		} else {
			if (temporalRef.getReference() != null) {
				innerView = helper.getViewName(temporalRef.getReference());
				if (!helper.isViewInstantiated(temporalRef.getReference())) {
					helper.instantiateView(temporalRef.getReference());
				}

				DataCriteria tempCrit = helper.findDataCriteria(temporalRef.getReference());
				if (tempCrit.getSpecificOccurrenceConst() != null) {
					occurrenceAlias = helper.getOccurrenceAlias(tempCrit);
				}
				if (tempCrit.getSubsetOperators() != null && tempCrit.getSubsetOperators().size() > 1) {
					throw new NotImplementedException("temporal relationships with right side subsets not implemented yet");
				}
			} else {
				// the reference was not processed yet, so return false to signal the criteria should be retried on the next iteration
				return null;
			}
		}

		String var1 = null;
		//if ("characteristic".equals(crit.getType())) {
		//	var1 = crit.getProperty();

		// TODO: determine if column or json data should be renamed, but reassign for now
		//	if ("birthtime".equals(var1)) {
		//		var1 = BIRTH_TIME;
		//	}

		// special case for expired
		//	else if ("expired".equals(var1)) {
		//		var1 = DEATH_DATE;
		//	}
		//}

		String var2 = null;
		String var2Alias = null;

		if (innerView != null) {
			var2Alias= (occurrenceAlias == null) ? helper.getNewFromTableAlias() : occurrenceAlias;
			fromClause.getJoins().add(new Join(var2Alias, innerView, helper.field(baseAlias, PATIENT_ID) + " = " + helper.field(var2Alias, PATIENT_ID)));
		}

		String refType = temporalRef.getType();
		where.append(" AND ");
		if (logger.isDebugEnabled()) {
			where.append("/* " + refType + " */ ");
		}

		// for 'SBS', 'SAS', 'SBE','SAE','EBS', 'EAS', 'EBE', 'EAE', 'SDU', 'EDU', 'ECW', 'SCW', 'ECWS', 'SCWE'
		if (refType.length() <= 4) {

			String source = refType.substring(0, 1);
			if (var1 == null) {
				if ("S".equals(source)) {
					var1 = EFFECTIVE_TIME_START;
				} else if ("E".equals(source)) {
					var1 = EFFECTIVE_TIME_END;
				} else {
					throw new RuntimeException("unknown type:" + refType);
				}
			}

			String op = null;
			String relation = refType.substring(1, 2);
			if ("A".equals(relation)) {
				op = ">";
			} else if ("B".equals(relation)) {
				op = "<";
			} else if ("C".equals(relation)) {
				op = "=";
			} else if ("D".equals(relation)) {
				// ignore for now, will be processed below
			} else {
				throw new RuntimeException("unknown type:" + refType);
			}

			String target = refType.substring(2, 3);
			if ("S".equals(target)) {
				var2 = start;
			} else if ("E".equals(target)) {
				var2 = end;
			} else if ("W".equals(target) && "C".equals(relation)) { // SCW, ECW, SCWE or ECWS 
				if (refType.length() == 3) {
					if ("S".equals(source))
						var2 = start;
					else if ("E".equals(source) ) {
						var2 = end;
					} else {
						throw new RuntimeException("unknown type:" + refType);
					}
				} else { // 'ECWS' or 'SCWE'
					if (refType.endsWith("S")) {
						var2 = start;
					} else if (refType.endsWith("E")) {
						var2 = end;
					} else {
						throw new RuntimeException("unknown type:" + refType);
					}
				}
			} else if ("U".equals(target) && "D".equals(relation)) { // EDU or SDU

				// For now, "fake it" by putting the whole 2nd clause in var2 
				// TODO: find a better way!
				op = ">=";
				var2 = millisEpoch(start) + " AND " + millisEpoch(helper.field(baseAlias, var1)) + " <= ";
				var2 += millisEpoch(helper.field(!end.startsWith("'") ? var2Alias : null, end));
			} else {
				throw new RuntimeException("unknown type:" + refType);
			}

			// NOTE: due to inconsistencies found in how H2 compares dates, the custom function MILLIS_EPOCH has 
			//       been developed to perform the comparisons more reliably
			
			if (innerView != null) {
				where.append(String.format("%s %s %s", millisEpoch(helper.field(baseAlias, var1)), op, millisEpoch(helper.field(var2Alias, var2))));
			} else {
				String var2Str = var2;
				if (var2.startsWith("'")) {
					var2Str = millisEpoch(var2);
				}
				where.append(String.format("%s %s %s", millisEpoch(helper.field(baseAlias, var1)), op, var2Str));
			}
		} else if ("DURING".equals(refType)) {
			where.append(String.format("%s >= %s AND %s <= %s", 
					helper.field(baseAlias, EFFECTIVE_TIME_START), millisEpoch(helper.field(var2Alias, start)), 
					helper.field(baseAlias, EFFECTIVE_TIME_END), millisEpoch(helper.field(var2Alias, end)))
			);
		} else if ("CONCURRENT".equals(refType)) {
			where.append(String.format("%s = %s AND %s = %s", 
					helper.field(baseAlias, EFFECTIVE_TIME_START), millisEpoch(helper.field(var2Alias, start)), 
					helper.field(baseAlias, EFFECTIVE_TIME_END), millisEpoch(helper.field(var2Alias, end)))
			);
		} else {
			throw new RuntimeException("unknown type:" + refType);
		}

		HQMFRange range = temporalRef.getRange();
		if (range != null) {

			//
			// TIMESTAMPDIFF(d1, d2) = d2 - d1
			//
			//  Precondition: var1 is the date field in the H2 view, var2 is the measurement period.
			//	    If relation is "after", the date field (from the H2 view) is the second parameter of the function.
			//      If the relation is "before", the date field from the H2 view is the first parameter of the function.
			String relation = refType.substring(1, 2);
			String dateOne = var1;
			String dateTwo = var2;
			String aliasOne = baseAlias;
			String aliasTwo = var2Alias;
			if (relation != null && "A".equals(relation)) {
				dateOne = var2;
				dateTwo = var1;
				aliasOne = var2Alias;
				aliasTwo = baseAlias;
			}

			String dateOneField = helper.field(aliasOne, dateOne);
			String dateTwoField = helper.field(aliasTwo, dateTwo);
			HQMFValue hqmfValue = range.getLow();
			if (hqmfValue != null && hqmfValue.getUnit() != null) {
				String op = hqmfValue.isInclusive() ? ">=" : ">";
				determineRangeSQL(where, dateOneField, dateTwoField, hqmfValue,	op);
			}
			hqmfValue = range.getHigh();
			if (hqmfValue != null && hqmfValue.getUnit() != null) {
				String op = range.getHigh().isInclusive() ? "<=" : "<";
				determineRangeSQL(where, dateOneField, dateTwoField, hqmfValue,	op);
			}
		}

		if ("MeasurePeriod".equals(temporalRef.getReference())) {
			return "MeasurePeriod";
		}
		return var2Alias;
	}
	
	private String millisEpoch(String field) {
		if (field.endsWith(EFFECTIVE_TIME_START) || field.endsWith(EFFECTIVE_TIME_END)) {
			return field;
		}
		return "MILLIS_EPOCH(" + field + ")";
	}

	private void determineRangeSQL(
			StringBuilder where, 
			String dateOneStr,
			String dateTwoStr, 
			HQMFValue hqmfValue, 
			String op) {
		
		String intervalFunc = determineIntervalFunction(hqmfValue);
		where.append(String.format(
			" AND %s(%s, %s) %s %s", intervalFunc, millisEpoch(dateOneStr), millisEpoch(dateTwoStr), op, hqmfValue.getValue()
		));
	}
	
	private String determineIntervalFunction(HQMFValue hqmfValue) {
		switch(hqmfValue.getUnit()) {
			case "a":   return "YEARS_DIFFERENCE";
			case "d":   return "DAYS_DIFFERENCE";
			case "h":   return "HOURS_DIFFERENCE";
			case "min": return "MINUTES_DIFFERENCE";
			case "mo":  return "MONTHS_DIFFERENCE";
			case "wk":  return "WEEKS_DIFFERENCE";
		}
		throw new RuntimeException("Unknown unit: " + hqmfValue.getUnit());
	}

	private void getValueSetClause(StringBuilder sql, String codeListId) {
		sql.append("(SELECT ")
				.append(CODE_ID)
				.append(" FROM ")
				.append(COMMON_SCHEMA)
				.append(".VALUE_SET_CODE WHERE ")
				.append(VALUE_SET_ID)
				.append(" = ")
				.append(translateValueSet(codeListId))
				.append(")");
	}

	private String translateValueSet(String codeListId) {
		ValueSet entity = this.valueSetRepository.findOne(ValueSetPredicate.searchByOid(codeListId, bundleId));
		if (entity == null || entity.getId() == null) {
			throw new IllegalStateException(String.format("Value set database record not found for code system -> %s", codeListId));
		}
		return entity.getId().toString();
	}
	
	private boolean isSpecialPatientCharacteristic(DataCriteria crit) {
		return "clinicalTrialParticipant".equals(crit.getProperty())
				|| "PatientCharacteristicCurrentCigaretteSmoker".equals(crit.getId()) 
				|| "OccurrenceATobaccoNonUser2".equals(crit.getId())
				|| "OccurrenceATobaccoUser3".equals(crit.getId())
				|| "PatientCharacteristicTobaccoNonUser".equals(crit.getId())
				|| "PatientCharacteristicTobaccoUser".equals(crit.getId())
				|| "OccurrenceAEcogPerformanceStatusPoor30".equals(crit.getId());
	}



}
