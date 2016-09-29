package com.ainq.caliphr.persistence.util.predicate.provider;

import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.QProvider;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 8/6/2015.
 */
public class ProviderPredicate {

    public static Predicate getAllProviders() {
        QProvider qProvider = QProvider.provider;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qProvider.lastName.isNotNull());
        return booleanBuilder;
    }

    public static Predicate searchByNpiAndGroup(String npi, PracticeGroup practiceGroup) {
        QProvider qProvider = QProvider.provider;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qProvider.npi.eq(npi));
        booleanBuilder.and(qProvider.group.id.eq(practiceGroup.getId()));
        return booleanBuilder;
    }
    
    public static Predicate searchByProviderNameAndGroup(PersonName personName, PracticeGroup practiceGroup, String npi) {
        QProvider qProvider = QProvider.provider;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        Boolean hasFirst=Boolean.FALSE, hasLast=Boolean.FALSE;
        if (personName.getFirstName() != null && (!personName.getFirstName().isEmpty())) {
            hasFirst = Boolean.TRUE;
            booleanBuilder.and(qProvider.firstName.isNotNull());
            booleanBuilder.and(qProvider.firstName.equalsIgnoreCase(personName.getFirstName()));
        }
        if (personName.getLastName() != null && (!personName.getLastName().isEmpty())) {
            hasLast = Boolean.TRUE;
            booleanBuilder.and(qProvider.lastName.isNotNull());
            booleanBuilder.and(qProvider.lastName.equalsIgnoreCase(personName.getLastName()));
        }
        if (personName.getMiddleName() != null && (!personName.getMiddleName().isEmpty())) {
            booleanBuilder.and(qProvider.middleName.isNotNull());
            booleanBuilder.and(qProvider.middleName.equalsIgnoreCase(personName.getMiddleName()));
        }

        // If no first and last name, perform an exact search on their full name
        if (!hasFirst && !hasLast && personName.getFullName() != null && (!personName.getFullName().isEmpty())) {
            booleanBuilder.and(qProvider.fullName.isNotNull());
            booleanBuilder.and(qProvider.fullName.equalsIgnoreCase(personName.getFullName()));
        }

        if (npi == null) {
            booleanBuilder.and(qProvider.npi.isNull());
        } else {
            booleanBuilder.and(qProvider.npi.equalsIgnoreCase(npi));
        }
        booleanBuilder.and(qProvider.group.id.eq(practiceGroup.getId()));
        return booleanBuilder;
    }
}
