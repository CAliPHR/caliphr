package com.ainq.caliphr.hqmf.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.hqmf.model.DataCriteria;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.model.type.HQMFTemporalReference;
import com.ainq.caliphr.hqmf.model.type.HQMFValue;
import com.ainq.caliphr.hqmf.util.H2PopulateUtil;

import ch.qos.logback.classic.Logger;
import lombok.Data;

@Component
@Scope("prototype")
public class HQMFGenerationHelper {
	
	static Logger logger = (Logger) LoggerFactory.getLogger(HQMFGenerationHelper.class);
	
	private Map<String, SqlEntry> sqls;
	
	// if the sql for this measure has already been generated in a previous run since JVM start, use the cached sql
	private static Map<String, Map<String, SqlEntry>> sqlCache = new HashMap<>();
	
	private HQMFDocument hqmfDoc;
	private GenerateSQLsFromHQMF generateSQLsFromHQMF;
	
	// to ensure unique table name aliases, keep a counter
	private int aliasCounter = 0;
	
	private Map<String, String> occurrenceKeyToAliasMap = new LinkedHashMap<String, String>();
	private Map<String, String> occurrenceAliasToViewNameMap = new LinkedHashMap<String, String>();
	
	@Autowired
	private H2PopulateUtil h2PopulateUtil;
	
	public static final String COMMON_SCHEMA = "common";
	public static final String SCHEMA = "caliphr";
	public static final String CODE_ID = "code_id";
	public static final String PATIENT_ID = "patient_id";
	public static final String VALUE_SET_ID = "value_set_id";
	public static final String EFFECTIVE_TIME_START = "effective_time_start";
	public static final String EFFECTIVE_TIME_END = "effective_time_end";
	public static final String STATUS = "status_code_name";
	public static final String BIRTH_TIME = "birth_time";
	public static final String DEATH_DATE = "death_date";
	public static final String GENDER = "gender";
	public static final String NEGATION_CODE_ID = "negation_code_id";
	public static final String MEASURE_PERIOD_START_PLACEHOLDER = "@MSR_PD_START_PH@";
	public static final String MEASURE_PERIOD_END_PLACEHOLDER = "@MSR_PD_END_PH@";
	
	public HQMFGenerationHelper(HQMFDocument hqmfDoc, GenerateSQLsFromHQMF generateSQLsFromHQMF) {
		this.hqmfDoc = hqmfDoc;
		this.generateSQLsFromHQMF = generateSQLsFromHQMF;
		
		sqls = sqlCache.get(hqmfDoc.getCmsId());
		if (sqls == null) {
			sqls = new LinkedHashMap<>();
			synchronized (HQMFGenerationHelper.class) {
				sqlCache.put(hqmfDoc.getCmsId(), sqls);
			}
		}
	}

	public String getSql(String key) {
		SqlEntry entry = sqls.get(key);
		return entry != null ? entry.getSql() : null;
	}
	
	public void putSql(String key, SqlEntry sql) {
		sqls.put(key, sql);
	}
 		
	public void putSql(String key, String sql) {
		sqls.put(key, new SqlEntry(sql));
	}
	
	public void markViewInstantiated(String key) {
		sqls.get(key).setViewInstantiated(true);
	}
	
	public boolean isViewInstantiated(String key) {
		SqlEntry entry = sqls.get(key);
		return entry != null && entry.isViewInstantiated();
	}
	
	public DataCriteria findDataCriteria(String id) {
		DataCriteria dataCrit = hqmfDoc.getDataCriteria().get(id);
		if (dataCrit != null) {
			return dataCrit;
		}
		if (hqmfDoc.getSourceDataCriteria() != null) {
			dataCrit = hqmfDoc.getSourceDataCriteria().get(id);
			if (dataCrit != null) {
				return dataCrit;
			}
		}
		throw new IllegalArgumentException("unknown criteria id: " + id);
		//return null;
	}
	
	public void instantiateView(String key) {
		logger.debug("***** {}", key);
		String sql = getSql(key);
		if (sql == null) {
			DataCriteria dataCrit = findDataCriteria(key);
			sql = generateSQLsFromHQMF.generateSQLForDataCriteria(dataCrit);
		}
		if (sql == null) {
			throw new IllegalStateException("key not found: " + key);
		}
		
		instantiateViewSQL(getViewName(key), sql);
		markViewInstantiated(key);
	}

