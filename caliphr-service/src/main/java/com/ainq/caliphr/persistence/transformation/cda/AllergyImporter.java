package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.*;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;

/**
 * Created by mmelusky on 5/15/2015.
 */
public interface AllergyImporter {

    void loadPatientAllergyEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot);
}
