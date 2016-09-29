package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem;

public interface CodeSystemDao {

	CodeSystem findOrCreateCodeSystem(String codeSystemOid, String codeSystemName);

}
