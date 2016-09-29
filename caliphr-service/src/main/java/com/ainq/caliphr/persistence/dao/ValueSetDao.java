
package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.persistence.model.ccda.*;
import com.ainq.caliphr.persistence.model.ccda.PracticeGroupType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

import java.util.List;

public interface ValueSetDao {
    ValueSet saveValueSet(ValueSet valueSet);

    Integer findExistingValueSetCode(ValueSetCode valueSetCode);

    ValueSetCode saveValueSetCode(ValueSetCode valueSetCode);

	Iterable<ValueSetCode> findCodesByValueSetOid(String oid);

    List<Bundle> getAllBundles();
    
	Bundle getActiveBundle();

    Bundle findBundleById(Integer bundleId);

    CodeMapping createEhrCodeMapping(EhrVendorType vendorType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem);

    CodeMapping createEhrDisplayNameMapping(EhrVendorType vendorType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem);

    CodeMapping createPracticeGroupCodeMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem);

    CodeMapping createPracticeGroupDisplayNameMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem);

	String findHl7OidForCodeSystemByName(String codeSystemName);

}
