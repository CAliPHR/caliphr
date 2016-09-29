package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.POCDMT000040Entry;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientImmunization;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;

/**
 * Created by mmelusky on 5/15/2015.
 */
public interface ImmunizationImporter {
    void loadPatientImmunizationEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, ExistingItemMap<PatientImmunization> existingImmunizations);
}
