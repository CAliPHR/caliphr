package com.ainq.caliphr.persistence.util.predicate.hqmf;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfDataCriteria.hqmfDataCriteria;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 11/3/2015.
 */
public class HqmfDataCriteriaPredicate {

    public static Predicate findDataCriteriaForHqmf(Long hqmfDocumentId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(hqmfDataCriteria.hqmfDoc.id.eq(hqmfDocumentId));
        return builder;
    }

}
