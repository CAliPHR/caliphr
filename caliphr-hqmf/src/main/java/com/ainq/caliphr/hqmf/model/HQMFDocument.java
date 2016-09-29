package com.ainq.caliphr.hqmf.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ainq.caliphr.hqmf.model.type.HQMFAttribute;
import com.ainq.caliphr.hqmf.model.type.HQMFEffectiveTime;

public class HQMFDocument {

	private String id;
	private String title;
	private String description;
	private String hqmfId;
	private String hqmfSetId;
	private String hqmfVersionNumber;
	private String cmsId;
	private HQMFEffectiveTime measurePeriod;
	private List<HQMFAttribute> attributes = new ArrayList<HQMFAttribute>();
	private List<PopulationCriteria> populationCriteria = new ArrayList<PopulationCriteria>();
	private List<Map<String, String>> populations = new ArrayList<Map<String, String>>();
	private Map<String, DataCriteria> dataCriteria = new LinkedHashMap<String, DataCriteria>();
	private Map<String, DataCriteria> sourceDataCriteria; 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHqmfId() {
		return hqmfId;
	}

	public void setHqmfId(String hqmfId) {
		this.hqmfId = hqmfId;
	}

	public String getHqmfSetId() {
		return hqmfSetId;
	}

	public void setHqmfSetId(String hqmfSetId) {
		this.hqmfSetId = hqmfSetId;
	}

	public String getHqmfVersionNumber() {
		return hqmfVersionNumber;
	}

	public void setHqmfVersionNumber(String hqmfVersionNumber) {
		this.hqmfVersionNumber = hqmfVersionNumber;
	}

	public List<PopulationCriteria> getPopulationCriteria() {
		return populationCriteria;
	}

	public void setPopulationCriteria(List<PopulationCriteria> populationCriteria) {
		this.populationCriteria = populationCriteria;
	}

	public List<Map<String, String>> getPopulations() {
		return populations;
	}

	public void setPopulations(List<Map<String, String>> populations) {
		this.populations = populations;
	}

	public Map<String, DataCriteria> getDataCriteria() {
		return dataCriteria;
	}

	public void setDataCriteria(Map<String, DataCriteria> dataCriteria) {
		this.dataCriteria = dataCriteria;
	}
	
	public void addDataCriteria(DataCriteria dataCrit) {
		dataCriteria.put(dataCrit.getId(), dataCrit);
	}

	public HQMFEffectiveTime getMeasurePeriod() {
		return measurePeriod;
	}

	public void setMeasurePeriod(HQMFEffectiveTime measurePeriod) {
		this.measurePeriod = measurePeriod;
	}

	public List<HQMFAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<HQMFAttribute> attributes) {
		this.attributes = attributes;
	}

	public String getCmsId() {
		return cmsId;
	}

	public void setCmsId(String cmsId) {
		this.cmsId = cmsId;
	}

	public Map<String, DataCriteria> getSourceDataCriteria() {
		return sourceDataCriteria;
	}

	public void setSourceDataCriteria(Map<String, DataCriteria> sourceDataCriteria) {
		this.sourceDataCriteria = sourceDataCriteria;
	}
	
	public void addSourceDataCriteria(DataCriteria sourceDataCrit) {
		if (sourceDataCriteria == null) {
			sourceDataCriteria = new LinkedHashMap<String, DataCriteria>();
		}
		sourceDataCriteria.put(sourceDataCrit.getId(), sourceDataCrit);
	}
}