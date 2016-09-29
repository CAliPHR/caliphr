package com.ainq.caliphr.persistence.transformation.cda.impl;

import org.hl7.v3.*;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientMedication;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.MedicationImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import java.util.Date;
import java.util.List;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class MedicationImporterImpl extends SectionImporter implements MedicationImporter {

    @Override
    public void loadPatientMedicationEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientMedication> existingMedications) {
        POCDMT000040SubstanceAdministration substanceAdministration = entry.getSubstanceAdministration();
        POCDMT000040Supply supply = entry.getSupply();
        POCDMT000040Act act = entry.getAct();
        if (act != null && act.getEntryRelationship() != null)
        {
            for (POCDMT000040EntryRelationship entryRelationship : act.getEntryRelationship()) {
                if (entryRelationship.getSubstanceAdministration() != null) {
                    loadSubstanceAdministration(entryRelationship.getSubstanceAdministration(), clinicalDocument, templateRoot, codeMappings, existingMedications);
                }
            }
        }else if (substanceAdministration != null) {
            loadSubstanceAdministration(substanceAdministration, clinicalDocument, templateRoot, codeMappings, existingMedications);
        }else if (supply != null) {
            loadSupply(supply, clinicalDocument, templateRoot, codeMappings, existingMedications);
        }
    }

    private void loadSupply(POCDMT000040Supply supply, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientMedication> existingMedications) {
        PatientMedication patientMedication = new PatientMedication();
        patientMedication.setPatient(clinicalDocument.getPatient());

        // Base Importer
        EffectiveTime effectiveTime = extractEffectiveDate(supply.getEffectiveTime(), supply.getAuthor());
        StatusCodeDetails statusCodeDetails = extractStatus(supply.getStatusCode());
        
        // External Id
        patientMedication.setExternalId(extractExternalId(supply.getId()));

        // Status Code
        patientMedication.setStatusCode(statusCodeDetails.getStatusCode());

        // Status Code Name
        patientMedication.setStatusCodeName(statusCodeDetails.getStatusCodeName());

        // Effective Start
        patientMedication.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());

        // Effective End
        patientMedication.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

        // Get medication product
        if (supply.getProduct() != null
                && supply.getProduct().getManufacturedProduct() != null
                && supply.getProduct().getManufacturedProduct().getManufacturedMaterial() != null
                && supply.getProduct().getManufacturedProduct().getManufacturedMaterial().getCode() != null) {
            CodeDetails productCodeDetails = extractCodeDetails(supply.getProduct().getManufacturedProduct().getManufacturedMaterial().getCode());
            if (productCodeDetails != null) {
                patientMedication.setProductCode(productCodeDetails.getCode());
                patientMedication.setProductCodeDescription(productCodeDetails.getCodeDescription());
            }
        }

        patientMedication.setTemplate(templateRoot);

        /*
            Code Mappings for Practice
         */
        this.practiceCodeMapper.mapPatientMedication(codeMappings, patientMedication);

        // Find the matching patient record from the database and update
        List<PatientMedication> existingMedList = existingMedications.getExisting(patientMedication.getEffectiveTimeStart(), patientMedication.getProductCode());
        if (existingMedList != null) {
	        for (PatientMedication record : existingMedList) {
	            if (CdaUtility.isSameCode(
		            		patientMedication.getProductCode(), record.getProductCode(), 
		            		patientMedication.getProductCodeDescription(), record.getProductCodeDescription()
		        		)
	                    && CdaUtility.isSameTemplate(patientMedication.getTemplate(), record.getTemplate())
	                    && CdaUtility.isSameTime(patientMedication.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
	                updateExistingMedicationRecord(record, patientMedication);
	                return;
	            }
	        }
        }

        patientMedication.setDateCreated(new Date());
        patientMedication.setDateUpdated(new Date());
        patientMedication.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientMedication.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().getPatientMedications().add(patientMedication);
        existingMedications.add(patientMedication, patientMedication.getEffectiveTimeStart(), patientMedication.getProductCode());
    }

    private void loadSubstanceAdministration(POCDMT000040SubstanceAdministration substanceAdministration, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientMedication> existingMedications) {
        PatientMedication patientMedication = new PatientMedication();
        patientMedication.setPatient(clinicalDocument.getPatient());

        // Base Importer
        CodeDetails codeDetails = extractCodeDetails(substanceAdministration.getRouteCode());
        EffectiveTime effectiveTime = extractEffectiveDate(substanceAdministration.getEffectiveTime(), substanceAdministration.getAuthor());
        StatusCodeDetails statusCodeDetails = extractStatus(substanceAdministration.getStatusCode());

        // External Id
        patientMedication.setExternalId(extractExternalId(substanceAdministration.getId()));
        
        // Status Code
        patientMedication.setStatusCode(statusCodeDetails.getStatusCode());

        // Status Code Name
        patientMedication.setStatusCodeName(statusCodeDetails.getStatusCodeName());

        // Effective Start
        patientMedication.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());

        // Effective End
        patientMedication.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

        // Dose Quantity
        if (substanceAdministration.getDoseQuantity() != null) {
            if (substanceAdministration.getDoseQuantity().getValue() != null) {
                patientMedication.setDoseQuantityValue(substanceAdministration.getDoseQuantity().getValue());
            }
            if (substanceAdministration.getDoseQuantity().getUnit() != null) {
                patientMedication.setDoseQuantityUnit(substanceAdministration.getDoseQuantity().getUnit());
            }
        }

        // Rate Quantity
        if (substanceAdministration.getRateQuantity() != null) {
            if (substanceAdministration.getRateQuantity().getValue() != null) {
                patientMedication.setRateQuantityValue(substanceAdministration.getRateQuantity().getValue());
            }
            if (substanceAdministration.getRateQuantity().getUnit() != null) {
                patientMedication.setRateQuantityValue(substanceAdministration.getRateQuantity().getUnit());
            }
        }

        // Administration Code
        if (substanceAdministration.getAdministrationUnitCode() != null) {
            CodeDetails administrationCodeDetails = extractCodeDetails(substanceAdministration.getAdministrationUnitCode());
            if (administrationCodeDetails != null) {
                patientMedication.setAdministrationCode(administrationCodeDetails.getCode());
                patientMedication.setAdministrationCodeDescription(administrationCodeDetails.getCodeDescription());
            }
        }

        // Product Code
                /*
                if (substanceAdministration.getEntryRelationship() != null) {
                    substanceAdministration.getEntryRelationship().stream().filter(entryRelationship -> entryRelationship.getSupply() != null && entryRelationship.getSupply().getProduct() != null
                            && entryRelationship.getSupply().getProduct().getManufacturedProduct() != null
                            && entryRelationship.getSupply().getProduct().getManufacturedProduct().getManufacturedMaterial() != null
                            && entryRelationship.getSupply().getProduct().getManufacturedProduct().getManufacturedMaterial().getCode() != null).forEach(entryRelationship -> {
                        CodeDetails productCodeDetails = extractCodeDetails(entryRelationship.getSupply().getProduct().getManufacturedProduct().getManufacturedMaterial().getCode());
                        if (productCodeDetails != null) {
                            patientMedication.setProductCode(productCodeDetails.getCode());
                            patientMedication.setProductCodeDescription(productCodeDetails.getCodeDescription());
                        }
                    });
                }
                */
        if (substanceAdministration.getConsumable() != null
                && substanceAdministration.getConsumable().getManufacturedProduct() != null
                && substanceAdministration.getConsumable().getManufacturedProduct().getManufacturedMaterial() != null
                && substanceAdministration.getConsumable().getManufacturedProduct().getManufacturedMaterial().getCode() != null) {
            CodeDetails productCodeDetails = extractCodeDetails(substanceAdministration.getConsumable().getManufacturedProduct().getManufacturedMaterial().getCode());
            if (productCodeDetails != null) {
                patientMedication.setProductCode(productCodeDetails.getCode());
                patientMedication.setProductCodeDescription(productCodeDetails.getCodeDescription());
            }
        }

        patientMedication.setTemplate(templateRoot);

        /*
            Code Mappings for Practice
         */
        this.practiceCodeMapper.mapPatientMedication(codeMappings, patientMedication);

        // Find the matching patient record from the database and update
        List<PatientMedication> existingMedList = existingMedications.getExisting(patientMedication.getEffectiveTimeStart(), patientMedication.getProductCode());
        if (existingMedList != null) {
	        for (PatientMedication record : existingMedList) {
	            if (CdaUtility.isSameCode(
		            		patientMedication.getProductCode(), record.getProductCode(), 
		            		patientMedication.getProductCodeDescription(), record.getProductCodeDescription()
		        		)
	                    && CdaUtility.isSameTemplate(patientMedication.getTemplate(), record.getTemplate())
	                    && CdaUtility.isSameTime(patientMedication.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
	                updateExistingMedicationRecord(record, patientMedication);
	                return;
	            }
	        }
        }

        patientMedication.setDateCreated(new Date());
        patientMedication.setDateUpdated(new Date());
        patientMedication.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        patientMedication.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

        clinicalDocument.getPatient().getPatientMedications().add(patientMedication);
        existingMedications.add(patientMedication, patientMedication.getEffectiveTimeStart(), patientMedication.getProductCode());
    }

    private void updateExistingMedicationRecord(PatientMedication record, PatientMedication patientMedication) {
        record.setExternalId(patientMedication.getExternalId());
        record.setDoseQuantityValue(patientMedication.getDoseQuantityValue());
        record.setDoseQuantityUnit(patientMedication.getDoseQuantityUnit());
        record.setRateQuantityValue(patientMedication.getRateQuantityValue());
        record.setRateQuantityUnit(patientMedication.getRateQuantityUnit());
        record.setProductCode(patientMedication.getProductCode());
        record.setProductCodeDescription(patientMedication.getProductCodeDescription());
        record.setAdministrationCode(patientMedication.getAdministrationCode());
        record.setAdministrationCodeDescription(patientMedication.getAdministrationCodeDescription());
        record.setEffectiveTimeStart(patientMedication.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientMedication.getEffectiveTimeEnd());
        record.setStatusCode(patientMedication.getStatusCode());
        record.setStatusCodeName(patientMedication.getStatusCodeName());
        record.setTemplate(patientMedication.getTemplate());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
