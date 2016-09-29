package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.POCDMT000040PatientRole;

import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;

/**
 * Created by mmelusky on 8/24/2015.
 */
public interface PatientInfoImporter {
	PatientInfoHolder findPatientBySSN(POCDMT000040PatientRole patientRole);

    PatientInfoHolder loadPatientInfo(POCDMT000040PatientRole patientRole, PracticeGroup patientSource);

    PatientInfoHolder findPatientBySourceAndMRN(PracticeGroup patientSource, String medicalRecordNumber);
}
