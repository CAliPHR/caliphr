package com.ainq.caliphr.persistence.dao.impl.reference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ainq.caliphr.persistence.dao.reference.StatusCodeDao;
import com.ainq.caliphr.persistence.dao.reference.StatusCodeNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.StatusCode;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.StatusCodeRepository;
import com.ainq.caliphr.persistence.util.predicate.ccda.StatusCodePredicate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

@Repository
public class StatusCodeDaoImpl implements StatusCodeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StatusCodeRepository statusCodeRepository;

    @Autowired
    private StatusCodeNewXactionDao statusCodeNewXactionDao;

    private final static Map<String, Integer> statusCodeCache = Collections.synchronizedMap(new HashMap<>());
    
    private final static StampedLock lock = new StampedLock();

    @Override
    public StatusCode findOrCreateStatusCode(String statusCode) {

        Integer statusCodeId = statusCodeCache.get(statusCode);
        if (statusCodeId != null) {
            return statusCodeRepository.getOne(statusCodeId);
        }

        // first query using the current transaction.  If no record is found, call a method to create one outside a transaction
        // and merge it back into the current persistence context
        StatusCode foundStatusCode = statusCodeRepository.findOne(StatusCodePredicate.searchByCodeName(statusCode));
        if (foundStatusCode == null) {
        	Long stamp = lock.writeLock();

            try {
	            foundStatusCode = statusCodeNewXactionDao.findOrCreateStatusCodeNewTransaction(statusCode);
	            foundStatusCode = entityManager.merge(foundStatusCode);
            } finally {
                lock.unlock(stamp);
            }
        }
        statusCodeCache.put(statusCode, foundStatusCode.getId());
        return foundStatusCode;
    }
}
