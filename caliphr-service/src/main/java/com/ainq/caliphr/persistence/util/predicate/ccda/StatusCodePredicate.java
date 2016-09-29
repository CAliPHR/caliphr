package com.ainq.caliphr.persistence.util.predicate.ccda;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QStatusCode;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 5/21/2015.
 */
public class StatusCodePredicate {

    public static Predicate searchByCodeName(String statusCode) {
        QStatusCode qStatusCode = QStatusCode.statusCode;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qStatusCode.statusCodeName.equalsIgnoreCase(statusCode));
        return booleanBuilder;
    }

}
