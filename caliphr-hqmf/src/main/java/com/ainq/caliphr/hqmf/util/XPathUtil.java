package com.ainq.caliphr.hqmf.util;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathUtil {
	
	private static final ThreadLocal<XPath> xPath = new ThreadLocal<XPath>() {
		@Override
		public XPath initialValue() {
			XPath xPath =  XPathFactory.newInstance().newXPath();
			NamespaceContext ctx = new NamespaceContext() {
			    @Override
				public String getNamespaceURI(String prefix) {
			        return 
			        	prefix.equals("cda") ? "urn:hl7-org:v3" : 
			        	prefix.equals("xsi") ? "http://www.w3.org/2001/XMLSchema-instance" : 
			        	prefix.equals("qdm") ? "urn:hhs-qdm:hqmf-r2-extensions:v1" : null; 
			    }
			    @Override
				@SuppressWarnings("rawtypes")
				public Iterator getPrefixes(String val) {
			        return null;
			    }
			    @Override
				public String getPrefix(String uri) {
			        return null;
			    }
			};
			xPath.setNamespaceContext(ctx);
			return xPath;
		}
	};
	
	public static String eval(String expression, Object obj) {
		try {
			return xPath.get().evaluate(expression, obj);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String evalOrNull(String expression, Object obj) {
		String result;
		try {
			result = xPath.get().evaluate(expression, obj);
			return "".equals(result) ? null : result;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Node evalToNode(String expression, Object obj) {
		try {
			return (Node) xPath.get().evaluate(expression, obj, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static NodeList evalToNodeList(String expression, Object obj) {
		try {
			return (NodeList) xPath.get().evaluate(expression, obj, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean evalToBoolean(String expression, Object obj) {
		try {
			return (boolean) xPath.get().evaluate(expression, obj, XPathConstants.BOOLEAN);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

}
