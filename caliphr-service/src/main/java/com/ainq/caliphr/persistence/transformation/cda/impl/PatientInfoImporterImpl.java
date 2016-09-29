package com.ainq.caliphr.persistence.transformation.cda.impl;

import ch.qos.logback.classic.Logger;
import lombok.val;

import org.apache.commons.lang3.StringUtils;
import org.hl7.v3.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.SecureTableDao;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.ccda.PersonAddress;
import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder.PatientPhoneNumberHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientInfo;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientPhoneNumber;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.State;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.StateRepository;
import com.ainq.caliphr.persistence.transformation.cda.PatientInfoImporter;
import com.ainq.caliphr.persistence.transformation.cda.SectionImporter;
import com.ainq.caliphr.persistence.transformation.cda.TemplateIdRoot;
import com.ainq.caliphr.persistence.util.predicate.patient.StatePredicate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mmelusky on 5/17/2015.
 */
@Component
public class PatientInfoImporterImpl extends SectionImporter implements PatientInfoImporter {

    // Constants
    static Logger logger = (Logger) LoggerFactory.getLogger(PatientInfoImporterImpl.class);

    // Instance Data

    @Autowired
    private SecureTableDao secureTableDao;

    @Autowired
    private StateRepository stateRepository;

    @Override
	public PatientInfoHolder findPatientBySSN(POCDMT000040PatientRole patientRole) {
        // Attempt to find the SSN (or return if existing record was found)
        if (patientRole.getId() != null) {
            for (II ii : patientRole.getId()) {
                if (ii.getRoot() != null && ii.getExtension() != null
                        && TemplateIdRoot.SSN.getRoot().equals(ii.getRoot())) {
                    String ssn = ii.getExtension().replaceAll("[^0-9]", StringUtils.EMPTY);
                    PatientInfoHolder foundPatient = secureTableDao.findPatientBySSN(ssn);
                    if (foundPatient != null) {
                        return foundPatient;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public PatientInfoHolder findPatientBySourceAndMRN(PracticeGroup practiceGroup, String medicalRecordNumber) {
        return secureTableDao.findPatientBySourceAndMRN(practiceGroup, medicalRecordNumber);
    }

    @Override
	public PatientInfoHolder loadPatientInfo(POCDMT000040PatientRole patientRole, PracticeGroup practiceGroup) {
        PatientInfo patientInfo = new PatientInfo();
        PatientInfoHolder patientInfoHolder = new PatientInfoHolder(patientInfo);
        if (patientInfo.getPatientPhoneNumbers() == null) {
            patientInfo.setPatientPhoneNumbers(new HashSet<>());
        }

        // Practice group
        patientInfo.setGroup(practiceGroup);

        // Medical Record Number and SSN
        if (patientRole.getId() != null) {
            patientRole.getId().stream().filter(ii -> ii.getRoot() != null && ii.getExtension() != null).forEach(ii -> {
                if (TemplateIdRoot.SSN.getRoot().equals(ii.getRoot())) {
                    //
                    //  SSN
                    String ssn = ii.getExtension().replaceAll("[^0-9]", StringUtils.EMPTY);
                    patientInfoHolder.setSsn(ssn);
                } else if (practiceGroup != null && practiceGroup.getSenderOid() != null
                        && ii.getRoot().equals(practiceGroup.getSenderOid())) {
                    //
                    //  MRN
                    patientInfoHolder.setMedicalRecordNumber(ii.getExtension().trim());
                }
            });
        }

        //
        // Empty MRN (fill in with UUID for testing purposes...  higher regions won't accept C-CDA data without source)
        if (practiceGroup != null && practiceGroup.getId() != null && practiceGroup.getId().equals(Constants.PracticeGroup.UNKNOWN_SENDING_GROUP_ID)) {
            patientInfoHolder.setMedicalRecordNumber(UUID.randomUUID().toString());
        }

        // Address
        if (patientRole.getAddr() != null) {
            for (AD addr : patientRole.getAddr()) {
                PersonAddress personAddress = new PersonAddress();
                if (addr.getContent() != null) {
                    personAddress = extractPersonAddress(addr.getContent());
                }
                if (personAddress != null) {
                    if (personAddress.getAddress() != null) {
                    	patientInfoHolder.setAddress(personAddress.getAddress());
                    }
                    if (personAddress.getAddress2() != null) {
                    	patientInfoHolder.setAddress2(personAddress.getAddress2());
                    }
                    if (personAddress.getCity() != null) {
                    	patientInfoHolder.setCity(personAddress.getCity());
                    }
                    if (personAddress.getState() != null) {
                    	patientInfoHolder.setStateValue(personAddress.getState());
                        State foundState = stateRepository.findOne(StatePredicate.searchByAbbreviation(personAddress.getState()));
                        if (foundState != null) {
                        	patientInfoHolder.setStateId(foundState.getId());
                        }
                    }
                    if (personAddress.getZipCode() != null) {
                    	patientInfoHolder.setZipcode(personAddress.getZipCode());
                    }
                    if (personAddress.getCountry() != null) {
                    	patientInfoHolder.setCountry(personAddress.getCountry());
                    }
                }
            }
        }

        // Telephone
        if (patientRole.getTelecom() != null) {
            for (TEL telcom : patientRole.getTelecom()) {
                if (telcom.getValue() != null && telcom.getUse() != null) {
                    for (String use : telcom.getUse()) {
                    	PatientPhoneNumberHolder phoneNumHolder = secureTableDao.findPhoneNumberByNumberAndType(patientInfo.getId(), telcom.getValue(), use);
                        if (phoneNumHolder == null) {
                        	val patientPhoneNumber = new PatientPhoneNumber();
                            patientPhoneNumber.setPatient(patientInfo);
                            patientPhoneNumber.setPhoneNumberType(use);
                            patientPhoneNumber.setDateCreated(new Date());
                            patientPhoneNumber.setDateUpdated(new Date());
                            patientPhoneNumber.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                            patientPhoneNumber.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
                            patientInfo.getPatientPhoneNumbers().add(patientPhoneNumber);

                        	phoneNumHolder = new PatientPhoneNumberHolder(patientPhoneNumber);
                        	phoneNumHolder.setPhoneNumber(telcom.getValue());
                        	patientInfoHolder.getPhoneNumberHolders().add(phoneNumHolder);
                        }
                    }
                }
            }
        }

        if (patientRole.getPatient() != null) {
            POCDMT000040Patient patient = patientRole.getPatient();

            // Parse DOB
            if (patient.getBirthTime() != null) {
                TS birthTime1 = patient.getBirthTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if (birthTime1.getValue() != null && birthTime1.getValue().length() == 8) {
                    sdf = new SimpleDateFormat("yyyyMMdd");
                }
                try {
                	patientInfoHolder.setBirthTime(sdf.parse(birthTime1.getValue()));
                } catch (ParseException ex) {
                    logger.error("Error parsing patient DOB", ex);
                    
                    // Throw a RTE, causing the transaction to roll back.  DOB is required!
                    throw new RuntimeException(String.format("Error parsing patient DOB %s", patient.getBirthTime()), ex);
                }
            } else {
                // DOB is required.  Throw a runtime exception.!
                throw new RuntimeException("Patient with NULL date of birth found.");
            }

            // Marital status
            if (patient.getMaritalStatusCode() != null && patient.getMaritalStatusCode().getDisplayName() != null) {
                patientInfo.setMaritalStatus(patient.getMaritalStatusCode().getDisplayName());
            }

            // Religous Affiliation
            if (patient.getReligiousAffiliationCode() != null && patient.getReligiousAffiliationCode().getDisplayName() != null) {
                patientInfo.setReligiousAffiliations(patient.getReligiousAffiliationCode().getDisplayName());
            }

            // Race
            if (patient.getRaceCode() != null && patient.getRaceCode() instanceof CD) {
                CD raceCode = patient.getRaceCode();
                CodeDetails codeDetails = extractCodeDetails(raceCode);
                patientInfo.setRaceCode(codeDetails.getCode());
                patientInfo.setRaceCodeDescription(codeDetails.getCodeDescription());
            }

            // Ethnic Group
            if (patient.getEthnicGroupCode() != null && patient.getEthnicGroupCode() instanceof CD) {
                CD ethnicGroupCode = patient.getEthnicGroupCode();
                CodeDetails codeDetails = extractCodeDetails(ethnicGroupCode);
                patientInfo.setEthnicityCode(codeDetails.getCode());
                patientInfo.setEthnicityCodeDescription(codeDetails.getCodeDescription());
            }

            // Guardian
            if (patient.getGuardian() != null) {
                for (POCDMT000040Guardian guardian : patient.getGuardian()) {
                    if (guardian.getGuardianPerson() != null) {
                        POCDMT000040Person guardianPerson = guardian.getGuardianPerson();
                        if (guardianPerson.getName() != null) {
                            PersonName personName = null;
                            for (PN pn : guardianPerson.getName()) {
                                if (pn.getContent() != null) {
                                    personName = extractPersonName(pn.getContent());
                                }
                                if (personName != null) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    if (personName.getFirstName() != null) {
                                        stringBuilder.append(personName.getFirstName() + " ");
                                    }
                                    if (personName.getLastName() != null) {
                                        stringBuilder.append(personName.getLastName() + " ");
                                    }
                                    patientInfo.setGuardian(stringBuilder.toString().trim());
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Language Communication
            if (patient.getLanguageCommunication() != null) {
                for (POCDMT000040LanguageCommunication language : patient.getLanguageCommunication()) {
                    if (language.getLanguageCode() != null && language.getLanguageCode().getDisplayName() != null) {
                        patientInfo.setLanguageCommunication(language.getLanguageCode().getDisplayName());
                    }
                }
            }

            // Parse Gender
            if (patient.getAdministrativeGenderCode() != null && patient.getAdministrativeGenderCode() instanceof CD) {
                CD genderCd = patient.getAdministrativeGenderCode();
                CodeDetails codeDetails = extractCodeDetails(genderCd);
                patientInfo.setGenderCode(codeDetails.getCode());
                patientInfo.setGenderCodeDescription(codeDetails.getCodeDescription());
            }

            // Parse First and Last Name
            PersonName personName = null;
            if (patient.getName() != null) {
                for (PN pn : patient.getName()) {
                    if (pn.getContent() != null) {
                        personName = extractPersonName(pn.getContent());
                    }
                    if (personName != null) {
                    	patientInfoHolder.setFirstName(personName.getFirstName());
                    	patientInfoHolder.setLastName(personName.getLastName());
                        break;
                    }
                }
            }
        }

        return patientInfoHolder;
    }

}
