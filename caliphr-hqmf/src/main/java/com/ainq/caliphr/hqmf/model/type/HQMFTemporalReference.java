package com.ainq.caliphr.hqmf.model.type;

import org.w3c.dom.Node;

import com.ainq.caliphr.hqmf.util.HQMFReferenceUtil;
import com.ainq.caliphr.hqmf.util.XPathUtil;

public class HQMFTemporalReference {
	
	private String type;
	private String reference;
	private HQMFRange range;
	
	public HQMFTemporalReference() {
	}

	public HQMFTemporalReference(Node node) {
		type = XPathUtil.evalOrNull("./@typeCode", node);
		reference = HQMFReferenceUtil.getReferenceId(XPathUtil.evalToNode("./*/cda:id", node));
		//XMLUtil.outputXmlForDebug(node);
		//System.out.println(xPath.evaluate(".//*/cda:high", nodeConstants.NODE));
		Node rangeDefNode = XPathUtil.evalToNode("./cda:pauseQuantity", node);
		if (rangeDefNode != null) {
			range = new HQMFRange(rangeDefNode, "PQ");
		} else if (
			XPathUtil.evalToNode(".//*/cda:high", node) != null ||
			XPathUtil.evalToNode(".//*/cda:low", node) != null ||
			XPathUtil.evalToNode(".//*/cda:width", node) != null) {
			range = new HQMFRange(node, "PQ");
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public HQMFRange getRange() {
		return range;
	}

	public void setRange(HQMFRange range) {
		this.range = range;
	}

}
