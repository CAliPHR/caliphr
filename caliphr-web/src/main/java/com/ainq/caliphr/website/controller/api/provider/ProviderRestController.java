package com.ainq.caliphr.website.controller.api.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ainq.caliphr.common.model.provider.Organization;
import com.ainq.caliphr.common.model.provider.Practice;
import com.ainq.caliphr.common.model.provider.Provider;
import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.service.provider.ProviderService;
import com.ainq.caliphr.website.utility.SecurityHelper;

import java.util.List;

/**
 * Created by mmelusky on 8/6/2015.
 */
@RestController
public class ProviderRestController {

    @Autowired
    private ProviderService providerService;

    private Integer userId;

    // Provider
    @RequestMapping(value = "/api/provider/providers/all", method = RequestMethod.POST)
    public List<Provider> getAllProviders() {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return providerService.getAllProviders(userId);
    }

    // Practice
    @RequestMapping(value = "/api/provider/practices/all", method = RequestMethod.POST)
    public List<Practice> getAllPracticeGroups() {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return providerService.getAllPracticeGroups(userId);
    }

    // Organization
    @RequestMapping(value = "/api/provider/organizations/all", method = RequestMethod.POST)
    public List<Organization> getAllOrganizations() {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return providerService.getAllOrganizations(userId);
    }

}
