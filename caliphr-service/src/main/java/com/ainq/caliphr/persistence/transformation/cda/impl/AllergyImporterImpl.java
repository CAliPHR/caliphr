package com.ainq.caliphr.persistence.transformation.cda.impl;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;
import org.hl7.v3.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientAllergy;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.AllergyImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.cda.TemplateIdRoot;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class AllergyImporterImpl extends SectionImporter implements AllergyImporter {

    // Constants
    static Logger logger = (Logger) LoggerFactory.getLogger(AllergyImporterImpl.class);

    public void loadPatientAllergies(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientAllergies() == null) {
            clinicalDocument.getPatient().setPatientAllergies(new HashSet<>());
        }
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                loadPatientAllergyEntry(entry, clinicalDocument, templateRoot);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
	@Override
    public void loadPatientAllergyEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (entry.getAct() != null) {
            PatientAllergy patientAllergy = new PatientAllergy();
            patientAllergy.setPatient(clinicalDocument.getPatient());
            POCDMT000040Act act = entry.getAct();

            // Base Importers
            CodeDetails codeDetails = extractCodeDetails(act.getCode());
            EffectiveTime effectiveTime = extractEffectiveDate(act.getEffectiveTime(), act.getAuthor());
            StatusCodeDetails statusCodeDetails = extractStatus(act.getStatusCode());
            
            // External Id
            patientAllergy.setExternalId(extractExternalId(act.getId()));

            // Status Code
            patientAllergy.setStatusCode(statusCodeDetails.getStatusCode());

            // Status Code Name
            patientAllergy.setStatusCodeName(statusCodeDetails.getStatusCodeName());

            // Code
            patientAllergy.setCode(codeDetails.getCode());

            // Code Description
            patientAllergy.setCodeDescription(codeDetails.getCodeDescription());

            // Extract Effective Date
            patientAllergy.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
            patientAllergy.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            if (act.getEntryRelationship() != null)
            {
                for (POCDMT000040EntryRelationship entryRelationship : act.getEntryRelationship())
                {
                    if (entryRelationship.getObservation() != null) {
                        POCDMT000040Observation observation = entryRelationship.getObservation();

                        // Substance
                        if (observation.getParticipant() != null) {
                            for (POCDMT000040Participant2 participant : observation.getParticipant()) {
                                if (participant.getParticipantRole() != null && participant.getParticipantRole().getPlayingEntity() != null
                                        && participant.getParticipantRole().getPlayingEntity().getCode() != null) {
                                    CodeDetails substanceCodeDetails = extractCodeDetails(participant.getParticipantRole().getPlayingEntity().getCode());
                                    if (substanceCodeDetails != null) {
                                        patientAllergy.setSubstanceCodeDescription(substanceCodeDetails.getCodeDescription());
                                    }
                                }
                            }
                        }

                        // Severity / Reaction
                        if (observation.getEntryRelationship() != null) {
                            for (POCDMT000040EntryRelationship observationEntryRelationship : observation.getEntryRelationship()) {
                                if (observationEntryRelationship.getObservation() != null) {
                                    if (observationEntryRelationship.getObservation() != null
                                            && observationEntryRelationship.getObservation().getTemplateId() != null
                                            && observationEntryRelationship.getObservation().getValue() != null)
                                    {
                                        CD cd = null;
                                        for (ANY any : observationEntryRelationship.getObservation().getValue()) {
                                            if (any instanceof CD) {
                                                cd = (CD) any;
                                            }
                                        }

                                        if (cd != null)
                                        {
                                            CodeDetails observationCodeDetails = extractCodeDetails(cd);
                                            if (observationCodeDetails != null) {
                                                for (II ii : observationEntryRelationship.getObservation().getTemplateId()) {
                                                    String root = StringUtils.EMPTY;
                                                    try {
                                                        root = ii.getRoot();
                                                        TemplateIdRoot foundTemplate = TemplateIdRoot.fromRoot(root.trim());
                                                        switch (foundTemplate) {
                                                            case ALLERGY__REACTION:
                                                                patientAllergy.setReactionCode(observationCodeDetails.getCode());
                                                                patientAllergy.setReactionCodeDescription(observationCodeDetails.getCodeDescription());
                                                                break;

                                                            case ALLERGY__SEVERITY:
                                                                patientAllergy.setSeverityCode(observationCodeDetails.getCode());
                                                                patientAllergy.setSeverityCodeDescription(observationCodeDetails.getCodeDescription());
                                                                break;
                                                        }
                                                    } catch (Exception ex) {
                                                        logger.error(String.format("Template ID %s not found", root), ex);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }
                }
            }

            patientAllergy.setTemplate(templateRoot);

            // Find the matching patient record from the database and update
            for (PatientAllergy record : clinicalDocument.getPatient().getPatientAllergies()) {
                if (CdaUtility.isSameCode(
	                		patientAllergy.getCode(), record.getCode(), 
	                		patientAllergy.getCodeDescription(), record.getCodeDescription()
		        		)
                        && CdaUtility.isSameTemplate(patientAllergy.getTemplate(), record.getTemplate())
                        && CdaUtility.isSameTime(patientAllergy.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                    updateExistingAllergyRecord(record, patientAllergy);
                    return;
                }
            }

            patientAllergy.setDateCreated(new Date());
            patientAllergy.setDateUpdated(new Date());
            patientAllergy.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientAllergy.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            
            clinicalDocument.getPatient().getPatientAllergies().add(patientAllergy);
        }
    }

    private void updateExistingAllergyRecord(PatientAllergy record, PatientAllergy patientAllergy) {
        record.setExternalId(patientAllergy.getExternalId());
        record.setSubstanceCodeId(patientAllergy.getSubstanceCodeId());
        record.setSubstanceCodeDescription(patientAllergy.getSubstanceCodeDescription());
        record.setReactionCode(patientAllergy.getReactionCode());
        record.setReactionCodeDescription(patientAllergy.getReactionCodeDescription());
        record.setSeverityCode(patientAllergy.getSeverityCode());
        record.setSeverityCodeDescription(patientAllergy.getSeverityCodeDescription());
        record.setEffectiveTimeStart(patientAllergy.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientAllergy.getEffectiveTimeEnd());
        record.setStatusCode(patientAllergy.getStatusCode());
        record.setStatusCodeName(patientAllergy.getStatusCodeName());
        record.setCode(patientAllergy.getCode());
        record.setCodeDescription(patientAllergy.getCodeDescription());
        record.setTemplate(patientAllergy.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
