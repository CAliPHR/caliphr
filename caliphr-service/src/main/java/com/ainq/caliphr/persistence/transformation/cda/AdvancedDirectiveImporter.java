package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.*;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;

/**
 * Created by mmelusky on 5/15/2015.
 */
@Component
public interface AdvancedDirectiveImporter {

    void loadAdvancedDirectiveEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot);
}
