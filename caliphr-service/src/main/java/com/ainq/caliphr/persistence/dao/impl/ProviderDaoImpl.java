package com.ainq.caliphr.persistence.dao.impl;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUserSecurity.applicationUserSecurity;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QOrganization.organization;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPracticeGroup.practiceGroup;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QProvider.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ProviderDao;
import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserSecurity;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Organization;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.PracticeGroupRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ProviderRepository;
import com.ainq.caliphr.persistence.util.predicate.provider.ProviderPredicate;
import com.mysema.query.jpa.impl.JPAQuery;

import ch.qos.logback.classic.Logger;

/**
 * Created by mmelusky on 8/6/2015.
 */

//  uncomment sections for group/org level security if ever needed in the future

@Repository
public class ProviderDaoImpl implements ProviderDao {

    static Logger logger = (Logger) LoggerFactory.getLogger(ProviderDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private PracticeGroupRepository practiceGroupRepository;

    @Autowired
    private ProviderRepository providerRepository;

    // create a cache that limits entries to 10,000 providers.  If needed, the least recently used (LRU) entry is removed
    @Value("#{cacheManager.getCache('com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider.customCache')}")
	private Cache providerByNpiCache;
    
    @Override
    @Transactional
    public List<Provider> getAllProviders(Integer userId) {

        if (userId != null && userId > 0) {
            List<Provider> returnList = new ArrayList<>();
            Set<Integer> recordIdList = new HashSet<>();

            // Provider Level Security
            List<Provider> providerSecurity = new JPAQuery(entityManager).from(provider)
                    .innerJoin(provider.applicationUserSecurities, applicationUserSecurity)
                    .where(applicationUserSecurity.user.id.eq(userId)
                            , provider.lastName.isNotEmpty()
                            , applicationUserSecurity.dateDisabled.isNull())
                    .list(provider);
            providerSecurity.stream().filter(record -> !recordIdList.contains(record.getId())).forEach(record -> {
                returnList.add(record);
                recordIdList.add(record.getId());
            });

            Collections.sort(returnList, (Provider o1, Provider o2) -> o1.getLastName().compareTo(o2.getLastName()));
            return returnList;
        } else {
            return new JPAQuery(entityManager).from(provider).where(provider.lastName.isNotEmpty())
                    .orderBy(provider.lastName.asc()).list(provider);
        }
    }

    @Override
    @Transactional
    public List<PracticeGroup> getAllPracticeGroups(Integer userId) {
        if (userId != null && userId > 0) {
            List<PracticeGroup> returnList = new ArrayList<>();
            Set<Integer> recordIdList = new HashSet<>();

            // Provider Level Security
            List<PracticeGroup> providerSecurity = new JPAQuery(entityManager).from(practiceGroup)
                    .innerJoin(practiceGroup.providers, provider)
                    .innerJoin(provider.applicationUserSecurities, applicationUserSecurity)
                    .where(applicationUserSecurity.user.id.eq(userId)
                            , practiceGroup.groupName.isNotEmpty()
                            , applicationUserSecurity.dateDisabled.isNull())
                    .list(practiceGroup);
            providerSecurity.stream().filter(record -> !recordIdList.contains(record.getId())).forEach(record -> {
                returnList.add(record);
                recordIdList.add(record.getId());
            });

            Collections.sort(returnList, (PracticeGroup o1, PracticeGroup o2) -> o1.getGroupName().compareTo(o2.getGroupName()));
            return returnList;
        } else {
            return new JPAQuery(entityManager).from(practiceGroup).where(practiceGroup.groupName.isNotEmpty())
                    .orderBy(practiceGroup.groupName.asc()).list(practiceGroup);
        }
    }

    @Override
    @Transactional
    public List<Organization> getAllOrganizations(Integer userId) {
        if (userId != null && userId > 0) {
            List<Organization> returnList = new ArrayList<>();
            Set<Integer> recordIdList = new HashSet<>();

            // Provider Level Security
            List<Organization> providerSecurity = new JPAQuery(entityManager).from(organization)
                    .innerJoin(organization.practiceGroups, practiceGroup)
                    .innerJoin(practiceGroup.providers, provider)
                    .innerJoin(provider.applicationUserSecurities, applicationUserSecurity)
                    .where(applicationUserSecurity.user.id.eq(userId)
                            , organization.organizationName.isNotEmpty()
                            , applicationUserSecurity.dateDisabled.isNull())
                    .list(organization);
            providerSecurity.stream().filter(record -> !recordIdList.contains(record.getId())).forEach(record -> {
                returnList.add(record);
                recordIdList.add(record.getId());
            });

            Collections.sort(returnList, (Organization o1, Organization o2) -> o1.getOrganizationName().compareTo(o2.getOrganizationName()));
            return returnList;
        } else {
            return new JPAQuery(entityManager).from(organization).where(organization.organizationName.isNotEmpty())
                    .orderBy(organization.organizationName.asc()).list(organization);
        }
    }

    @Override
    @Transactional
    public Boolean checkProviderUserRelationship(Integer providerId, Integer userId) {
        List<ApplicationUserSecurity> applicationUserSecurities = new JPAQuery(entityManager).from(applicationUserSecurity)
                .where(applicationUserSecurity.user.id.eq(userId), applicationUserSecurity.dateDisabled.isNull()).list(applicationUserSecurity);
        for (ApplicationUserSecurity applicationUserSecurity : applicationUserSecurities) {
            if (applicationUserSecurity.getProvider() != null && applicationUserSecurity.getProvider().getId() != null && applicationUserSecurity.getProvider().getId().equals(providerId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Provider findProviderByNPIAndGroup(String npi, PracticeGroup practiceGroup) {
    	String key = npi + "_" + practiceGroup.getId();
    	Integer providerId = providerByNpiCache.get(key, Integer.class);
    	if (providerId != null) {
            return providerRepository.getOne(providerId);
        }

        Provider provider = providerRepository.findOne(ProviderPredicate.searchByNpiAndGroup(npi, practiceGroup));
        if (provider != null) {
        	providerByNpiCache.put(key, provider.getId());
        }
        return provider;
    }

    @Override
    public Provider createProviderFromHl7Message(String npi, String firstName, String lastName, PracticeGroup group) {
        Provider provider = new Provider();
        provider.setGroup(group);
        provider.setFirstName(firstName);
        provider.setLastName(lastName);
        provider.setNpi(npi);
        provider.setDateCreated(new Date());
        provider.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        provider.setDateUpdated(new Date());
        provider.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        return providerRepository.save(provider);
    }

    @Override
    @Transactional
    public PracticeGroup findGroupById(Integer groupId) {
        return practiceGroupRepository.findOne(groupId);
    }

 
    @Override
    @Transactional
    public PracticeGroup findGroupBySenderOid(String senderOid) {
        return new JPAQuery(entityManager).from(practiceGroup).where(practiceGroup.senderOid.equalsIgnoreCase(senderOid.trim())
                , practiceGroup.dateDisabled.isNull())
        		.setHint("org.hibernate.cacheable", "true")
                .setHint("org.hibernate.cacheRegion", "com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup.queryCache")
        		.singleResult(practiceGroup);
    }

     
    @Override
    @Transactional
    public Provider saveProvider(Provider provider) {

    	// NOTE: this method assumes the caller has already locked to ensure other threads do not attempt to insert a duplicate

        providerRepository.save(provider);

        return provider;
    }
    
    @Override
    @Transactional
    public Provider findProviderByNameAndGroup(PersonName personName, PracticeGroup practiceGroup, String npi) {
        String key = personName.getLastName() + "_" + personName.getFirstName() + "_" + practiceGroup.getId();
        Integer providerId = providerByNpiCache.get(key, Integer.class);
        if (providerId != null) {
            return providerRepository.getOne(providerId);
        }

        Provider provider = providerRepository.findOne(ProviderPredicate.searchByProviderNameAndGroup(personName, practiceGroup, npi));
        if (provider != null) {
            providerByNpiCache.put(key, provider.getId());
        }
        return provider;
    }

}
