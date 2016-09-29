package com.ainq.caliphr.persistence.transformation.cda.impl;

import ch.qos.logback.classic.Logger;

import org.hl7.v3.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientFamilyHistory;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.FamilyHistoryImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class FamilyHistoryImporterImpl extends SectionImporter implements FamilyHistoryImporter {

    static Logger logger = (Logger) LoggerFactory.getLogger(FamilyHistoryImporterImpl.class);

    public void loadFamilyHistory(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientFamilyHistories() == null) {
            clinicalDocument.getPatient().setPatientFamilyHistories(new HashSet<>());
        }
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                loadFamilyHistoryEntry(entry, clinicalDocument, templateRoot);
            }
        }
    }

    @Override
    public void loadFamilyHistoryEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (entry.getOrganizer() != null) {
            POCDMT000040Organizer organizer = entry.getOrganizer();
            if (organizer.getComponent() != null) {
                for (POCDMT000040Component4 component : organizer.getComponent()) {
                    POCDMT000040Observation observation = component.getObservation();
                    PatientFamilyHistory patientFamilyHistory = new PatientFamilyHistory();
                    patientFamilyHistory.setPatient(clinicalDocument.getPatient());

                    // Base Importers
                    CodeDetails codeDetails = extractCodeDetails(observation.getCode());
                    EffectiveTime effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());

                    // External Id
                    patientFamilyHistory.setExternalId(extractExternalId(observation.getId()));
                    
                    // Code
                    patientFamilyHistory.setCode(codeDetails.getCode());

                    // Code Description
                    patientFamilyHistory.setCodeDescription(codeDetails.getCodeDescription());

                    // Effective Time
                    patientFamilyHistory.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
                    patientFamilyHistory.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

                    if (observation.getEntryRelationship() != null) {
                        for (POCDMT000040EntryRelationship entryRelationship : observation.getEntryRelationship()) {
                            if (entryRelationship.getObservation() != null) {
                                POCDMT000040Observation entryObservation = entryRelationship.getObservation();
                                if (entryObservation.getValue() != null) {
                                    for (ANY observationValue : entryObservation.getValue()) {
                                        if (observationValue instanceof PQ) {
                                            // Age at onset
                                            PQ pq = (PQ) observationValue;
                                            if (pq.getValue() != null) {
                                                try {
                                                    patientFamilyHistory.setAgeAtOnset(Integer.parseInt(pq.getValue()));
                                                } catch (Exception ex) {
                                                    logger.error("Error parsing family history age at onset -> ", ex);
                                                }
                                            }
                                        } else if (observationValue instanceof CD) {
                                            // Diagnosis Code
                                            CD cd = (CD) observationValue;
                                            CodeDetails diagnosisCodeDetails = extractCodeDetails(cd);
                                            if (diagnosisCodeDetails != null) {
                                                patientFamilyHistory.setDiagnosisCode(diagnosisCodeDetails.getCode());
                                                patientFamilyHistory.setDiagnosisCodeDescription(diagnosisCodeDetails.getCodeDescription());
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                    patientFamilyHistory.setTemplate(templateRoot);
                    patientFamilyHistory.setDateCreated(new Date());
                    patientFamilyHistory.setDateUpdated(new Date());
                    patientFamilyHistory.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                    patientFamilyHistory.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                    clinicalDocument.getPatient().getPatientFamilyHistories().add(patientFamilyHistory);
                }
            }
        }
    }
}
