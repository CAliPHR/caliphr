package com.ainq.caliphr.persistence.transformation;

import org.hl7.v3.POCDMT000040Performer1;

import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

import javax.xml.bind.JAXBElement;
import java.util.List;

public interface ClinicalDocumentParser {

    ClinicalDocument archiveClinicalDocument(JAXBElement<?> document, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, Boolean archiveToFileSystem);

    PatientInfoHolder findPatientBySSN(JAXBElement<?> document);

    PatientInfoHolder createPatientInfo(JAXBElement<?> document, PracticeGroup practiceGroup);

    Provider createProviderDetails(POCDMT000040Performer1 performer, PracticeGroup practiceGroup);

    void loadPatientSections(JAXBElement<?> document, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, List<CodeMapping> codeMappings);

    DocumentType validateClinicalDocument(JAXBElement<?> document);

    PracticeGroup findSendingGroup(JAXBElement<?> document);

    PatientInfoHolder findPatientBySourceAndMRN(JAXBElement<?> document, PracticeGroup patientSource);

}
