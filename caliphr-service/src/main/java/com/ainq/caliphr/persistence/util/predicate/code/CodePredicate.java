package com.ainq.caliphr.persistence.util.predicate.code;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMappingType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.EhrVendor;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

import ch.qos.logback.classic.Logger;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QCode.code;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QCodeMapping.codeMapping;

import org.slf4j.LoggerFactory;

public class CodePredicate {
	
	static Logger logger = (Logger) LoggerFactory.getLogger(CodePredicate.class);
	
    public static Predicate searchByCode(String codeName, String codeSystem, String codeSystemName, String codeDescription) {
        BooleanBuilder builder = new BooleanBuilder();
        if (codeName != null) {
        	builder.and(code.codeName.eq(codeName));
        } else {
        	builder.and(code.codeName.isNull());
        }
        
        if (codeSystem != null) {
        	builder.and(code.codeSystem.hl7Oid.eq(codeSystem));
        }
        
        if (codeSystemName != null && codeSystem == null) {
        	builder.and(code.codeSystem.codeSystemName.equalsIgnoreCase(codeSystemName));
        }
        
        if (codeDescription != null && codeName == null) {
        	builder.and(code.description.eq(codeDescription));
        }
        
        return builder;
    }

    public static Predicate searchByCodeAndCodeSystemName(String codeName, String codeSystemName) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(code.codeName.eq(codeName));
        builder.and(code.codeSystem.codeSystemName.eq(codeSystemName));
        return builder;
    }

    public static Predicate findCodeMapping(CodeMappingType codeMappingType, PracticeGroup practiceGroup, EhrVendor ehrVendor, String fromValue, String fromCodeSystem, String displayName) {
        BooleanBuilder builder = new BooleanBuilder();
        if (codeMappingType != null) {
            builder.and(codeMapping.type.id.eq(codeMappingType.getId()));
        }
        if (practiceGroup != null) {
            builder.and(codeMapping.group.id.eq(practiceGroup.getId()));
        }
        if (ehrVendor != null) {
            builder.and(codeMapping.vendor.id.eq(ehrVendor.getId()));
        }
        if (fromValue != null) {
            builder.and(codeMapping.fromCodeName.equalsIgnoreCase(fromValue));
        }
        if (fromCodeSystem != null) {
            builder.and(codeMapping.fromCodeSystem.equalsIgnoreCase(fromCodeSystem));
        }
        if (displayName != null) {
            builder.and(codeMapping.fromDisplayName.equalsIgnoreCase(displayName));
        }
        return builder;
    }
}
