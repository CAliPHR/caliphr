package com.ainq.caliphr.persistence.util.predicate.code;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.QValueSetCode;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ValueSet;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class ValueSetCodePredicate {
    public ValueSetCodePredicate() {
    }

    public static Predicate searchByValueSetAndCode(Code code, ValueSet valueSet) {
        QValueSetCode qValueSetCode = QValueSetCode.valueSetCode;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qValueSetCode.code.id.eq(code.getId()));
        builder.and(qValueSetCode.valueSet.eq(valueSet));
        return builder;
    }
}
