package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.POCDMT000040Encounter;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientPlanOfCare;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.PlanOfCareImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class PlanOfCareImporterImpl extends SectionImporter implements PlanOfCareImporter {

    public void loadPatientPlanOfCares(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientPlanOfCares() == null) {
            clinicalDocument.getPatient().setPatientPlanOfCares(new HashSet<>());
        }
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                loadPatientPlanOfCareEntry(entry, clinicalDocument, templateRoot);
            }
        }
    }

    @Override
    public void loadPatientPlanOfCareEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        /*
                    According to this the child node is "encounter":
                        http://bluebuttonplus.org/healthrecords.html

                    Most data is found in the observation node in reality though.  Handling both cases. (mmelusky)
                 */

        if (entry.getEncounter() != null || entry.getObservation() != null) {
            PatientPlanOfCare patientPlanOfCare = new PatientPlanOfCare();
            patientPlanOfCare.setPatient(clinicalDocument.getPatient());
            POCDMT000040Encounter encounter = entry.getEncounter();
            POCDMT000040Observation observation = entry.getObservation();

            // Base import
            CodeDetails codeDetails = new CodeDetails();
            EffectiveTime effectiveTime = new EffectiveTime();
            StatusCodeDetails statusCodeDetails = new StatusCodeDetails();
            if (observation != null) {
                codeDetails = extractCodeDetails(observation.getCode());
                effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
                statusCodeDetails = extractStatus(observation.getStatusCode());
                patientPlanOfCare.setExternalId(extractExternalId(observation.getId()));
            } else {
                codeDetails = extractCodeDetails(encounter.getCode());
                effectiveTime = extractEffectiveDate(encounter.getEffectiveTime(), encounter.getAuthor());
                statusCodeDetails = extractStatus(encounter.getStatusCode());
                patientPlanOfCare.setExternalId(extractExternalId(encounter.getId()));
            }

            // Extract Effective Date
            patientPlanOfCare.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
            patientPlanOfCare.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            // Status Code
            patientPlanOfCare.setStatusCode(statusCodeDetails.getStatusCode());

            // Status Code Name
            patientPlanOfCare.setStatusCodeName(statusCodeDetails.getStatusCodeName());

            // Code
            patientPlanOfCare.setCode(codeDetails.getCode());

            // Code Description
            patientPlanOfCare.setCodeDescription(codeDetails.getCodeDescription());

            patientPlanOfCare.setTemplate(templateRoot);

            // Find the matching patient record from the database and update
            for (PatientPlanOfCare record : clinicalDocument.getPatient().getPatientPlanOfCares()) {
                if (CdaUtility.isSameCode(
	                		patientPlanOfCare.getCode(), record.getCode(), 
	                		patientPlanOfCare.getCodeDescription(), record.getCodeDescription()
		        		)
                        && CdaUtility.isSameTemplate(patientPlanOfCare.getTemplate(), record.getTemplate())
                        && CdaUtility.isSameTime(patientPlanOfCare.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                    updateExistingPlanOfCareRecord(record, patientPlanOfCare);
                    return;
                }
            }

            patientPlanOfCare.setDateCreated(new Date());
            patientPlanOfCare.setDateUpdated(new Date());
            patientPlanOfCare.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientPlanOfCare.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

            clinicalDocument.getPatient().getPatientPlanOfCares().add(patientPlanOfCare);
        }
    }

    private void updateExistingPlanOfCareRecord(PatientPlanOfCare record, PatientPlanOfCare patientPlanOfCare) {
        record.setExternalId(patientPlanOfCare.getExternalId());
        record.setEffectiveTimeStart(patientPlanOfCare.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientPlanOfCare.getEffectiveTimeEnd());
        record.setCode(patientPlanOfCare.getCode());
        record.setCodeDescription(patientPlanOfCare.getCodeDescription());
        record.setTemplate(patientPlanOfCare.getTemplate());
        record.setStatusCode(patientPlanOfCare.getStatusCode());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
