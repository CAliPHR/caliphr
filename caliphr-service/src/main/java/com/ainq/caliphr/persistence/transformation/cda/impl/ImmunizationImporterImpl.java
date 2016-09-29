package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientImmunization;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.ImmunizationImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.List;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class ImmunizationImporterImpl extends SectionImporter implements ImmunizationImporter {

    @Override
    public void loadPatientImmunizationEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, ExistingItemMap<PatientImmunization> existingImmunizations) {
        if (entry.getSubstanceAdministration() != null) {
            PatientImmunization patientImmunization = new PatientImmunization();
            patientImmunization.setPatient(clinicalDocument.getPatient());
            POCDMT000040SubstanceAdministration substanceAdministration = entry.getSubstanceAdministration();

            // Base import
            CodeDetails codeDetails = extractCodeDetails(substanceAdministration.getRouteCode());
            EffectiveTime effectiveTime = extractEffectiveDate(substanceAdministration.getEffectiveTime(), substanceAdministration.getAuthor());
            StatusCodeDetails statusCodeDetails = extractStatus(substanceAdministration.getStatusCode());
            
            // External Id
            patientImmunization.setExternalId(extractExternalId(substanceAdministration.getId()));

            // Status Code
            patientImmunization.setStatusCode(statusCodeDetails.getStatusCode());

            // Status Code Name
            patientImmunization.setStatusCodeName(statusCodeDetails.getStatusCodeName());

            // Dose
            if (substanceAdministration.getDoseQuantity() != null) {
                patientImmunization.setDoseQuantityValue(substanceAdministration.getDoseQuantity().getValue());
                patientImmunization.setDoseQuantityUnit(substanceAdministration.getDoseQuantity().getUnit());
            }

            // Extract Effective Date
            patientImmunization.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
            patientImmunization.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

            // Product Code
            if (substanceAdministration.getConsumable() != null && substanceAdministration.getConsumable().getManufacturedProduct() != null
                    && substanceAdministration.getConsumable().getManufacturedProduct().getManufacturedMaterial() != null
                    && substanceAdministration.getConsumable().getManufacturedProduct().getManufacturedMaterial().getCode() != null) {
                CodeDetails productCodeDetails = extractCodeDetails(substanceAdministration.getConsumable().getManufacturedProduct().getManufacturedMaterial().getCode());
                if (productCodeDetails != null) {
                    patientImmunization.setProductCode(productCodeDetails.getCode());
                    patientImmunization.setProductCodeDescription(productCodeDetails.getCodeDescription());
                }
            }

            patientImmunization.setTemplate(templateRoot);

            // Find the matching patient record from the database and update
            List<PatientImmunization> existingImmunizationList = existingImmunizations.getExisting(patientImmunization.getEffectiveTimeStart(), patientImmunization.getProductCode());
            if (existingImmunizationList != null) {
	            for (PatientImmunization record : existingImmunizationList) {
	                if (CdaUtility.isSameCode(
	                			patientImmunization.getProductCode(), record.getProductCode(), 
	                			patientImmunization.getProductCodeDescription(), record.getProductCodeDescription()
			        		)
	                        && CdaUtility.isSameTemplate(patientImmunization.getTemplate(), record.getTemplate())
	                        && CdaUtility.isSameTime(patientImmunization.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
	                    updateExistingImmunizationRecord(record, patientImmunization);
	                    return;
	                }
	            }
            }

            patientImmunization.setDateCreated(new Date());
            patientImmunization.setDateUpdated(new Date());
            patientImmunization.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            patientImmunization.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

            clinicalDocument.getPatient().getPatientImmunizations().add(patientImmunization);
            existingImmunizations.add(patientImmunization, patientImmunization.getEffectiveTimeStart(), patientImmunization.getProductCode());
        }
    }

    private void updateExistingImmunizationRecord(PatientImmunization record, PatientImmunization patientImmunization) {
        record.setExternalId(patientImmunization.getExternalId());
        record.setDoseQuantityValue(patientImmunization.getDoseQuantityValue());
        record.setDoseQuantityUnit(patientImmunization.getDoseQuantityUnit());
        record.setProductCode(patientImmunization.getProductCode());
        record.setProductCodeDescription(patientImmunization.getProductCodeDescription());
        record.setEffectiveTimeStart(patientImmunization.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientImmunization.getEffectiveTimeEnd());
        record.setStatusCode(patientImmunization.getStatusCode());
        record.setStatusCodeName(patientImmunization.getStatusCodeName());
        record.setTemplate(patientImmunization.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
