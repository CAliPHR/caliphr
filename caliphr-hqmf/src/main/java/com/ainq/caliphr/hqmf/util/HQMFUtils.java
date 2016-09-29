package com.ainq.caliphr.hqmf.util;

import org.w3c.dom.Node;

import com.ainq.caliphr.hqmf.model.type.HQMFValue;

public class HQMFUtils {

	public static HQMFValue optionalValue(String xpathExpr, Node node, String type) {
		Node valueDef = XPathUtil.evalToNode(xpathExpr, node);	
		if (valueDef != null) {
			return new HQMFValue(valueDef, type, false);
		}
		return null;
	}
	
}
