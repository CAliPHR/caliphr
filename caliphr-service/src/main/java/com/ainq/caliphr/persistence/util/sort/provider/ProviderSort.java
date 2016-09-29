package com.ainq.caliphr.persistence.util.sort.provider;

import org.springframework.data.querydsl.QSort;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QProvider;

/**
 * Created by mmelusky on 8/10/2015.
 */
public class ProviderSort {

    public static QSort sortByLastNameAsc() {
        return new QSort(QProvider.provider.lastName.asc());
    }

}
