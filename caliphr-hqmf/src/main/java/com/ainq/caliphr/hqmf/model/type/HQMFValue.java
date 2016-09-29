package com.ainq.caliphr.hqmf.model.type;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.w3c.dom.Node;

import com.ainq.caliphr.hqmf.util.XPathUtil;

public class HQMFValue {

	private static FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmm");
	
	private String type;
	private String unit;
	private String value;
	private boolean inclusive;
	private boolean derived;
	
	public HQMFValue() {
		
	}
	
	public HQMFValue(Node node) {
		this(node, "PQ", false);
	}
	
	public HQMFValue(Node node, String defaultType, boolean forceInclusive) {
		type = StringUtils.defaultIfEmpty(XPathUtil.evalOrNull("./@xsi:type", node), defaultType);
		
		unit = XPathUtil.evalOrNull("./@unit", node);
		value = XPathUtil.evalOrNull("./@value", node);
		
		boolean inclusiveVal = XPathUtil.evalToBoolean("../@" + node.getNodeName() + "Closed", node);
		inclusive = inclusiveVal || forceInclusive;
		
		setDerived("DER".equals(XPathUtil.eval("./@nullFlavor", node)));
	}
	
	public Date asDate() {
		try {
			return DATE_FORMATTER.parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isInclusive() {
		return inclusive;
	}

	public void setInclusive(boolean inclusive) {
		this.inclusive = inclusive;
	}

	public boolean isDerived() {
		return derived;
	}

	public void setDerived(boolean derived) {
		this.derived = derived;
	}
}
