package com.ainq.caliphr.hqmf.controller.api.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.web.bind.annotation.*;

import com.ainq.caliphr.common.model.provider.Practice;
import com.ainq.caliphr.common.model.provider.Provider;
import com.ainq.caliphr.hqmf.model.projection.provider.OrganizationDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.service.ProviderService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mmelusky on 8/6/2015.
 */
@RestController
public class ProviderRestController {

    @Autowired
    private ProjectionFactory projectionFactory;

    @Autowired
    private ProviderService providerService;

    // Provider
    @RequestMapping(value = "/api/provider/providers/all", method = RequestMethod.POST)
    public List<Provider> getAllProviders(@RequestParam(value="userId", required = false) Integer userId) {

        // Need to add group ID to result set
        List<Provider> providers = new ArrayList<>();
        for (com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider provider : providerService.getAllProviders(userId)) {
            Provider p = new Provider();
            p.setId(provider.getId());
            p.setNpi(provider.getNpi());
            p.setFirstName(provider.getFirstName());
            p.setLastName(provider.getLastName());
            p.setMiddleName(provider.getMiddleName());

            //
            // Full Name Logic
            if (provider.getLastName() != null && !provider.getLastName().isEmpty() && provider.getFirstName() != null && !provider.getFirstName().isEmpty()) {
                p.setFullName(provider.getFirstName() + " " + provider.getLastName());
            } else if (provider.getLastName() != null && !provider.getLastName().isEmpty()) {
                p.setFullName(provider.getLastName());
            } else if (provider.getFullName() != null && !provider.getFullName().isEmpty()) {
                p.setFullName(provider.getFullName());
            } else {
                p.setFullName(StringUtils.EMPTY);
            }

            /* Add on provider group */
            if (provider.getGroup() != null && provider.getGroup().getId() != null) {
                p.setGroupId(provider.getGroup().getId());
            }

            providers.add(p);
        }
        return providers;
    }

    // Practice
    @RequestMapping(value = "/api/provider/practices/all", method = RequestMethod.POST)
    public List<Practice> getAllPracticeGroups(@RequestParam(value="userId", required = false) Integer userId) {

        List<Practice> practices = new ArrayList<>();
        for (PracticeGroup group : providerService.getAllPracticeGroups(userId)) {
            Practice p = new Practice();
            p.setId(group.getId());
            p.setGroupName(group.getGroupName());

            /* Add organization to practice */
            if (group.getOrganization() != null) {
                p.setOrganizationId(group.getOrganization().getId());
            }

            practices.add(p);
        }
        return practices;
    }

    // Organization
    @RequestMapping(value = "/api/provider/organizations/all", method = RequestMethod.POST)
    public List<OrganizationDetails> getAllOrganizations(@RequestParam(value="userId", required = false) Integer userId) {
        return providerService.getAllOrganizations(userId).stream()
                .map(organization -> projectionFactory.createProjection(OrganizationDetails.class, organization))
                .collect(Collectors.toList());
    }
}
