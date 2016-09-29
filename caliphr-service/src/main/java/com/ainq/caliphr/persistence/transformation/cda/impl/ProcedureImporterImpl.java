package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.hl7.v3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.transformation.cda.ProcedureImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class ProcedureImporterImpl extends SectionImporter implements ProcedureImporter {
	
	// TEMPORARY KLUDGE: it turns out that translation codes are important for identification of connected records.  As we are not currently storing
	// translation codes in the database, they will temporarily be kept in a ThreadLocal map when a file is running
	private static final ThreadLocal<Map<Long, Set<Code>>> translationMap = new ThreadLocal<Map<Long, Set<Code>>>() {
        @Override protected Map<Long, Set<Code>> initialValue() {
            return new HashMap<>();
        }
	};
	
	@Autowired EntityManager entityManager;
	
	public void resetThreadLocal() {
		translationMap.get().clear();
	}

    @Override
    public void loadPatientProcedureEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientProcedure> existingProcedures) {
        PatientProcedure patientProcedure = new PatientProcedure();

        // Base import
        CodeDetails codeDetails = null;
        EffectiveTime effectiveTime = null;
        StatusCodeDetails statusCodeDetails = null;

        POCDMT000040Procedure proc = entry.getProcedure();
        POCDMT000040Act act = entry.getAct();
        POCDMT000040Observation observation = entry.getObservation();
        if (proc != null) {
            codeDetails = extractCodeDetails(proc.getCode());
            effectiveTime = extractEffectiveDate(proc.getEffectiveTime(), proc.getAuthor());
            statusCodeDetails = extractStatus(proc.getStatusCode());
            
            // External Id
            patientProcedure.setExternalId(extractExternalId(proc.getId()));

            List<POCDMT000040EntryRelationship> entryRelationship = proc.getEntryRelationship();
			if (proc.isNegationInd() != null && proc.isNegationInd() == true) {
                patientProcedure.setNegationDetail(extractNegation(entryRelationship));
            }

            // REASON
            CodeDetails reasonCodeDetails = getReasonCodeDetails(entryRelationship);
            if (reasonCodeDetails != null) {
                patientProcedure.setReasonCode(reasonCodeDetails.getCode());
                patientProcedure.setReasonCodeDescription(reasonCodeDetails.getCodeDescription());
            }
            
            // Result
            if (entryRelationship != null) {
            	entryRelationship.stream()
            		.filter(entryRel -> XActRelationshipEntryRelationship.REFR.equals(entryRel.getTypeCode()))
            		.forEach(entryRel -> {
            			
            		POCDMT000040Observation observRel = entryRel.getObservation();
            		if (observRel != null && observRel.getValue() != null) {
            			observRel.getValue().stream().filter(value -> value instanceof CD).forEach(value -> {
							CodeDetails resultValueCodeDetails = extractCodeDetails((CD)value);
							patientProcedure.setResultValueCode(resultValueCodeDetails.getCode());
			                patientProcedure.setResultValueCodeDescription(resultValueCodeDetails.getCodeDescription());
            			});
            		}
            	});
            }
        } else if (act != null) {
            codeDetails = extractCodeDetails(act.getCode());
            effectiveTime = extractEffectiveDate(act.getEffectiveTime(), act.getAuthor());
            statusCodeDetails = extractStatus(act.getStatusCode());
            
            // External Id
            patientProcedure.setExternalId(extractExternalId(act.getId()));

            if (act.isNegationInd() != null && act.isNegationInd() == true) {
                patientProcedure.setNegationDetail(extractNegation(act.getEntryRelationship()));
            }

            // REASON
            CodeDetails reasonCodeDetails = getReasonCodeDetails(act.getEntryRelationship());
            if (reasonCodeDetails != null) {
                patientProcedure.setReasonCode(reasonCodeDetails.getCode());
                patientProcedure.setReasonCodeDescription(reasonCodeDetails.getCodeDescription());
            }
        } else if (observation != null) {
            codeDetails = extractCodeDetails(observation.getCode());
            effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
            statusCodeDetails = extractStatus(observation.getStatusCode());
            
            // External Id
            patientProcedure.setExternalId(extractExternalId(observation.getId()));

            if (observation.getValue() != null) {
                for (ANY observationValue : observation.getValue()) {
                    if (observationValue instanceof PQ) {
                        PQ pq = (PQ) observationValue;
                        if (pq.getValue() != null) {
                            patientProcedure.setValueString(pq.getValue());
                        }
                        if (pq.getUnit() != null) {
                         	patientProcedure.setValueUnit(pq.getUnit());
                        }
                    } else if (observationValue instanceof CD) {
                        CD observationCd = (CD) observationValue;
                        CodeDetails valueCodeDetails = extractCodeDetails(observationCd);
                        patientProcedure.setValueCode(valueCodeDetails.getCode());
                        patientProcedure.setValueCodeDescription(valueCodeDetails.getCodeDescription());
                    } else if (observationValue instanceof ST) {
                        ST st = (ST) observationValue;
                        
                        // ST uses mixed content in the XSD, i.e. mixed="true".  Jaxb does not handle such elements adequately by default,
                    	// and requires a custom binding setting of generateMixedExtensions="true" (specified in jaxb-bindings.xjb)
                    	// For more details, see http://stackoverflow.com/questions/4049067/jaxb-xjc-compiler-disregarding-mixed-true-on-xml-schema-documents
                    	if (st.getContent() != null) {
                    		StringBuilder stVal = new StringBuilder();
                    		st.getContent().stream().filter(content -> content instanceof String).forEach(stVal::append);
                    		patientProcedure.setValueString(stVal.toString());
                    	}
                    }
                }
            }
        } else {
            return;
        }

        // Extract Effective Date
        patientProcedure.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
        patientProcedure.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

        // Status Code
        patientProcedure.setStatusCode(statusCodeDetails.getStatusCode());

        // Status Code Name
        patientProcedure.setStatusCodeName(statusCodeDetails.getStatusCodeName());

        // Code
        patientProcedure.setCode(codeDetails.getCode());

        // Code Description
        patientProcedure.setCodeDescription(codeDetails.getCodeDescription());
        patientProcedure.setTemplate(templateRoot);

        // Connected Record ID
        // see description of temporary kludge at top of this file
        boolean foundConnectedRecord = false;
        List<PatientProcedure> existingProcedureList = existingProcedures.getExistingForEffectiveTimeStart(patientProcedure.getEffectiveTimeStart());
        if (existingProcedureList != null) {
            List<PatientProcedure> prevProcs = new ArrayList<>(existingProcedureList);
            prevProcs.sort((p1, p2) -> {
                Long id1 = p1 != null ? p1.getId() : null;
                Long id2 = p2 != null ? p2.getId() : null;
                return ObjectUtils.compare(id1, id2);
            });
            for (PatientProcedure prevProc : prevProcs) {
            	Set<Code> translations = translationMap.get().get(prevProc.getId());
				if (CdaUtility.isSameTime(prevProc.getEffectiveTimeStart(), patientProcedure.getEffectiveTimeStart())
                		&& CdaUtility.isSameTime(prevProc.getEffectiveTimeEnd(), patientProcedure.getEffectiveTimeEnd())
                		&& (CdaUtility.isSameCode(prevProc.getCode(), patientProcedure.getCode(), prevProc.getCodeDescription(), patientProcedure.getCodeDescription())
                			|| (codeDetails != null && codeDetails.getTranslationCodes().contains(prevProc.getCode()))
                			|| (translations != null && patientProcedure.getCode() != null && translations.contains(patientProcedure.getCode()))
                		)
                	) {
            		
            		// if the resolved record itself has another record connected to it, propagate it up so all records end up connected to just one
            		patientProcedure.setConnectedRecord(prevProc.getConnectedRecord() == null ? prevProc : prevProc.getConnectedRecord());
                    foundConnectedRecord = true;
                    break;
                }
            }
        }
        if (!foundConnectedRecord) {
        	patientProcedure.setConnectedRecord(null);
    	}

        /*
            Code Mappings for Practice
         */
        this.practiceCodeMapper.mapPatientProcedure(codeMappings, patientProcedure);

        // Find the matching patient record from the database and update
        existingProcedureList = existingProcedures.getExisting(patientProcedure.getEffectiveTimeStart(), patientProcedure.getCode());
        if (existingProcedureList != null) {
	        for (PatientProcedure record : existingProcedureList) {
	            if (CdaUtility.isSameCode(
		            		patientProcedure.getCode(), record.getCode(), 
		            		patientProcedure.getCodeDescription(), record.getCodeDescription()
		        		)
	                    && CdaUtility.isSameTemplate(patientProcedure.getTemplate(), record.getTemplate())
	                    && CdaUtility.isSameCode(
	                    		patientProcedure.getValueCode(), record.getValueCode(), 
	                    		patientProcedure.getValueCodeDescription(), record.getValueCodeDescription()
			        	)
	                    && CdaUtility.isSameCode(
	                    		patientProcedure.getResultValueCode(), record.getResultValueCode(), 
	                    		patientProcedure.getResultValueCodeDescription(), record.getResultValueCodeDescription()
			        	)
	                    && CdaUtility.isSameValue(patientProcedure.getValueString(), record.getValueString())
	                    && CdaUtility.isSameTime(patientProcedure.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
	            	
	                updateExistingProcedureRecord(record, patientProcedure);
	                
	                // see description of temporary kludge at top of this file
	                if (!translationMap.get().containsKey(record.getId())) {
	                	translationMap.get().put(record.getId(), codeDetails.getTranslationCodes());
	                } else {
	                	translationMap.get().get(record.getId()).addAll(codeDetails.getTranslationCodes());
	                }
	                
	                return;
	            }
	        }
        }

        patientProcedure.setDateCreated(new Date());
        patientProcedure.setDateUpdated(new Date());
        patientProcedure.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientProcedure.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().addPatientProcedure(patientProcedure);
        existingProcedures.add(patientProcedure, patientProcedure.getEffectiveTimeStart(), patientProcedure.getCode());
        
        // see description of temporary kludge at top of this file
        if (codeDetails.getTranslationCodes().size() > 0) {
        	entityManager.flush();
        	translationMap.get().put(patientProcedure.getId(), codeDetails.getTranslationCodes());
        }
    }

    private void updateExistingProcedureRecord(PatientProcedure record, PatientProcedure patientProcedure) {
        record.setNegationDetail(updateNegationDetailRecord(record.getNegationDetail(), patientProcedure.getNegationDetail()));
        record.setExternalId(patientProcedure.getExternalId());
        record.setReasonCode(patientProcedure.getReasonCode());
        record.setReasonCodeDescription(patientProcedure.getReasonCodeDescription());
        record.setResultValueCode(patientProcedure.getResultValueCode());
        record.setResultValueCodeDescription(patientProcedure.getResultValueCodeDescription());
        record.setEffectiveTimeStart(patientProcedure.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientProcedure.getEffectiveTimeEnd());
        record.setStatusCode(patientProcedure.getStatusCode());
        record.setStatusCodeName(patientProcedure.getStatusCodeName());
        record.setCode(patientProcedure.getCode());
        record.setCodeDescription(patientProcedure.getCodeDescription());
        record.setTemplate(patientProcedure.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }

}
