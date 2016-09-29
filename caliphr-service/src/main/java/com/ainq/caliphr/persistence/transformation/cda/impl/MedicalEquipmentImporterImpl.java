package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Participant2;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040Supply;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientMedicalEquipment;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.MedicalEquipmentImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class MedicalEquipmentImporterImpl extends SectionImporter implements MedicalEquipmentImporter {

    public void loadMedicalEquipment(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {

        if (clinicalDocument.getPatient().getPatientMedicalEquipments() == null) {
            clinicalDocument.getPatient().setPatientMedicalEquipments(new HashSet<>());
        }
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                loadMedicalEquipmentEntry(entry, clinicalDocument, templateRoot);
            }
        }
    }

    @Override
    public void loadMedicalEquipmentEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (entry.getSupply() != null) {
            PatientMedicalEquipment patientMedicalEquipment = new PatientMedicalEquipment();
            patientMedicalEquipment.setPatient(clinicalDocument.getPatient());
            POCDMT000040Supply supply = entry.getSupply();
            EffectiveTime effectiveTime = extractEffectiveDate(supply.getEffectiveTime(), supply.getAuthor());
            StatusCodeDetails statusCodeDetails = extractStatus(supply.getStatusCode());

            // External Id
            patientMedicalEquipment.setExternalId(extractExternalId(supply.getId()));
            
            // Status Code
            patientMedicalEquipment.setStatusCode(statusCodeDetails.getStatusCode());

            // Status Code Name
            patientMedicalEquipment.setStatusCodeName(statusCodeDetails.getStatusCodeName());

            if (supply.getParticipant() != null) {
                for (POCDMT000040Participant2 participant : supply.getParticipant()) {
                    if (participant != null
                            && participant.getParticipantRole() != null
                            && participant.getParticipantRole().getPlayingEntity() != null
                            && participant.getParticipantRole().getPlayingDevice().getCode() != null) {
                        CodeDetails codeDetails = extractCodeDetails(participant.getParticipantRole().getPlayingDevice().getCode());
                        if (codeDetails != null) {

                            // Code
                            patientMedicalEquipment.setCode(codeDetails.getCode());

                            // Code Description
                            patientMedicalEquipment.setCodeDescription(codeDetails.getCodeDescription());

                        }
                    }
                }
            }

            // Effective Date
            patientMedicalEquipment.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
            patientMedicalEquipment.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            patientMedicalEquipment.setTemplate(templateRoot);

            // Find the matching patient record from the database and update
            for (PatientMedicalEquipment record : clinicalDocument.getPatient().getPatientMedicalEquipments()) {
                if (CdaUtility.isSameCode(
	                		patientMedicalEquipment.getCode(), record.getCode(), 
	                		patientMedicalEquipment.getCodeDescription(), record.getCodeDescription()
		        		)
                        && CdaUtility.isSameTemplate(patientMedicalEquipment.getTemplate(), record.getTemplate())
                        && CdaUtility.isSameTime(patientMedicalEquipment.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                    updateExistingMedicalEquipmentRecord(record, patientMedicalEquipment);
                    return;
                }
            }

            patientMedicalEquipment.setDateCreated(new Date());
            patientMedicalEquipment.setDateUpdated(new Date());
            patientMedicalEquipment.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientMedicalEquipment.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

            clinicalDocument.getPatient().getPatientMedicalEquipments().add(patientMedicalEquipment);
        }
    }

    private void updateExistingMedicalEquipmentRecord(PatientMedicalEquipment record, PatientMedicalEquipment patientMedicalEquipment) {
        record.setTemplate(patientMedicalEquipment.getTemplate());
        record.setExternalId(patientMedicalEquipment.getExternalId());
        record.setEffectiveTimeStart(patientMedicalEquipment.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientMedicalEquipment.getEffectiveTimeEnd());
        record.setCode(patientMedicalEquipment.getCode());
        record.setCodeDescription(patientMedicalEquipment.getCodeDescription());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