	public void instantiateViewSQL(String key, String sql) {
		h2PopulateUtil.testSQLUpdate(resolvePlaceholders(sql), key);
		
		//if (logger.isDebugEnabled()) {
			//h2PopulateUtil.testSQLQuery("SELECT * FROM " + field(SCHEMA, key), key + "_COUNT");
		//}
	}

	private String resolvePlaceholders(String sql) {
		sql = sql.replaceAll(MEASURE_PERIOD_START_PLACEHOLDER,  toDateFormat(hqmfDoc.getMeasurePeriod().getLow()));
		sql = sql.replaceAll(MEASURE_PERIOD_END_PLACEHOLDER,  toDateFormat(hqmfDoc.getMeasurePeriod().getHigh()));
		return sql;
	}
	
	public boolean hasSpecificOccurrence(String key) {
		DataCriteria dataCrit = findDataCriteria(key);
		if (dataCrit.getSpecificOccurrenceConst() != null) {
			return true;
		}
		if (dataCrit.getSourceDataCriteria() != null && 
			!dataCrit.getId().equals(dataCrit.getSourceDataCriteria()) && 
			hasSpecificOccurrence(dataCrit.getSourceDataCriteria())) {
			return true;
		}
		return hasTemporalSpecificOccurrence(dataCrit.getTemporalReferences());
	}
	
	public boolean hasTemporalSpecificOccurrence(List<HQMFTemporalReference> temporalRefs) {
		if (temporalRefs != null) {
			for (HQMFTemporalReference temporalRef : temporalRefs) {
				if (temporalRef.getReference() != null && !"MeasurePeriod".equals(temporalRef.getReference()) && hasSpecificOccurrence(temporalRef.getReference())) {
					return true;
				}					
			}
		}
		return false;
	}
	
	public boolean hasSpecificOccurrenceAlias(DataCriteria dataCrit, String alias) {
		if (hasLeftSideOccurrenceAlias(dataCrit, alias)) {
			return true;
		}
		return hasRightSideSpecificOccurrenceAlias(dataCrit, alias);
	}

