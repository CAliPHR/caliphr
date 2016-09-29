package com.ainq.caliphr.persistence.transformation.cda.impl;

import java.util.Date;
import java.util.HashSet;

import org.hl7.v3.POCDMT000040Section;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientInstruction;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.InstructionImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class InstructionImporterImpl extends SectionImporter implements InstructionImporter {

    @Override
    public void loadPatientInstructions(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientInstructions() == null) {
            clinicalDocument.getPatient().setPatientInstructions(new HashSet<>());
        }

        PatientInstruction patientInstruction = new PatientInstruction();
        patientInstruction.setPatient(clinicalDocument.getPatient());

        // Base Importers
        CodeDetails codeDetails = extractCodeDetails(section.getCode());
        
        // External Id
        patientInstruction.setExternalId(extractExternalId(section.getId()));

        // Code
        patientInstruction.setCode(codeDetails.getCode());

        // Code Description
        patientInstruction.setCodeDescription(codeDetails.getCodeDescription());

        patientInstruction.setTemplate(templateRoot);

        // Find the matching patient record from the database and update
        for (PatientInstruction record : clinicalDocument.getPatient().getPatientInstructions()) {
            if (CdaUtility.isSameCode(
	            		patientInstruction.getCode(), record.getCode(), 
	            		patientInstruction.getCodeDescription(), record.getCodeDescription()
	        		)
                    && CdaUtility.isSameTemplate(patientInstruction.getTemplate(), record.getTemplate())
                    && CdaUtility.isSameTime(patientInstruction.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
                updateExistingInstructionRecord(record, patientInstruction);
                return;
            }
        }

        patientInstruction.setDateCreated(new Date());
        patientInstruction.setDateUpdated(new Date());
        patientInstruction.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientInstruction.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().getPatientInstructions().add(patientInstruction);
    }

    private void updateExistingInstructionRecord(PatientInstruction record, PatientInstruction patientInstruction) {
        record.setExternalId(patientInstruction.getExternalId());
        record.setEffectiveTimeStart(patientInstruction.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientInstruction.getEffectiveTimeEnd());
        record.setStatusCode(patientInstruction.getStatusCode());
        record.setCode(patientInstruction.getCode());
        record.setCodeDescription(patientInstruction.getCodeDescription());
        record.setTemplate(patientInstruction.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
