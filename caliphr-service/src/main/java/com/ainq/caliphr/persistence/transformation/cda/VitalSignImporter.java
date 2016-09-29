package com.ainq.caliphr.persistence.transformation.cda;

import org.hl7.v3.POCDMT000040Entry;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientVitalSign;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;
import com.ainq.caliphr.persistence.transformation.impl.ExistingItemContext.ExistingItemMap;

/**
 * Created by mmelusky on 5/15/2015.
 */
public interface VitalSignImporter {

	void loadPatientVitalSignEntry(POCDMT000040Entry entry, ClinicalDocument clinicalDocument, TemplateRoot templateRoot, ExistingItemMap<PatientVitalSign> existingVitalSigns);
}
