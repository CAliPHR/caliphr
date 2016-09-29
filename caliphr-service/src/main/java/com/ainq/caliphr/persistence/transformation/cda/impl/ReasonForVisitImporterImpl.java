package com.ainq.caliphr.persistence.transformation.cda.impl;

import java.util.Date;
import java.util.HashSet;

import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientReasonForVisit;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.ReasonForVisitImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class ReasonForVisitImporterImpl extends SectionImporter implements ReasonForVisitImporter {

    @Override
    public void loadPatientReasonForVisits(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientReasonForVisits() == null) {
            clinicalDocument.getPatient().setPatientReasonForVisits(new HashSet<>());
        }
        PatientReasonForVisit patientReasonForVisit = new PatientReasonForVisit();
        patientReasonForVisit.setPatient(clinicalDocument.getPatient());

        // Base import
        CodeDetails codeDetails = extractCodeDetails(section.getCode());

        // External Id
        patientReasonForVisit.setExternalId(extractExternalId(section.getId()));
        
        // Code
        patientReasonForVisit.setCode(codeDetails.getCode());

        // Code Description
        patientReasonForVisit.setCodeDescription(codeDetails.getCodeDescription());

        patientReasonForVisit.setTemplate(templateRoot);

        // Find the matching patient record from the database and update
        for (PatientReasonForVisit record : clinicalDocument.getPatient().getPatientReasonForVisits()) {
            if (CdaUtility.isSameCode(
	            		patientReasonForVisit.getCode(), record.getCode(), 
	            		patientReasonForVisit.getCodeDescription(), record.getCodeDescription()
	        		)
                    && CdaUtility.isSameTemplate(patientReasonForVisit.getTemplate(), record.getTemplate())
                    && CdaUtility.isSameTime(patientReasonForVisit.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                updateExistingReasonForVisitRecord(record, patientReasonForVisit);
                return;
            }
        }

        patientReasonForVisit.setDateCreated(new Date());
        patientReasonForVisit.setDateUpdated(new Date());
        patientReasonForVisit.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientReasonForVisit.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().getPatientReasonForVisits().add(patientReasonForVisit);
    }

    private void updateExistingReasonForVisitRecord(PatientReasonForVisit record, PatientReasonForVisit patientReasonForVisit) {
        record.setExternalId(patientReasonForVisit.getExternalId());
        record.setEffectiveTimeStart(patientReasonForVisit.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientReasonForVisit.getEffectiveTimeEnd());
        record.setCode(patientReasonForVisit.getCode());
        record.setCodeDescription(patientReasonForVisit.getCodeDescription());
        record.setTemplate(patientReasonForVisit.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
