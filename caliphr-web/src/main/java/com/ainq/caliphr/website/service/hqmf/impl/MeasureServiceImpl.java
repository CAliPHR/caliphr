package com.ainq.caliphr.website.service.hqmf.impl;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.common.model.result.Measure;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.controller.api.hqmf.MeasureRestController;
import com.ainq.caliphr.website.service.hqmf.MeasureService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Service
public class MeasureServiceImpl implements MeasureService {

    static Logger logger = (Logger) LoggerFactory.getLogger(MeasureRestController.class);

    @Autowired
    private Environment environment;

    @SuppressWarnings("unchecked")
	@Override
    public List<Measure> getAllActiveMeasures(Integer providerId, Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("providerId", providerId);
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.MEASURES_ACTIVE_FOR_PROVIDER, params, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Map<String, String> getMeasureAttributes(Integer hqmfDocId, Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("hqmfDocId", hqmfDocId);
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.MEASURE_ATTRIBUTES_FOR_DOCUMENT, params, HashMap.class);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Patient> getPatientResult(Integer resultId, Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("resultId", resultId);
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.PATIENT_RESULT_INFO, params, ArrayList.class);
    }
}
