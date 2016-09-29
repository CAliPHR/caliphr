package com.ainq.caliphr.persistence.dao.impl;

import java.util.concurrent.locks.Lock;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.dao.CodeDao;
import com.ainq.caliphr.persistence.dao.CodeNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.CodeRepository;
import com.ainq.caliphr.persistence.util.predicate.code.CodePredicate;
import com.google.common.util.concurrent.Striped;

import ch.qos.logback.classic.Logger;

@Repository
public class CodeDaoImpl implements CodeDao {
	
	static Logger logger = (Logger) LoggerFactory.getLogger(CodeDaoImpl.class);
	
	private final static Striped<Lock> codeLocks = Striped.lock(35);
	
	@Autowired
	private CodeNewXactionDao codeNewXactionDao;
	
	@Autowired
    private CodeRepository codeRepository;
	
	// caches
    
	@Value("#{cacheManager.getCache('com.ainq.caliphr.persistence.model.obj.caliphrDb.Code.customCache')}")
	private Cache codeCache;
	
	@Override
    public Code findOrCreateCode(String codeName, String codeSystemOid, String codeSystemName, String codeDescription, String cdaDescription) {
	
		MultiKey cacheKey = new MultiKey(codeName, codeSystemOid, codeSystemName, codeDescription);
		Integer codeId = codeCache.get(cacheKey, Integer.class);
		if (codeId != null) {
			return codeRepository.getOne(codeId);
		}

		// obtain the lock to prevent other threads from creating duplicate records
    	Lock lock = codeLocks.get(codeName != null ? codeName : codeDescription);
    	lock.lock();
		
    	try {
	    	Code code = findCodeNoCache(codeName, codeSystemOid, codeSystemName, codeDescription); 
	    	if (code == null) {
	    		code = codeNewXactionDao.createCodeNewTransaction(codeName, codeSystemOid, codeSystemName, codeDescription, cdaDescription);
	    		code = codeRepository.getOne(code.getId());
    		}
    		codeCache.put(cacheKey, code.getId());
    		return code;
	    	} catch (Exception e) {
	    		logger.error(null, e);
	    		throw e;
	    	} finally {
	    		lock.unlock();
	    	}
    	}
	
	@Transactional
    @Override
    public Code saveCode(Code code) {
        return this.codeRepository.save(code);
    }
	
	@Override
	public Code findCodeNoCache(String codeName, String codeSystemOid, String codeSystemName, String codeDescription) {
		return codeRepository.findOne(CodePredicate.searchByCode(codeName, codeSystemOid, codeSystemName, codeDescription));
	}

	@Override
	public void updateLatestCdaDescription(Code code, String cdaDescription) {
		
		// update the cda description in a new transaction in a locked fashion, to avoid deadlocks
		Lock lock = codeLocks.get(code.getId());
    	lock.lock();
    	try {
    		code = codeNewXactionDao.updateLatestCdaDescriptionNewTransaction(code, cdaDescription);
    		code = codeRepository.getOne(code.getId());
    	} catch (Exception e) {
    		logger.error(null, e);
    		throw e;
    	} finally {
    		lock.unlock();
    	}
		
	}

	
}

