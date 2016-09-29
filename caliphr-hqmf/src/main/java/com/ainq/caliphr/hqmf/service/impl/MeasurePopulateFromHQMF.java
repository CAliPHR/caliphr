package com.ainq.caliphr.hqmf.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ainq.caliphr.hqmf.model.DataCriteria;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.model.PopulationCriteria;
import com.ainq.caliphr.hqmf.model.type.HQMFAnyValue;
import com.ainq.caliphr.hqmf.model.type.HQMFAttribute;
import com.ainq.caliphr.hqmf.model.type.HQMFCoded;
import com.ainq.caliphr.hqmf.model.type.HQMFEffectiveTime;
import com.ainq.caliphr.hqmf.model.type.HQMFGenericValueContainer;
import com.ainq.caliphr.hqmf.model.type.HQMFIdentifier;
import com.ainq.caliphr.hqmf.model.type.HQMF_ED;
import com.ainq.caliphr.hqmf.util.XPathUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MeasurePopulateFromHQMF {

	public static void populate(HQMFDocument hqmfDoc, Resource resource) throws ParserConfigurationException, SAXException, IOException {
		
		Document xmlDoc = parseXmlDocument(resource);
		
		hqmfDoc.setHqmfId(XPathUtil.eval("cda:QualityMeasureDocument/cda:id/@extension", xmlDoc));
		hqmfDoc.setHqmfSetId(XPathUtil.eval("cda:QualityMeasureDocument/cda:setId/@extension", xmlDoc));
		hqmfDoc.setHqmfVersionNumber(XPathUtil.eval("cda:QualityMeasureDocument/cda:versionNumber/@value", xmlDoc));
		
		hqmfDoc.setTitle(XPathUtil.evalOrNull("cda:QualityMeasureDocument/cda:title/@value", xmlDoc));
		hqmfDoc.setDescription(XPathUtil.evalOrNull("cda:QualityMeasureDocument/cda:text/@value", xmlDoc));
		
		Node measurePeriodNode = XPathUtil.evalToNode("cda:QualityMeasureDocument/cda:controlVariable/cda:measurePeriod/cda:value", xmlDoc);
		hqmfDoc.setMeasurePeriod(new HQMFEffectiveTime(measurePeriodNode));
		
		// # Extract measure attributes
		NodeList attribNodes = XPathUtil.evalToNodeList("/cda:QualityMeasureDocument/cda:subjectOf/cda:measureAttribute", xmlDoc);
		for (int i = 0; i < attribNodes.getLength(); i++) {
			Node attribNode = attribNodes.item(i);
			String id = XPathUtil.evalOrNull("./cda:id/@root", attribNode);
			String code = XPathUtil.evalOrNull("./cda:code/@code", attribNode);
			String name = XPathUtil.evalOrNull("./cda:code/cda:displayName/@value", attribNode);
			String value = XPathUtil.evalOrNull("./cda:value/@value", attribNode);
			
			HQMFIdentifier idObj = null;
			if (XPathUtil.evalOrNull("./cda:id", attribNode) != null) {
				idObj = new HQMFIdentifier(
					XPathUtil.evalOrNull("./cda:id/@xsi:type", attribNode), id, XPathUtil.evalOrNull("./cda:id/@extension", attribNode)
				);
			}
			
			HQMFCoded codeObj = null;
			if (XPathUtil.evalOrNull("./cda:code", attribNode) != null) {
				String nullFlavor = XPathUtil.evalOrNull("./cda:code/@nullFlavor", attribNode);
				String oText = XPathUtil.evalOrNull("./cda:code/cda:originalText/@value", attribNode);
				codeObj = new HQMFCoded(
					StringUtils.defaultString(XPathUtil.evalOrNull("./cda:code/@xsi:type", attribNode), "CD"),
					XPathUtil.evalOrNull("./cda:code/@codeSystem", attribNode),
					code,
					XPathUtil.evalOrNull("./cda:code/@valueSet", attribNode),
					name,
					nullFlavor,
					oText
				);
				
				// # Mapping for nil values to align with 1.0 parsing
				if (code == null) {
					code = nullFlavor;
				}
				
				if (name == null) {
					name = oText;
				}
			}
			
			Object valueObj = null;
			if (XPathUtil.evalToNode("./cda:value", attribNode) != null) {
				String type = XPathUtil.evalOrNull("./cda:value/@xsi:type", attribNode);
				switch(type) {
					case "II":
						valueObj = new HQMFIdentifier(
							type, 
							XPathUtil.evalOrNull("./cda:value/@root", attribNode), 
							XPathUtil.evalOrNull("./cda:value/@extension", attribNode)
						);
						if (value == null) {
							value = XPathUtil.evalOrNull("./cda:value/@extension", attribNode);
						}
						break;
					case "ED":
						valueObj = new HQMF_ED(type, value, XPathUtil.evalOrNull("./cda:value/@mediaType", attribNode));
						break;
					case "CD":
						valueObj = new HQMFCoded(
							"CD", 
							XPathUtil.evalOrNull("./cda:value/@codeSystem", attribNode),
							XPathUtil.evalOrNull("./cda:value/@code", attribNode),
							XPathUtil.evalOrNull("./cda:value/@valueSet", attribNode),
							XPathUtil.evalOrNull("./cda:value/cda:displayName/@value", attribNode),
							null,
							null
						);
						break;
					default:
						valueObj = !StringUtils.isBlank(value) ? new HQMFGenericValueContainer(type, value) : new HQMFAnyValue(type);
				}
			}
			
			// # Handle the cms_id
			if ("eMeasure Identifier".equals(name)) {
				hqmfDoc.setCmsId("CMS" + value + "v" + hqmfDoc.getHqmfVersionNumber());
			}

			hqmfDoc.getAttributes().add(new HQMFAttribute(id, code, value, null, name, idObj, codeObj, valueObj));
		}
		
		// set it up to execute in muliple theads, if needed
		
		//ExecutorService executor = Executors.newWorkStealingPool();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //ExecutorService executor = Executors.newFixedThreadPool(6);
		
		NodeList dataCritNodes = XPathUtil.evalToNodeList("cda:QualityMeasureDocument/cda:component/cda:dataCriteriaSection/cda:entry", xmlDoc);
		for (int i = 0; i < dataCritNodes.getLength(); i++) {
			Node dataCritNode = dataCritNodes.item(i);
			executor.execute(() -> {
				
				DataCriteria dataCrit = new DataCriteria(dataCritNode);
				if (dataCrit.isSourceDataDriteria()) {
					hqmfDoc.addSourceDataCriteria(dataCrit);
				} else {
					synchronized (hqmfDoc.getDataCriteria()) {
						hqmfDoc.addDataCriteria(dataCrit);
					}
				}
	        });
		}
		
		 executor.shutdown();
        try {
			executor.awaitTermination(1200, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		Map<String, String> idsByHqmfId = new HashMap<String, String>();
		Map<String, Integer> populationCounters = new HashMap<String, Integer>();
		
		NodeList popNodes = XPathUtil.evalToNodeList("cda:QualityMeasureDocument/cda:component/cda:populationCriteriaSection", xmlDoc);
		for (int i = 0; i < popNodes.getLength(); i++) {
			Node popDefNode = popNodes.item(i);
			Map<String, String> population = new HashMap<String, String>();
			
			String stratifierIdDef = XPathUtil.evalOrNull("cda:templateId/cda:item[@root=\"2.16.840.1.113883.3.100.1.2\"]/@controlInformationRoot", popDefNode);
			if (stratifierIdDef != null) {
				//System.out.println("stratifierIdDef="+ stratifierIdDef);
				population.put("stratification", stratifierIdDef);
			}
			
			PopulationCriteria.POPULATION_CRITERIA_MAP.forEach((critId, critElemName) -> {
				
				Node critDef = XPathUtil.evalToNode("cda:component[cda:" + critElemName + "]", popDefNode);
				if (critDef != null) {
					PopulationCriteria criteria = new PopulationCriteria(critDef);
					

		            // check to see if we have an identical population criteria.
		            // this can happen since the hqmf 2.0 will export a DENOM, NUMER, etc for each population, even if identical.
		            // if we have identical, just re-use it rather than creating DENOM_1, NUMER_1, etc.
					Gson gson = new GsonBuilder().create();
					String critJson = gson.toJson(criteria);
					List<PopulationCriteria> identical = hqmfDoc.getPopulationCriteria().stream()
						.filter(p -> gson.toJson(p).replaceAll(",\"id\":\".*?\"", "").equals(critJson))  // strip out the id attribute for comparison purposes
						.collect(Collectors.toList());
					
					if (identical.isEmpty()) {
						// -----------------------------------
						// this section constructs a human readable id.  The first IPP will be IPP, the second will be IPP_1, etc.  This allows the populations to be
			            // more readable.  The alternative would be to have the hqmf ids in the populations, which would work, but is difficult to read the populations.
			              
						String idName = criteria.getHqmfId() + "-" + population.get("stratification");
						if (idsByHqmfId.containsKey(idName)) {
							criteria.setId(idsByHqmfId.get(criteria.getHqmfId()));
						} else {
							if (populationCounters.containsKey(critId)) {
								int count = populationCounters.get(critId);
								count++;
								populationCounters.put(critId, count);
								criteria.setId(critId + "_" + count);
							} else {
								populationCounters.put(critId, 0);
								criteria.setId(critId);
							}
						}
						idsByHqmfId.put(idName, criteria.getId());
						
						hqmfDoc.getPopulationCriteria().add(criteria);
						population.put(critId, criteria.getId());
						
					} else {
						// if the population matched a previous one, just use the same id
						population.put(critId, identical.get(0).getId());
					}
				}
				
			});
			
			String idDefNode = XPathUtil.evalOrNull("cda:id/@extension", popDefNode);
			population.put("id", idDefNode != null ? idDefNode : "Population " + i);
			String titleDefNode = XPathUtil.evalOrNull("cda:title/@value", popDefNode);
			population.put("title", titleDefNode != null ? titleDefNode : "Population " + i);
			if (XPathUtil.evalToNode("cda:QualityMeasureDocument/cda:component/cda:measureObservationsSection", xmlDoc) != null) {
				population.put("OBSERV", "OBSERV");
			}
			
			hqmfDoc.getPopulations().add(population);
		}
		
		// #look for observation data in separate section but create a population for it if it exists
		Node obsSectionNode = XPathUtil.evalToNode("cda:QualityMeasureDocument/cda:component/cda:measureObservationsSection", xmlDoc);
		if (obsSectionNode != null) {
			NodeList criteriaDefNodes = XPathUtil.evalToNodeList("cda:definition", obsSectionNode);
			for (int i = 0; i < criteriaDefNodes.getLength(); i++) {
				String critId = "OBSERV";
				Map<String, String> population = new HashMap<String, String>();
				PopulationCriteria criteria = new PopulationCriteria(criteriaDefNodes.item(i));
				criteria.setType("OBSERV");
				
				// # this section constructs a human readable id.  The first IPP will be IPP, the second will be IPP_1, etc.  This allows the populations to be
		        // # more readable.  The alternative would be to have the hqmf ids in the populations, which would work, but is difficult to read the populations.
				if (idsByHqmfId.containsKey(criteria.getHqmfId())) {
					criteria.setId(idsByHqmfId.get(criteria.getHqmfId()));
				} else {
					if (populationCounters.containsKey(critId)) {
						int count = populationCounters.get(critId);
						count++;
						populationCounters.put(critId, count);
						criteria.setId(critId + "_" + count);
					} else {
						populationCounters.put(critId, 0);
						criteria.setId(critId);
					}
				}
				idsByHqmfId.put(criteria.getHqmfId(), criteria.getId());
				
				hqmfDoc.getPopulationCriteria().add(criteria);
				population.put(critId, criteria.getId());
				
				hqmfDoc.getPopulations().add(population);
			}
		}
		
	}

	private static Document parseXmlDocument(Resource resource) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		
		StringBuilder doc = new StringBuilder();
		
		// before running through the XML parser, pre-process the XML to replace newlines in multi-line attributes with explicit \n characters
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
		String line=null;
		boolean replaceLineEndings = false;
        while( (line = bufferedReader.readLine()) != null ){
        	doc.append(line);
        	
        	// toggle whether line endings should be replaced when there is an odd number of quote characters on the line
        	if (StringUtils.countMatches(line, "\"") % 2 == 1) {
        		replaceLineEndings = !replaceLineEndings;
        	}
        	if(replaceLineEndings) {
        		doc.append("\\n");
        	}
		}
        bufferedReader.close();
		
		//System.out.println(doc);
		return builder.parse(new ByteArrayInputStream(doc.toString().getBytes(StandardCharsets.UTF_8)));
	}
	
}
