package com.ainq.caliphr.persistence.transformation.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientImmunization;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientInfo;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientMedication;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientProblem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientProcedure;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientResult;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientVitalSign;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * 
 * For efficient duplicate detection, maintain a multi-level map of existing items (such as medications, procedures etc)
 * keyed off of effective time start and code.  This minimizes the number of items which would need to be compared to detect 
 * duplicate records.  
 * 
 * @author drosenbaum
 *
 */
public class ExistingItemContext {
	
	static final FastDateFormat tsCompareFormat = FastDateFormat.getInstance("yyyyMMddHHmm");
	
	private ExistingItemMap<PatientImmunization> existingImmunizations = new ExistingItemMap<>();
	private ExistingItemMap<PatientMedication> existingMedications = new ExistingItemMap<>();
	private ExistingItemMap<PatientProblem> existingProblems = new ExistingItemMap<>();
	private ExistingItemMap<PatientProcedure> existingProcedures = new ExistingItemMap<>();
	private ExistingItemMap<PatientResult> existingResults = new ExistingItemMap<>();
	private ExistingItemMap<PatientVitalSign> existingVitalSigns = new ExistingItemMap<>();
	
	public ExistingItemContext(ClinicalDocument clinicalDocument) {
		
		
		PatientInfo patient = clinicalDocument.getPatient();
		patient.getPatientImmunizations().forEach(i -> existingImmunizations.add(i, i.getEffectiveTimeStart(), i.getProductCode()));
		patient.getPatientMedications().forEach(i -> existingMedications.add(i, i.getEffectiveTimeStart(), i.getProductCode()));
		patient.getPatientProblems().forEach(i -> existingProblems.add(i, i.getEffectiveTimeStart(), i.getCode()));
		patient.getPatientProcedures().forEach(i -> existingProcedures.add(i, i.getEffectiveTimeStart(), i.getCode()));
		patient.getPatientVitalSigns().forEach(i -> existingVitalSigns.add(i, i.getEffectiveTimeStart(), i.getCode()));
		patient.getPatientResults().forEach(result -> existingResults.add(result, result.getEffectiveTimeStart(), result.getCode()));
	}
	
	public ExistingItemMap<PatientImmunization> getExistingImmunizations() {
		return existingImmunizations;
	}


	public ExistingItemMap<PatientMedication> getExistingMedications() {
		return existingMedications;
	}


	public ExistingItemMap<PatientProblem> getExistingProblems() {
		return existingProblems;
	}


	public ExistingItemMap<PatientProcedure> getExistingProcedures() {
		return existingProcedures;
	}


	public ExistingItemMap<PatientResult> getExistingResults() {
		return existingResults;
	}


	public ExistingItemMap<PatientVitalSign> getExistingVitalSigns() {
		return existingVitalSigns;
	}


	/**
	 * Use an inner data structure with generics which would allow the same double map to be used for any item 
	 * 
	 * @author drosenbaum
	 *
	 * @param <E>
	 */
	public class ExistingItemMap<E> {
		
		private Map<String, ListMultimap<Integer, E>> itemMap = new HashMap<>();
		
		public List<E> getExisting(Date effectiveTimeStart, Code code) {
			ListMultimap<Integer, E> multiMap = itemMap.get(getEffectiveStartKey(effectiveTimeStart));
			return multiMap != null ? multiMap.get(getCodeKey(code)) : null;
		}
	
		/**
		 * this method returns all records for a given date, regardless of code
		 */
		public List<E> getExistingForEffectiveTimeStart(Date effectiveTimeStart) {
			ListMultimap<Integer, E> multiMap = itemMap.get(getEffectiveStartKey(effectiveTimeStart));
			if (multiMap != null) {
				return new ArrayList<>(multiMap.values());
			}
			return null;
		}
		
		public void add(E item, Date effectiveTimeStart, Code code) {
			String effectiveStartKey = getEffectiveStartKey(effectiveTimeStart);
			ListMultimap<Integer, E> multiMap = itemMap.get(effectiveStartKey);
			if (multiMap == null) {
				multiMap = ArrayListMultimap.create();
				itemMap.put(effectiveStartKey, multiMap);
			}
			multiMap.put(getCodeKey(code), item);
		}
		
		private String getEffectiveStartKey(Date effectiveStartTime) {
			return (effectiveStartTime != null) ? tsCompareFormat.format(effectiveStartTime) : null;
		}
		
		private Integer getCodeKey(Code code) {
			return (code != null) ? code.getId() : null;
		}
	
	}
}
