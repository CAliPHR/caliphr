package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.CD;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Observation;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientPayer;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.PatientPayerImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by mmelusky on 8/24/2015.
 */
@Component
public class PatientPayerImporterImpl extends SectionImporter implements PatientPayerImporter {

    @Override
    public void loadPatientPayerEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot) {
        if (clinicalDocument.getPatient().getPatientPayers() == null) {
            clinicalDocument.getPatient().setPatientPayers(new HashSet<PatientPayer>());
        }
        if (entry.getObservation() != null) {
            POCDMT000040Observation payerObservation = entry.getObservation();
            PatientPayer patientPayer = new PatientPayer();
            patientPayer.setPatient(clinicalDocument.getPatient());

            // Payer Code Value
            if (payerObservation.getValue() != null) {
                payerObservation.getValue().stream().filter(val -> val instanceof CD).forEach(val -> {
                    CD valueCode = (CD) val;
                    CodeDetails codeDetails = extractCodeDetails(valueCode);

                    // Code
                    patientPayer.setPayerCode(codeDetails.getCode());

                    // Code Description
                    patientPayer.setPayerCodeDescription(codeDetails.getCodeDescription());

                });
            }

            // Status Code Details
            if (payerObservation.getStatusCode() != null) {
                StatusCodeDetails statusCodeDetails = extractStatus(payerObservation.getStatusCode());

                // Status Code
                patientPayer.setStatusCode(statusCodeDetails.getStatusCode());

                // TODO status code name??
            }

            patientPayer.setTemplate(templateRoot);

            // Find the matching patient record from the database and update
            for (PatientPayer record : clinicalDocument.getPatient().getPatientPayers()) {
                if (CdaUtility.isSameCode(
                        patientPayer.getPayerCode(), record.getPayerCode(),
                        patientPayer.getPayerCodeDescription(), record.getPayerCodeDescription())
                        && CdaUtility.isSameTemplate(patientPayer.getTemplate(), record.getTemplate())) {
                    updateExistingPayerRecord(record, patientPayer);
                    return;
                }
            }

            patientPayer.setDateCreated(new Date());
            patientPayer.setDateUpdated(new Date());
            patientPayer.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientPayer.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

            clinicalDocument.getPatient().getPatientPayers().add(patientPayer);
        }
    }

    private void updateExistingPayerRecord(PatientPayer record, PatientPayer patientPayer) {
        record.setStatusCode(patientPayer.getStatusCode());
        record.setPayerCode(patientPayer.getPayerCode());
        record.setPayerCodeDescription(patientPayer.getPayerCodeDescription());
        record.setTemplate(patientPayer.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }


}
