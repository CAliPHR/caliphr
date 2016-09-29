package com.ainq.caliphr.hqmf.model.type;

import org.w3c.dom.Node;

import com.ainq.caliphr.hqmf.util.XPathUtil;

public class HQMFCoded {
	
	private String type;
	private String system;
	private String code;
	private String codeListId;
	private String title;
	private String nullFlavor;
	private String originalText;
	private String value;
	private boolean derived;
	private String unit;

	public HQMFCoded() {
		
	}
	
	public HQMFCoded(String type, String system, String code, String codeListId, String title, String nullFlavor, String originalText) {
		this.type = type;
		this.system = system;
		this.code = code;
		this.codeListId = codeListId;
		this.title = title;
		this.setNullFlavor(nullFlavor);
		this.setOriginalText(originalText);
		
		this.value = code;
		this.derived = false;
		this.unit = null;
	}
	
	public HQMFCoded(Node node) {
		type = XPathUtil.evalOrNull("./@xsi:type", node);
		if (type == null) {
			type = "CD";
		}
		system = XPathUtil.evalOrNull("./@codeSystem", node);
		code = XPathUtil.evalOrNull("./@code", node);
		codeListId = XPathUtil.evalOrNull("./@valueSet", node);
		title = XPathUtil.evalOrNull("./*/@value", node);
		
		value = code;
		derived = false;
		unit = null;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeListId() {
		return codeListId;
	}

	public void setCodeListId(String codeListId) {
		this.codeListId = codeListId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNullFlavor() {
		return nullFlavor;
	}

	public void setNullFlavor(String nullFlavor) {
		this.nullFlavor = nullFlavor;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean getDerived() {
		return derived;
	}

	public void setDerived(boolean derived) {
		this.derived = derived;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}