package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientAdvancedDirective;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.AdvancedDirectiveImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class AdvancedDirectiveImporterImpl extends SectionImporter implements AdvancedDirectiveImporter {

    public void loadAdvancedDirectives(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientAdvancedDirectives() == null) {
            clinicalDocument.getPatient().setPatientAdvancedDirectives(new HashSet<>());
        }
        if (section.getEntry() != null) {
            section.getEntry().stream().filter(entry -> entry.getSupply() != null).forEach(entry -> {
                loadAdvancedDirectiveEntry(entry, clinicalDocument, templateRoot);
            });
        }
    }

    @Override
    public void loadAdvancedDirectiveEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        PatientAdvancedDirective patientAdvancedDirective = new PatientAdvancedDirective();
        patientAdvancedDirective.setPatient(clinicalDocument.getPatient());
        POCDMT000040Observation observation = entry.getObservation();
        CodeDetails codeDetails = extractCodeDetails(observation.getCode());
        EffectiveTime effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());

        // External Id
        patientAdvancedDirective.setExternalId(extractExternalId(observation.getId()));

        // Code
        patientAdvancedDirective.setCode(codeDetails.getCode());

        // Code Description
        patientAdvancedDirective.setCodeDescription(codeDetails.getCodeDescription());

        // Effective Date
        patientAdvancedDirective.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
        patientAdvancedDirective.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());
        
        patientAdvancedDirective.setTemplate(templateRoot);

        // Find the matching patient record from the database and update
        for (PatientAdvancedDirective record : clinicalDocument.getPatient().getPatientAdvancedDirectives()) {
            if (CdaUtility.isSameCode(
            			patientAdvancedDirective.getCode(), record.getCode(), 
            			patientAdvancedDirective.getCodeDescription(), record.getCodeDescription()
	        		)
                    && CdaUtility.isSameTemplate(patientAdvancedDirective.getTemplate(), record.getTemplate())
                    && CdaUtility.isSameTime(patientAdvancedDirective.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                updateExistingAdvancedDirectiveRecord(record, patientAdvancedDirective);
                return;
            }
        }

        patientAdvancedDirective.setDateCreated(new Date());
        patientAdvancedDirective.setDateUpdated(new Date());
        patientAdvancedDirective.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientAdvancedDirective.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().getPatientAdvancedDirectives().add(patientAdvancedDirective);
    }

    private void updateExistingAdvancedDirectiveRecord(PatientAdvancedDirective record, PatientAdvancedDirective patientAdvancedDirective) {
        record.setExternalId(patientAdvancedDirective.getExternalId());
        record.setEffectiveTimeStart(patientAdvancedDirective.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientAdvancedDirective.getEffectiveTimeEnd());
        record.setTemplate(patientAdvancedDirective.getTemplate());
        record.setCode(patientAdvancedDirective.getCode());
        record.setCodeDescription(patientAdvancedDirective.getCodeDescription());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
