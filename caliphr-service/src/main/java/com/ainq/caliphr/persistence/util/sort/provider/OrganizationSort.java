package com.ainq.caliphr.persistence.util.sort.provider;

import org.springframework.data.querydsl.QSort;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QOrganization;

/**
 * Created by mmelusky on 8/10/2015.
 */
public class OrganizationSort {
    public static QSort sortByNameAsc() {
        return new QSort(QOrganization.organization.organizationName.asc());
    }
}
