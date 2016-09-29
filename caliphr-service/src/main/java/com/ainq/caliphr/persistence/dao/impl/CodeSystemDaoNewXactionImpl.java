package com.ainq.caliphr.persistence.dao.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.CodeSystemNewXactionDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.QCodeSystem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.CodeSystemRepository;

@Repository
public class CodeSystemDaoNewXactionImpl implements CodeSystemNewXactionDao {
	
	@Autowired
    protected CodeSystemRepository codeSystemRepository;
		
	@Override	
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CodeSystem findOrCreateCodeSystemNewTransaction(String codeSystemOid, String codeSystemName) {
		
		// This method assumes the caller has already locked to prevent other threads from creating duplicate rows
    	
		// query again, in case another thread created a record while waiting for the lock
    	CodeSystem codeSystem = findCodeSystemByOidOrName(codeSystemOid, codeSystemName);
    	if (codeSystem == null) {
			codeSystem = new CodeSystem();
			codeSystem.setHl7Oid(codeSystemOid);
			codeSystem.setCodeSystemName(codeSystemName);
			codeSystem.setDateCreated(new Date());
			codeSystem.setDateUpdated(new Date());
            codeSystem.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
            codeSystem.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
			codeSystem = codeSystemRepository.save(codeSystem);
    	}
    	return codeSystem;
    }
	
	@Override
	public CodeSystem findCodeSystemByOidOrName(String codeSystemOid, String codeSystemName) {
		CodeSystem codeSystem;
		if (codeSystemOid != null) {
    		codeSystem = this.codeSystemRepository.findOne(QCodeSystem.codeSystem.hl7Oid.eq(codeSystemOid));
    	} else {
    		codeSystem = this.codeSystemRepository.findOne(QCodeSystem.codeSystem.codeSystemName.eq(codeSystemName));
    	}
		return codeSystem;
	}


}
