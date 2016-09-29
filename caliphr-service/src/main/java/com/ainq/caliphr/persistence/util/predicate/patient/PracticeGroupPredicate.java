package com.ainq.caliphr.persistence.util.predicate.patient;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPracticeGroup.practiceGroup;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;


/**
 * Created by mmelusky on 5/21/2015.
 */
public class PracticeGroupPredicate {

    public static Predicate findGroupBySendingOid(String oid) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(practiceGroup.senderOid.equalsIgnoreCase(oid));
        return booleanBuilder;
    }

}
