package com.ainq.caliphr.hqmf.model.type;

public class HQMFAttribute {
	
	private String id;
	private String code;
	private String value;
	private String unit;
	private String name;
	private HQMFIdentifier idObj;
	private HQMFCoded codeObj;
	private Object valueObj;
	
	public HQMFAttribute() {
		super();
	}

	public HQMFAttribute(String id, String code, String value, String unit,
			String name, HQMFIdentifier idObj, HQMFCoded codeObj, Object valueObj) {
		super();
		this.id = id;
		this.code = code;
		this.value = value;
		this.unit = unit;
		this.name = name;
		this.idObj = idObj;
		this.codeObj = codeObj;
		this.valueObj = valueObj;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HQMFIdentifier getIdObj() {
		return idObj;
	}

	public void setIdObj(HQMFIdentifier idObj) {
		this.idObj = idObj;
	}

	public HQMFCoded getCodeObj() {
		return codeObj;
	}

	public void setCodeObj(HQMFCoded codeObj) {
		this.codeObj = codeObj;
	}

	public Object getValueObj() {
		return valueObj;
	}

	public void setValueObj(Object valueObj) {
		this.valueObj = valueObj;
	}
	
	
}
