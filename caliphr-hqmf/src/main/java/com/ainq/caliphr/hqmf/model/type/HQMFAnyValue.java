package com.ainq.caliphr.hqmf.model.type;

import org.w3c.dom.Node;

/**
 * # Used to represent 'any value' in criteria that require a value be present but
 * # don't specify any restrictions on that value
 *
 * @author drosenbaum
 *
 */
public class HQMFAnyValue {

	private String type;
	
	public HQMFAnyValue() {
		
	}
	
	public HQMFAnyValue(String type) {
		super();
		this.type = type;
		if (type == null) {
			type = "ANYNonNull";
		}
	}
	
	public HQMFAnyValue(Node node) {
		this(node, "ANYNonNull");
	}
	
	public HQMFAnyValue(Node node, String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
