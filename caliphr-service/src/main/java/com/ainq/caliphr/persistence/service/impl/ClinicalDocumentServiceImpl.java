package com.ainq.caliphr.persistence.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.input.CountingInputStream;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Performer1;
import org.hl7.v3.POCDMT000040ServiceEvent;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.ainq.caliphr.common.util.format.FileSizeFormat;
import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ClinicalDocumentDao;
import com.ainq.caliphr.persistence.dao.SecureTableDao;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PatientEncounter;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ClinicalDocumentRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ParseStatusTypeRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.PracticeGroupRepository;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentParser;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;
import com.ainq.caliphr.persistence.transformation.cda.ClinicalDocumentType;
import com.ainq.caliphr.persistence.transformation.cda.ParseStatus;

import ch.qos.logback.classic.Logger;

@Service
public class ClinicalDocumentServiceImpl implements ClinicalDocumentService {

    // Logger
    private final static Logger logger = (Logger) LoggerFactory.getLogger(ClinicalDocumentServiceImpl.class);

    private static final JAXBContext jaxbCtx;
    static {
    	try {
			jaxbCtx = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ClinicalDocumentParser clinicalDocumentParser;

    @Autowired
    private ClinicalDocumentDao clinicalDocumentDao;
    
    @Autowired
    private ClinicalDocumentRepository clinicalDocumentRepository;

    @Autowired
    private PracticeGroupRepository practiceGroupRepository;

    @Autowired
    private SecureTableDao secureTableDao;

    @Autowired
    private ParseStatusTypeRepository parseStatusTypeRepository;
    
    @Transactional
    @Override
    public ClinicalDocument createClinicalDocument(ClinicalDocumentProcessTask task) {
    	
    	StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        ClinicalDocument clinicalDocument = new ClinicalDocument();
        try {
	        clinicalDocument.setFileName(task.getDocumentName());
	        
	        clinicalDocument.setDateCreated(new Date());
	        clinicalDocument.setDateUpdated(new Date());
	        clinicalDocument.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
	        clinicalDocument.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
	        clinicalDocument = this.clinicalDocumentDao.saveClinicalDocument(clinicalDocument);
	        task.setClinicalDocument(clinicalDocument);
        } catch (Exception ex) {
        	logger.error("Clinical Document Process Error " + clinicalDocument.getFileName(), ex);
        	throw new IllegalStateException("Clinical Document Process Error -> ", ex);
        }
    	
    	try {
    		// Deserialize the stream into an object
	    	
	        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
	        JAXBElement<?> document = null;
	        
            // Find file size and name
	        InputStream is = null;
	        if (task.getFileEntry() != null) {
	        	is = new FileInputStream(task.getFileEntry());
            } else if (task.getResource() != null) {
                is = task.getResource().getInputStream();
            } else if (task.getInputStream() != null) {
                is = task.getInputStream();
            } else if (task.getXml() != null) {
            	is = new ByteArrayInputStream(task.getXml().getBytes());
            } else {
                throw new IllegalStateException("Unsupported CDA resource type!");
            }
	        if (is != null) {
	        	try (CountingInputStream cis = new CountingInputStream(new BufferedInputStream(is))) {
	        		document = (JAXBElement<?>) unmarshaller.unmarshal(new InputStreamReader(cis, StandardCharsets.UTF_8));
	        		task.setFileSize(FileSizeFormat.getSizeInKB(cis.getByteCount()));
	        	}
            }
	        if (document != null && document.getValue() != null && document.getValue() instanceof POCDMT000040ClinicalDocument) {
	        	task.setDocument(document);
	        } else {
	        	
	        }
	        
	        clinicalDocument.setParseStatusType(parseStatusTypeRepository.getOne(ParseStatus.SUCCESS.getTypeId()));
	        clinicalDocument.setFileSizeKb(BigDecimal.valueOf(task.getFileSize()));
	        
	        //
	        //  Validate the source of the patient data (the sending group/practice)
	        PracticeGroup practiceGroup;
	        try {
	            practiceGroup = clinicalDocumentParser.findSendingGroup(task.getDocument());
	        } catch (Exception ex) {
	        	logger.warn(null, ex);
	        	stopWatch.stop();
	            createClinicalDocumentParseError(clinicalDocument, ex, stopWatch.getTotalTimeMillis());
	            throw new IllegalStateException("Clinical Document Process Error " + clinicalDocument.getFileName(), ex);
	        }
	        
	        // Save patient info
	        PatientInfoHolder patientInfoHolder = clinicalDocumentParser.findPatientBySourceAndMRN(document, practiceGroup);
	        if (patientInfoHolder == null) {
	        	
                // One more check
	        	patientInfoHolder = clinicalDocumentParser.findPatientBySourceAndMRN(document, practiceGroup);
                if (patientInfoHolder == null) {
                	patientInfoHolder = clinicalDocumentParser.createPatientInfo(document, practiceGroup);
                    if (patientInfoHolder != null) {
                    	this.clinicalDocumentDao.savePatientInfo(patientInfoHolder.getPatientInfo());
                        secureTableDao.savePatientInfoHolder(patientInfoHolder);

                        // Save patient phone numbers
                        patientInfoHolder.getPhoneNumberHolders().forEach(phoneHolder -> {
                            this.clinicalDocumentDao.savePatientPhoneNumber(phoneHolder.getPatientPhoneNumber());
                            secureTableDao.savePatientPhoneNumberHolder(phoneHolder);
                        });
                    }
                }
	        }
	        task.setMedicalRecordNum(patientInfoHolder.getMedicalRecordNumber());
	        
	        
	        clinicalDocument.setPatient(patientInfoHolder.getPatientInfo());
	        
	        this.clinicalDocumentDao.saveClinicalDocument(clinicalDocument);
	        
	        
	        
    	} catch (JAXBException ex) {
    		
    		// XML parsing errors should just be logged in the error table, but not rethrown to the caller
    		logger.error("Clinical Document Process Error " + clinicalDocument.getFileName() + " -> ", ex);
            clinicalDocument.setParseStatusType(parseStatusTypeRepository.findOne(ParseStatus.PARSING_ERRORS.getTypeId()));
            this.clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, ex);
            return null;
    	} catch (Exception ex) {
    		
    		// other exceptions besides for XML parsing errors should be rethrown to the caller
    		logger.error("Clinical Document Process Error " + clinicalDocument.getFileName() + " -> ", ex);
            clinicalDocument.setParseStatusType(parseStatusTypeRepository.findOne(ParseStatus.PARSING_ERRORS.getTypeId()));
            this.clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, ex);
	        throw new IllegalStateException("Clinical Document Process Error -> ", ex);
        }    
    	
        return clinicalDocument;

    }


