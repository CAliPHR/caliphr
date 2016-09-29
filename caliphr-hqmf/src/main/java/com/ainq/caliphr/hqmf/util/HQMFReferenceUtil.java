package com.ainq.caliphr.hqmf.util;

import org.w3c.dom.Node;

public class HQMFReferenceUtil {
	
	public static String getReferenceId(Node node) {
		return XPathUtil.evalOrNull("./@extension", node);
	}

}
