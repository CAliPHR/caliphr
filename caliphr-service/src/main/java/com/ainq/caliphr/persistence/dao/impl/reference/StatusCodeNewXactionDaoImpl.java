package com.ainq.caliphr.persistence.dao.impl.reference;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.dao.reference.StatusCodeNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.StatusCode;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.StatusCodeRepository;
import com.ainq.caliphr.persistence.util.predicate.ccda.StatusCodePredicate;

/**
 * Created by mmelusky on 5/10/2016.
 */
@Repository
public class StatusCodeNewXactionDaoImpl implements StatusCodeNewXactionDao {
    
    @Autowired
    private StatusCodeRepository statusCodeRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StatusCode findOrCreateStatusCodeNewTransaction(String statusCode) {
    	
    	// This method assumes the caller has already locked to prevent other threads from creating duplicate rows

        // query again, in case another thread created a record while waiting for the lock
        StatusCode foundStatusCode = statusCodeRepository.findOne(StatusCodePredicate.searchByCodeName(statusCode));
        if (foundStatusCode == null) {
            foundStatusCode = new StatusCode();
            foundStatusCode.setStatusCodeName(statusCode);
            foundStatusCode.setDateCreated(new Date());
            foundStatusCode.setDateUpdated(new Date());
            foundStatusCode = statusCodeRepository.saveAndFlush(foundStatusCode);
        }
        return foundStatusCode;
    }
}
