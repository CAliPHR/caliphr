package com.ainq.caliphr.hqmf.model.type;

import org.w3c.dom.Node;

public class HQMFEffectiveTime extends HQMFRange {

	public HQMFEffectiveTime() {
		super();
	}

	public HQMFEffectiveTime(Node node) {
		super(node, "IVL_TS");
	}

}
