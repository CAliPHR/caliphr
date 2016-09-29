package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.hl7.v3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientProblem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.ProblemImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.*;

import javax.persistence.EntityManager;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class ProblemImporterImpl extends SectionImporter implements ProblemImporter {
	
	
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
    public void loadPatientProblemEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientProblem> existingProblems) {
        PatientProblem patientProblem = new PatientProblem();
        POCDMT000040Act act = entry.getAct();
        POCDMT000040Observation observation = entry.getObservation();

        // Base import
        CodeDetails codeDetails = null;
        CodeDetails problemCodeDetails = null;
        EffectiveTime effectiveTime = null;
        StatusCodeDetails statusCodeDetails = null;
        if (act != null) {
            codeDetails = extractCodeDetails(act.getCode());
            effectiveTime = extractEffectiveDate(act.getEffectiveTime(), act.getAuthor());
            statusCodeDetails = extractStatus(act.getStatusCode());
            patientProblem.setExternalId(extractExternalId(act.getId()));
        } else if (observation != null) {
            codeDetails = extractCodeDetails(observation.getCode());
            effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
            statusCodeDetails = extractStatus(observation.getStatusCode());
            patientProblem.setExternalId(extractExternalId(observation.getId()));
        } else {
            return;
        }

        // Extract Effective Date
        patientProblem.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
        patientProblem.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

        // Status Code
        patientProblem.setStatusCode(statusCodeDetails.getStatusCode());

        // Status Code Name
        patientProblem.setStatusCodeName(statusCodeDetails.getStatusCodeName());

        // Code
        patientProblem.setCode(codeDetails.getCode());

        // Code Description
        patientProblem.setCodeDescription(codeDetails.getCodeDescription());

        // Problem Code
        if (observation != null && observation.getValue() != null)
        {
            for (ANY observationValue : observation.getValue()) {
                if (observationValue instanceof CD) {
                    CD cd = (CD) observationValue;
                    problemCodeDetails = extractCodeDetails(cd);
                    if (problemCodeDetails != null) {
                        patientProblem.setProblemCode(problemCodeDetails.getCode());
                        patientProblem.setProblemCodeDescription(problemCodeDetails.getCodeDescription());
                    }
                }
            }

            // Negation Detail
            if (observation.isNegationInd() != null && observation.isNegationInd() == true && observation.getEntryRelationship() != null) {
                patientProblem.setNegationDetail(extractNegation(observation.getEntryRelationship()));
            }
        }
        else
        {
            List<POCDMT000040EntryRelationship> entryRelationshipList = null;
            if (act != null && act.getEntryRelationship() != null) {
                entryRelationshipList = act.getEntryRelationship();
            } else if (observation != null && observation.getEntryRelationship() != null) {
                entryRelationshipList = observation.getEntryRelationship();
            }

            // Negation Detail
            if ((act != null && act.isNegationInd() != null && act.isNegationInd() == true) || (observation != null && observation.isNegationInd() != null && observation.isNegationInd() == true)) {
                patientProblem.setNegationDetail(extractNegation(entryRelationshipList));
            }

            if (entryRelationshipList != null) {
                for (POCDMT000040EntryRelationship entryRelationship : entryRelationshipList) {
                    if (entryRelationship.getObservation() != null) {
                        if (entryRelationship.getObservation().getValue() != null) {
                            for (ANY observationValue : entryRelationship.getObservation().getValue()) {
                                if (observationValue instanceof CD) {
                                    CD cd = (CD) observationValue;
                                    problemCodeDetails = extractCodeDetails(cd);
                                    if (problemCodeDetails != null) {
                                        patientProblem.setProblemCode(problemCodeDetails.getCode());
                                        patientProblem.setProblemCodeDescription(problemCodeDetails.getCodeDescription());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Ordinality
		if (observation != null && observation.getPriorityCode() != null)
        {
            CodeDetails priorityCodeDetails = extractCodeDetails(observation.getPriorityCode());
			if (priorityCodeDetails != null) {
				patientProblem.setOrdinalityCode(priorityCodeDetails.getCode());
                patientProblem.setOrdinalityCodeDescription(priorityCodeDetails.getCodeDescription());
			}
        }

        // laterality
        if (observation != null && observation.getTargetSiteCode() != null) {
            for (CD targetSiteCode : observation.getTargetSiteCode()) {
                CodeDetails lateralityCodeDetails = extractCodeDetails(targetSiteCode);
                if (lateralityCodeDetails != null) {
                    patientProblem.setLateralityCode(lateralityCodeDetails.getCode());
                    patientProblem.setLateralityCodeDescription(lateralityCodeDetails.getCodeDescription());
                }
                break;
            }
        }

        patientProblem.setTemplate(templateRoot);
        
        /*
            Code Mappings for Practice
         */
        this.practiceCodeMapper.mapPatientProblem(codeMappings, patientProblem);

        // Find the matching patient record from the database and update
        List<PatientProblem> existingProblemList = existingProblems.getExisting(patientProblem.getEffectiveTimeStart(), patientProblem.getCode());
        if (existingProblemList != null) {
	        for (PatientProblem record : existingProblemList) {
	            if (CdaUtility.isSameCode(
	            			patientProblem.getProblemCode(), record.getProblemCode(), 
	            			patientProblem.getProblemCodeDescription(), record.getProblemCodeDescription()
	            		)
	                    && CdaUtility.isSameTemplate(patientProblem.getTemplate(), record.getTemplate())
	                    && CdaUtility.isSameTime(patientProblem.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {

	                updateExistingProblemRecord(record, patientProblem);
	                
	                // see description of temporary kludge at top of this file
	                if (!translationMap.get().containsKey(record.getId())) {
	                	translationMap.get().put(record.getId(), problemCodeDetails.getTranslationCodes());
	                } else {
	                	translationMap.get().get(record.getId()).addAll(problemCodeDetails.getTranslationCodes());
	                }
	                
	                return;
	            }
	        }
        }

        // Connected record ID
        // see description of temporary kludge at top of this file
        boolean foundConnectedRecord = false;
        existingProblemList = existingProblems.getExistingForEffectiveTimeStart(patientProblem.getEffectiveTimeStart());
        if (existingProblemList != null) {
            List<PatientProblem> prevProblems = new ArrayList<>(existingProblemList);
            prevProblems.sort((p1, p2) -> {
                Long id1 = p1 != null ? p1.getId() : null;
                Long id2 = p2 != null ? p2.getId() : null;
                return ObjectUtils.compare(id1, id2);
            });
            for (PatientProblem prevProblem : prevProblems) {
            	Set<Code> translations = translationMap.get().get(prevProblem.getId());
            	if (CdaUtility.isSameTime(prevProblem.getEffectiveTimeStart(), patientProblem.getEffectiveTimeStart())
                		&& CdaUtility.isSameTime(prevProblem.getEffectiveTimeEnd(), patientProblem.getEffectiveTimeEnd())
                		&& (CdaUtility.isSameCode(prevProblem.getProblemCode(), patientProblem.getProblemCode(), prevProblem.getProblemCodeDescription(), patientProblem.getProblemCodeDescription())
                			|| (problemCodeDetails != null && problemCodeDetails.getTranslationCodes().contains(prevProblem.getProblemCode()))
                			|| (translations != null && patientProblem.getProblemCode() != null && translations.contains(patientProblem.getProblemCode()))
                		)
                	) {
            		
            		// if the resolved record itself has another record connected to it, propagate it up so all records end up connected to just one
                    patientProblem.setConnectedRecord(prevProblem.getConnectedRecord() == null ? prevProblem : prevProblem.getConnectedRecord());
                    foundConnectedRecord = true;
                    break;
                }
            }
        }
    	if (!foundConnectedRecord) {
            patientProblem.setConnectedRecord(null);
    	}

        patientProblem.setDateCreated(new Date());
        patientProblem.setDateUpdated(new Date());
        patientProblem.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientProblem.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        
		clinicalDocument.getPatient().addPatientProblem(patientProblem);
		existingProblems.add(patientProblem, patientProblem.getEffectiveTimeStart(), patientProblem.getCode());
		
		// see description of temporary kludge at top of this file
		if (problemCodeDetails.getTranslationCodes().size() > 0) {
			entityManager.flush();
			translationMap.get().put(patientProblem.getId(), problemCodeDetails.getTranslationCodes());
		}
    }
    
    private void updateExistingProblemRecord(PatientProblem record, PatientProblem patientProblem) {
        record.setNegationDetail(updateNegationDetailRecord(record.getNegationDetail(), patientProblem.getNegationDetail()));
        record.setExternalId(patientProblem.getExternalId());
        record.setProblemCode(patientProblem.getProblemCode());
        record.setProblemCodeDescription(patientProblem.getProblemCodeDescription());
        record.setOrdinalityCode(patientProblem.getOrdinalityCode());
        record.setOrdinalityCodeDescription(patientProblem.getOrdinalityCodeDescription());
        record.setEffectiveTimeStart(patientProblem.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientProblem.getEffectiveTimeEnd());
        record.setStatusCode(patientProblem.getStatusCode());
        record.setStatusCodeName(patientProblem.getStatusCodeName());
        record.setCode(patientProblem.getCode());
        record.setCodeDescription(patientProblem.getCodeDescription());
        record.setTemplate(patientProblem.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }

}
