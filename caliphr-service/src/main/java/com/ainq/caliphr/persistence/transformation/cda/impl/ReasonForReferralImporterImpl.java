package com.ainq.caliphr.persistence.transformation.cda.impl;

import java.util.Date;
import java.util.HashSet;

import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientReasonForReferral;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.ReasonForReferralImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class ReasonForReferralImporterImpl extends SectionImporter implements ReasonForReferralImporter {

    @Override
    public void loadPatientReasonForReferrals(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientReasonForReferrals() == null) {
            clinicalDocument.getPatient().setPatientReasonForReferrals(new HashSet<>());
        }
        PatientReasonForReferral patientReasonForReferral = new PatientReasonForReferral();
        patientReasonForReferral.setPatient(clinicalDocument.getPatient());

        // Base import
        CodeDetails codeDetails = extractCodeDetails(section.getCode());
        
        // External Id
        patientReasonForReferral.setExternalId(extractExternalId(section.getId()));

        // Code
        patientReasonForReferral.setCode(codeDetails.getCode());

        // Code Description
        patientReasonForReferral.setCodeDescription(codeDetails.getCodeDescription());

        patientReasonForReferral.setTemplate(templateRoot);

        // Find the matching patient record from the database and update
        for (PatientReasonForReferral record : clinicalDocument.getPatient().getPatientReasonForReferrals()) {
            if (CdaUtility.isSameCode(
	            		patientReasonForReferral.getCode(), record.getCode(), 
	            		patientReasonForReferral.getCodeDescription(), record.getCodeDescription()
	        		)
                    && CdaUtility.isSameTemplate(patientReasonForReferral.getTemplate(), record.getTemplate())
                    && CdaUtility.isSameTime(patientReasonForReferral.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                updateExistingReasonForReferral(record, patientReasonForReferral);
                return;
            }
        }

        patientReasonForReferral.setDateCreated(new Date());
        patientReasonForReferral.setDateUpdated(new Date());
        patientReasonForReferral.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientReasonForReferral.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().getPatientReasonForReferrals().add(patientReasonForReferral);
    }

    private void updateExistingReasonForReferral(PatientReasonForReferral record, PatientReasonForReferral patientReasonForReferral) {
        record.setCode(patientReasonForReferral.getCode());
        record.setCodeDescription(patientReasonForReferral.getCodeDescription());
        record.setExternalId(patientReasonForReferral.getExternalId());
        record.setNegationDetail(patientReasonForReferral.getNegationDetail());
        record.setEffectiveTimeStart(patientReasonForReferral.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientReasonForReferral.getEffectiveTimeEnd());
        record.setTemplate(patientReasonForReferral.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
