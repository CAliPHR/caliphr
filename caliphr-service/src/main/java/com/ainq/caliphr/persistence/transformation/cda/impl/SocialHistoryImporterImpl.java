package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.ANY;
import org.hl7.v3.CD;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientSocialHistory;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.cda.SocialHistoryImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/17/2015.
 */
@Component
public class SocialHistoryImporterImpl extends SectionImporter implements SocialHistoryImporter {

    public void loadPatientSocialHistories(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientSocialHistories() == null) {
            clinicalDocument.getPatient().setPatientSocialHistories(new HashSet<>());
        }
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                loadPatientSocialHistoryEntry(entry, clinicalDocument, templateRoot);
            }
        }
    }

    @Override
    public void loadPatientSocialHistoryEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (entry.getObservation() != null) {
            PatientSocialHistory patientSocialHistory = new PatientSocialHistory();
            patientSocialHistory.setPatient(clinicalDocument.getPatient());
            POCDMT000040Observation observation = entry.getObservation();

            // Base import
            CodeDetails codeDetails = extractCodeDetails(observation.getCode());
            EffectiveTime effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
            StatusCodeDetails statusCodeDetails = extractStatus(observation.getStatusCode());
            
            // External Id
            patientSocialHistory.setExternalId(extractExternalId(observation.getId()));

            // Extract Effective Date
            patientSocialHistory.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
            patientSocialHistory.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            // Status Code
            patientSocialHistory.setStatusCode(statusCodeDetails.getStatusCode());

            // Status Code Name
            patientSocialHistory.setStatusCodeName(statusCodeDetails.getStatusCodeName());

            // Code
            patientSocialHistory.setCode(codeDetails.getCode());

            // Code Description
            patientSocialHistory.setCodeDescription(codeDetails.getCodeDescription());

            if (observation != null && observation.getValue() != null)
            {
                for (ANY observationValue : observation.getValue()) {
                    if (observationValue instanceof CD) {
                        CD cd = (CD) observationValue;
                        CodeDetails valueCodeDetails = extractCodeDetails(cd);
                        if (valueCodeDetails != null) {
                        	patientSocialHistory.setValueCode(valueCodeDetails.getCode());
                        	patientSocialHistory.setValueCodeDescription(valueCodeDetails.getCodeDescription());
                        }
                    }
                }
            }

            patientSocialHistory.setTemplate(templateRoot);

            // Find the matching patient record from the database and update
            for (PatientSocialHistory record : clinicalDocument.getPatient().getPatientSocialHistories()) {
                if (CdaUtility.isSameCode(
	                		patientSocialHistory.getCode(), record.getCode(), 
	                		patientSocialHistory.getCodeDescription(), record.getCodeDescription()
		        		)
                        && CdaUtility.isSameTemplate(patientSocialHistory.getTemplate(), record.getTemplate())
                        && CdaUtility.isSameTime(patientSocialHistory.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                    updateExistingSocialHistoryRecord(record, patientSocialHistory);
                    return;
                }
            }

            patientSocialHistory.setDateCreated(new Date());
            patientSocialHistory.setDateUpdated(new Date());
            patientSocialHistory.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientSocialHistory.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

            clinicalDocument.getPatient().getPatientSocialHistories().add(patientSocialHistory);
        }
    }

    private void updateExistingSocialHistoryRecord(PatientSocialHistory record, PatientSocialHistory patientSocialHistory) {
        record.setExternalId(patientSocialHistory.getExternalId());
        record.setValueCode(patientSocialHistory.getValueCode());
        record.setValueCodeDescription(patientSocialHistory.getValueCodeDescription());
        record.setEffectiveTimeStart(patientSocialHistory.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientSocialHistory.getEffectiveTimeEnd());
        record.setStatusCode(patientSocialHistory.getStatusCode());
        record.setStatusCodeName(patientSocialHistory.getStatusCodeName());
        record.setCode(patientSocialHistory.getCode());
        record.setCodeDescription(patientSocialHistory.getCodeDescription());
        record.setTemplate(patientSocialHistory.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
