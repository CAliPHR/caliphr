package com.ainq.caliphr.persistence.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.common.model.sandbox.gson.GsonValueConcept;
import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.CodeDao;
import com.ainq.caliphr.persistence.dao.ValueSetDao;
import com.ainq.caliphr.persistence.model.ccda.*;
import com.ainq.caliphr.persistence.model.ccda.PracticeGroupType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.service.ValueSetService;

@Service
public class ValueSetServiceImpl implements ValueSetService {
	
	@Autowired
	private EntityManager entityManager;

    @Autowired
    private ValueSetDao valueSetDao;
    
    @Autowired
    private CodeDao codeDao;

    @Override
    public List<Bundle> getAllBundles() {
        return valueSetDao.getAllBundles();
    }

    @Override
    @Transactional
    public void processBundleValueSetRecords(List<GsonValueConcept> gsonValueConcepts, ValueSet valueSet) {


        for (GsonValueConcept valueConcept : gsonValueConcepts) {

            // Save Code, or update existing code values
            Code code = this.codeDao.findOrCreateCode(valueConcept.getCode(), valueConcept.getCode_system(), valueConcept.getCode_system_name(), valueConcept.getDisplay_name(), null);

            // Save Value Set Code
            ValueSetCode valueSetCode = new ValueSetCode();
            valueSetCode.setCode(code);
            valueSetCode.setValueSet(entityManager.merge(valueSet));
            valueSetCode.setDateCreated(new Date());
            valueSetCode.setDateUpdated(new Date());
            valueSetCode.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            valueSetCode.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            Integer recordId = this.valueSetDao.findExistingValueSetCode(valueSetCode);
            if (recordId != null) {
                valueSetCode.setId(recordId);
            } else {
                this.valueSetDao.saveValueSetCode(valueSetCode);
            }
        }
    }
    
    @Override
    @Transactional
    public void createEhrCodeMapping(EhrVendorType vendorType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem) {
        this.valueSetDao.createEhrCodeMapping(vendorType, codeMapType, fromValue, fromCodeSystem, toValue, toCodeSystem);
    }

    @Override
    @Transactional
    public void createEhrDisplayNameMapping(EhrVendorType vendorType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem) {
        this.valueSetDao.createEhrDisplayNameMapping(vendorType, codeMapType, displayName, toValue, toCodeSystem);
    }

    @Override
    public void createPracticeGroupCodeMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem) {
        this.valueSetDao.createPracticeGroupCodeMapping(practiceGroupType, codeMapType, fromValue, fromCodeSystem, toValue, toCodeSystem);
    }

    @Override
    public void createPracticeGroupDisplayNameMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem) {
        this.valueSetDao.createPracticeGroupDisplayNameMapping(practiceGroupType, codeMapType, displayName, toValue, toCodeSystem);
    }

    // for performance, cache the hl7 oid locally
    private static Map<String, String> codeSystemHl7OidMap = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    @Transactional
    public String findHl7OidForCodeSystemByName(String codeSystemName) {
        String result = codeSystemHl7OidMap.get(codeSystemName);
        if (result == null) {
        	result = valueSetDao.findHl7OidForCodeSystemByName(codeSystemName);
        	codeSystemHl7OidMap.put(codeSystemName, result);
        }
        return result;
    }
}
