package com.ainq.caliphr.persistence.service;

import java.util.Date;
import java.util.List;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;

/**
 * Created by mmelusky on 8/6/2015.
 */
public interface ProviderService {

    // Provider
    List<Provider> getAllProviders(Integer userId);

    // Practice
    List<PracticeGroup> getAllPracticeGroups(Integer userId);

    // Organization
    List<Organization> getAllOrganizations(Integer userId);

    // Group lookup using OID
    PracticeGroup findGroupBySenderOid(String senderOid);

    // Decrypt CCDA files (async)
    void decryptClinicalDocuments(String groupId, Date startDate, Date endDate);

    // Reprocess CCDA files (async)
    void reprocessClinicalDocuments(String groupId, Date startDate, Date endDate);

}
