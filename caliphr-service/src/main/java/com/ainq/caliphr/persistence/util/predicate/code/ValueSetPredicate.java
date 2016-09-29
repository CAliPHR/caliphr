package com.ainq.caliphr.persistence.util.predicate.code;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QValueSet;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class ValueSetPredicate {
	public ValueSetPredicate() {
	}

	public static Predicate searchByOid(String oid, int bundleId) {
		QValueSet qValueSet = QValueSet.valueSet;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qValueSet.hl7Oid.eq(oid));
		builder.and(qValueSet.bundle.id.eq(bundleId));
		return builder;
	}
}
