package com.ainq.caliphr.persistence.util.predicate.hqmf;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfAttribute;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class HqmfAttributePredicate {
	public HqmfAttributePredicate() {
	}

	public static Predicate getHqmfAttributesByHqmfDocId(Long hqmfDocId) {
		QHqmfAttribute qHqmfAttribute = QHqmfAttribute.hqmfAttribute;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qHqmfAttribute.hqmfDoc.id.eq(hqmfDocId));
		builder.and(qHqmfAttribute.dateDisabled.isNull());
		return builder;
	}
}
