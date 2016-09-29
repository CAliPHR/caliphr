package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;

public interface CodeNewXactionDao {

	Code createCodeNewTransaction(String codeName, String codeSystemOid, String codeSystemName, String codeDescription, String cdaDescription);

	Code updateLatestCdaDescriptionNewTransaction(Code code, String cdaDescription);

}
