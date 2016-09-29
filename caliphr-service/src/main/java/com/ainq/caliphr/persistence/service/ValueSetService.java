package com.ainq.caliphr.persistence.service;

import com.ainq.caliphr.common.model.sandbox.gson.GsonValueConcept;
import com.ainq.caliphr.persistence.model.ccda.CodeMapType;
import com.ainq.caliphr.persistence.model.ccda.EhrVendorType;
import com.ainq.caliphr.persistence.model.ccda.PracticeGroupType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Bundle;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ValueSet;

import java.util.List;

public interface ValueSetService {

    List<Bundle> getAllBundles();
    
    void processBundleValueSetRecords(List<GsonValueConcept> gsonValueConcepts, ValueSet valueSet);

    void createEhrCodeMapping(EhrVendorType vendorType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem);

    void createEhrDisplayNameMapping(EhrVendorType vendorType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem);

    void createPracticeGroupCodeMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem);

    void createPracticeGroupDisplayNameMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem);

    String findHl7OidForCodeSystemByName(String codeSystemName);
}
