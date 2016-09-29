package com.ainq.caliphr.persistence.util.predicate.patient;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QState;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 5/21/2015.
 */
public class StatePredicate {

    public static Predicate searchByAbbreviation(String abbreviation) {
        QState qState = QState.state;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qState.abbreviation.equalsIgnoreCase(abbreviation));
        return booleanBuilder;
    }

}
