package com.ainq.caliphr.hqmf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ainq.caliphr.hqmf.util.XPathUtil;

public class PopulationCriteria {
	
	public static final Map<String, String> POPULATION_CRITERIA_MAP = new HashMap<String, String>();
	static {
		POPULATION_CRITERIA_MAP.put("IPP", "initialPopulationCriteria");
		POPULATION_CRITERIA_MAP.put("DENOM", "denominatorCriteria");
		POPULATION_CRITERIA_MAP.put("NUMER", "numeratorCriteria");
		POPULATION_CRITERIA_MAP.put("DENEXCEP", "denominatorExceptionCriteria");
		POPULATION_CRITERIA_MAP.put("DENEX", "denominatorExclusionCriteria");
		POPULATION_CRITERIA_MAP.put("STRAT", "stratifierCriteria");
		POPULATION_CRITERIA_MAP.put("MSRPOPL", "measurePopulationCriteria");
	}
	
	private boolean conjunction = true;
	private String id;
	private String type;
	private String title;
	private String hqmfId;
	private String aggregator;
	private List<Precondition> preconditions = new ArrayList<Precondition>();
	
	public PopulationCriteria() {
		
	}
	
	public PopulationCriteria(Node node) {
		hqmfId = XPathUtil.evalOrNull("./*/cda:id/@extension", node);
		if (hqmfId == null) {
			hqmfId = XPathUtil.evalOrNull("./*/cda:typeId/@extension", node);
		}
		title = XPathUtil.evalOrNull("./*/cda:code/cda:displayName/@value", node);
		type  = XPathUtil.evalOrNull("./*/cda:code/@code", node);
		
		String obsTest = XPathUtil.eval("./cda:measureObservationDefinition/@classCode", node);
		if (title == null && "OBS".equals(obsTest)) {
			title = XPathUtil.evalOrNull("../cda:code/cda:displayName/@value", node);
			aggregator = XPathUtil.evalOrNull("./cda:measureObservationDefinition/cda:methodCode/cda:item/@code", node);
		}
		
		// The id extension is not required, if it's not provided use the code
		if (hqmfId == null) {
			hqmfId = type;
		}
		
		NodeList precondNodes = XPathUtil.evalToNodeList("./*/cda:precondition[not(@nullFlavor)]", node);
		for (int i = 0; i < precondNodes.getLength(); i++) {
			preconditions.add(new Precondition(precondNodes.item(i)));
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isConjunction() {
		return conjunction;
	}
	public void setConjunction(boolean conjunction) {
		this.conjunction = conjunction;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHqmfId() {
		return hqmfId;
	}
	public void setHqmfId(String hqmfId) {
		this.hqmfId = hqmfId;
	}
	public List<Precondition> getPreconditions() {
		return preconditions;
	}
	public void setPreconditions(List<Precondition> preconditions) {
		this.preconditions = preconditions;
	}

	public String getAggregator() {
		return aggregator;
	}

	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}

}
