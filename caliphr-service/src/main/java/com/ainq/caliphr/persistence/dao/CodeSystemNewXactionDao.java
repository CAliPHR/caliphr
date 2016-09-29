package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem;

public interface CodeSystemNewXactionDao {

	CodeSystem findOrCreateCodeSystemNewTransaction(String codeSystemOid, String codeSystemName);

	CodeSystem findCodeSystemByOidOrName(String codeSystemOid, String codeSystemName);

}
