package com.ainq.caliphr.persistence.dao.impl;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.dao.MeasureSupplementalDataDao;
import com.ainq.caliphr.persistence.dao.SecureTableDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.*;
import com.ainq.caliphr.persistence.util.predicate.hqmf.HqmfAttributePredicate;
import com.ainq.caliphr.persistence.util.predicate.hqmf.HqmfPopulationSetPredicate;
import com.ainq.caliphr.persistence.util.predicate.hqmf.HqmfPredicate;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUserSecurity.applicationUserSecurity;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QBundle.bundle;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QClinicalDocument.clinicalDocument;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfAttribute.hqmfAttribute;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfDataCriteria.hqmfDataCriteria;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfDocument.hqmfDocument;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfPopulation.hqmfPopulation;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfPopulationSet.hqmfPopulationSet;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QOrganization.organization;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientEncounter.patientEncounter;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientInfo.patientInfo;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPracticeAvailableMeasure.practiceAvailableMeasure;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPracticeGroup.practiceGroup;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QProvider.provider;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QResult.result;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QResultPatient.resultPatient;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QResultSupplemental.resultSupplemental;

import java.util.*;
import java.util.stream.Collectors;

//import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfSourceDataCriteria.hqmfSourceDataCriteria;
//import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfPrecondition.hqmfPrecondition;