    @Transactional
    @Override
    public void processClinicalDocumentRecord(JAXBElement<?> document, Boolean logToFileSystem, Long clinicalDocumentId, String medicalRecordNum) {

        

        //
        //  Ensure proper clinical document has been passed in (either CCDA or QRDA1)
        //
        //  -> Throw exception if otherwise
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        ClinicalDocument clinicalDocument = clinicalDocumentRepository.findOne(clinicalDocumentId);
        
        //
        //  Thread logging
        if (Thread.currentThread() != null) {
            logger.debug(String.format("CDA Processing thread %s - %s", Thread.currentThread().getName(), clinicalDocument.getFileName()));
        }

        //
        // Pull the title from <ClinicalDocument> -> <title>
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        if (documentRoot != null && documentRoot.getTitle() != null && documentRoot.getTitle().getContent() != null) {
            for (Serializable content : documentRoot.getTitle().getContent()) {
                if (content instanceof String) {
                    clinicalDocument.setTitle((String) content);
                    break;
                }
            }
        }
        

        PracticeGroup practiceGroup = practiceGroupRepository.getOne(clinicalDocument.getPatient().getGroup().getId());
        
        //
        // Populate the clinical document
        clinicalDocument = this.clinicalDocumentParser.archiveClinicalDocument(document, clinicalDocument, practiceGroup, logToFileSystem);

        //
        // Validate the clinical document
        try {
            clinicalDocument.setType(this.clinicalDocumentParser.validateClinicalDocument(document));
        } catch (Exception ex) {
            stopWatch.stop();
            logger.warn("error while validating document " + clinicalDocument.getFileName(), ex);
            createClinicalDocumentParseError(clinicalDocument, ex, stopWatch.getTotalTimeMillis());
            return;
        }

        //  Save the list of providers found in documentOf element.  We will need these since providers
        //  are tied with the encounter. Ignore the providers in documentationOf in case of C_CDA documents
        
        List<Provider> providerList = new ArrayList<>();
        try {

            //	updated method to ignore providers in documentationOf in case of C_CDA documents
        	
        	if (clinicalDocument.getType().getHl7Oid().equals(ClinicalDocumentType.CAT_I.getRoot())) {
            	providerList = this.loadDocumentProviders(document, clinicalDocument, practiceGroup);
        	}
            
        } catch (Exception ex) {
        	logger.warn("error while loading CCDA document providers " + clinicalDocument.getFileName(), ex);
            clinicalDocument.setParseStatusType(parseStatusTypeRepository.findOne(ParseStatus.PARSING_ERRORS.getTypeId()));
            this.clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, ex);
        }


