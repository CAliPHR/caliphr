package com.ainq.caliphr.website.service.provider.impl;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ainq.caliphr.common.model.provider.Organization;
import com.ainq.caliphr.common.model.provider.Practice;
import com.ainq.caliphr.common.model.provider.Provider;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.service.provider.ProviderService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    static Logger logger = (Logger) LoggerFactory.getLogger(ProviderServiceImpl.class);

    @Autowired
    private Environment environment;

    @SuppressWarnings("unchecked")
	@Override
    public List<Provider> getAllProviders(Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.PROVIDERS_ALL, params, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Practice> getAllPracticeGroups(Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.PRACTICES_ALL, params, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Organization> getAllOrganizations(Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.ORGANIZATIONS_ALL, params, ArrayList.class);
    }
}
