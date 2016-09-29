package com.ainq.caliphr.persistence.dao;

import java.util.List;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoSecureLight;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder.PatientPhoneNumberHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;

public interface SecureTableDao {

    void savePatientInfoHolder(PatientInfoHolder holder);

    PatientInfoHolder findPatientBySSN(String ssn);

    List<PatientInfoSecureLight> findPatientInfoSecureLight(List<Integer> patientIds);

    List<Patient> findAllPatientInfo(List<Integer> patientIds);

    List<Patient> findPatientBasicInfo(List<Integer> patientIds);

    PatientPhoneNumberHolder findPhoneNumberByNumberAndType(Integer patientId, String phoneNumber, String use);

    void savePatientPhoneNumberHolder(PatientPhoneNumberHolder phoneHolder);

    PatientInfoHolder findPatientBySourceAndMRN(PracticeGroup practiceGroup, String medicalRecordNumber);

    Byte[] createMrnHash(String medicalRecordNumber);
}
