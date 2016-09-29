package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientFuncCogStatus;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.FuncCogStatusImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class FuncCogStatusImporterImpl extends SectionImporter implements FuncCogStatusImporter {

    public void loadPatientFuncCogStatuses(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientFuncCogStatuses() == null) {
            clinicalDocument.getPatient().setPatientFuncCogStatuses(new HashSet<>());
        }
        if (section.getEntry() != null) {
            for (POCDMT000040Entry entry : section.getEntry()) {
                loadPatientFuncCogStatusEntry(entry, clinicalDocument, templateRoot);
            }
        }
    }

    @Override
    public void loadPatientFuncCogStatusEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (entry.getObservation() != null) {
            PatientFuncCogStatus patientFuncCogStatus = new PatientFuncCogStatus();
            patientFuncCogStatus.setPatient(clinicalDocument.getPatient());
            POCDMT000040Observation observation = entry.getObservation();

            // Base import
            CodeDetails codeDetails = extractCodeDetails(observation.getCode());
            EffectiveTime effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
            StatusCodeDetails statusCodeDetails = extractStatus(observation.getStatusCode());
            
            // External Id
            patientFuncCogStatus.setExternalId(extractExternalId(observation.getId()));

            // Extract Effective Date
            patientFuncCogStatus.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
            patientFuncCogStatus.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            // Status Code
            patientFuncCogStatus.setStatusCode(statusCodeDetails.getStatusCode());

            // Status Code Name
            patientFuncCogStatus.setStatusCodeName(statusCodeDetails.getStatusCodeName());

            // Code
            patientFuncCogStatus.setCode(codeDetails.getCode());

            // Code Description
            patientFuncCogStatus.setCodeDescription(codeDetails.getCodeDescription());

            patientFuncCogStatus.setTemplate(templateRoot);
            patientFuncCogStatus.setDateCreated(new Date());
            patientFuncCogStatus.setDateUpdated(new Date());
            patientFuncCogStatus.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientFuncCogStatus.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            clinicalDocument.getPatient().getPatientFuncCogStatuses().add(patientFuncCogStatus);
        }
    }
}
