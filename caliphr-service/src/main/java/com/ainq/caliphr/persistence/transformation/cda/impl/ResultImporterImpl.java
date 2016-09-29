package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.hl7.v3.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.transformation.cda.ResultImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import ch.qos.logback.classic.Logger;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class ResultImporterImpl extends SectionImporter implements ResultImporter {
	
	static Logger logger = (Logger) LoggerFactory.getLogger(ResultImporterImpl.class);
	
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
    public void loadPatientResultEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientResult> existingResultMap) {
        POCDMT000040Organizer organizer = entry.getOrganizer();
        if (organizer != null && organizer.getComponent() != null) {
            organizer.getComponent().stream().filter(component -> component.getObservation() != null).forEach(component -> {
                POCDMT000040Observation observation = component.getObservation();
                loadPatientObservation(observation, clinicalDocument, templateRoot, codeMappings, existingResultMap);
            });
        } else if (entry.getObservation() != null) {
            loadPatientObservation(entry.getObservation(), clinicalDocument, templateRoot, codeMappings, existingResultMap);
        }
    }

    private void loadPatientObservation(POCDMT000040Observation observation, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientResult> existingResultMap) {
    	PatientResult patientResult = new PatientResult();
    	
    	// Base Importers
        CodeDetails codeDetails = extractCodeDetails(observation.getCode());
        EffectiveTime effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
        StatusCodeDetails statusCodeDetails = extractStatus(observation.getStatusCode());
        
        // External Id
        patientResult.setExternalId(extractExternalId(observation.getId()));

        // Status Code
        patientResult.setStatusCode(statusCodeDetails.getStatusCode());

        // Status Code Name
        patientResult.setStatusCodeName(statusCodeDetails.getStatusCodeName());

        // Code
        patientResult.setCode(codeDetails.getCode());

        // Code Description
        patientResult.setCodeDescription(codeDetails.getCodeDescription());

        // Extract Effective Date
	    patientResult.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
	    patientResult.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

        // Result Value
        if (observation.getValue() != null) {

            // Save the value and unit if it exists
            observation.getValue().stream().filter(val -> val instanceof PQ).forEach(val -> {
                PQ pq = (PQ) val;
                if (pq.getValue() != null && pq.getUnit() != null) {
                    patientResult.setResultValue(pq.getValue());
                    patientResult.setResultValueUnit(pq.getUnit());
                }
            });
            
            observation.getValue().stream().filter(val -> val instanceof ST).forEach(val -> {
            	ST st = (ST) val;
            	
            	// ST uses mixed content in the XSD, i.e. mixed="true".  Jaxb does not handle such elements adequately by default,
            	// and requires a custom binding setting of generateMixedExtensions="true" (specified in jaxb-bindings.xjb)
            	// For more details, see http://stackoverflow.com/questions/4049067/jaxb-xjc-compiler-disregarding-mixed-true-on-xml-schema-documents
            	if (st.getContent() != null) {
            		StringBuilder stVal = new StringBuilder();
            		st.getContent().stream().filter(content -> content instanceof String).forEach(stVal::append);
            		patientResult.setResultValue(stVal.toString());
            	}
            });

            // Save the code if it exists
            observation.getValue().stream().filter(val -> val instanceof CD).forEach(val -> {

                // Extract code details from object
                CD observationCd = (CD)val;
                CodeDetails valueCodeDetails = extractCodeDetails(observationCd);
                patientResult.setValueCode(valueCodeDetails.getCode());
                patientResult.setValueCodeDescription(valueCodeDetails.getCodeDescription());

            });
        }

        // Reference Range
        if (observation.getReferenceRange() != null) {
            for (POCDMT000040ReferenceRange referenceRange : observation.getReferenceRange()) {
                if (referenceRange.getObservationRange() != null && referenceRange.getObservationRange().getValue() != null) {
                    if (referenceRange.getObservationRange().getValue() instanceof IVLPQ) {
                        IVLPQ ivlpq = (IVLPQ) referenceRange.getObservationRange().getValue();
                        if (ivlpq.getRest() != null) {
                            for (JAXBElement<? extends PQ> rest : ivlpq.getRest()) {
                                if (rest.getName() != null && rest.getName().getLocalPart() != null
                                        && rest.getValue() instanceof IVXBPQ) {
                                    IVXBPQ restValue = (IVXBPQ) rest.getValue();
                                    if (restValue.getValue() != null && restValue.getUnit() != null) {
                                        if (rest.getName().getLocalPart().equalsIgnoreCase(TIMESTAMP_LOW)) {
                                            patientResult.setReferenceRangeLowValue(restValue.getValue());
                                            patientResult.setReferenceRangeLowValueUnit(restValue.getUnit());
                                        } else if (rest.getName().getLocalPart().equalsIgnoreCase(TIMESTAMP_HIGH)) {
                                            patientResult.setReferenceRangeHighValue(restValue.getValue());
                                            patientResult.setReferenceRangeHighValueUnit(restValue.getUnit());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        patientResult.setTemplate(templateRoot);

        /*
            Code Mappings for Practice
         */
        this.practiceCodeMapper.mapPatientResult(codeMappings, patientResult);

        // Find the matching patient record from the database and update
        List<PatientResult> existingResultList = existingResultMap.getExisting(patientResult.getEffectiveTimeStart(), patientResult.getCode());
        if (existingResultList != null) {
			for (PatientResult record : existingResultList) {
	            if (CdaUtility.isSameCode(
		            		patientResult.getCode(), record.getCode(), 
		            		patientResult.getCodeDescription(), record.getCodeDescription()
		        		)
		                && CdaUtility.isSameTemplate(patientResult.getTemplate(), record.getTemplate())
		                && CdaUtility.isSameCode(
	                		patientResult.getValueCode(), record.getValueCode(), 
	                		patientResult.getValueCodeDescription(), record.getValueCodeDescription()
		        		)
		                && CdaUtility.isSameValue(patientResult.getResultValue(), record.getResultValue())
		                && CdaUtility.isSameTime(patientResult.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
	            	
	                updateExistingResultRecord(record, patientResult);
	                
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
        
        // Connected Record ID
        // see description of temporary kludge at top of this file
        boolean foundConnectedRecord = false;
        existingResultList = existingResultMap.getExistingForEffectiveTimeStart(patientResult.getEffectiveTimeStart());
        if (existingResultList != null) {
        	List<PatientResult> prevResults = new ArrayList<>(existingResultList);
        	prevResults.sort((p1, p2) -> {
                Long id1 = p1 != null ? p1.getId() : null;
                Long id2 = p2 != null ? p2.getId() : null;
                return ObjectUtils.compare(id1, id2);
            });
        	for (PatientResult prevResult : prevResults) {
        		Set<Code> translations = translationMap.get().get(prevResult.getId());
        		if (CdaUtility.isSameTime(prevResult.getEffectiveTimeStart(), patientResult.getEffectiveTimeStart())
                		&& CdaUtility.isSameTime(prevResult.getEffectiveTimeEnd(), patientResult.getEffectiveTimeEnd())
                		&& (CdaUtility.isSameCode(prevResult.getCode(), patientResult.getCode(), prevResult.getCodeDescription(), patientResult.getCodeDescription())
                			|| (codeDetails != null && codeDetails.getTranslationCodes().contains(prevResult.getCode()))
                			|| (translations != null && patientResult.getCode() != null && translations.contains(patientResult.getCode()))
                		)
                	) {
            		
            		// if the resolved record itself has another record connected to it, propagate it up so all records end up connected to just one
        			patientResult.setConnectedRecord(prevResult.getConnectedRecord() == null ? prevResult : prevResult.getConnectedRecord());
                    foundConnectedRecord = true;
                    break;
                }
            }
        }
        if (!foundConnectedRecord) {
        	patientResult.setConnectedRecord(null);
    	}

        patientResult.setDateCreated(new Date());
        patientResult.setDateUpdated(new Date());
        patientResult.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientResult.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        
        clinicalDocument.getPatient().addPatientResult(patientResult);
        existingResultMap.add(patientResult, patientResult.getEffectiveTimeStart(), patientResult.getCode());
        
        
        // see description of temporary kludge at top of this file
        if (codeDetails.getTranslationCodes().size() > 0) {
        	entityManager.flush();
        	translationMap.get().put(patientResult.getId(), codeDetails.getTranslationCodes());
    	}
     	
    }

    private void updateExistingResultRecord(PatientResult record, PatientResult patientResult) {
        record.setExternalId(patientResult.getExternalId());
        record.setReferenceRangeLowValue(patientResult.getReferenceRangeLowValue());
        record.setReferenceRangeLowValueUnit(patientResult.getReferenceRangeLowValueUnit());
        record.setReferenceRangeHighValue(patientResult.getReferenceRangeHighValue());
        record.setReferenceRangeHighValueUnit(patientResult.getReferenceRangeHighValueUnit());
        record.setResultValue(patientResult.getResultValue());
        record.setResultValueUnit(patientResult.getResultValueUnit());
        record.setValueCode(patientResult.getValueCode());
        record.setValueCodeDescription(patientResult.getValueCodeDescription());
        record.setEffectiveTimeStart(patientResult.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientResult.getEffectiveTimeEnd());
        record.setStatusCode(patientResult.getStatusCode());
        record.setStatusCodeName(patientResult.getStatusCodeName());
        record.setCode(patientResult.getCode());
        record.setCodeDescription(patientResult.getCodeDescription());
        record.setTemplate(patientResult.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }

}
