package com.ainq.caliphr.persistence.dao.impl;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ValueSetDao;
import com.ainq.caliphr.persistence.model.ccda.CodeMapType;
import com.ainq.caliphr.persistence.model.ccda.EhrVendorType;
import com.ainq.caliphr.persistence.model.ccda.PracticeGroupType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.*;
import com.ainq.caliphr.persistence.util.predicate.code.*;
import com.mysema.query.jpa.impl.JPAQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QCodeSystem.codeSystem;

import java.util.*;

@Repository
public class ValueSetDaoImpl implements ValueSetDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private CodeMappingTypeRepository codeMappingTypeRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private EhrVendorRepository ehrVendorRepository;

    @Autowired
    private PracticeGroupRepository practiceGroupRepository;

    @Autowired
    private CodeMappingRepository codeMappingRepository;

    @Autowired
    private ValueSetRepository valueSetRepository;

    @Autowired
    private ValueSetCodeRepository valueSetCodeRepository;
    
    public ValueSetDaoImpl() {
    }

    @Override
    @Transactional
    public ValueSet saveValueSet(ValueSet valueSet) {
        ValueSet entity = this.valueSetRepository.findOne(ValueSetPredicate.searchByOid(valueSet.getHl7Oid(), valueSet.getBundle().getId()));
        if (entity != null) {
            if (entity.getId() > 0) {
                valueSet.setId(entity.getId());
            }
            if (entity.getBundle() != null) {
                // Use existing bundle from the database entity
                valueSet.setBundle(entity.getBundle());
            }
        } else {
        	valueSet.setBundle(entityManager.merge(valueSet.getBundle()));
        }
        return this.valueSetRepository.save(valueSet);
    }
    
    @Override
    public Bundle getActiveBundle() {
    	return this.bundleRepository.findOne(QBundle.bundle.dateDisabled.isNull());
    }

    @Override
    public Integer findExistingValueSetCode(ValueSetCode valueSetCode) {
        ValueSetCode entity = this.valueSetCodeRepository.findOne(ValueSetCodePredicate.searchByValueSetAndCode(valueSetCode.getCode(), valueSetCode.getValueSet()));
        return entity != null ? entity.getId() : null;
    }

    @Transactional
    @Override
    public ValueSetCode saveValueSetCode(ValueSetCode valueSetCode) {
        if (valueSetCode.getCode() != null) {
            valueSetCode.setCode(this.codeRepository.findOne(valueSetCode.getCode().getId()));
        }

        if (valueSetCode.getValueSet() != null) {
            valueSetCode.setValueSet(this.valueSetRepository.findOne(valueSetCode.getValueSet().getId()));
        }

        return this.valueSetCodeRepository.save(valueSetCode);
    }

    @Override
    public Iterable<ValueSetCode> findCodesByValueSetOid(String oid) {
        return valueSetCodeRepository.findAll(QValueSetCode.valueSetCode.valueSet.hl7Oid.eq(oid));
    }

    @Override
    public List<Bundle> getAllBundles() {
        return bundleRepository.findAll();
    }

    @Override
    public Bundle findBundleById(Integer bundleId) {
        return this.bundleRepository.findOne(bundleId);
    }

    @Transactional
    @Override
    public CodeMapping createEhrCodeMapping(EhrVendorType vendorType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem) {

        CodeMapping codeMapping = findCodeMapping(codeMapType, null, vendorType, fromValue, fromCodeSystem, null);
        addToCodeMapping(codeMapping, toValue, toCodeSystem);
        return codeMappingRepository.save(codeMapping);
    }

    @Override
    @Transactional
    public CodeMapping createEhrDisplayNameMapping(EhrVendorType vendorType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem) {

        CodeMapping codeMapping = findCodeMapping(codeMapType, null, vendorType, null, null, displayName);
        addToCodeMapping(codeMapping, toValue, toCodeSystem);
        return codeMappingRepository.save(codeMapping);
    }

    @Override
    @Transactional
    public CodeMapping createPracticeGroupCodeMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String fromValue, String fromCodeSystem, String toValue, String toCodeSystem) {

        CodeMapping codeMapping = findCodeMapping(codeMapType, practiceGroupType, null, fromValue, fromCodeSystem, null);
        addToCodeMapping(codeMapping, toValue, toCodeSystem);
        return codeMappingRepository.save(codeMapping);
    }

    @Override
    @Transactional
    public CodeMapping createPracticeGroupDisplayNameMapping(PracticeGroupType practiceGroupType, CodeMapType codeMapType, String displayName, String toValue, String toCodeSystem) {

        CodeMapping codeMapping = findCodeMapping(codeMapType, practiceGroupType, null, null, null, displayName);
        addToCodeMapping(codeMapping, toValue, toCodeSystem);
        return codeMappingRepository.save(codeMapping);
    }

    @Override
    @Transactional
    public String findHl7OidForCodeSystemByName(String codeSystemName) {
        return new JPAQuery(entityManager).from(codeSystem)
                .where(codeSystem.codeSystemName.eq(codeSystemName))
                .setHint("org.hibernate.cacheable", "true")  
                .setHint("org.hibernate.cacheRegion", "com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem.queryCache")
                .setHint("org.hibernate.readOnly", "true")
                .singleResult(codeSystem.hl7Oid);
    }

    //
    //  Private methods
    private CodeMapping findCodeMapping(CodeMapType codeMapType, PracticeGroupType practiceGroupType, EhrVendorType vendorType, String fromValue, String fromCodeSystem, String displayName) {

        // Query
        PracticeGroup practiceGroup = null;
        if (practiceGroupType != null) {
            practiceGroup = practiceGroupRepository.findOne(practiceGroupType.getGroupId());
        }
        EhrVendor ehrVendor = null;
        if (vendorType != null) {
            ehrVendor = ehrVendorRepository.findOne(vendorType.getVendorId());
        }
        CodeMappingType codeMappingType = codeMappingTypeRepository.findOne(codeMapType.getTypeId());
        Code fromCode = null;
        if (fromValue != null && fromCodeSystem != null) {
            fromCode = codeRepository.findOne(CodePredicate.searchByCodeAndCodeSystemName(fromValue, fromCodeSystem));
        }
        CodeMapping codeMapping = codeMappingRepository.findOne(CodePredicate.findCodeMapping(codeMappingType, practiceGroup, ehrVendor, fromValue, fromCodeSystem, displayName));

        // Create new
        if (codeMapping == null) {
            codeMapping = new CodeMapping();
            if (fromCode != null) {
                codeMapping.setFromCode(fromCode);
            }
            if (fromValue != null && fromCodeSystem != null) {
                codeMapping.setFromCodeName(fromValue);
                codeMapping.setFromCodeSystem(fromCodeSystem);
            }
            if (displayName != null) {
                codeMapping.setFromDisplayName(displayName);
            }
            if (ehrVendor != null) {
                codeMapping.setVendor(ehrVendor);
            }
            if (practiceGroup != null) {
                codeMapping.setGroup(practiceGroup);
            }
            codeMapping.setType(codeMappingType);
            codeMapping.setDateCreated(new Date());
            codeMapping.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            codeMapping.setDateUpdated(new Date());
            codeMapping.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        }
        return codeMapping;
    }

    private void addToCodeMapping(CodeMapping codeMapping, String toValue, String toCodeSystem) {

        // Query for existing To Codes
        if (codeMapping.getCodeMappingToCodes() == null) {
            codeMapping.setCodeMappingToCodes(new HashSet<>());
        }
        Code toCode = codeRepository.findOne(CodePredicate.searchByCodeAndCodeSystemName(toValue, toCodeSystem));
        if (toCode == null) {
            throw new IllegalStateException("To Code not found! ->" + toValue + " " + toCodeSystem);
        }
        for (CodeMappingToCode record : codeMapping.getCodeMappingToCodes()) {
            if (record.getToCode() != null && record.getToCode().getId().equals(toCode.getId())) {
                return;
            }
        }

        // Create New To Code
        CodeMappingToCode codeMappingToCode = new CodeMappingToCode();
        codeMappingToCode.setCodeMapping(codeMapping);
        codeMappingToCode.setToCode(toCode);
        codeMappingToCode.setDateCreated(new Date());
        codeMappingToCode.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        codeMappingToCode.setDateUpdated(new Date());
        codeMappingToCode.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        codeMapping.getCodeMappingToCodes().add(codeMappingToCode);
    }

}
