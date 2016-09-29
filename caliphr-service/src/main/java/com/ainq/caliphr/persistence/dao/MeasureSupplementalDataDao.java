package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Result;

public interface MeasureSupplementalDataDao {

	void determineSupplementalData(Result result, Integer userId);

}