	public boolean hasLeftSideOccurrenceAlias(DataCriteria dataCrit, String alias) {
		if (dataCrit.getSpecificOccurrenceConst() != null && alias.equals(getOccurrenceAlias(dataCrit))) {
			return true;
		}
		if (dataCrit.getSourceDataCriteria() != null && !dataCrit.getId().equals(dataCrit.getSourceDataCriteria())) {
			DataCriteria srcCrit = findDataCriteria(dataCrit.getSourceDataCriteria());
			if (hasSpecificOccurrenceAlias(srcCrit, alias)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRightSideSpecificOccurrenceAlias(DataCriteria dataCrit, String alias) {
		if (dataCrit.getTemporalReferences() != null) {
			for (HQMFTemporalReference temporalRef : dataCrit.getTemporalReferences()) {
				if (temporalRef.getReference() != null && !"MeasurePeriod".equals(temporalRef.getReference())) {
					DataCriteria temporalCrit = findDataCriteria(temporalRef.getReference());
					if (hasSpecificOccurrenceAlias(temporalCrit, alias)) {
						return true;
					}
				}					
			}
		}
		return false;
	}
	
	public String getOccurrenceAlias(String specificOccConstant, String specificOccLetter, String critId) {
		String key = specificOccConstant + "$" + specificOccLetter;
		String alias = occurrenceKeyToAliasMap.get(key);
		if (alias != null) {
			return alias;
		}
		String value = "o" + (occurrenceKeyToAliasMap.size()+1);
		occurrenceKeyToAliasMap.put(key, value);
		occurrenceAliasToViewNameMap.put(value, getViewName(critId));
		return value;
	}
	
	public String getOccurrenceViewName(String occAlias) {
		return occurrenceAliasToViewNameMap.get(occAlias);
	}

	public String getOccurrenceAlias(DataCriteria dataCrit) {
		return getOccurrenceAlias(dataCrit.getSpecificOccurrenceConst(), dataCrit.getSpecificOccurrence(), dataCrit.getSourceDataCriteria());
	}
	
	public String getOccurrenceAliasIfExists(String key) {
		DataCriteria dataCrit = findDataCriteria(key);
		return getOccurrenceAliasIfExists(dataCrit);
	}
	
	public String getOccurrenceAliasIfExists(DataCriteria dataCrit) {
		if (dataCrit.getSpecificOccurrenceConst() == null || dataCrit.getSpecificOccurrence() == null) {
			return null;
		}
		return getOccurrenceAlias(dataCrit.getSpecificOccurrenceConst(), dataCrit.getSpecificOccurrence(), dataCrit.getSourceDataCriteria());
	}
	
	public String getViewName(String key) {
		return hqmfDoc.getCmsId() + "_" + key;
	}
	
	public String getNewFromTableAlias() {
		return "t" + aliasCounter++;
	}
	
	public String getTemporalOccurenceAliasIfExists(DataCriteria dataCrit) {
		if (dataCrit.getTemporalReferences() == null || dataCrit.getTemporalReferences().size() == 0) {
			return null;
		}
		for (DataCriteria temporalCrit : getTemporalDataCriteria(dataCrit.getTemporalReferences())) {
			if (temporalCrit != null && temporalCrit.getSpecificOccurrenceConst() != null) {
				return getOccurrenceAlias(temporalCrit);
			}
		}
		return null;
	}
	
	public List<String> getTemporalRefNames(List<HQMFTemporalReference> temporalRefs) {
		if (temporalRefs == null) {
			return null;
		}
		List<String> list = null;
		for (HQMFTemporalReference temporalRef : temporalRefs) {
			String refName = temporalRef.getReference();
			if (refName != null && !"MeasurePeriod".equals(refName)) {
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(refName);
			}
		}
		return list;
	}
	
	public List<DataCriteria> getTemporalDataCriteria(List<HQMFTemporalReference> temporalRefs) {
		List<String> refNames = getTemporalRefNames(temporalRefs);
		if (refNames != null) {
			List<DataCriteria> list = new ArrayList<>();
			for (String refName : refNames) {
				list.add(findDataCriteria(refName));
			}
			return list;
		}
		return null;
	}
	
	public Set<String> getAllOccurrences(DataCriteria crit) {
		Set<String> occAliases = new LinkedHashSet<String>();
		
		if (crit.getSpecificOccurrenceConst() != null) {
			occAliases.add(getOccurrenceAlias(crit));
		}
		List<DataCriteria> temporalDataCriteria = getTemporalDataCriteria(crit.getTemporalReferences());
		if (temporalDataCriteria != null) {
			for (DataCriteria temporalCrit : temporalDataCriteria) {
				occAliases.addAll(getAllOccurrences(temporalCrit));
			}
		}
		if (!crit.getId().equals(crit.getSourceDataCriteria())) {
			DataCriteria srcCrit = findDataCriteria(crit.getSourceDataCriteria());
			String nestedTemporalAlias = getTemporalOccurenceAliasIfExists(srcCrit);
			if (nestedTemporalAlias != null) {
				throw new NotImplementedException("did not implement groups with nested temporal occurrences yet");
				//occAliases.add(temporalAlias);
			}
		}
		if (crit.getChildrenCriteria() != null) {
			for (String child : crit.getChildrenCriteria()) {
				DataCriteria grpCrit = findDataCriteria(child);
				occAliases.addAll(getAllOccurrences(grpCrit)); 
			}
		}
		return occAliases;
	}
	
	public String field(String schemaName, String field) {
		StringBuilder str = new StringBuilder();
		output(str, schemaName, field);
		return str.toString();
	}
	
	public void output(StringBuilder str, String schemaName, String... fields) {
		for (int i = 0; i < fields.length; i++) {
			if (schemaName != null && !fields[i].startsWith("'")) {
				str.append(schemaName);
				str.append(".");
			}
			str.append(fields[i]);
			if (i < fields.length-1) {
				str.append(", ");
			}
		}
	}
	
	/**
	 * if the sql for this measure has already been generated in a previous run since JVM start, use the cached sql
	 */
	public void executeSqlIfCached() {
		Map<String, SqlEntry> sqlsForCmsId = sqlCache.get(hqmfDoc.getCmsId());
		if (sqlsForCmsId != null) {
			for (Entry<String, SqlEntry> entry : sqlsForCmsId.entrySet()) {
				String viewName = getViewName(entry.getKey());
				logger.debug("*********** cached " + viewName);
				String sql = resolvePlaceholders(entry.getValue().getSql());
				h2PopulateUtil.testSQLUpdate(sql, viewName);
			}
		}
	}
	
	private String toDateFormat(HQMFValue hqmfValue) {
		return DateFormatUtils.ISO_DATE_FORMAT.format(hqmfValue.asDate());
	}
	
	public static void removeFromSqlCache(String cmsId) {
		synchronized (HQMFGenerationHelper.class) {
			sqlCache.remove(cmsId);
		}
	}

	public @Data class SqlEntry {
		private final String sql;
		private boolean viewInstantiated;
	}

}
