package com.ainq.caliphr.persistence.util.predicate.hqmf;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfPopulationSet;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class HqmfPopulationSetPredicate {
	public HqmfPopulationSetPredicate() {
	}

	public static Predicate searchByPopulationSetKey(Long hqmfDocId, String populationSetKey, int index) {
		QHqmfPopulationSet qHqmfPopulationSet = QHqmfPopulationSet.hqmfPopulationSet;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qHqmfPopulationSet.hqmfDocument.id.eq(hqmfDocId));
		builder.and(qHqmfPopulationSet.key.eq(populationSetKey));
		builder.and(qHqmfPopulationSet.index.eq(index));
		return builder;
	}
}
