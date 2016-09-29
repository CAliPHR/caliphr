package com.ainq.caliphr.persistence.dao.reference;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.StatusCode;

public interface StatusCodeDao {

	StatusCode findOrCreateStatusCode(String statusCode);

}
