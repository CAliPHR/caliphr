package com.ainq.caliphr.hqmf.service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.service.impl.GeneratePopulationSqlStatements.PopulationGenerationContext;

// @Service
public interface HQMFService {

	HQMFDocument parseHQMF(Resource file) throws ParserConfigurationException, SAXException, IOException;

	Map<String, PopulationGenerationContext> generateSQL(HQMFDocument hqmfDoc, Integer userId, int bundleId);
	
	void calculateMeasures(Integer providerId, Date reportingPeriodStart, Date reportingPeriodEnd, Integer userId);

}
