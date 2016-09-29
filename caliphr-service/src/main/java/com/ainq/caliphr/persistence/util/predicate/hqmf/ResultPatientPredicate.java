package com.ainq.caliphr.persistence.util.predicate.hqmf;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QResultPatient;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class ResultPatientPredicate {
	
	public static Predicate searchForActiveResultPatientForResultId(Long resultId) {
		QResultPatient qResultPatient = QResultPatient.resultPatient;
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qResultPatient.result.id.eq(resultId));
		builder.and(qResultPatient.dateDisabled.isNull());
		return builder;
	}

}

