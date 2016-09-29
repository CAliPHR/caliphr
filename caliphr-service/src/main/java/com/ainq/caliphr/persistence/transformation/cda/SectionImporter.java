package com.ainq.caliphr.persistence.transformation.cda;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.hl7.v3.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.CodeDao;
import com.ainq.caliphr.persistence.dao.reference.StatusCodeDao;
import com.ainq.caliphr.persistence.model.ccda.CodeDetails;
import com.ainq.caliphr.persistence.model.ccda.EffectiveTime;
import com.ainq.caliphr.persistence.model.ccda.PersonAddress;
import com.ainq.caliphr.persistence.model.ccda.PersonName;
import com.ainq.caliphr.persistence.model.ccda.StatusCodeDetails;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeSystem;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientNegationDetail;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.StatusCode;
import com.ainq.caliphr.persistence.transformation.util.CdaUtility;

import ch.qos.logback.classic.Logger;
import lombok.val;

/**
 * Created by mmelusky on 5/15/2015.
 */
public abstract class SectionImporter {

    // Timestamp field constants
    protected static final String TIMESTAMP_LOW = "low";
    protected static final String TIMESTAMP_HIGH = "high";
    protected static final String TIMESTAMP_CENTER = "center";

    // Date time format constants
    public static final Integer DATE_FORMAT_SIZE = 14;
    public static final Integer DATE_YEAR_SIZE = 4;
    public static final Integer DATE_YEAR_MONTH_SIZE = 6;
    public static final Integer DATE_YEAR_MONTH_DAY_SIZE = 8;
    public static final Integer DATE_YEAR_MONTH_DAY_HOUR_SIZE = 10;
    public static final Integer DATE_YEAR_MONTH_DAY_HOUR_MIN_SIZE = 12;
    public static final String DATE_YEAR_EMPTY = "0000";

    // JAXB HL7 serialization (mixed content to blame)
    private static final String JAXB_GIVEN = "given";
    private static final String JAXB_FAMILY = "family";
    private static final String JAXB_STREET_ADDRESS = "streetaddressline";
    private static final String JAXB_CITY = "city";
    private static final String JAXB_STATE = "state";
    private static final String JAXB_ZIP = "postalcode";
    private static final String JAXB_COUNTRY = "country";

    // Class Data
    static Logger logger = (Logger) LoggerFactory.getLogger(SectionImporter.class);

    // Repositories

    @Autowired
    private CodeDao codeDao;
    
    @Autowired
    private StatusCodeDao statusCodeDao;

    // Components

    @Autowired
    protected PracticeCodeMapper practiceCodeMapper;

    /*
        Base Class Methods
     */
    protected EffectiveTime extractEffectiveDate(TS time) {
        String startTime = null;
        String endTime = null;
        EffectiveTime effectiveTime = new EffectiveTime();
        IVLTS ivlts = null;
        try {
            ivlts = (IVLTS) time;
        } catch (Exception ex) {

            // Attempt to simply extract the "time" value from the object
            if (time.getValue() != null) {
                startTime = time.getValue();
                endTime = time.getValue();
            } else {
                // Empty date time value passed in.
                return effectiveTime;
            }

        }
        if (ivlts != null) {
            if (ivlts.getValue() != null) {
                startTime = ivlts.getValue();
                endTime = ivlts.getValue();
            } else if (ivlts.getRest() != null) {
                for (JAXBElement<? extends QTY> rest : ivlts.getRest()) {
                    if (rest.getName() != null && rest.getValue() != null && rest.getValue() instanceof IVXBTS) {
                        IVXBTS restValue = (IVXBTS) rest.getValue();
                        if (rest.getName().getLocalPart().equalsIgnoreCase(TIMESTAMP_CENTER)) {
                            startTime = restValue.getValue();
                            endTime = restValue.getValue();
                        } else {
                            if (rest.getName().getLocalPart().equalsIgnoreCase(TIMESTAMP_LOW)) {
                                startTime = restValue.getValue();
                            } else if (rest.getName().getLocalPart().equalsIgnoreCase(TIMESTAMP_HIGH)) {
                                endTime = restValue.getValue();
                            }
                        }
                    }
                }
            }
        }

        // Attempt to parse dates

        // Start Time
        if (startTime != null && startTime != StringUtils.EMPTY) {
            try {
                if (!startTime.startsWith(DATE_YEAR_EMPTY)) {
                    effectiveTime.setEffectiveTimeStart(parseCdaDateTime(startTime));
                }
            } catch (Exception ex) {
                logger.error("Exception parsing <section> effective time start -> ", ex);
            }
        }

        // End Time
        if (endTime != null && endTime != StringUtils.EMPTY) {
            try {
                if (!endTime.startsWith(DATE_YEAR_EMPTY)) {
                    effectiveTime.setEffectiveTimeEnd(parseCdaDateTime(endTime));
                }
            } catch (Exception ex) {
                logger.error("Exception parsing <section> effective time start -> ", ex);
            }
        }

        return effectiveTime;
    }

