package com.ainq.caliphr.persistence.util.predicate.security;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUser;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUserPasswordRequest;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 8/25/2015.
 */
public class UserSecurityPredicate {
    public static Predicate findUserByEmail(String emailAddress) {
        QApplicationUser qApplicationUser = QApplicationUser.applicationUser;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qApplicationUser.emailAddress.toLowerCase().eq(emailAddress.toLowerCase()));
        return booleanBuilder;
    }

    public static Predicate findPasswordRequestByToken(String token) {
        QApplicationUserPasswordRequest qApplicationUserPasswordRequest = QApplicationUserPasswordRequest.applicationUserPasswordRequest;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qApplicationUserPasswordRequest.uuid.eq(token));
        return booleanBuilder;
    }
}
