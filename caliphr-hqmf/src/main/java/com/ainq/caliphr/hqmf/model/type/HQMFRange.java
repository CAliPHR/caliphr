package com.ainq.caliphr.hqmf.model.type;

import org.w3c.dom.Node;

import com.ainq.caliphr.hqmf.util.HQMFUtils;

public class HQMFRange {
	
	private String type;
	private HQMFValue low;
	private HQMFValue high;
	private HQMFValue width;
	
	public HQMFRange() {
		
	}
	
	public HQMFRange(Node node) {
		this(node, null);
	}
	
	public HQMFRange(Node node, String type) {
		this.type = type;
		//String defaultElemName = defaultElementName();
		//XMLUtil.outputXmlForDebug(node);
		String defaultBoundsType = defaultBoundsType();
		//System.out.println("defaultElemName="+defaultElemName);
		//low = HQMFUtils.optionalValue(defaultElemName + "//*/cda:low", node, defaultBoundsType);
		//high = HQMFUtils.optionalValue(defaultElemName + "//*/cda:high", node, defaultBoundsType);
		//width = HQMFUtils.optionalValue(defaultElemName + "//*/cda:width", node, "PQ");
		low = HQMFUtils.optionalValue(".//cda:low", node, defaultBoundsType);
		high = HQMFUtils.optionalValue(".//cda:high", node, defaultBoundsType);
		width = HQMFUtils.optionalValue(".//cda:width", node, "PQ");
	}
	
//	private String defaultElementName() {
//		if ("IVL_PQ".equals(type)) {
//			return ".";
//		} else if ("IVL_TS".equals(type)) {
//			return "cda:phase";
//		}
//		return "cda:uncertainRange";
//	}
	
	private String defaultBoundsType() {
		return "IVL_TS".equals(type) ? "TS" : "PQ";
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HQMFValue getLow() {
		return low;
	}

	public void setLow(HQMFValue low) {
		this.low = low;
	}

	public HQMFValue getHigh() {
		return high;
	}

	public void setHigh(HQMFValue high) {
		this.high = high;
	}

	public HQMFValue getWidth() {
		return width;
	}

	public void setWidth(HQMFValue width) {
		this.width = width;
	}

}