    /**
     * Can parse times in the following formats:
     * <p>
     * yyyy
     * yyyyMM
     * yyyyMMdd
     * yyyyMMddHH
     * yyyyMMddHHmm
     * yyyyMMddHHmmss
     * <p>
     * Values longer than yyyyMMddHHmmss are truncated
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseCdaDateTime(String dateStr) throws ParseException {
        if (dateStr.length() > DATE_FORMAT_SIZE) {
            dateStr = dateStr.substring(0, DATE_FORMAT_SIZE);
        } else if (dateStr.length() == DATE_YEAR_MONTH_DAY_HOUR_MIN_SIZE) {
            dateStr += "00";
        } else if (dateStr.length() == DATE_YEAR_MONTH_DAY_HOUR_SIZE) {
            dateStr += "0000";
        } else if (dateStr.length() == DATE_YEAR_MONTH_DAY_SIZE) {
            dateStr += "000000";
        } else if (dateStr.length() == DATE_YEAR_MONTH_SIZE) {
            dateStr += "01000000";
        } else if (dateStr.length() == DATE_YEAR_SIZE) {
            dateStr += "0101000000";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setLenient(false);
        return formatter.parse(dateStr);
    }

    protected EffectiveTime extractEffectiveDate(List<SXCMTS> sxcmts, List<POCDMT000040Author> authors) {
        EffectiveTime effectiveTime = null;
        if (sxcmts != null) {
            for (SXCMTS time : sxcmts) {
                effectiveTime = extractEffectiveDate(time);
                break;
            }
        }
        if (effectiveTime != null && (effectiveTime.getEffectiveTimeStart() != null || effectiveTime.getEffectiveTimeEnd() != null)) {
            return effectiveTime;
        }
        return extractEffectiveDateFromAuthor(authors);
    }

    protected EffectiveTime extractEffectiveDate(TS time, List<POCDMT000040Author> authors) {
        EffectiveTime effectiveTime = extractEffectiveDate(time);
        if (effectiveTime.getEffectiveTimeStart() != null || effectiveTime.getEffectiveTimeEnd() != null) {
            return effectiveTime;
        }
        return extractEffectiveDateFromAuthor(authors);
    }

    protected EffectiveTime extractEffectiveDateFromAuthor(List<POCDMT000040Author> authors) {
        if (authors != null) {
            for (POCDMT000040Author author : authors) {
                if (author.getTime() != null) {
                    return extractEffectiveDate(author.getTime());
                }
            }
        }
        return new EffectiveTime();
    }

    protected CodeDetails extractCodeDetails(CD cd) {
        CodeDetails codeDetails = new CodeDetails();
        if (cd != null) {
            // Display name
            if (cd.getDisplayName() != null) {
                codeDetails.setCodeDescription(cd.getDisplayName());
            } else if (cd.getOriginalText() != null) {
            	codeDetails.setCodeDescription(CdaUtility.getOriginalText(cd.getOriginalText()));
            }

            //  Query for existing code
            Code foundCode = null;
            String codeStr           = StringUtils.trimToNull(cd.getCode());
            String codeSystemStr     = StringUtils.trimToNull(cd.getCodeSystem());
            String codeSystemNameStr = StringUtils.trimToNull(cd.getCodeSystemName());
        	if ( (codeStr != null) && (codeSystemStr != null || codeSystemNameStr != null) ) {
        		codeSystemNameStr = codeSystemStr == null ?  codeSystemNameStr : null;
                foundCode = codeDao.findOrCreateCode(codeStr, codeSystemStr, codeSystemNameStr, null, codeDetails.getCodeDescription());
                if (foundCode != null) {
                    codeDetails.setCode(foundCode);
                    if (StringUtils.isNotBlank(codeDetails.getCodeDescription()) && !codeDetails.getCodeDescription().equals(foundCode.getLatestCdaDescription())) {
                    	codeDao.updateLatestCdaDescription(foundCode, codeDetails.getCodeDescription());
                    }
                }
            }
            
            // capture translation codes where appropriate
            
            if (foundCode == null && cd.getTranslation() != null) {
            	if (cd.getTranslation().size() > 1) {
            		logger.debug("more than one translation found");
            	}
            	for (CD translationCd : cd.getTranslation()) {
            		CodeDetails translationCodeDetails = extractCodeDetails(translationCd);
            		if (translationCodeDetails != null && translationCodeDetails.getCode() != null) {
            			codeDetails.addTranslationCode(translationCodeDetails.getCode());
            		}
            		if (translationCodeDetails.getCode() != null ) {
            			return translationCodeDetails;
            		}
            		if (StringUtils.isBlank(codeDetails.getCodeDescription()) && StringUtils.isNotBlank(translationCodeDetails.getCodeDescription())) {
            			return translationCodeDetails;
            		}
            		// else default to codeDetails below
            	}
            }
            if (foundCode != null && cd.getTranslation() != null) {
            	if (cd.getTranslation().size() > 1) {
            		logger.debug("more than one translation found");
            	}
            	CodeDetails translationCodeDetails = null;
            	for (CD translationCd : cd.getTranslation()) {
            		translationCodeDetails = extractCodeDetails(translationCd);
            		if (translationCodeDetails != null && translationCodeDetails.getCode() != null) {
            			codeDetails.addTranslationCode(translationCodeDetails.getCode());
            		}
            	}
            	if (translationCodeDetails == null || translationCodeDetails.getCode() == null) {
            		return codeDetails;
            	}
            	
            	// favor codes of RxNorm.  TODO: revisit this logic to capture all translations, not only select ones
            	CodeSystem codeCodeSystem = codeDetails.getCode().getCodeSystem();
            	CodeSystem translationCodeSystem = translationCodeDetails.getCode().getCodeSystem();
				if ("RxNorm".equals(translationCodeSystem.getCodeSystemName()) && !"RxNorm".equals(codeCodeSystem.getCodeSystemName())) {
					return translationCodeDetails;
				}
            }
        }
        return codeDetails;
    }
    
    protected StatusCodeDetails extractStatus(CS cs) {
        StatusCodeDetails statusCodeDetails = new StatusCodeDetails();
        if (cs != null && cs.getCode() != null) {
            String statusCode = cs.getCode().toLowerCase();
            StatusCode foundStatusCode = statusCodeDao.findOrCreateStatusCode(statusCode);
			statusCodeDetails.setStatusCode(foundStatusCode);
            statusCodeDetails.setStatusCodeName(statusCode);
        }
        return statusCodeDetails;
    }

    @SuppressWarnings("rawtypes")
	protected PersonName extractPersonName(List<Serializable> content) {
        PersonName personName = new PersonName();
        String firstName = StringUtils.EMPTY;
        String lastName = StringUtils.EMPTY;
        String middleName = StringUtils.EMPTY;
        String fullName = StringUtils.EMPTY;
        for (Serializable serializable : content) {
            if (serializable instanceof JAXBElement) {
                JAXBElement element = (JAXBElement) serializable;
                if (element.getName() != null && element.getName().getLocalPart() != null && element.getValue() != null && element.getValue() instanceof String) {
                    String elementName = element.getName().getLocalPart().toLowerCase().trim();
                    String value;
                    switch (elementName) {
                        case JAXB_GIVEN:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                if (firstName.length() > 0) {
                                    middleName = value;
                                } else {
                                    firstName = value;
                                }
                            }
                            break;
                        case JAXB_FAMILY:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                lastName = value;
                            }
                            break;
                    }
                }
            } else if (serializable instanceof String) {

                //
                //  If <name> element contains the full name...
                fullName = (String) serializable;
                if (fullName != null && ! fullName.replaceAll("^\\s+|\\s+$", "").isEmpty()) {
                    personName.setFullName(fullName.replaceAll("^\\s+|\\s+$", ""));
                }
            }
        }

        personName.setFirstName(firstName.trim());
        personName.setLastName(lastName.trim());
        personName.setMiddleName(middleName.trim());
        return personName;
    }

    @SuppressWarnings("rawtypes")
	protected PersonAddress extractPersonAddress(List<Serializable> content) {
        PersonAddress personAddress = new PersonAddress();
        String address = StringUtils.EMPTY;
        String address2 = StringUtils.EMPTY;
        String city = StringUtils.EMPTY;
        String state = StringUtils.EMPTY;
        String zip = StringUtils.EMPTY;
        String country = StringUtils.EMPTY;
        for (Serializable serializable : content) {
            if (serializable instanceof JAXBElement) {
                JAXBElement element = (JAXBElement) serializable;
                if (element.getName() != null && element.getName().getLocalPart() != null && element.getValue() != null && element.getValue() instanceof String) {
                    String elementName = element.getName().getLocalPart().toLowerCase().trim();
                    String value;
                    switch (elementName) {
                        case JAXB_STREET_ADDRESS:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                if (address.length() > 0) {
                                    address2 = (String) element.getValue();
                                } else {
                                    address = (String) element.getValue();
                                }
                            }
                            break;
                        case JAXB_CITY:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                city = (String) element.getValue();
                            }
                            break;
                        case JAXB_STATE:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                state = (String) element.getValue();
                            }
                            break;
                        case JAXB_ZIP:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                zip = (String) element.getValue();
                            }
                            break;
                        case JAXB_COUNTRY:
                            value = (String) element.getValue();
                            if (value != null && ! value.isEmpty()) {
                                country = (String) element.getValue();
                            }
                            break;
                    }
                }
            }
        }

        personAddress.setAddress(address.trim());
        personAddress.setAddress2(address2.trim());
        personAddress.setCity(city.trim());
        personAddress.setState(state.trim());
        personAddress.setZipCode(zip.trim());
        personAddress.setCountry(country.trim());
        return personAddress;
    }
    
    protected PatientNegationDetail extractNegation(List<POCDMT000040EntryRelationship> entryRelationships) {
		if (entryRelationships != null) {
			 for (val entryRelationship : entryRelationships) {
				 if (entryRelationship.getTypeCode().equals(XActRelationshipEntryRelationship.RSON) &&
					entryRelationship.getObservation() != null && 
					entryRelationship.getObservation().getValue() != null) {
					
					if (findTemplateId(entryRelationship.getObservation().getTemplateId(), TemplateIdRoot.NEGATION_REASON__QRDA)) {
						for (ANY observationValue : entryRelationship.getObservation().getValue()) {
						    if (observationValue instanceof CD) {
						        CD observationCd = (CD) observationValue;
						        CodeDetails valueCodeDetails = extractCodeDetails(observationCd);
								PatientNegationDetail negationDetail = new PatientNegationDetail();
						        negationDetail.setCode(valueCodeDetails.getCode());
						        negationDetail.setCodeDescription(valueCodeDetails.getCodeDescription());
						        
						        negationDetail.setDateCreated(new Date());
						        negationDetail.setDateUpdated(new Date());
						        negationDetail.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
						        negationDetail.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
						        
								return negationDetail;
						    }
						}
					}
				 }
			}
		}
		return null;
	}

    protected PatientNegationDetail updateNegationDetailRecord(PatientNegationDetail oldNegationDetail, PatientNegationDetail newNegationDetail) {

        if (oldNegationDetail == null && newNegationDetail == null) {
            return null; // No need to update
        } else if (oldNegationDetail != null && newNegationDetail == null) {
            oldNegationDetail.setDateDisabled(new Date());  // Disable existing detail record
            return oldNegationDetail;
        } else if (oldNegationDetail == null) {
            oldNegationDetail = new PatientNegationDetail();    // Instantiate new record
        }

        oldNegationDetail.setCode(newNegationDetail.getCode());
        oldNegationDetail.setCodeDescription(newNegationDetail.getCodeDescription());
        oldNegationDetail.setValue(newNegationDetail.getValue());
        oldNegationDetail.setDateUpdated(new Date());
        oldNegationDetail.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        oldNegationDetail.setDateDisabled(null);
        return oldNegationDetail;
    }

    protected boolean findTemplateId(List<II> templateIds, TemplateIdRoot root) {
		if (templateIds != null) {
			for (II templateId : templateIds) {
				if (root.getRoot().equals(templateId.getRoot())) {
					return true;
				}
			}
		}
		return false;
	}
    
    protected CodeDetails getReasonCodeDetails(List<POCDMT000040EntryRelationship> entryRelationships) {
    	if (entryRelationships != null) {
        	for (val entryRltnshp : entryRelationships) {
        		if (entryRltnshp.getTypeCode() == XActRelationshipEntryRelationship.RSON) {
        			if (entryRltnshp.getObservation() != null && entryRltnshp.getObservation().getValue() != null) {
        				for (val obsv : entryRltnshp.getObservation().getValue()) {
        					if (obsv instanceof CD) {
        						CodeDetails reasonCodeDetails = extractCodeDetails((CD)obsv);
        						if (reasonCodeDetails != null) {
        							return reasonCodeDetails;
        						}
        					}
        				}
        				
        			}
        		}
        	}
        }
    	return null;
    }
    
    protected String extractExternalId(List<II> ids) {
		if (ids != null) {
			for (val id : ids) {
				String result = extractExternalId(id);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

    protected String extractExternalId(II id) {
		if (id != null) {
			return id.getExtension() != null ? id.getExtension() : id.getRoot();
		}
		return null;
	}


}
