package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.reference.TemplateRootDao;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.PersonAddress;
import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.transformation.cda.ClinicalDocumentType;
import com.ainq.caliphr.persistence.transformation.cda.EncounterImporter;
import com.ainq.caliphr.persistence.transformation.cda.ProviderImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.cda.TemplateIdRoot;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import ch.qos.logback.classic.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/17/2015.
 */
@Component
public class EncounterImporterImpl extends SectionImporter implements EncounterImporter {
	
    // Class Data
    static Logger logger = (Logger) LoggerFactory.getLogger(EncounterImporterImpl.class);

    @Autowired
    private ProviderImporter providerImporter;

    @Autowired
    private TemplateRootDao templateRootDao;

    @Override
    public void loadPatientEncounterEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, PracticeGroup practiceGroup) {
        if (entry.getEncounter() != null) {
            PatientEncounter patientEncounter = new PatientEncounter();
            patientEncounter.setPatient(clinicalDocument.getPatient());
            POCDMT000040Encounter encounter = entry.getEncounter();
            CodeDetails codeDetails = extractCodeDetails(encounter.getCode());
            StatusCodeDetails statusCodeDetails = extractStatus(encounter.getStatusCode());
            EffectiveTime effectiveTime = extractEffectiveDate(encounter.getEffectiveTime(), encounter.getAuthor());
            
            // External Id
            patientEncounter.setExternalId(extractExternalId(encounter.getId()));

            // Code
            patientEncounter.setCode(codeDetails.getCode());

            // Code Description
            patientEncounter.setCodeDescription(codeDetails.getCodeDescription());

            // Effective Time
           	patientEncounter.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
           	patientEncounter.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            // Status Code
            patientEncounter.setStatusCode(statusCodeDetails.getStatusCode());

            // Performer/Location
            if (encounter.getPerformer() != null) {
                encounter.getPerformer().stream().filter(performer -> performer.getAssignedEntity() != null).forEach(performer -> {
                    POCDMT000040AssignedEntity assignedEntity = performer.getAssignedEntity();
                    if (assignedEntity.getAddr() != null) {
                        PersonAddress personAddress = new PersonAddress();
                        for (AD addr : assignedEntity.getAddr()) {
                            if (addr.getContent() != null) {
                                personAddress = extractPersonAddress(addr.getContent());
                            }
                            if (personAddress != null) {
                                StringBuilder stringBuilder = new StringBuilder();
                                if (personAddress.getAddress() != null) {
                                    stringBuilder.append(personAddress.getAddress() + " ");
                                }
                                if (personAddress.getCity() != null) {
                                    stringBuilder.append(personAddress.getCity() + " ");
                                }
                                if (personAddress.getState() != null) {
                                    stringBuilder.append(personAddress.getState() + " ");
                                }
                                if (personAddress.getZipCode() != null) {
                                    stringBuilder.append(personAddress.getZipCode() + " ");
                                }
                                patientEncounter.setEncounterLocation(stringBuilder.toString().trim());
                            }
                        }
                    }

                    if (assignedEntity.getAssignedPerson() != null) {
                        POCDMT000040Person assignedPerson = assignedEntity.getAssignedPerson();
                        if (assignedPerson.getName() != null) {
                            PersonName personName = new PersonName();
                            for (PN pn : assignedPerson.getName()) {
                                if (pn.getContent() != null) {
                                    personName = extractPersonName(pn.getContent());
                                }
                                if (personName != null) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    if (personName.getFirstName() != null && (!personName.getFirstName().isEmpty())) {
                                        stringBuilder.append(personName.getFirstName() + " ");
                                    }
                                    if (personName.getLastName() != null && (!personName.getLastName().isEmpty())) {
                                        stringBuilder.append(personName.getLastName() + " ");
                                    }
                                
                                    //  Cases where the encounter name can be null
                                    if (stringBuilder.toString().trim().length() == 0 && personName.getFullName() != null) {
                                        patientEncounter.setPerformer(personName.getFullName().trim());
                                    } else {
                                        patientEncounter.setPerformer(stringBuilder.toString().trim());
                                    }
                                }
                            }
                        }
                    }
                });

                if (encounter.getPerformer().size() > 1) {
                    throw new IllegalStateException("Encounter found with multiple performers!");
                } else if (encounter.getPerformer().size() == 1) {
                    patientEncounter.setProvider(createProviderEncounter(encounter.getPerformer().get(0), practiceGroup));
                }
            }

            patientEncounter.setTemplate(templateRoot);
 
            //Check for diagnosis values
            ArrayList<PatientEncounterDiagnosis> encounterDiagnoses = new ArrayList<PatientEncounterDiagnosis>();
            if (patientEncounter.getPatientEncounterDiagnoses() == null) {
                patientEncounter.setPatientEncounterDiagnoses(new HashSet<>());
            }
            if (encounter.getEntryRelationship() != null && encounter.getEntryRelationship().size() > 0) {
                for (POCDMT000040EntryRelationship entryRelationship : encounter.getEntryRelationship()) {
                	if (entryRelationship.getAct() != null) {
	                    POCDMT000040Act act = entryRelationship.getAct();
	                    if (act.getTemplateId() != null) {
	                    	if ((act.getTemplateId() != null) && (act.getEntryRelationship() != null)) {
	                    		for (II templateId : act.getTemplateId()) {
	                    			if (templateId.getRoot() != null && templateId.getRoot().equals(TemplateIdRoot.ENCOUNTER__ENCOUNTER_DIAGNOSIS.getRoot())) {
	                    				for (POCDMT000040EntryRelationship actEntryRelationship : act.getEntryRelationship()) {
	    	                        		encounterDiagnoses.add(extractPatientEncounterDiagnosis(patientEncounter, actEntryRelationship.getObservation()));
	    	                            }
	                    			}
	                    		}
	                    	}
	                    }
                	}
                }
            }
            
            for (PatientEncounter record : clinicalDocument.getPatient().getPatientEncounters()) {
                if (CdaUtility.isSameCode(
	                		patientEncounter.getCode(), record.getCode(), 
	                		patientEncounter.getCodeDescription(), record.getCodeDescription()
		        		)
                        && CdaUtility.isSameTemplate(patientEncounter.getTemplate(), record.getTemplate())
                        && CdaUtility.isSameTime(patientEncounter.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                	
                	if (clinicalDocument.getType().getHl7Oid().equals(ClinicalDocumentType.C_CDA.getRoot())
                    		&& !CdaUtility.isSameValue(patientEncounter.getProvider(), record.getProvider())) {
                		continue;
                	}
                	
                	updateExistingEncounterRecord(record, patientEncounter);
                	
                	if (encounterDiagnoses != null) {
                		encounterDiagnoses.forEach((encounterDiagnosis) -> {
                			updateExistingEncounterDiagnosisRecord(record, encounterDiagnosis);
                		});
                	}
                	
                    return;
                }
            } 
            
            if (encounterDiagnoses != null) {
            	encounterDiagnoses.forEach((encounterDiagnosis) -> {
            		//patientEncounter.addPatientEncounterDiagnosis(encounterDiagnosis);
            		updateExistingEncounterDiagnosisRecord(patientEncounter, encounterDiagnosis);
            	});
            }
            
            patientEncounter.setDateCreated(new Date());
            patientEncounter.setDateUpdated(new Date());
            patientEncounter.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientEncounter.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            
        	clinicalDocument.getPatient().getPatientEncounters().add(patientEncounter);
            
        }
    }

    private PatientEncounterDiagnosis extractPatientEncounterDiagnosis(PatientEncounter patientEncounter, POCDMT000040Observation actEntryRelationshipObservation) {

    	// Extracting Encounter diagnosis values
    	
    	//Set Values
    	PatientEncounterDiagnosis patientEncounterDiagnosis = new PatientEncounterDiagnosis();

        // Encounter ID
    	patientEncounterDiagnosis.setEncounter(patientEncounter);
    	
    	//Code details
    	if (actEntryRelationshipObservation.getValue().size() > 1) {
    		logger.info("more than one values found in diagnosis");
    	}
    	if ((actEntryRelationshipObservation.getValue() == null) || (actEntryRelationshipObservation.getValue().size() < 1)) {

        	//Ignore diagnosis if no code description exists
    		return null;
    	}
    	for (ANY diagnosisValue : actEntryRelationshipObservation.getValue()) {
    		CD diagnosisProblemCode = (CD) diagnosisValue;
    		
        	CodeDetails codeDetails = extractCodeDetails(diagnosisProblemCode);
        	
        	//Ignore diagnosis if no code description exists
        	if ((codeDetails.getCodeDescription() == null) && (codeDetails.getCode() == null)) {
        		return null;
        	}
        	
        	// Problem code details
    	    patientEncounterDiagnosis.setProblemCode(codeDetails.getCode());
    	    
    	    //Problem Code description
    	    patientEncounterDiagnosis.setProblemCodeDescription(codeDetails.getCodeDescription());
    	    
    	    break;
    	}

    	//StatusCode
    	StatusCodeDetails statusCodeDetails = extractStatus(actEntryRelationshipObservation.getStatusCode());
    	patientEncounterDiagnosis.setStatusCode(statusCodeDetails.getStatusCode());
    	patientEncounterDiagnosis.setStatusCodeName(statusCodeDetails.getStatusCodeName());
    	
    	//Template ID
        TemplateRoot templateRoot = templateRootDao.findOrCreateTemplate(TemplateIdRoot.ENCOUNTER__ENCOUNTER_DIAGNOSIS.getRoot());
        patientEncounterDiagnosis.setTemplate(templateRoot);
    	
    	//External ID
    	String externalId = extractExternalId(actEntryRelationshipObservation.getId());
    	patientEncounterDiagnosis.setExternalId(externalId);
    	
    	//Effective dates
        EffectiveTime effectiveTime = extractEffectiveDate(actEntryRelationshipObservation.getEffectiveTime(), actEntryRelationshipObservation.getAuthor());
        patientEncounterDiagnosis.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
        patientEncounterDiagnosis.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

        //Activity dates
        patientEncounterDiagnosis.setDateCreated(new Date());
        patientEncounterDiagnosis.setDateUpdated(new Date());
        patientEncounterDiagnosis.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientEncounterDiagnosis.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
        
        return patientEncounterDiagnosis;
    }

    private void updateExistingEncounterRecord(PatientEncounter record, PatientEncounter patientEncounter) {
        record.setExternalId(patientEncounter.getExternalId());
        record.setEncounterLocation(patientEncounter.getEncounterLocation());
        record.setPerformer(patientEncounter.getPerformer());
        record.setProvider(patientEncounter.getProvider());
        record.setEffectiveTimeStart(patientEncounter.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientEncounter.getEffectiveTimeEnd());
        record.setCode(patientEncounter.getCode());
        record.setCodeDescription(patientEncounter.getCodeDescription());
        record.setTemplate(patientEncounter.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }

    private void updateExistingEncounterDiagnosisRecord(PatientEncounter record, PatientEncounterDiagnosis patientEncounterDiagnosis) {
    	
    	if (patientEncounterDiagnosis != null) {
        	//Update EncounterDiagnosis
        	for (PatientEncounterDiagnosis encounterDiagnosisRecord : record.getPatientEncounterDiagnoses()){
    			if ((CdaUtility.isSameCode(patientEncounterDiagnosis.getProblemCode(), encounterDiagnosisRecord.getProblemCode(), 
    					patientEncounterDiagnosis.getProblemCodeDescription(), encounterDiagnosisRecord.getProblemCodeDescription()))
    				&& CdaUtility.isSameTemplate(patientEncounterDiagnosis.getTemplate(), encounterDiagnosisRecord.getTemplate())
                    && CdaUtility.isSameTime(patientEncounterDiagnosis.getEffectiveTimeStart(), encounterDiagnosisRecord.getEffectiveTimeStart())) {

    				encounterDiagnosisRecord.setEncounter(record);
    				encounterDiagnosisRecord.setExternalId(patientEncounterDiagnosis.getExternalId());
    				encounterDiagnosisRecord.setProblemCode(patientEncounterDiagnosis.getProblemCode());
    				encounterDiagnosisRecord.setProblemCodeDescription(patientEncounterDiagnosis.getProblemCodeDescription());
    				encounterDiagnosisRecord.setStatusCode(patientEncounterDiagnosis.getStatusCode());
    				encounterDiagnosisRecord.setStatusCodeName(patientEncounterDiagnosis.getStatusCodeName());
    				encounterDiagnosisRecord.setTemplate(patientEncounterDiagnosis.getTemplate());
    				encounterDiagnosisRecord.setEffectiveTimeStart(patientEncounterDiagnosis.getEffectiveTimeStart());
    				encounterDiagnosisRecord.setEffectiveTimeEnd(patientEncounterDiagnosis.getEffectiveTimeEnd());
    				encounterDiagnosisRecord.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
    				encounterDiagnosisRecord.setDateDisabled(null);
    				// setDateUpdated is set automatically in the @PreUpdate method of the entity
    				
    				return;
    			}
    		}
        	
        	record.addPatientEncounterDiagnosis(patientEncounterDiagnosis);
    	}
    }

    private Provider createProviderEncounter(POCDMT000040Performer2 performer, PracticeGroup practiceGroup) {
        Provider provider = this.providerImporter.loadProviderInfo(performer, practiceGroup);
        return provider;
    }

}
