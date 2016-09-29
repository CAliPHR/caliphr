package com.ainq.caliphr.persistence.dao.impl;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.persistence.dao.QrdaDao;
import com.ainq.caliphr.persistence.dao.SecureTableDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.HqmfDataCriteriaRepository;
import com.ainq.caliphr.persistence.model.qrda.cat1.QrdaCat1Extract;
import com.ainq.caliphr.persistence.model.qrda.cat1.QrdaCat1ReportingParameters;
import com.ainq.caliphr.persistence.model.qrda.cat1.entry.QrdaCat1Entry;
import com.ainq.caliphr.persistence.model.qrda.cat1.entry.QrdaCat1Ordinality;
import com.ainq.caliphr.persistence.model.qrda.cat1.entry.QrdaCat1Reason;
import com.ainq.caliphr.persistence.model.qrda.cat1.entry.QrdaCat1Severity;
import com.ainq.caliphr.persistence.model.qrda.cat1.patient.QrdaCat1Patient;
import com.ainq.caliphr.persistence.model.qrda.cat1.provider.*;
import com.ainq.caliphr.persistence.transformation.cda.TemplateIdRoot;
import com.ainq.caliphr.persistence.util.predicate.hqmf.HqmfDataCriteriaPredicate;
import com.mysema.query.jpa.impl.JPAQuery;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUserSecurity.applicationUserSecurity;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QClinicalDocument.clinicalDocument;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QCode.code;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfDocument.hqmfDocument;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfPopulationSet.hqmfPopulationSet;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientAllergy.patientAllergy;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientEncounter.patientEncounter;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientInfo.patientInfo;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientMedicalEquipment.patientMedicalEquipment;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientMedication.patientMedication;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientPayer.patientPayer;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientPlanOfCare.patientPlanOfCare;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientProblem.patientProblem;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientProcedure.patientProcedure;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientResult.patientResult;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientSocialHistory.patientSocialHistory;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QProvider.provider;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QResult.result;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QResultPatient.resultPatient;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QTemplateRoot.templateRoot;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QValueSet.valueSet;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QValueSetCode.valueSetCode;

import java.util.*;

/**
 * Created by mmelusky on 10/20/2015.
 */
