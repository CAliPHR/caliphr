package com.ainq.caliphr.persistence.util.predicate.hqmf;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfDocument;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class HqmfPredicate {
	public HqmfPredicate() {
	}

	public static Predicate searchForActiveHqmfDocumentByCmsId(String cmsId, Integer providerId) {
		QHqmfDocument qHqmfDocument = QHqmfDocument.hqmfDocument;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(providerId != null ? qHqmfDocument.provider.id.eq(providerId) : qHqmfDocument.provider.isNull());
		builder.and(qHqmfDocument.cmsId.eq(cmsId));
		builder.and(qHqmfDocument.dateDisabled.isNull());
		return builder;
	}
	
	public static Predicate searchForAllActiveHqmfDocumentsByProviderAndUser(Integer providerId, Integer userId) {
		QHqmfDocument qHqmfDocument = QHqmfDocument.hqmfDocument;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(providerId != null ? qHqmfDocument.provider.id.eq(providerId) : qHqmfDocument.provider.isNull());
		builder.and(userId != null ? qHqmfDocument.userId.eq(userId) : qHqmfDocument.userId.isNull());
		builder.and(qHqmfDocument.dateDisabled.isNull());
		return builder;
	}

}
