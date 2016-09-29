package com.ainq.caliphr.persistence.transformation.cda.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hl7.v3.AD;
import org.hl7.v3.II;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040AssignedEntity;
import org.hl7.v3.POCDMT000040Performer1;
import org.hl7.v3.POCDMT000040Performer2;
import org.hl7.v3.POCDMT000040Person;
import org.hl7.v3.TEL;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ProviderDao;
import com.ainq.caliphr.persistence.model.ccda.PersonAddress;
import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ProviderPhoneNumber;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.State;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.StateRepository;
import com.ainq.caliphr.persistence.transformation.cda.ProviderImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.cda.TemplateIdRoot;
import com.ainq.caliphr.persistence.util.predicate.patient.StatePredicate;
import com.google.common.util.concurrent.Striped;

/**
 * Created by mmelusky on 5/17/2015.
 */
@Component
public class ProviderImporterImpl extends SectionImporter implements ProviderImporter {

    // Constants
    static Logger logger = (Logger) LoggerFactory.getLogger(ProviderImporterImpl.class);

    // Instance Data
    
    @Autowired
	private ApplicationContext appCxt;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment environment;

    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private ProviderDao providerDao;
    
    private final Integer MAX_NPI_LENGTH = 15;
    
    private final static Striped<Lock> providerLock = Striped.lock(60);
    
    // get a spring bean so method local calls would have AOP applied
    private ProviderImporter selfProxy;
    
    @PostConstruct
    private void init() {
    	selfProxy = appCxt.getBean(ProviderImporter.class);
    }

    @Override
	public Provider findProviderByGroupAndNPI(Object performer, PracticeGroup practiceGroup) {

        //
        // Attempt to find the provider using NPI
        String foundNpi = null;
        POCDMT000040AssignedEntity assignedEntity = getAssignedEntity(performer);
        if (assignedEntity != null && assignedEntity.getId() != null) {
            for (II ii : assignedEntity.getId()) {
                if (ii.getRoot() != null && ii.getExtension() != null && TemplateIdRoot.NPI.getRoot().equals(ii.getRoot())) {
                    foundNpi = ii.getExtension();
                    break;
                }
            }
        }
        
        if (foundNpi != null && foundNpi.length() > MAX_NPI_LENGTH) {

            //
            //  Invalid NPI.  Example is a provider sending GUID values instead of 10 digit NPIs in this field.
            //
            //  Search by fname and lname instead.
            PersonName personName = getPersonNameFromAssignedEntity(assignedEntity);
            if (personName == null
                    || (personName != null && (personName.getLastName() == null || personName.getLastName().isEmpty() || personName.getFirstName() == null || personName.getFirstName().isEmpty()))) {
                // throw new IllegalStateException("Empty Provider Name with Invalid Provider NPI found");
                return null; // null provider for the encounter
            }
            return providerDao.findProviderByNameAndGroup(personName, practiceGroup, null);
        }

        // Remove any digits from potentially valid NPI records.
        foundNpi = foundNpi.replaceAll("\\D+", "").trim();
        if (foundNpi == null || foundNpi.length() == 0) {
            if (Boolean.parseBoolean(environment.getProperty(Constants.PropertyKey.ALLOW_PROVIDER_WITH_EMPTY_NPI))) {
                return null;
            } else {
                throw new IllegalStateException("Cannot import providers from clinical document without NPI.");
            }
        }
        if (foundNpi != null && foundNpi.length() > 0) {
            return providerDao.findProviderByNPIAndGroup(foundNpi, practiceGroup);
        }
        
        return null;
    }

