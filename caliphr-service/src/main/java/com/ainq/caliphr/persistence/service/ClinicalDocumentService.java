package com.ainq.caliphr.persistence.service;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;

import javax.xml.bind.JAXBElement;

public interface ClinicalDocumentService {

    // Clinical Document
	ClinicalDocument createClinicalDocument(ClinicalDocumentProcessTask task);
	
    void processClinicalDocumentRecord(JAXBElement<?> document, Boolean logToFileSystem, Long clinicalDocumentId, String medicalRecordNum);

}
