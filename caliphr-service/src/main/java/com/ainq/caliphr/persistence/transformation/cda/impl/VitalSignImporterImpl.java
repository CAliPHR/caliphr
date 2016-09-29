package com.ainq.caliphr.persistence.transformation.cda.impl;

import java.util.Date;
import java.util.List;

import org.hl7.v3.*;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientVitalSign;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.cda.VitalSignImporter;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public class VitalSignImporterImpl extends SectionImporter implements VitalSignImporter {

    @Override
    public void loadPatientVitalSignEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, ExistingItemMap<PatientVitalSign> existingVitalSigns) {
        if (entry.getOrganizer() != null) {

            POCDMT000040Organizer organizer = entry.getOrganizer();



            if (organizer.getComponent() != null) {
                organizer.getComponent().stream().filter(component -> component.getObservation() != null).forEach(component -> {
                    PatientVitalSign patientVitalSign = new PatientVitalSign();
                    patientVitalSign.setPatient(clinicalDocument.getPatient());

                    POCDMT000040Observation observation = component.getObservation();


                    // Base Importers
                    CodeDetails codeDetails = extractCodeDetails(observation.getCode());
                    EffectiveTime effectiveTime = extractEffectiveDate(observation.getEffectiveTime(), observation.getAuthor());
                    StatusCodeDetails statusCodeDetails = extractStatus(observation.getStatusCode());
                    
                    // External Id
                    patientVitalSign.setExternalId(extractExternalId(observation.getId()));

                    // Status Code
                    patientVitalSign.setStatusCode(statusCodeDetails.getStatusCode());

                    // Status Code Name
                    patientVitalSign.setStatusCodeName(statusCodeDetails.getStatusCodeName());

                    // Code
                    patientVitalSign.setCode(codeDetails.getCode());

                    // Code Description
                    patientVitalSign.setCodeDescription(codeDetails.getCodeDescription());

                    // Extract Effective Date
                    patientVitalSign.setEffectiveTimeStart(effectiveTime.getEffectiveTimeStart());
                    patientVitalSign.setEffectiveTimeEnd(effectiveTime.getEffectiveTimeEnd());

                    // Extract Value
                    if (observation.getValue() != null) {
                        for (ANY observationValue : observation.getValue()) {
                            if (observationValue instanceof PQ) {
                                PQ pq = (PQ) observationValue;
                                if (pq.getValue() != null) {
                                    patientVitalSign.setRecordValue(pq.getValue());
                                }
                                if (pq.getUnit() != null) {
                                    patientVitalSign.setRecordValueUnit(pq.getUnit());
                                }
                            }
                            else if (observationValue instanceof CD) {
                                CD observationCd = (CD) observationValue;
                                CodeDetails valueCodeDetails = extractCodeDetails(observationCd);
                                patientVitalSign.setValueCode(valueCodeDetails.getCode());
                                patientVitalSign.setValueCodeDescription(valueCodeDetails.getCodeDescription());
                            }
                        }
                    }

                    patientVitalSign.setTemplate(templateRoot);

                    // Find the matching patient record from the database and update
	                List<PatientVitalSign> existingVitalList = existingVitalSigns.getExisting(patientVitalSign.getEffectiveTimeStart(), patientVitalSign.getCode());
	                if (existingVitalList != null) {
						for (PatientVitalSign record : existingVitalList) {
	                        if (CdaUtility.isSameCode(
	                        		patientVitalSign.getCode(), record.getCode(), 
	                        		patientVitalSign.getCodeDescription(), record.getCodeDescription()
	        	        		)
                                && CdaUtility.isSameTemplate(patientVitalSign.getTemplate(), record.getTemplate())
                                && CdaUtility.isSameTime(patientVitalSign.getEffectiveTimeStart(), record.getEffectiveTimeStart())) {
	                            updateExistingVitalSignRecord(record, patientVitalSign);
	                            return;
	                        }
	                    }
	                }

                    patientVitalSign.setDateCreated(new Date());
                    patientVitalSign.setDateUpdated(new Date());
                    patientVitalSign.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                    patientVitalSign.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);

                    clinicalDocument.getPatient().getPatientVitalSigns().add(patientVitalSign);
                    existingVitalSigns.add(patientVitalSign, patientVitalSign.getEffectiveTimeStart(), patientVitalSign.getCode());
                });
            }
        }
    }
    
    private void updateExistingVitalSignRecord(PatientVitalSign record, PatientVitalSign patientVitalSign) {
        record.setExternalId(patientVitalSign.getExternalId());
        record.setEffectiveTimeStart(patientVitalSign.getEffectiveTimeStart());
        record.setEffectiveTimeEnd(patientVitalSign.getEffectiveTimeEnd());
        record.setStatusCode(patientVitalSign.getStatusCode());
        record.setStatusCodeName(patientVitalSign.getStatusCodeName());
        record.setCode(patientVitalSign.getCode());
        record.setCodeDescription(patientVitalSign.getCodeDescription());
        record.setValueCode(patientVitalSign.getValueCode());
        record.setValueCodeDescription(patientVitalSign.getValueCodeDescription());
        record.setTemplate(patientVitalSign.getTemplate());
        record.setRecordValue(patientVitalSign.getRecordValue());
        record.setRecordValueUnit(patientVitalSign.getRecordValueUnit());
        record.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        record.setDateDisabled(null);
        // setDateUpdated is set automatically in the @PreUpdate method of the entity
    }
}
