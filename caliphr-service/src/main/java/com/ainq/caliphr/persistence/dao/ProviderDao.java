package com.ainq.caliphr.persistence.dao;

import java.util.List;

import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Organization;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;

/**
 * Created by mmelusky on 8/6/2015.
 */
public interface ProviderDao {

    List<Provider> getAllProviders(Integer userId);

    List<PracticeGroup> getAllPracticeGroups(Integer userId);

    List<Organization> getAllOrganizations(Integer userId);

    Boolean checkProviderUserRelationship(Integer providerId, Integer userId);

	Provider findProviderByNPIAndGroup(String npi, PracticeGroup practiceGroup);

    Provider createProviderFromHl7Message(String npi, String firstName, String lastName, PracticeGroup group);

    PracticeGroup findGroupById(Integer groupId);

    PracticeGroup findGroupBySenderOid(String senderOid);

	Provider saveProvider(Provider provider);
	
	Provider findProviderByNameAndGroup(PersonName personName, PracticeGroup practiceGroup, String npi);

}
