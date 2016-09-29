package com.ainq.caliphr.persistence.dao;

import javax.xml.bind.JAXBElement;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

import java.util.List;

public interface ClinicalDocumentDao {

    ClinicalDocument saveClinicalDocument(ClinicalDocument clinicalDocument);

    PatientPayer savePatientPayer(PatientPayer patientPayer);

    PatientInfo savePatientInfo(PatientInfo patientInfo);

    PatientPhoneNumber savePatientPhoneNumber(PatientPhoneNumber patientPhoneNumber);

	Iterable<ProviderPhoneNumber> findProviderPhoneNumbers(Provider provider);

    ProviderPhoneNumber saveProviderPhoneNumber(ProviderPhoneNumber providerPhoneNumber);

    ClinicalDocumentParseError saveClinicalDocumentParseError(ClinicalDocument clinicalDocument, Exception ex);

    void endDateExistingPatientProblems(PatientInfo patientInfo, JAXBElement<?> document);

    List<CodeMapping> findActiveCodeMappings(PracticeGroup practiceGroup);
}
