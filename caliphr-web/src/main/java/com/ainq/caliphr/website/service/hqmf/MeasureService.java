package com.ainq.caliphr.website.service.hqmf;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.common.model.result.Measure;
import java.util.List;
import java.util.Map;

/**
 * Created by mmelusky on 8/25/2015.
 */
public interface MeasureService {
    List<Measure> getAllActiveMeasures(Integer providerId, Integer userId);

    Map<String,String> getMeasureAttributes(Integer hqmfDocId, Integer userId);

    List<Patient> getPatientResult(Integer resultId, Integer userId);
}
