package com.ainq.caliphr.persistence.model.ccda;

import java.util.HashSet;
import java.util.Set;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;

/**
 * Created by mmelusky on 5/21/2015.
 */
public class CodeDetails {

    private Code code;
    private String codeDescription;
    private Set<Code> translationCodes = new HashSet<Code>();
    

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

	public Set<Code> getTranslationCodes() {
		return translationCodes;
	}

	public void setTranslationCodes(Set<Code> translationCodes) {
		this.translationCodes = translationCodes;
	}
	
	public void addTranslationCode(Code code) {
		translationCodes.add(code);
	}
}