@Repository
public class MeasureDaoImpl implements MeasureDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BundleRepository bundleRepository;

    @Autowired
    private HqmfAttributeRepository hqmfAttributeRepository;

    @Autowired
    private HqmfDocumentRepository hqmfDocumentRepository;

    @Autowired
    private HqmfPopulationSetRepository hqmfPopulationSetRepository;

    @Autowired
    private PatientInfoRepository patientInfoRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private MeasureSupplementalDataDao measureSupplementalDataDao;

    @Autowired
    SecureTableDao secureTableDao;

    @Transactional
    @Override
    public HqmfDocument getActiveHqmfDocumentByCmsId(String cmsId, Integer providerId) {
        return hqmfDocumentRepository.findOne(HqmfPredicate.searchForActiveHqmfDocumentByCmsId(cmsId, providerId));
    }

    @Transactional
    @Override
    public Iterable<HqmfDocument> getAllActiveHqmfDocuments(Integer providerId, Integer userId) {

        // fetch a number of items eagerly to improve performance

        QHqmfDocument qHqmfdoc = QHqmfDocument.hqmfDocument;
        QHqmfPopulationSet qHqmfPopSet = QHqmfPopulationSet.hqmfPopulationSet;
        List<HqmfDocument> hqmfDocs = new JPAQuery(entityManager).from(qHqmfdoc)
                .innerJoin(qHqmfdoc.measurePeriod).fetch()
                .leftJoin(qHqmfdoc.domain).fetch()
                .leftJoin(qHqmfdoc.hqmfPopulationSets, qHqmfPopSet).fetch()
                .leftJoin(qHqmfPopSet.results).fetch()
                .where(HqmfPredicate.searchForAllActiveHqmfDocumentsByProviderAndUser(providerId, userId))
                .distinct()
                .orderBy(qHqmfdoc.cmsId.asc())
                .list(qHqmfdoc);

        return hqmfDocs;
    }

    @Transactional
    @Override
    public Iterable<HqmfDocument> getHqmfDocumentsById(Iterable<Long> ids) {
        return hqmfDocumentRepository.findAll(ids);
    }

    @Transactional
    @Override
    public void saveHqmfDocument(HqmfDocument hqmfDocument) {
        hqmfDocumentRepository.save(hqmfDocument);
    }

    @Transactional
    @Override
    public Iterable<HqmfAttribute> getHqmfAttributesByHqmfDocId(Long hqmfDocId) {
        return hqmfAttributeRepository.findAll(HqmfAttributePredicate.getHqmfAttributesByHqmfDocId(hqmfDocId));
    }

    @Transactional
    @Override
    public List<Bundle> getActiveBundles() {
        return new JPAQuery(entityManager).from(bundle).where(bundle.dateDisabled.isNull()).list(bundle);
    }

    @Transactional
    @Override
    public Bundle getBundleById(Integer bundleId) {
        return bundleRepository.findOne(bundleId);
    }

    @Transactional
    @Override
    public List<PracticeAvailableMeasure> getAvailableMeasuresForProvider(Integer providerId, Integer bundleId) {

        //
        // optional feature - Specify What Measures to Use Per Practice
        Date now = new Date();

        // Both dates null
        BooleanBuilder d1 = new BooleanBuilder();
        d1.and(practiceAvailableMeasure.effectiveDateStart.isNull());
        d1.and(practiceAvailableMeasure.effectiveDateEnd.isNull());

        // End date null
        BooleanBuilder d2 = new BooleanBuilder();
        d2.and(practiceAvailableMeasure.effectiveDateStart.isNotNull());
        d2.and(practiceAvailableMeasure.effectiveDateStart.loe(now));
        d2.and(practiceAvailableMeasure.effectiveDateEnd.isNull());

        // Start date null
        BooleanBuilder d3 = new BooleanBuilder();
        d3.and(practiceAvailableMeasure.effectiveDateStart.isNull());
        d3.and(practiceAvailableMeasure.effectiveDateEnd.isNotNull());
        d3.and(practiceAvailableMeasure.effectiveDateEnd.goe(now));

        // Both dates set
        BooleanBuilder d4 = new BooleanBuilder();
        d4.and(practiceAvailableMeasure.effectiveDateStart.isNotNull());
        d4.and(practiceAvailableMeasure.effectiveDateStart.loe(now));
        d4.and(practiceAvailableMeasure.effectiveDateEnd.isNotNull());
        d4.and(practiceAvailableMeasure.effectiveDateEnd.goe(now));

        return new JPAQuery(entityManager).from(practiceAvailableMeasure)
                .innerJoin(practiceAvailableMeasure.bundle, bundle)
                .innerJoin(practiceAvailableMeasure.group, practiceGroup)
                .innerJoin(practiceGroup.providers, provider)
                .where(provider.id.eq(providerId)
                        , practiceAvailableMeasure.dateDisabled.isNull()
                        , bundle.id.eq(bundleId)
                        , d1.or(d2).or(d3).or(d4))  // date logic
                .list(practiceAvailableMeasure);
    }

    @Transactional
    @Override
    public void saveResult(Long hqmfDocId, String populationSetKey, int index, Result result, Iterable<Integer> patientIdList, Integer userId) {
        getPopulationSet(hqmfDocId, populationSetKey, index).addResult(result);
        patientIdList.forEach(patientId -> {
            ResultPatient resultPatient = new ResultPatient();
            resultPatient.setPatient(patientInfoRepository.getOne(patientId));
            resultPatient.setUserCreated(userId);
            resultPatient.setDateCreated(new Date());
            resultPatient.setUserUpdated(userId);
            resultPatient.setDateUpdated(new Date());
            result.addResultPatient(resultPatient);
        });
        resultRepository.save(result);

        measureSupplementalDataDao.determineSupplementalData(result, userId);
    }

    @Transactional
    @Override
    public HqmfPopulationSet getPopulationSet(Long hqmfDocId, String populationSetKey, int index) {
        return hqmfPopulationSetRepository.findOne(HqmfPopulationSetPredicate.searchByPopulationSetKey(hqmfDocId, populationSetKey, index));
    }

    @Transactional
    @Override
    public List<Patient> getPatientByActiveResultPatientForResultId(Long resultId, Integer userId) {

        // Input validation
        if (userId == null || userId <= 0 || resultId == null || resultId <= 0) {
            return Collections.emptyList();
        }

        // security check on user
        List<ResultPatient> resultPatients = new JPAQuery(entityManager).from(resultPatient)
                .innerJoin(resultPatient.patient, patientInfo)
                .innerJoin(patientInfo.clinicalDocuments, clinicalDocument)
                .innerJoin(patientInfo.patientEncounters, patientEncounter)
                .innerJoin(patientEncounter.provider, provider)
                .innerJoin(provider.group, practiceGroup)
                .innerJoin(provider.applicationUserSecurities, applicationUserSecurity)
                .innerJoin(practiceGroup.organization, organization)
                .where(
                        applicationUserSecurity.user.id.eq(userId),
                        applicationUserSecurity.dateDisabled.isNull(),
                        resultPatient.dateDisabled.isNull(),
                        resultPatient.result.id.eq(resultId))
                .list(resultPatient);


        // Get all patient ids from the result to use them in an IN query
        // TODO: for efficiency, consider refactoring to use a subquery so the IDs will not need to be bound,
        //       and perhaps even get all the data with just one query
        List<Integer> patientIds = StreamUtils.createStreamFromIterator(resultPatients.iterator())
                .map(resultPatient -> resultPatient.getPatient().getId())
                .collect(Collectors.toList());

        // Pull the duplicates found in the query (due to security being able to fetch between org, practice and provider
        patientIds = new ArrayList<>(new LinkedHashSet<>(patientIds));

        return patientIds.isEmpty() ? Collections.emptyList() : secureTableDao.findPatientBasicInfo(patientIds);
    }

    @Transactional
    @Override
    public void markActiveMeasuresInactive(Integer providerId, Integer userId) {

        Set<Long> docIds = StreamUtils.createStreamFromIterator(
                hqmfDocumentRepository.findAll(HqmfPredicate.searchForAllActiveHqmfDocumentsByProviderAndUser(providerId, userId)).iterator())
                .map(HqmfDocument::getId)
                .collect(Collectors.toSet());

        Date now = new Date();
        new JPAUpdateClause(entityManager, hqmfAttribute)
                .where(hqmfAttribute.hqmfDoc.id.in(docIds))
                .set(hqmfAttribute.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, hqmfDataCriteria)
                .where(hqmfDataCriteria.hqmfDoc.id.in(docIds))
                .set(hqmfDataCriteria.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, resultPatient)
                .where(resultPatient.result.id.eqAny(new JPASubQuery().from(result).where(result.hqmfPopulationSet.hqmfDocument.id.in(docIds)).list(result.id)))
                .set(resultPatient.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, resultSupplemental)
                .where(resultSupplemental.result.id.eqAny(new JPASubQuery().from(result).where(result.hqmfPopulationSet.hqmfDocument.id.in(docIds)).list(result.id)))
                .set(resultSupplemental.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, result)
                .where(result.id.eqAny(new JPASubQuery().from(result).where(result.hqmfPopulationSet.hqmfDocument.id.in(docIds)).list(result.id)))
                .set(result.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, hqmfPopulationSet)
                .where(hqmfPopulationSet.hqmfDocument.id.in(docIds))
                .set(hqmfPopulationSet.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, hqmfPopulation)
                .where(hqmfPopulation.hqmfDoc.id.in(docIds))
                .set(hqmfPopulation.dateDisabled, now)
                .execute();

        new JPAUpdateClause(entityManager, hqmfDocument)
                .where(hqmfDocument.id.in(docIds))
                .set(hqmfDocument.dateDisabled, now)
                .execute();

    }

}
