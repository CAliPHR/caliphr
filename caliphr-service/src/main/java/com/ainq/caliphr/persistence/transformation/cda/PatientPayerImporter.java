package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.POCDMT000040Entry;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;

/**
 * Created by mmelusky on 8/24/2015.
 */
public interface PatientPayerImporter {
    void loadPatientPayerEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot);
}
