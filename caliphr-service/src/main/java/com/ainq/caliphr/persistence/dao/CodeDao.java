package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;

public interface CodeDao {

	Code findOrCreateCode(String codeName, String codeSystemOid, String codeSystemName, String codeDescription, String cdaDescription);
	
	Code findCodeNoCache(String codeName, String codeSystemOid, String codeSystemName, String codeDescription);

	Code saveCode(Code code);

	void updateLatestCdaDescription(Code code, String cdaDescription);

}
