package com.ainq.caliphr.website.service.qrda.impl;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ainq.caliphr.common.model.extract.QrdaExtractRequest;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.service.qrda.QrdaService;

import ch.qos.logback.classic.Logger;

/**
 * Created by mmelusky on 10/21/2015.
 */
@Service
public class QrdaServiceImpl implements QrdaService {

    static Logger logger = (Logger) LoggerFactory.getLogger(QrdaServiceImpl.class);

    @Autowired
    private Environment environment;

    @Override
    public byte[] processQrdaCat1Export(Iterable<Long> hqmfIds, Integer userId) {
        QrdaExtractRequest request = new QrdaExtractRequest();
        request.setUserId(userId);
        request.setHqmfIds(hqmfIds);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT) + Constants.ApiUri.QRDA_CAT1_EXPORT, request, byte[].class);
    }

    @Override
    public String processQrdaCat3Export(Iterable<Long> hqmfIds, Integer userId) {
        QrdaExtractRequest request = new QrdaExtractRequest();
        request.setUserId(userId);
        request.setHqmfIds(hqmfIds);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT) + Constants.ApiUri.QRDA_CAT3_EXPORT, request, String.class);
    }

	@Override
	public String processQrdaCat1Import(String filename, byte[] uploadedBytes, Integer userId) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        params.add("file", new ByteArrayResource(uploadedBytes) {
        	@Override
            public String getFilename() {
                return filename;
            }
        });
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(params);
        return new RestTemplate().exchange(environment.getProperty(Constants.PropertyKey.API_ROOT)
                   + Constants.ApiUri.QRDA_CAT1_IMPORT, HttpMethod.POST, requestEntity, String.class).getBody();
	}
}
