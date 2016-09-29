package com.ainq.caliphr.persistence.dao.impl;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QCodeMapping.codeMapping;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientProblem.patientProblem;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBElement;

import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ClinicalDocumentDao;
import com.ainq.caliphr.persistence.mail.CaliphrMailer;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.*;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ClinicalDocumentDaoImpl implements ClinicalDocumentDao {

    static Logger logger = (Logger) LoggerFactory.getLogger(ClinicalDocumentDaoImpl.class);

    @Autowired
    private ApplicationContext appCxt;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ClinicalDocumentRepository clinicalDocumentRepository;

    @Autowired
    private ClinicalDocumentParseErrorRepository clinicalDocumentParseErrorRepository;

    @Autowired
    private PatientInfoRepository patientInfoRepository;

    @Autowired
    private PatientPhoneNumberRepository patientPhoneNumberRepository;

    @Autowired
    private ProviderPhoneNumberRepository providerPhoneNumberRepository;

    @Autowired
    private PatientPayerRepository patientPayerRepository;

    @Override
    public ClinicalDocument saveClinicalDocument(ClinicalDocument clinicalDocument) {
        return this.clinicalDocumentRepository.save(clinicalDocument);
    }

    @Override
    public PatientPayer savePatientPayer(PatientPayer patientPayer) {
        return this.patientPayerRepository.save(patientPayer);
    }

    @Override
    public PatientInfo savePatientInfo(PatientInfo patientInfo) {
        return this.patientInfoRepository.saveAndFlush(patientInfo);
    }

    @Override
    public PatientPhoneNumber savePatientPhoneNumber(PatientPhoneNumber patientPhoneNumber) {
        return this.patientPhoneNumberRepository.saveAndFlush(patientPhoneNumber);
    }
    
    @Override
    public Iterable<ProviderPhoneNumber> findProviderPhoneNumbers(Provider provider) {
    	return providerPhoneNumberRepository.findAll(QProviderPhoneNumber.providerPhoneNumber.provider.eq(provider).and(QProviderPhoneNumber.providerPhoneNumber.dateDisabled.isNull()));
    }

    @Override
    public ProviderPhoneNumber saveProviderPhoneNumber(ProviderPhoneNumber providerPhoneNumber) {
        return this.providerPhoneNumberRepository.save(providerPhoneNumber);
    }
    
    @Override
    public ClinicalDocumentParseError saveClinicalDocumentParseError(ClinicalDocument clinicalDocument, Exception ex) {

        //
        //  Email out the error to the Caliphr IT team
        List<String> to = new ArrayList<>();
        CaliphrMailer caliphrMailer = appCxt.getBean(CaliphrMailer.class);
        to.add(caliphrMailer.getDevSupportEmailAddress());
        StringBuilder errorMessage = new StringBuilder();
        if (clinicalDocument != null && clinicalDocument.getId() != null) {
            errorMessage.append(String.format("Document ID %s parse errors -> ", clinicalDocument.getId()));
            errorMessage.append(System.getProperty("line.separator"));
        }
        errorMessage.append(ExceptionUtils.getStackTrace(ex));

        try {
            caliphrMailer.setTo(to);
            caliphrMailer.setSubject("Caliphr - Clinical Document Parsing Errors");
            caliphrMailer.setHtmlHeading("Clinical Document Parsing Errors");
            caliphrMailer.setTextContent(errorMessage.toString());
            caliphrMailer.setHtmlContent(errorMessage.toString());
            caliphrMailer.generateAndSendEmail();
        } catch (MessagingException e) {
            logger.error(String.format("Error in sending out the CCDA process error email."), e);
        } catch (MailSendException e) {
        	logger.error(String.format("Error in sending out the CCDA process error email."), e);
        }

        //
        //  Persist the record to the database
        ClinicalDocumentParseError clinicalDocumentParseError = new ClinicalDocumentParseError();
        clinicalDocumentParseError.setDocument(clinicalDocument);
        clinicalDocumentParseError.setStackTrace(ExceptionUtils.getStackTrace(ex));
        clinicalDocumentParseError.setDateCreated(new Date());
        clinicalDocumentParseError.setDateUpdated(new Date());
        clinicalDocumentParseError.setUserCreated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        clinicalDocumentParseError.setUserUpdated(Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
        return this.clinicalDocumentParseErrorRepository.save(clinicalDocumentParseError);
    }

    @Override
    public void endDateExistingPatientProblems(PatientInfo patientInfo, JAXBElement<?> document) {

        //
        //  we'll need to end-date all existing problem records for that patient
        //  that are not in the current problem list of the new CCDA.

        //
        //  Extract the new end date from the document
        Date effectiveTimeEnd = null;
        try {
            POCDMT000040ClinicalDocument clinicalDocument = (POCDMT000040ClinicalDocument) document.getValue();
            if (clinicalDocument.getEffectiveTime() != null && clinicalDocument.getEffectiveTime().getValue() != null) {
                String effectiveTime = clinicalDocument.getEffectiveTime().getValue();
                effectiveTime = effectiveTime.substring(0, Math.min(effectiveTime.length(), 8));
                effectiveTimeEnd = new SimpleDateFormat("yyyyMMdd").parse(effectiveTime);
            }
        } catch (Exception ex) {
            effectiveTimeEnd = new Date();
        }

        // Update effective end dates
        //
        // Per Daniel: like in KPs example, if (A, B, C, D) exist and the new file has (A, B, C, E), only D should be end-dated.
        //      the rest should remain open
        new JPAUpdateClause(entityManager, patientProblem)
                .where(patientProblem.patient.id.eq(patientInfo.getId())
                        , patientProblem.effectiveTimeEnd.isNull()
                        , patientProblem.dateDisabled.isNull())
                .set(Arrays.asList(patientProblem.effectiveTimeEnd, patientProblem.dateUpdated, patientProblem.userUpdated)
                        , Arrays.asList(effectiveTimeEnd, new Date(), Constants.ApplicationUser.ADMINISTRATIVE_USER_ID))
                .execute();
        entityManager.flush();
    }

    @Transactional
    @Override
    public List<CodeMapping> findActiveCodeMappings(PracticeGroup practiceGroup) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.or(codeMapping.isGloballyMapped.eq(Boolean.TRUE));
        if (practiceGroup != null && practiceGroup.getId() > 0) {
            whereClause.or(codeMapping.group.id.eq(practiceGroup.getId()));
        }
        if (practiceGroup != null && practiceGroup.getVendor() != null && practiceGroup.getVendor().getId() > 0) {
            whereClause.or(codeMapping.vendor.id.eq(practiceGroup.getVendor().getId()));
        }
        return new JPAQuery(entityManager).from(codeMapping)
                .where(whereClause, codeMapping.dateDisabled.isNull())
                .setHint("org.hibernate.cacheable", "true")
                .setHint("org.hibernate.cacheRegion", "com.ainq.caliphr.persistence.model.obj.caliphrDb.CodeMapping.queryCache")
                .list(codeMapping);
    }

}
