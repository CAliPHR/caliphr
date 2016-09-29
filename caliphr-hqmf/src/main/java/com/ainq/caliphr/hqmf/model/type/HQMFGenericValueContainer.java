package com.ainq.caliphr.hqmf.model.type;

public class HQMFGenericValueContainer {
	
	private String type;
	private String value;
	
	public HQMFGenericValueContainer(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
