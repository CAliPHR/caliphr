package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.POCDMT000040Section;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;

/**
 * Created by mmelusky on 5/15/2015.
 */
public interface ReasonForVisitImporter {

    void loadPatientReasonForVisits(POCDMT000040Section section, ClinicalDocument clinicalDocument, TemplateRoot templateRoot);
}
