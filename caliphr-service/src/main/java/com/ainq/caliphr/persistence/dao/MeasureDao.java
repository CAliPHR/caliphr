package com.ainq.caliphr.persistence.dao;

import java.util.List;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

public interface MeasureDao {
	
	void saveResult(Long hqmfDocId, String populationSetKey, int index, Result result, Iterable<Integer> patientIdList, Integer userId);
	
	HqmfPopulationSet getPopulationSet(Long hqmfDocId, String populationSetKey, int index);

	void saveHqmfDocument(HqmfDocument hqmfDocument);
		
	HqmfDocument getActiveHqmfDocumentByCmsId(String cmsId, Integer providerId);

	void markActiveMeasuresInactive(Integer providerId, Integer userId);

	Iterable<HqmfDocument> getAllActiveHqmfDocuments(Integer providerId, Integer userId);

	List<Patient> getPatientByActiveResultPatientForResultId(Long resultId, Integer userId);

	Iterable<HqmfDocument> getHqmfDocumentsById(Iterable<Long> ids);

	Iterable<HqmfAttribute> getHqmfAttributesByHqmfDocId(Long hqmfDocId);

	List<Bundle> getActiveBundles();

	Bundle getBundleById(Integer bundleId);

	List<PracticeAvailableMeasure> getAvailableMeasuresForProvider(Integer providerId, Integer bundleId);
}
