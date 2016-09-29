package com.ainq.caliphr.hqmf.model.type;

import org.w3c.dom.Node;

import com.ainq.caliphr.hqmf.util.XPathUtil;

public class HQMFSubsetOperator {
	
	private String type;
	private Object value;

	public HQMFSubsetOperator() {

	}

	public HQMFSubsetOperator(Node node) {
		//XMLUtil.outputXmlForDebug(node);
		type = XPathUtil.evalOrNull("./cda:subsetCode/@code", node);
		if (type == null) {
			type = XPathUtil.evalOrNull("./qdm:subsetCode/@code", node);
		}
		if (type == null) {
			String seqNum = XPathUtil.evalOrNull("./cda:sequenceNumber/@value", node);
			if ("1".equals(seqNum)) {
				type = "FIRST";
			}
		}
		Node valueDefNode = XPathUtil.evalToNode("./*/cda:repeatNumber", node);
		if (valueDefNode == null) {
			valueDefNode = XPathUtil.evalToNode("./*/cda:value", node);
		}
		if (valueDefNode != null) {
			String valueType = XPathUtil.evalOrNull("./@xsi:type", node);
			if ("ANY".equals(valueType)) {
				value = new HQMFAnyValue();
			}
		}
		if (valueDefNode != null && value == null) {
			value = new HQMFRange(valueDefNode, "IVL_PQ");
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