    @Override
	public Provider loadProviderInfo(Object performer, PracticeGroup practiceGroup) {
    	
    	Provider provider = null;

        // Provider Details
        POCDMT000040AssignedEntity assignedEntity = getAssignedEntity(performer);
        if (assignedEntity != null) {

            // NPI
            String foundNpi = null;
            if (assignedEntity.getId() != null) {
                for (II ii : assignedEntity.getId()) {
                    if (ii.getRoot() != null && ii.getExtension() != null && TemplateIdRoot.NPI.getRoot().equals(ii.getRoot())) {
                        foundNpi = ii.getExtension();
                        break;
                    }
                }
            }
            if (foundNpi == null || foundNpi.length() == 0) {
                if (Boolean.parseBoolean(environment.getProperty(Constants.PropertyKey.ALLOW_PROVIDER_WITH_EMPTY_NPI))) {
                	// foundNpi = PROVIDER_NOT_FOUND;
                    // return null;    // Provider needs NPI record at minimum...
                    //
                    //
                    foundNpi = null;
                } else {
                    throw new IllegalStateException("Cannot import providers from clinical document without NPI.");
                }
            }
            
            if (foundNpi == null || (foundNpi != null && foundNpi.length() > MAX_NPI_LENGTH)) {
                //
                //  No NPI...  (found using name search)
                PersonName personName = getPersonNameFromAssignedEntity(assignedEntity);
                Boolean hasFirst = (personName != null && personName.getFirstName() != null && (!personName.getFirstName().isEmpty()));
                Boolean hasLast = (personName != null && personName.getLastName() != null && (!personName.getLastName().isEmpty()));
                Boolean hasFull = (personName != null && personName.getFullName() != null && (!personName.getFullName().isEmpty()));
                if (! (hasFirst || hasLast || hasFull) )
                {
                    return null;
                }
                Lock lock = providerLock.get(personName.getLastName() + "-" + personName.getFirstName());
                lock.lock();

                try {
                    provider = selfProxy.createOrUpdateProviderFromAssignedEntity(assignedEntity, practiceGroup, null);
                    provider = entityManager.merge(provider);
                } finally {
                    lock.unlock();
                }
            } else {
                //
                // sanitize the NPI, removing non-digits
                foundNpi = foundNpi.replaceAll("\\D+", "").trim();

	            // lock on the npi so other threads could not potentially insert a duplicate
	            Lock lock = providerLock.get(foundNpi);
	        	lock.lock();
	        	
	        	try {
	        		provider = selfProxy.createOrUpdateProviderFromAssignedEntity(assignedEntity, practiceGroup, foundNpi);
	        		provider = entityManager.merge(provider);
	        	} finally {
	        		lock.unlock();
	        	}
            }
        	
        }

        return provider;
    }
    
