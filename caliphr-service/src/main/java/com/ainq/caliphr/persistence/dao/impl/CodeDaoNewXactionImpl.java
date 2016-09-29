package com.ainq.caliphr.persistence.dao.impl;

import java.util.Date;

import javax.persistence.EntityManager;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.CodeNewXactionDao;
import com.ainq.caliphr.persistence.dao.CodeSystemDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.CodeRepository;

import ch.qos.logback.classic.Logger;

@Repository
public class CodeDaoNewXactionImpl implements CodeNewXactionDao {
	
	static Logger logger = (Logger) LoggerFactory.getLogger(CodeDaoNewXactionImpl.class);
    	
	@Autowired
    private CodeRepository codeRepository;
	
	@Autowired
	private CodeSystemDao codeSystemDao;
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Code createCodeNewTransaction(String codeName, String codeSystemOid, String codeSystemName, String codeDescription, String cdaDescription) {
    	
		// This method assumes the caller has already locked to prevent other threads from creating duplicate rows
		
		CodeSystem codeSystem = codeSystemDao.findOrCreateCodeSystem(codeSystemOid, codeSystemName);
		Code code = new Code();
		code.setCodeName(codeName);
		code.setCodeSystem(codeSystem);
		code.setDescription(codeDescription);
		code.setLatestCdaDescription(cdaDescription);
		code.setDateCreated(new Date());
		code.setDateUpdated(new Date());
		code.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
		code.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
		code = codeRepository.saveAndFlush(code);
		return code;
    	
    }
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Code updateLatestCdaDescriptionNewTransaction(Code code, String cdaDescription) {
		
		// This method assumes the caller has already locked to prevent other threads from updating at the same time
		
		code = entityManager.merge(code);
		code.setLatestCdaDescription(cdaDescription);
		return codeRepository.saveAndFlush(code);
	}

}

