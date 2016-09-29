package com.ainq.caliphr.persistence.dao.reference;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.StatusCode;

/**
 * Created by mmelusky on 5/10/2016.
 */
public interface StatusCodeNewXactionDao {
    StatusCode findOrCreateStatusCodeNewTransaction(String statusCode);
}