    /**
     * This method creates or updates the provider in a new transaction, so commits occur right away 
     * and avoid deadlock with other threads.
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public Provider createOrUpdateProviderFromAssignedEntity(POCDMT000040AssignedEntity assignedEntity, PracticeGroup practiceGroup, String npi) {
    	
    	 // NOTE: this method assumes the caller has already locked to ensure other threads do not attempt to insert a duplicate
    	
    	 practiceGroup = entityManager.merge(practiceGroup);
    	 PersonName personName = getPersonNameFromAssignedEntity(assignedEntity);
         Provider provider = null;
         if (npi == null) {
             provider = providerDao.findProviderByNameAndGroup(personName, practiceGroup, npi);
         } else {
             provider = providerDao.findProviderByNPIAndGroup(npi, practiceGroup);
         }

         if (provider == null) {
         	provider = new Provider();
	            provider.setDateCreated(new Date());
	            provider.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
	            provider.setDateUpdated(new Date());
	            provider.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
         }
         
         // only update the address of the provider if it has not been set before
         if (provider.getAddress() != null && !provider.getAddress().trim().isEmpty()) {
        	 return provider;
         }
         
         if (npi != null && npi.length() > 0) {
             provider.setNpi(npi);
         }

         // Set the practice/group the provider is assigned to
         provider.setGroup(practiceGroup);

         // Name
         if ((personName != null)
         		//At least one of First name, last name, full name must be populated
         		&& ((personName.getFirstName() != null && !personName.getFirstName().isEmpty())
         				|| (personName.getLastName() != null && !personName.getLastName().isEmpty())
         				|| (personName.getFullName() != null && !personName.getFullName().isEmpty()))
         ) {

             // First name
             if (personName.getFirstName() != null 
             		&& !personName.getFirstName().isEmpty()
             ) {
                 provider.setFirstName(personName.getFirstName());
             }

             // Middle name
             if (personName.getMiddleName() != null
             		&& !personName.getMiddleName().isEmpty()
             ) {
                 provider.setMiddleName(personName.getMiddleName());
             }

             // Last name
             if ( personName.getLastName() != null
             		&& !personName.getLastName().isEmpty()
             ) {
                 provider.setLastName(personName.getLastName());
             }

             // Full name
             if ( personName.getFullName() != null 
             		&& ! personName.getFullName().isEmpty()
             ) {
                 provider.setFullName(personName.getFullName());
             } else if (personName.getFirstName() != null 
             				&& !personName.getFirstName().isEmpty()
             				&& personName.getLastName() != null 
             				&& !personName.getLastName().isEmpty()
             ) {
                 provider.setFullName(String.format("%s %s", personName.getFirstName(), personName.getLastName()));
             }
         }

         // Address
         if (assignedEntity.getAddr() != null) {
             for (AD addr : assignedEntity.getAddr()) {
                 PersonAddress personAddress = new PersonAddress();
                 if (addr.getContent() != null) {
                     personAddress = extractPersonAddress(addr.getContent());
                 }
                 if ((personAddress != null) 
                 		// All of stline1, city, state should be populated
                 		&& (personAddress.getAddress() != null && !personAddress.getAddress().isEmpty())
                 		&& (personAddress.getCity() != null && !personAddress.getCity().isEmpty())
                 		&& (personAddress.getState() != null && !personAddress.getState().isEmpty())
                 ) {

                     // address1 (not checking for empty or null address since already verified)
                     provider.setAddress(personAddress.getAddress());

                     // address2
                     if ( personAddress.getAddress2() != null 
                     		&& !personAddress.getAddress2().isEmpty()
                     ) {
                         provider.setAddress2(personAddress.getAddress2());
                     }

                     // City (not checking for empty or null city since already verified)
                     provider.setCity(personAddress.getCity());

                     // State (not checking for empty or null s since already verified)
                     provider.setStateValue(personAddress.getState());
                     State foundState = stateRepository.findOne(StatePredicate.searchByAbbreviation(personAddress.getState()));
                     if (foundState != null) {
                         provider.setState(foundState);
                     }

                     // zip
                     if (personAddress.getZipCode() != null 
                     		&& !personAddress.getZipCode().isEmpty()
                     ) {
                         provider.setZipcode(personAddress.getZipCode());
                     }

                     // Country
                     if (personAddress.getCountry() != null 
                     		&& !personAddress.getCountry().isEmpty()
                     ) {
                         provider.setCountry(personAddress.getCountry());
                     }
                 }
             }
         }
     	
         if (provider.getId() == null) {
	            provider = this.providerDao.saveProvider(provider);
	            provider = entityManager.merge(provider);
         }
         
         updateProviderPhone(assignedEntity, provider);
         
         return provider;
    }
    
    private void updateProviderPhone(POCDMT000040AssignedEntity assignedEntity, Provider provider) {
    	
        if (provider.getProviderPhoneNumbers() == null) {
            provider.setProviderPhoneNumbers(new HashSet<>());
        }
    	
        // Phone Number
        if (assignedEntity.getTelecom() != null) {
            for (TEL telcom : assignedEntity.getTelecom()) {
                if (telcom.getValue() != null && telcom.getUse() != null) {
                    for (String use : telcom.getUse()) {
                        Boolean existingPhone = false;
                        for (ProviderPhoneNumber currentProviderPhoneNumber : provider.getProviderPhoneNumbers()) {
                        	if (currentProviderPhoneNumber.getPhoneNumber().equalsIgnoreCase(telcom.getValue())
                        			&& currentProviderPhoneNumber.getPhoneNumberType().equalsIgnoreCase(use)
                        	) {
	                            existingPhone = true;
	                            break;
                        	}
                        }
                        
                        if (!existingPhone) {
                        	ProviderPhoneNumber providerPhoneNumber = new ProviderPhoneNumber();
                            providerPhoneNumber.setProvider(provider);
                            providerPhoneNumber.setPhoneNumber(telcom.getValue());
                            providerPhoneNumber.setPhoneNumberType(use);
                            providerPhoneNumber.setDateCreated(new Date());
                            providerPhoneNumber.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                            providerPhoneNumber.setDateUpdated(new Date());
                            providerPhoneNumber.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                            provider.getProviderPhoneNumbers().add(providerPhoneNumber);
                        }
                    }
                }
            }
        }
    }

    private POCDMT000040AssignedEntity getAssignedEntity(Object performer) {
        if (performer instanceof POCDMT000040Performer1) {
            POCDMT000040Performer1 performer1 = (POCDMT000040Performer1) performer;
            return performer1.getAssignedEntity();
        } else if (performer instanceof POCDMT000040Performer2) {
            POCDMT000040Performer2 performer2 = (POCDMT000040Performer2) performer;
            return performer2.getAssignedEntity();
        }
        return null;
    }

    private PersonName getPersonNameFromAssignedEntity(POCDMT000040AssignedEntity assignedEntity) {
        if (assignedEntity.getAssignedPerson() != null) {
            POCDMT000040Person assignedPerson = assignedEntity.getAssignedPerson();

            if (assignedPerson.getName() != null) {
                for (PN pn : assignedPerson.getName()) {
                    if (pn.getContent() != null) {
                        if (pn.getContent() != null) {
                            return extractPersonName(pn.getContent());
                        }
                    }
                }
            }
        }
        return null;
    }
    
}
