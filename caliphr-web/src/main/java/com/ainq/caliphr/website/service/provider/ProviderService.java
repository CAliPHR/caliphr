package com.ainq.caliphr.website.service.provider;

import com.ainq.caliphr.common.model.provider.Organization;
import com.ainq.caliphr.common.model.provider.Practice;
import com.ainq.caliphr.common.model.provider.Provider;
import java.util.List;

/**
 * Created by mmelusky on 8/25/2015.
 */
public interface ProviderService {
    List<Provider> getAllProviders(Integer userId);
    List<Practice> getAllPracticeGroups(Integer userId);
    List<Organization> getAllOrganizations(Integer userId);
}
