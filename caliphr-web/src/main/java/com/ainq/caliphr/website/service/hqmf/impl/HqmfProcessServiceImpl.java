package com.ainq.caliphr.website.service.hqmf.impl;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.service.hqmf.HqmfProcessService;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Service
public class HqmfProcessServiceImpl implements HqmfProcessService {

    static Logger logger = (Logger) LoggerFactory.getLogger(HqmfProcessServiceImpl.class);

    @Autowired
    private Environment environment;

    @Override
    public JsonResponse processHqmf(Integer providerId, String startDate, String endDate, Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.add("providerId", providerId);
        params.add("startDate", startDate);
        params.add("endDate", endDate);
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT) + Constants.ApiUri.PROCESS_HQMF, params, JsonResponse.class);
    }
}
