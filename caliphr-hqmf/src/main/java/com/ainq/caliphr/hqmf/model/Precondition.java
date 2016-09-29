package com.ainq.caliphr.hqmf.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ainq.caliphr.hqmf.util.XPathUtil;

public class Precondition {
	
	private List<Precondition> preconditions;
	private String conjunctionCode;
	private String reference;
	
	public Precondition() {
		
	}
	
	public Precondition(Node node) {
		NodeList precondNodes = XPathUtil.evalToNodeList("./*/cda:precondition", node);
		for (int i = 0; i < precondNodes.getLength(); i++) {
			if (preconditions == null) {
				preconditions = new ArrayList<Precondition>();
			}
			preconditions.add(new Precondition(precondNodes.item(i)));
		}
		Node referenceDef = XPathUtil.evalToNode("./*/cda:id", node);
		if (referenceDef == null) {
			referenceDef = XPathUtil.evalToNode("./cda:join/cda:templateId/cda:item", node);
		}
		if (referenceDef != null) {
			reference = XPathUtil.evalOrNull("./@extension", referenceDef);
		}
		
		if (preconditions != null) {
			Node conjunctionCodeNode = XPathUtil.evalToNode("./*[1]", node);
			if (conjunctionCodeNode != null) {
				conjunctionCode = conjunctionCodeNode.getNodeName();
			}
		}
	}
	public List<Precondition> getPreconditions() {
		return preconditions;
	}
	public void setPreconditions(List<Precondition> preconditions) {
		this.preconditions = preconditions;
	}
	public void addPrecondition(Precondition precondition) {
		if (preconditions == null) {
			preconditions = new ArrayList<Precondition>();
		}
		preconditions.add(precondition);
	}
	public String getConjunctionCode() {
		return conjunctionCode;
	}
	public void setConjunctionCode(String conjunctionCode) {
		this.conjunctionCode = conjunctionCode;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	

}
