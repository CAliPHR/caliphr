package com.ainq.caliphr.persistence.util.sort.provider;

import org.springframework.data.querydsl.QSort;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QPracticeGroup;

/**
 * Created by mmelusky on 8/10/2015.
 */
public class PracticeGroupSort {
    public static QSort sortByNameAsc() {
        return new QSort(QPracticeGroup.practiceGroup.groupName.asc());
    }
}