@Repository
public class QrdaDaoImpl implements QrdaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SecureTableDao secureTableDao;

    @Autowired
    private HqmfDataCriteriaRepository hqmfDataCriteriaRepository;

    // Joda Time Datetime formatter
    private static final String JODA_TIME_HL7_DATE_FORMAT = "YYYYMMdd";
    private static final String JODA_TIME_HL7_DATETIME_FORMAT = "YYYYMMddHms";
    private static final String JODA_TIME_REPORTING_HEADER = "M d, y H:m:s";

    // Query DSL specific
    private static final String STATUS_CODE_COMPLETED = "completed";
    private static final String STATUS_CODE_NEW = "new";

    /*
        Implemented methods
     */

    @Override
    public List<Patient> findPatientsForHqmfDocument(Long hqmfDocumentId, Integer userId) {
        List<Integer> patientIds = new JPAQuery(entityManager).from(patientInfo)
                .innerJoin(patientInfo.resultPatients, resultPatient)
                .innerJoin(resultPatient.result, result)
                .innerJoin(result.hqmfPopulationSet, hqmfPopulationSet)
                .innerJoin(hqmfPopulationSet.hqmfDocument, hqmfDocument)
                .innerJoin(hqmfDocument.provider, provider)
                .innerJoin(provider.applicationUserSecurities, applicationUserSecurity)
                .where(hqmfDocument.id.eq(hqmfDocumentId)
                        , applicationUserSecurity.user.id.eq(userId)
                        , applicationUserSecurity.dateDisabled.isNull()
                        , result.dateDisabled.isNull()
                        , resultPatient.dateDisabled.isNull()
                        , hqmfPopulationSet.dateDisabled.isNull()
                        , patientInfo.dateDisabled.isNull()).list(patientInfo.id);
        return secureTableDao.findAllPatientInfo(patientIds);
    }

    @Override
    public QrdaCat1Extract retrieveQrdaCat1Extract(Patient patient, HqmfDocument hqmfDocument) {

        QrdaCat1Extract extract = new QrdaCat1Extract();

        // UUID
        extract.setUuid(UUID.randomUUID().toString());

        // Current Date in UTC
        extract.setEffectiveTime(new DateTime(DateTimeZone.UTC).toString(JODA_TIME_HL7_DATETIME_FORMAT));

        // Patient Information
        extract.setPatient(buildPatientSection(patient, hqmfDocument));

        // Provider Information
        extract.setProvider(buildProviderSection(hqmfDocument));

        // Measure Details
        extract.setMeasures(buildMeasureSection(hqmfDocument));

        // Reporting Period
        extract.setReportingParameters(buildReportingPeriodSection(hqmfDocument));

        // Entries
        extract.setEntries(buildEntriesSection(patient, hqmfDocument));

        return extract;
    }

    /*
        Private Methods below
     */

    private QrdaCat1Patient buildPatientSection(Patient patient, HqmfDocument hqmfDocument) {
        QrdaCat1Patient qrdaCat1Patient = new QrdaCat1Patient();

        // mrn
        if (patient.getMedicalRecordNumber() != null) {
            qrdaCat1Patient.setMedicalRecordNumber(patient.getMedicalRecordNumber());
        }

        // src
        if (hqmfDocument.getProvider() != null && hqmfDocument.getProvider().getGroup() != null
                && hqmfDocument.getProvider().getGroup().getSenderOid() != null) {
            qrdaCat1Patient.setSourceOid(hqmfDocument.getProvider().getGroup().getSenderOid());
        }

        // first name
        if (patient.getFirstName() != null) {
            qrdaCat1Patient.setFirstName(patient.getFirstName());
        }

        // last name
        if (patient.getLastName() != null) {
            qrdaCat1Patient.setLastName(patient.getLastName());
        }

        // address 1
        if (patient.getAddress() != null) {
            qrdaCat1Patient.setAddress1(patient.getAddress());
        }

        // address 2
        if (patient.getAddress2() != null) {
            qrdaCat1Patient.setAddress2(patient.getAddress2());
        }

        // city
        if (patient.getCity() != null) {
            qrdaCat1Patient.setCity(patient.getCity());
        }

        // state
        if (patient.getStateValue() != null) {
            qrdaCat1Patient.setState(patient.getStateValue());
        }

        // zip
        if (patient.getZipcode() != null) {
            qrdaCat1Patient.setPostalCode(patient.getZipcode());
        }

        // country
        if (patient.getCountry() != null) {
            qrdaCat1Patient.setCountry(patient.getCountry());
        }

        // gender
        if (patient.getGender() != null) {
            qrdaCat1Patient.setGender(patient.getGender());
        }

        // birth time
        if (patient.getBirthTime() != null) {
            qrdaCat1Patient.setBirthTime(new DateTime(patient.getBirthTime()).toString(DateTimeFormat.forPattern(JODA_TIME_HL7_DATE_FORMAT)));
        }

        // race
        if (patient.getRace() != null) {
            qrdaCat1Patient.setRace(patient.getRace());
        }

        // ethnicity
        if (patient.getEthnicity() != null) {
            qrdaCat1Patient.setEthnicity(patient.getEthnicity());
        }

        // language
        if (patient.getLanguage() != null) {
            qrdaCat1Patient.setLanguage(patient.getLanguage());
        }

        return qrdaCat1Patient;
    }

    private QrdaCat1Provider buildProviderSection(HqmfDocument hqmfDocument) {

        QrdaCat1Provider provider = new QrdaCat1Provider();

        if (hqmfDocument.getProvider() != null) {

            // Name
            provider.setProviderName(formatProviderName(hqmfDocument.getProvider()));
            provider.setFirstName(hqmfDocument.getProvider().getFirstName());
            provider.setLastName(hqmfDocument.getProvider().getLastName());

            // NPI
            List<QrdaCat1Id> ids = new ArrayList<>();
            QrdaCat1Id id = new QrdaCat1Id();
            id.setExtension(hqmfDocument.getProvider().getNpi());
            id.setRoot(TemplateIdRoot.NPI.getRoot());
            ids.add(id);
            provider.setId(ids);

            // Address
            QrdaCat1Address address = new QrdaCat1Address();
            address.setAddress1(hqmfDocument.getProvider().getAddress());
            address.setAddress2(hqmfDocument.getProvider().getAddress2());
            address.setCity(hqmfDocument.getProvider().getCity());
            address.setState(hqmfDocument.getProvider().getStateValue());
            address.setZipCode(hqmfDocument.getProvider().getZipcode());
            address.setCountry(hqmfDocument.getProvider().getCountry());
            provider.setAddress(address);

            // Telcom
            List<QrdaCat1Telecom> telcomList = new ArrayList<>();
            if (hqmfDocument.getProvider().getProviderPhoneNumbers() != null) {
                for (ProviderPhoneNumber phoneNumber : hqmfDocument.getProvider().getProviderPhoneNumbers()) {
                    QrdaCat1Telecom telcom = new QrdaCat1Telecom();
                    telcom.setUse(phoneNumber.getPhoneNumberType());
                    telcom.setValue(phoneNumber.getPhoneNumber());
                    telcomList.add(telcom);
                }
            }
            provider.setPhoneNumbers(telcomList);
        }

        return provider;
    }

    private List<QrdaCat1Measure> buildMeasureSection(HqmfDocument hqmfDocument) {
        List<QrdaCat1Measure> qrdaCat1Measures = new ArrayList<>();
        QrdaCat1Measure measure = new QrdaCat1Measure();
        measure.setId(hqmfDocument.getId().toString());
        measure.setTitle(hqmfDocument.getTitle());
        measure.setHqmf_id(hqmfDocument.getHqmfId());
        measure.setHqmf_set_id(hqmfDocument.getHqmfSetId());
        measure.setHqmf_version_number(hqmfDocument.getHqmfVersionNumber().toString());
        qrdaCat1Measures.add(measure);
        return qrdaCat1Measures;
    }

    private QrdaCat1ReportingParameters buildReportingPeriodSection(HqmfDocument hqmfDocument) {
        QrdaCat1ReportingParameters reportingParameters = new QrdaCat1ReportingParameters();
        if (hqmfDocument.getMeasurePeriod() != null) {
            reportingParameters.setExtension(hqmfDocument.getMeasurePeriod().getId().toString());
            reportingParameters.setStartDateNumeric(hqmfDocument.getMeasurePeriod().getLow());
            reportingParameters.setEndDateNumeric(hqmfDocument.getMeasurePeriod().getHigh());

            // Format ordinal output for headers
            // DateTimeFormatter iso = ISODateTimeFormat.dateTime();
            DateTimeFormatter header = DateTimeFormat.forPattern(JODA_TIME_REPORTING_HEADER);
            DateTimeFormatter formatter = DateTimeFormat.forPattern(JODA_TIME_HL7_DATE_FORMAT);

            String startTime = hqmfDocument.getMeasurePeriod().getLow();
            if (startTime != null && startTime.length() > 8) {
                startTime = startTime.substring(0, 8);
            }
            String endTime = hqmfDocument.getMeasurePeriod().getHigh();
            if (endTime != null && endTime.length() > 8) {
                endTime = endTime.substring(0, 8);
            }
            DateTime low = formatter.parseDateTime(startTime);
            DateTime high = formatter.parseDateTime(endTime);
            reportingParameters.setReportingPeriod(QrdaCat1ReportingParameters.reportingPeriodHeader(header.print(low), header.print(high)));
        }
        return reportingParameters;
    }

    private List<QrdaCat1Entry> buildEntriesSection(Patient patient, HqmfDocument hqmfDocument) {

        List<QrdaCat1Entry> entries = new ArrayList<>();
        Integer patientId = patient.getId();
        Integer providerId = hqmfDocument.getProvider().getId();

        // Find all data criteria for the document
        Set<String> codeSystemSet = new HashSet<>();
        Iterable<HqmfDataCriteria> hqmfDataCriteriaIterable = hqmfDataCriteriaRepository.findAll(HqmfDataCriteriaPredicate.findDataCriteriaForHqmf(hqmfDocument.getId()));
        for (HqmfDataCriteria hqmfDataCriteria : hqmfDataCriteriaIterable) {
            if (hqmfDataCriteria.getCodeListId() != null && (!codeSystemSet.contains(hqmfDataCriteria.getCodeListId()))) {
                codeSystemSet.add(hqmfDataCriteria.getCodeListId());
            }
        }

        /*
            Allergy templatesQ
         */
        List<PatientAllergy> allergies = new JPAQuery(entityManager).from(patientAllergy)
                .innerJoin(patientAllergy.template, templateRoot)
                .innerJoin(patientAllergy.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .innerJoin(patientAllergy.code, code)
                .innerJoin(code.valueSetCodes, valueSetCode)
                .innerJoin(valueSetCode.valueSet, valueSet)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientAllergy.statusCodeName.equalsIgnoreCase(STATUS_CODE_COMPLETED)
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.ALLERGY__QRDA_MEDICATION_ADVERSE_EFFECT.getRoot()
                                , TemplateIdRoot.ALLERGY__QRDA_MEDICATION_INTOLERANCE.getRoot()
                                , TemplateIdRoot.ALLERGY__QRDA_MEDICATION_ALLERGY.getRoot()
                                , TemplateIdRoot.ALLERGY__QRDA_INTOLERANCE.getRoot()
                        )
                        , valueSet.hl7Oid.in(codeSystemSet)
                )
                .list(patientAllergy);
        for (PatientAllergy record : allergies) {
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(),
                    record.getTemplate(), record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), null, null, null, record.getEffectiveTimeStart(),   // no value code result value / result value unit
                    record.getEffectiveTimeEnd()
                    , null, null, null, record.getSeverityCode(), null, null)); // no reason or ord
        }

        /*
            Encounter templates
         */
        List<PatientEncounter> encounters = new JPAQuery(entityManager).from(patientEncounter)
                .innerJoin(patientEncounter.template, templateRoot)
                .innerJoin(patientEncounter.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientEncounter.provider, provider)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.ENCOUNTER__QRDA_PERFORMED.getRoot()
                                , TemplateIdRoot.ENCOUNTER__QRDA_ORDER.getRoot()
                        )
                )
                .list(patientEncounter);
        for (PatientEncounter record : encounters) {

            // Loop for all codes
            if (record.getCode() != null) {
                for (ValueSetCode codeValueSetCode : record.getCode().getValueSetCodes()) {
                    if (codeValueSetCode.getValueSet() != null && codeValueSetCode.getValueSet().getHl7Oid() != null) {
                        if (codeSystemSet.contains(codeValueSetCode.getValueSet().getHl7Oid())) {
                            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(), record.getTemplate(),
                                    record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), null, null, null, record.getEffectiveTimeStart(), record.getEffectiveTimeEnd()    // no value code result value / result value unit
                                    , null, null, null, null, codeValueSetCode.getValueSet().getHl7Oid(), null)); // no reason code, ord or sev
                        }
                    }
                }
            }
        }

        /*
            Medical Equipment Templates
         */
        List<PatientMedicalEquipment> medicalEquipment = new JPAQuery(entityManager).from(patientMedicalEquipment)
                .innerJoin(patientMedicalEquipment.template, templateRoot)
                .innerJoin(patientMedicalEquipment.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .innerJoin(patientMedicalEquipment.code, code)
                .innerJoin(code.valueSetCodes, valueSetCode)
                .innerJoin(valueSetCode.valueSet, valueSet)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientMedicalEquipment.statusCodeName.equalsIgnoreCase(STATUS_CODE_COMPLETED)
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.MEDICAL_EQUIPMENT__QRDA.getRoot()
                        )
                        , valueSet.hl7Oid.in(codeSystemSet)
                )
                .list(patientMedicalEquipment);
        for (PatientMedicalEquipment record : medicalEquipment) {
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(), record.getTemplate(),
                    record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), null, null, null, record.getEffectiveTimeStart(), record.getEffectiveTimeEnd()    // no value code result value / result value unit
                    , record.getReasonCode(), record.getReasonEffectiveTime(), null, null, null, null)); // no ord or sev
        }

        /*
            Medications Templates
         */
        List<PatientMedication> medications = new JPAQuery(entityManager).from(patientMedication)
                .innerJoin(patientMedication.template, templateRoot)
                .innerJoin(patientMedication.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .innerJoin(patientMedication.productCode, code)
                .innerJoin(code.valueSetCodes, valueSetCode)
                .innerJoin(valueSetCode.valueSet, valueSet)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.MEDICATIONS__QRDA_DISCHARGE_MEDICATION_ACTIIVE.getRoot()
                                , TemplateIdRoot.MEDICATIONS__QRDA_MEDICATIONS_ADMINISTERED.getRoot()
                                , TemplateIdRoot.MEDICATIONS__QRDA_MEDICATIONS_ADMINISTERED2.getRoot()
                                , TemplateIdRoot.MEDICATIONS__QRDA_ORDERED.getRoot()
                                , TemplateIdRoot.MEDICATIONS__QRDA_DISPENSED.getRoot()
                        )
                        , valueSet.hl7Oid.in(codeSystemSet)
                )
                .list(patientMedication);
        for (PatientMedication record : medications) {
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(),
                    record.getTemplate(), null, null, null, null, record.getProductCode(), null, null, record.getEffectiveTimeStart(),  // result value / result value unit
                    record.getEffectiveTimeEnd()
                    , record.getReasonCode(), record.getReasonEffectiveTime(), null, null, null, null)); // no ord or sev
        }

        /*
            Plan of Care Template
         */
        List<PatientPlanOfCare> plansOfCare = new JPAQuery(entityManager).from(patientPlanOfCare)
                .innerJoin(patientPlanOfCare.template, templateRoot)
                .innerJoin(patientPlanOfCare.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .innerJoin(patientPlanOfCare.code, code)
                .innerJoin(code.valueSetCodes, valueSetCode)
                .innerJoin(valueSetCode.valueSet, valueSet)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientPlanOfCare.statusCodeName.equalsIgnoreCase(STATUS_CODE_COMPLETED)
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.PLAN_OF_CARE__QRDA.getRoot()
                        )
                        , valueSet.hl7Oid.in(codeSystemSet)
                )
                .list(patientPlanOfCare);
        for (PatientPlanOfCare record : plansOfCare) {
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(), record.getTemplate(),
                    record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), null, null, null, record.getEffectiveTimeStart(), record.getEffectiveTimeEnd(),   // no value code result value / result value unit
                    null, null, null, null, null, null)); // no reason code, ord, sev
        }

        /*
            Problem templates
         */
        List<PatientProblem> problems = new JPAQuery(entityManager).from(patientProblem)
                .innerJoin(patientProblem.template, templateRoot)
                .innerJoin(patientProblem.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientProblem.statusCodeName.equalsIgnoreCase(STATUS_CODE_COMPLETED)
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.CONDITION__QRDA_CHARACTERISTIC_AGE.getRoot()
                                , TemplateIdRoot.CONDITION__QRDA_DIAGNOSIS_RESOLVED.getRoot()
                                , TemplateIdRoot.CONDITION__QRDA_DIAGNOSIS_ACTIVE.getRoot()
                                , TemplateIdRoot.CONDITION__QRDA_DIAGNOSIS_INACTIVE.getRoot()
                                , TemplateIdRoot.CONDITION__QRDA_ECOG_STATUS.getRoot()
                                , TemplateIdRoot.CONDITION__QRDA_GESTATIONAL_AGE.getRoot()
                                , TemplateIdRoot.CONDITION__QRDA_SYMPTOM_ACTIVE.getRoot()
                        )
                )
                .list(patientProblem);
        for (PatientProblem record : problems) {
            if (record.getProblemCode() != null) {
                for (ValueSetCode codeValueSetCode : record.getProblemCode().getValueSetCodes()) {
                    if (codeValueSetCode.getValueSet() != null && codeValueSetCode.getValueSet().getHl7Oid() != null) {
                        if (codeSystemSet.contains(codeValueSetCode.getValueSet().getHl7Oid())) {
                            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(),
                                    record.getTemplate(), record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), record.getProblemCode(), null, null, record.getEffectiveTimeStart(),  // result value / result value unit
                                    record.getEffectiveTimeEnd()
                                    , null, null, record.getOrdinalityCode(), record.getSeverityCode(), null, codeValueSetCode.getValueSet().getHl7Oid())); // no reason
                        }
                    }
                }
            }
        }

        /*
            Payer Templates
         */
        List<PatientPayer> insuranceCompanies = new JPAQuery(entityManager).from(patientPayer)
                .innerJoin(patientPayer.template, templateRoot)
                .innerJoin(patientPayer.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.PATIENT_CHARACTERISTIC_PAYER.getRoot()
                        )
                )
                .list(patientPayer);
        for (PatientPayer record : insuranceCompanies) {
            String codeValueSetId = null;
            if (record.getPayerCode() != null) {
                for (ValueSetCode codeValueSetCode : record.getPayerCode().getValueSetCodes()) {
                    if (codeValueSetCode.getValueSet() != null && codeValueSetCode.getValueSet().getHl7Oid() != null) {
                        codeValueSetId = codeValueSetCode.getValueSet().getHl7Oid();
                    }
                }
            }
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(),
                    record.getTemplate(), record.getPayerCode(), null, null, null, null, null, null, null, null // no value or effective time, result value / result value unit
                    , null, null, null, null, codeValueSetId, null)); // no reason, ord, sev
        }

        /*
            Procedure templates
         */
        List<PatientProcedure> procedures = new JPAQuery(entityManager).from(patientProcedure)
                .innerJoin(patientProcedure.template, templateRoot)
                .innerJoin(patientProcedure.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.PROCEDURE__QRDA_EXAM_PERFORMED.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_PROVIDER_PATIENT_COMM.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_INTERVENTION_ORDERED.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_INTERVENTION_PERFORMED.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_PROVIDER_PROVIDER_COMM.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_PATIENT_PROVIDER_COMM.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_PROCEDURE_PERFORMED.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_UNLABELED_2.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_RISK_CATEGORY_ASSESSMENT.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_DIAGNOSIS_STUDY_PERFORMED.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_DIAGNOSIS_STUDY_RESULT.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_DIAGNOSTIC_STUDY_ORDER.getRoot()
                                , TemplateIdRoot.PROCEDURE__QRDA_ORDER.getRoot()
                        )
                )
                .list(patientProcedure);
        for (PatientProcedure record : procedures) {
            Code valueCode = record.getValueCode();
            if (valueCode == null) {
                valueCode = record.getResultValueCode();
            }

            // Loop for all codes
            if (record.getCode() != null) {
                for (ValueSetCode codeValueSetCode : record.getCode().getValueSetCodes()) {
                    if (codeValueSetCode.getValueSet() != null && codeValueSetCode.getValueSet().getHl7Oid() != null) {
                        if (codeSystemSet.contains(codeValueSetCode.getValueSet().getHl7Oid())) {
                            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(),
                                    record.getTemplate(), record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), valueCode, record.getValueString(), record.getValueUnit(), record.getEffectiveTimeStart(),
                                    record.getEffectiveTimeEnd(), record.getReasonCode(), record.getReasonEffectiveTime()
                                    , record.getOrdinalityCode(), null, codeValueSetCode.getValueSet().getHl7Oid(), null)); // no severity}
                        }
                    }
                }
            }

        }

        /*
            Result template
         */
        List<PatientResult> results = new JPAQuery(entityManager).from(patientResult)
                .innerJoin(patientResult.template, templateRoot)
                .innerJoin(patientResult.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.RESULT__QRDA_FUNCTIONAL_FINDING.getRoot()
                                , TemplateIdRoot.RESULT__QRDA_INTERVENTION.getRoot()
                                , TemplateIdRoot.RESULT__QRDA_LAB_FINDING.getRoot()
                                , TemplateIdRoot.RESULT__QRDA_LAB_PERFORMED.getRoot()
                                , TemplateIdRoot.RESULT__QRDA_LAB_ORDER.getRoot()
                                , TemplateIdRoot.RESULT__QRDA_LAB_RESULT.getRoot()
                        )
                )
                .list(patientResult);
        for (PatientResult record : results) {
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(),
                    record.getTemplate(), record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), record.getValueCode(), record.getResultValue(), record.getResultValueUnit(), record.getEffectiveTimeStart(),
                    record.getEffectiveTimeEnd()
                    , record.getReasonCode(), record.getReasonEffectiveTime(), null, null, null, null)); // no ordinality or severity
        }

        /*
            Social History
         */
        List<PatientSocialHistory> histories = new JPAQuery(entityManager).from(patientSocialHistory)
                .innerJoin(patientSocialHistory.template, templateRoot)
                .innerJoin(patientSocialHistory.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .innerJoin(patientSocialHistory.valueCode, code)
                .innerJoin(code.valueSetCodes, valueSetCode)
                .innerJoin(valueSetCode.valueSet, valueSet)
                .where(
                        clinicalDocument.dateDisabled.isNull()
                        , patientSocialHistory.statusCodeName.equalsIgnoreCase(STATUS_CODE_COMPLETED)
                        , patientInfo.id.eq(patientId)
                        , provider.id.eq(providerId)
                        , templateRoot.hl7Oid.in(
                                TemplateIdRoot.SOCIAL_HISTORY__QRDA_TOBACCO_USE.getRoot()
                        )
                        , valueSet.hl7Oid.in(codeSystemSet)
                )
                .list(patientSocialHistory);
        for (PatientSocialHistory record : histories) {
            entries.addAll(populateQrdaCat1EntryCodes(codeSystemSet, record.getTemplate(), record.getId(), record.getNegationDetail(), record.getTemplate(),
                    record.getCode(), record.getCode().getCodeName(), record.getCodeDescription(), record.getCode().getCodeSystem().getHl7Oid(), record.getValueCode(), null, null, record.getEffectiveTimeStart(), record.getEffectiveTimeEnd() // result value / result value unit
                    , null, null, null, null, null, null)); // no reason code or ordinality or severity
        }

        return entries;
    }

    private List<QrdaCat1Entry> populateQrdaCat1EntryCodes(Set<String> codeSystemSet, TemplateRoot recordTemplate, Long id, PatientNegationDetail negationDetail, TemplateRoot template, Code code, String codeName, String codeDescription, String codeSystem, Code valueCode, String resultValue, String resultValueUnit, Date effectiveTimeStart, Date effectiveTimeEnd, Code reasonCode, Date reasonEffectiveTime, Code ordinalityCode, Code severityCode, String codeValueSetArg, String valueValueSetArg) {

        List<QrdaCat1Entry> entries = new ArrayList<>();

        if (code != null && code.getValueSetCodes() != null) {
            //
            //  Found code, find corresponding value set (best match)
            String codeValueSetId = codeValueSetArg;    // This can be passed in for encounters and procedures
            if (codeValueSetId == null) {
                for (ValueSetCode codeValueSetCode : code.getValueSetCodes()) {
                    if (codeValueSetCode.getValueSet() != null && codeValueSetCode.getValueSet().getHl7Oid() != null) {
                        if (codeSystemSet.contains(codeValueSetCode.getValueSet().getHl7Oid())) {
                            codeValueSetId = codeValueSetCode.getValueSet().getHl7Oid();
                            break;
                        }
                    }
                }
            }

            //
            //  Find the value set for a value if found
            if (valueCode != null && valueCode.getValueSetCodes() != null) {
                String valueCodeValueSetOid = null;
                for (ValueSetCode valueCodeValueSetCode : valueCode.getValueSetCodes()) {
                    if (valueCodeValueSetCode.getValueSet() != null && valueCodeValueSetCode.getValueSet().getHl7Oid() != null) {
                        if (codeSystemSet.contains(valueCodeValueSetCode.getValueSet().getHl7Oid())) {
                            valueCodeValueSetOid = valueCodeValueSetCode.getValueSet().getHl7Oid();
                            break;
                        }
                    }
                }
                if (valueCodeValueSetOid != null) {
                    entries.add(addQrdaCat1Entry(recordTemplate, id, negationDetail, template, code, codeName, codeDescription,
                            codeSystem, valueCode, resultValue, resultValueUnit, effectiveTimeStart, effectiveTimeEnd, reasonCode,
                            reasonEffectiveTime, ordinalityCode, severityCode, codeValueSetId, valueCodeValueSetOid));
                } else {
                    entries.add(addQrdaCat1Entry(recordTemplate, id, negationDetail, template, code, codeName, codeDescription,
                            codeSystem, valueCode, resultValue, resultValueUnit, effectiveTimeStart, effectiveTimeEnd, reasonCode,
                            reasonEffectiveTime, ordinalityCode, severityCode, codeValueSetId, null));
                }
            } else {
                entries.add(addQrdaCat1Entry(recordTemplate, id, negationDetail, template, code, codeName, codeDescription,
                        codeSystem, valueCode, resultValue, resultValueUnit, effectiveTimeStart, effectiveTimeEnd, reasonCode,
                        reasonEffectiveTime, ordinalityCode, severityCode, codeValueSetId, null));
            }
        } else if (valueCode != null && valueCode.getValueSetCodes() != null) {
            //
            //  Code not found but value exists, find value set for value
            String valueCodeValueSetOid = valueValueSetArg;
            if (valueCodeValueSetOid == null) {
                for (ValueSetCode valueCodeValueSetCode : valueCode.getValueSetCodes()) {
                    if (valueCodeValueSetCode.getValueSet() != null && valueCodeValueSetCode.getValueSet().getHl7Oid() != null) {
                        if (codeSystemSet.contains(valueCodeValueSetCode.getValueSet().getHl7Oid())) {
                            valueCodeValueSetOid = valueCodeValueSetCode.getValueSet().getHl7Oid();
                            break;
                        }
                    }
                }
            }
            if (valueCodeValueSetOid != null) {
                entries.add(addQrdaCat1Entry(recordTemplate, id, negationDetail, template, code, codeName, codeDescription,
                        codeSystem, valueCode, resultValue, resultValueUnit, effectiveTimeStart, effectiveTimeEnd, reasonCode,
                        reasonEffectiveTime, ordinalityCode, severityCode, null, valueCodeValueSetOid));
            }
        } else {
            //
            //  Neither found code or found value
            entries.add(addQrdaCat1Entry(recordTemplate, id, negationDetail, template, code, codeName, codeDescription,
                    codeSystem, valueCode, resultValue, resultValueUnit, effectiveTimeStart, effectiveTimeEnd, reasonCode,
                    reasonEffectiveTime, ordinalityCode, severityCode, null, null));
        }

        return entries;
    }

    private QrdaCat1Entry addQrdaCat1Entry(TemplateRoot recordTemplate, Long id, PatientNegationDetail negationDetail, TemplateRoot template, Code code, String codeName, String codeDescription, String codeSystem, Code valueCode, String resultValue, String resultValueUnit, Date effectiveTimeStart, Date effectiveTimeEnd, Code reasonCode, Date reasonEffectiveTime, Code ordinalityCode, Code severityCode, String codeValueSetId, String valueCodeValueSetOid) {
        QrdaCat1Entry entry = new QrdaCat1Entry();

        // ID
        entry.setId(id.toString());
        entry.setNegationInd((negationDetail != null ? "true" : "false"));
        entry.setUuid(UUID.randomUUID().toString());

        // Template
        if (recordTemplate != null) {
            entry.setTemplate(recordTemplate.getHl7Oid());
        }

        // Code
        setEntryCode(entry, code, template, codeName, codeDescription, codeSystem, codeValueSetId);

        // Effective Time
        if (effectiveTimeStart != null) {
            entry.setLow(formatTimeValue(effectiveTimeStart));
        }
        if (effectiveTimeEnd != null) {
            entry.setHigh(formatTimeValue(effectiveTimeEnd));
        }

        // Value code
        if (valueCode != null) {
            entry.setValueCode(valueCode.getCodeName());
            if (valueCode.getCodeSystem() != null) {
                entry.setValueCodeSystem(valueCode.getCodeSystem().getHl7Oid());
            }
            entry.setValueDescription(valueCode.getDescription());
            if (valueCodeValueSetOid != null) {
                entry.setValueValueSetOid(valueCodeValueSetOid);
            }
        } else {
            entry.setResultValue(resultValue);
            entry.setResultValueUnit(resultValueUnit);
        }

        // Add the reason
        setEntryReason(entry, reasonCode, reasonEffectiveTime);

        // Add the ordinality
        setEntryOrdinality(entry, ordinalityCode);

        // Add the severity
        setEntrySeverity(entry, severityCode);

        return entry;
    }

    private void setEntryReason(QrdaCat1Entry entry, Code reasonCode, Date reasonEffectiveTime) {
        if (reasonCode != null) {
            QrdaCat1Reason qrdaCat1Reason = new QrdaCat1Reason();
            if (reasonEffectiveTime != null) {
                qrdaCat1Reason.setEffectiveTime(formatTimeValue(reasonEffectiveTime));
            }
            qrdaCat1Reason.setId(reasonCode.getId().toString());
            qrdaCat1Reason.setReasonCode(reasonCode.getCodeName());
            if (reasonCode.getCodeSystem() != null) {
                qrdaCat1Reason.setReasonCodeSystem(reasonCode.getCodeSystem().getHl7Oid());
            }
            qrdaCat1Reason.setReasonCodeDescription(reasonCode.getDescription());
            if (reasonCode.getValueSetCodes() != null) {
                for (ValueSetCode reasonValueSet : reasonCode.getValueSetCodes()) {
                    qrdaCat1Reason.setReasonCodeValueSetOid(reasonValueSet.getValueSet().getHl7Oid());
                    break;
                }
            }
        }
    }

    private void setEntryOrdinality(QrdaCat1Entry entry, Code ordinalityCode) {
        if (ordinalityCode != null) {
            QrdaCat1Ordinality ordinality = new QrdaCat1Ordinality();
            ordinality.setId(ordinalityCode.getId().toString());
            ordinality.setOrdinalityCode(ordinalityCode.getCodeName());
            ordinality.setOrdinalityCodeDescription(ordinalityCode.getDescription());
            if (ordinalityCode.getCodeSystem() != null) {
                ordinality.setOrdinalityCodeSystem(ordinalityCode.getCodeSystem().getHl7Oid());
            }
            if (ordinalityCode.getValueSetCodes() != null) {
                for (ValueSetCode ordinalityValueSet : ordinalityCode.getValueSetCodes()) {
                    ordinality.setOrdinalityValueSetOid(ordinalityValueSet.getValueSet().getHl7Oid());
                    break;
                }
            }
            entry.setOrdinality(ordinality);
        }
    }

    private void setEntryCode(QrdaCat1Entry entry, Code code, TemplateRoot template, String codeName, String codeDescription, String codeSystem, String codeValueSetOid) {
        if (code != null) {
            if (template != null) {
                // TODO fix this! (we need template data loaded in template_root table)
                // entry.setDescription(template);
                entry.setDescription(code.getDescription());
            } else {
                entry.setDescription(code.getDescription());
            }
            entry.setCodeCode(code.getCodeName());
            if (code.getCodeSystem() != null) {
                entry.setCodeSystem(code.getCodeSystem().getHl7Oid());
            }
            entry.setCodeDescription(code.getDescription());
            if (codeValueSetOid != null) {
                entry.setCodeValueSetOid(codeValueSetOid);
            }
        } else {
            entry.setCodeCode(codeName);
            entry.setCodeDescription(codeDescription);
            entry.setCodeValueSetOid(codeSystem);
        }
    }

    private void setEntrySeverity(QrdaCat1Entry entry, Code severityCode) {
        if (severityCode != null) {
            QrdaCat1Severity severity = new QrdaCat1Severity();
            severity.setId(severityCode.getId().toString());
            severity.setSeverityCode(severityCode.getCodeName());
            severity.setSeverityCodeDescription(severityCode.getDescription());
            if (severityCode.getCodeSystem() != null) {
                severity.setSeverityCodeSystem(severityCode.getCodeSystem().getHl7Oid());
            }
            if (severityCode.getValueSetCodes() != null) {
                for (ValueSetCode severityValueSet : severityCode.getValueSetCodes()) {
                    severity.setSeverityValueSetOid(severityValueSet.getValueSet().getHl7Oid());
                    break;
                }
            }
            entry.setSeverity(severity);
        }
    }

    private String formatProviderName(Provider provider) {
        return String.format("%s %s", provider.getFirstName(), provider.getLastName());
    }

    private String formatTimeValue(Date date) {
        if (date == null) {
            return "UNK";
        } else {
            return DateTimeFormat.forPattern(JODA_TIME_HL7_DATE_FORMAT).print(new DateTime(date));
        }
    }

}
