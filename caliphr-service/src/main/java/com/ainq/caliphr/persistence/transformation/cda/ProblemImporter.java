package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.POCDMT000040Entry;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientProblem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;

import java.util.List;

/**
 * Created by mmelusky on 5/15/2015.
 */
public interface ProblemImporter {

    void loadPatientProblemEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, List<CodeMapping> codeMappings, ExistingItemMap<PatientProblem> existingProblems);
}