        // Find all code mappings for the practice
        List<CodeMapping> codeMappings = this.clinicalDocumentDao.findActiveCodeMappings(practiceGroup);

        //
        // additional logic to ensure active problems only
        if (practiceGroup.isActiveProblemsOnlyFlag() != null
                && practiceGroup.isActiveProblemsOnlyFlag().equals(Boolean.TRUE)
                && clinicalDocument.getPatient() != null
                && clinicalDocument.getPatient().getId() != null
                && clinicalDocument.getPatient().getId() > 0) {
            this.clinicalDocumentDao.endDateExistingPatientProblems(clinicalDocument.getPatient(), document);
        }

        // Build Patient Sections
        this.clinicalDocumentParser.loadPatientSections(document, clinicalDocument, practiceGroup, codeMappings);

        // encounter
        if (clinicalDocument.getPatient().getPatientEncounters() != null) {

            for (PatientEncounter encounter : clinicalDocument.getPatient().getPatientEncounters()) {

                // use documentationOf section if QRDA 
                if (encounter.getProvider() == null && clinicalDocument.getType().getHl7Oid().equals(ClinicalDocumentType.CAT_I.getRoot())) {
                    if (providerList != null && providerList.size() == 1) {

                        //
                        //  Save the provider from the documentationOf section
                        //this.saveProviderDetails(providerList.get(0));

                        //
                        //  Tie the provider to the encounter
                        encounter.setProvider(providerList.get(0));
                    } else if (providerList != null && providerList.size() > 1) {
                        stopWatch.stop();
                        createClinicalDocumentParseError(clinicalDocument, new Exception("Document found with multiple providers and no provider for encounter."), stopWatch.getTotalTimeMillis());
                        return;
                    }
                }
            }

        }
        
        afterProcessClinicalDocumentRecord(clinicalDocument, practiceGroup, medicalRecordNum);

        //  Completion
        entityManager.flush();

        stopWatch.stop();
        clinicalDocument.setParseTime(BigDecimal.valueOf(stopWatch.getTotalTimeMillis()));
    }

    protected void afterProcessClinicalDocumentRecord(ClinicalDocument clinicalDocument, PracticeGroup practiceGroup, String medicalRecordNum) {
		
	}


	/*
        Helper method to navigate through document to save providers without creating duplicate records.
     */
    private List<Provider> loadDocumentProviders(JAXBElement<?> document, ClinicalDocument clinicalDocument, PracticeGroup practiceGroup) {

        List<Provider> providers = new ArrayList<>();

        // Find providers in the document
        POCDMT000040ClinicalDocument documentRoot = (POCDMT000040ClinicalDocument) document.getValue();
        if (documentRoot.getDocumentationOf() != null) {
            /*
                Create and save the provider details
             */
            documentRoot.getDocumentationOf().stream().filter(documentationOf -> documentationOf.getServiceEvent() != null).forEach(documentationOf -> {
                POCDMT000040ServiceEvent serviceEvent = documentationOf.getServiceEvent();
                if (serviceEvent.getPerformer() != null) {
                    for (POCDMT000040Performer1 performer : serviceEvent.getPerformer()) {

                        /*
                            Create and save the provider details
                         */
                        Provider provider = this.clinicalDocumentParser.createProviderDetails(performer, practiceGroup);
                        if (provider != null) {
                            providers.add(provider);
                        }
                    }
                }
            });
        }

        return providers;
    }

    /*
        Helper function to add parCl errors to the clinical document
     */
    private void createClinicalDocumentParseError(ClinicalDocument clinicalDocument, Exception ex, Long totalTimeMillis) {
        clinicalDocument.setParseTime(BigDecimal.valueOf(totalTimeMillis));
        clinicalDocument.setParseStatusType(parseStatusTypeRepository.findOne(ParseStatus.FAILED_VALIDATION.getTypeId()));
        this.clinicalDocumentDao.saveClinicalDocument(clinicalDocument);
        this.clinicalDocumentDao.saveClinicalDocumentParseError(clinicalDocument, ex);
    }
}
