package com.ainq.caliphr.persistence.dao.impl;

import java.util.concurrent.locks.StampedLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ainq.caliphr.persistence.dao.CodeSystemDao;
import com.ainq.caliphr.persistence.dao.CodeSystemNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.CodeSystemRepository;

@Repository
public class CodeSystemDaoImpl implements CodeSystemDao {

	private final static StampedLock codeSystemlock = new StampedLock();
	
	@Autowired
	private CodeSystemNewXactionDao codeSystemNewXactionDao;
	
	@Autowired
    protected CodeSystemRepository codeSystemRepository;
	
	@Override
    public CodeSystem findOrCreateCodeSystem(String codeSystemOid, String codeSystemName) {
    	CodeSystem codeSystem = codeSystemNewXactionDao.findCodeSystemByOidOrName(codeSystemOid, codeSystemName);
    	if (codeSystem == null) {
    		Long stamp = codeSystemlock.writeLock();
    		try {
    			codeSystem = codeSystemNewXactionDao.findOrCreateCodeSystemNewTransaction(codeSystemOid, codeSystemName);
    			codeSystem = codeSystemRepository.getOne(codeSystem.getId());
    		} finally {
    			codeSystemlock.unlock(stamp);
            }
    	}
    	return codeSystem;
    } 
	
}
