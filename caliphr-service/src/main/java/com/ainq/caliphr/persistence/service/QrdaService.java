package com.ainq.caliphr.persistence.service;

import java.util.List;

import com.ainq.caliphr.common.model.extract.cat1.QrdaCat1ZipFile;

/**
 * Created by mmelusky on 6/3/2015.
 */
public interface QrdaService {

    List<QrdaCat1ZipFile> exportQrdaCategory1(Iterable<Long> hqmfIds, Integer userId);
	String exportQrdaCategory3(Iterable<Long> hqmfIds, Integer userId);
}
