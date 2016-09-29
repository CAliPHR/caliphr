package com.ainq.caliphr.website.service.security.impl;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.request.PasswordResetRequest;
import com.ainq.caliphr.common.model.security.ApplicationUser;
import com.ainq.caliphr.common.model.security.ApplicationUserPasswordHistory;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.service.security.UserSecurityService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Service
public class UserSecurityServiceImpl implements UserSecurityService {

    static Logger logger = (Logger) LoggerFactory.getLogger(UserSecurityServiceImpl.class);

    @Autowired
    private Environment environment;

    @Override
    public ApplicationUser findUserByEmail(String emailAddress) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("emailAddress", emailAddress);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.SECURITY_USER_LOGIN, params, ApplicationUser.class);
    }

    @Override
    public JsonResponse sendPasswordResetLink(String emailAddress) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("emailAddress", emailAddress);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.SECURITY_USER_PASSWORD_RESET_REQUEST, params, JsonResponse.class);
    }

    @Override
    public ApplicationUser findUserByPasswordRequestToken(String token) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("token", token);
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.SECURITY_USER_PASSWORD_RESET_TOKEN, params, ApplicationUser.class);
    }

    @Override
    public JsonResponse resetUserPassword(PasswordResetRequest passwordResetRequest) {
        return new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.SECURITY_USER_PASSWORD_RESET_SUBMIT, passwordResetRequest, JsonResponse.class);
    }

    @Override
    public List<ApplicationUserPasswordHistory> findUserPasswordHistories(Integer userId) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("userId", userId);
        return Arrays.stream(new RestTemplate().postForObject(environment.getProperty(Constants.PropertyKey.API_ROOT)
                + Constants.ApiUri.SECURITY_USER_PASSWORD_HISTORY_CHECK, params, ApplicationUserPasswordHistory[].class)).collect(Collectors.toList());
    }
}
