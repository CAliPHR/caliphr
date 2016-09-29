package com.ainq.caliphr.hqmf.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.service.impl.GeneratePopulationSqlStatements.PopulationGenerationContext;
import com.ainq.caliphr.hqmf.util.HQMFJsonUtil;
import com.ainq.caliphr.hqmf.util.MeasureMetadataUtil;
import com.ainq.caliphr.persistence.dao.SecureTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * This class can be used to verify measure calculations are correct when running against the Master Patient List.  It uses the
 * expected results contained within the bundle for comparison.
 * 
 * 
 * @author drosenbaum
 *
 */
@Component
public class VerifyMeasureCalculations {

	private static JsonArray resultsByMeasureJsonObj;
	private static JsonArray resultsByPatientJsonObj;
	
	public static StringBuilder outResults = new StringBuilder();
	public static volatile Set<String> passedList = new TreeSet<String>();
	public static volatile Set<String> failedList = new TreeSet<String>();
	public static volatile Set<String> uniquePassed = new TreeSet<String>();
	
	@Autowired
	private MeasureMetadataUtil measureMetadataUtil;
	
	@Autowired 
	private SecureTableDao secureTableDao;
	
	public void verify(HQMFDocument doc, Map<String, PopulationGenerationContext> genCtxs) {
		boolean allPassed = true;
		for (int i = 0; i < doc.getPopulations().size(); i++) {
			try { 
				Map<String, String> population = doc.getPopulations().get(i);
				String ippKey = population.get("IPP");
				String denomKey = population.get("DENOM");
				String denexKey = population.get("DENEX");
				String numerKey = population.get("NUMER");
				String denexcepKey = population.get("DENEXCEP");
				
				String stratId = population.get("STRAT");
				if (stratId != null) {
					ippKey += "_" + stratId;
					denomKey += "_" + stratId;
					denexKey += "_" + stratId;
					numerKey += "_" + stratId;
					denexcepKey += "_" + stratId;
				}
				PopulationGenerationContext ippGenCxt = genCtxs.get(ippKey);
				PopulationGenerationContext denomGenCxt = genCtxs.get(denomKey);
				PopulationGenerationContext denexGenCxt = genCtxs.get(denexKey);
				PopulationGenerationContext numerGenCxt = genCtxs.get(numerKey);
				PopulationGenerationContext denexcepGenCxt = genCtxs.get(denexcepKey);
				
				Character sub = doc.getPopulations().size() > 1 ? (char)('a' + i) : null;
				allPassed &= verify(doc, sub, ippGenCxt, denomGenCxt, denexGenCxt, numerGenCxt, denexcepGenCxt, secureTableDao);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (allPassed) {
			addUniquePassedMeasureCount(doc.getCmsId());
		}
	}
	
	private synchronized boolean verify(HQMFDocument hqmfDoc, Character subId, PopulationGenerationContext ippGenCxt, PopulationGenerationContext denomGenCxt, 
			PopulationGenerationContext denexGenCxt, PopulationGenerationContext numerGenCxt, PopulationGenerationContext denexcepGenCxt,
			SecureTableDao secureTableDao) throws IOException {
		String measureNum = hqmfDoc.getCmsId() + (subId != null ? subId : " ");
		out("\n ***** Results for " + measureNum + ":");
		boolean passed = true;
		JsonArray jsonArray = getResultsByMeasureJsonObj();
		for (JsonElement measure : jsonArray) {
			JsonObject jsonObj = measure.getAsJsonObject();
			if (jsonObj.get("measure_id").getAsString().equals(hqmfDoc.getHqmfId())) {
				if (subId == null || jsonObj.get("sub_id").getAsCharacter() == subId) {
					JsonObject jsonResult = jsonObj.get("result").getAsJsonObject();
					passed &= check(hqmfDoc, "IPP", ippGenCxt.getResults().size(), jsonResult);
					passed &= comparePatients(hqmfDoc, subId, "IPP", ippGenCxt, secureTableDao);
					
					passed &= check(hqmfDoc, "DENOM", denomGenCxt.getResults().size(), jsonResult);
					passed &= comparePatients(hqmfDoc, subId, "DENOM", denomGenCxt, secureTableDao);
					
					passed &= check(hqmfDoc, "NUMER", numerGenCxt.getResults().size(), jsonResult);
					passed &= comparePatients(hqmfDoc, subId, "NUMER", numerGenCxt, secureTableDao);
					
					passed &= check(hqmfDoc, "DENEX", denexGenCxt != null ? denexGenCxt.getResults().size() : null, jsonResult);
					if (denexGenCxt != null) {
						passed &= comparePatients(hqmfDoc, subId, "DENEX", denexGenCxt, secureTableDao);
					}
					
					passed &= check(hqmfDoc, "DENEXCEP", denexcepGenCxt != null ? denexcepGenCxt.getResults().size() : null, jsonResult);
					if (denexcepGenCxt != null) {
						passed &= comparePatients(hqmfDoc, subId, "DENEXCEP", denexcepGenCxt, secureTableDao);
					}
					//break; // just verify first result for now
				}
			}
		}
		
		
		
		if (passed) {
			passedList.add(measureNum);
		} else {
			failedList.add(measureNum);
		}
		
		return passed;
	}
	
	private boolean comparePatients(HQMFDocument hqmfDoc, Character subId, String populationName, 
			PopulationGenerationContext popCtx, SecureTableDao secureTableDao) throws IOException {
		
		Set<String> determined = new HashSet<String>(); 
		for (Map<String, Object> result : popCtx.getResults()) {
			Patient p = secureTableDao.findPatientBasicInfo(Arrays.asList((Integer)result.get("patient_id"))).get(0);
			determined.add(p.getFirstName() + " " + p.getLastName());
			out(String.format("%s - %s %s", p.getId(), p.getFirstName(), p.getLastName()));
		}
		
		boolean success = true;
		JsonArray jsonArray = getResultsByPatientJsonObj();
		for (JsonElement measure : jsonArray) {
			JsonObject jsonObj = measure.getAsJsonObject();
			JsonObject value = jsonObj.get("value").getAsJsonObject();
			if (value.get("measure_id").getAsString().equals(hqmfDoc.getHqmfId())) { 
				if (subId == null || value.get("sub_id").getAsCharacter() == subId) {
					int resultNum = value.get(populationName).getAsInt();
					if (resultNum > 0) {
						String name = value.get("first").getAsString() + " " + value.get("last").getAsString();
						if (determined.contains(name)) {
							//out("matched in " + populationName + ": " + name);
							determined.remove(name);
						} else {
							out("**** expected name not found in " + populationName + ": " + name);
							success = false;
						}
					}
				}
			}
		}
		for (String name : determined) {
			out("***** Unknown patient in " + populationName + ": " + name);
			success = false;
		}
		return success;
	}

	private boolean check(HQMFDocument hqmfDoc, String populationName, Integer actual, JsonObject jsonResult) {
		int expected = jsonResult.get(populationName).getAsInt();
		actual = actual != null ? actual : 0;
		if (actual != expected) {
			out("****** " + hqmfDoc.getCmsId() + ": " + populationName + " does not match - expected " + expected + 
					", actual " + actual);
			return false;
		} else {
			out(hqmfDoc.getCmsId() + ": " + populationName + " matched - expected " + expected);
			return true;
		}
	}
	
	private JsonArray getResultsByMeasureJsonObj() throws IOException {
		if (resultsByMeasureJsonObj == null) {
			synchronized (HQMFJsonUtil.class) {
				if (resultsByMeasureJsonObj == null) {
					JsonParser jsonParser = new JsonParser();
					JsonReader jsonReader = new JsonReader(new InputStreamReader(
							new	FileSystemResource(measureMetadataUtil.getActiveBundleRoot() + "/results/by_measure.json").getInputStream()));
					resultsByMeasureJsonObj = jsonParser.parse(jsonReader)
														.getAsJsonArray();
				}
			}
		}
		return resultsByMeasureJsonObj;
	}
	
	private JsonArray getResultsByPatientJsonObj() throws IOException {
		if (resultsByPatientJsonObj == null) {
			synchronized (HQMFJsonUtil.class) {
				if (resultsByPatientJsonObj == null) {
					JsonParser jsonParser = new JsonParser();
					JsonReader jsonReader = new JsonReader(new InputStreamReader(
							new	FileSystemResource(measureMetadataUtil.getActiveBundleRoot() + "/results/by_patient.json").getInputStream()));
					resultsByPatientJsonObj = jsonParser.parse(jsonReader)
														.getAsJsonArray();
				}
			}
		}
		return resultsByPatientJsonObj;
	}
	
	private static void out(String str) {
		System.out.println(str);
		outResults.append(str).append("\n");
	}
	
	public static synchronized void addUniquePassedMeasureCount(String measureNum) {
		if (failedList.stream().filter(m -> measureNum.equals(m) || measureNum.equals(m.substring(0, m.length() - 1))).count() == 0) {
			uniquePassed.add(measureNum);
		}
	}
	
	public static void outputResults() {
		System.out.println(outResults.toString());
		
		System.out.println(
				"\nTotal Passed: " + passedList.size() + 
				" Total Failed: " + failedList.size() +
				" (Total measures: " + (passedList.size() + failedList.size()) + 
				" Unique Passed: " + uniquePassed.size() + ")\n");
		System.out.format("Passed: %s\nFailed: %s\nUnique Passed: %s\n", 
				passedList,
				failedList,
				uniquePassed);
	}
	
	public static void reset() {
		outResults.setLength(0);
		passedList.clear();
		failedList.clear();
		uniquePassed.clear();
	}
}
