package com.ainq.caliphr.persistence.dao.impl;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientInfo.patientInfo;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QPatientPayer.patientPayer;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QResultPatient.resultPatient;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Repository;

import com.ainq.caliphr.persistence.dao.MeasureSupplementalDataDao;
import com.ainq.caliphr.persistence.dao.ValueSetDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Result;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ResultSupplemental;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.CodeRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.ResultSupplementalRepository;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.QTuple;
import com.mysema.query.types.query.ListSubQuery;

@Repository
public class MeasureSupplementalDataDaoImpl implements MeasureSupplementalDataDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private ResultSupplementalRepository resultSupplementalRepository;
    @Autowired
    private ValueSetDao valueSetDao;

    @Override
    public void determineSupplementalData(Result result, Integer userId) {

        // TODO: consider refactoring all these queries into one UNION query

        ListSubQuery<Integer> patientIdExp = new JPASubQuery()
                .from(resultPatient)
                .where(resultPatient.result.id.eq(result.getId()))
                .list(resultPatient.patient.id);

        // Race
        List<Tuple> supplementalResults = new JPAQuery(entityManager)
                .from(patientInfo)
                .where(patientInfo.id.in(patientIdExp))
                .groupBy(patientInfo.raceCode.id)
                .list(patientInfo.raceCode.id, patientInfo.raceCode.id.count());
        saveSupplementalResults(result, supplementalResults, userId);

        // Ethnicity
        supplementalResults = new JPAQuery(entityManager)
                .from(patientInfo)
                .where(patientInfo.id.in(patientIdExp))
                .groupBy(patientInfo.ethnicityCode.id)
                .list(patientInfo.ethnicityCode.id, patientInfo.ethnicityCode.id.count());
        saveSupplementalResults(result, supplementalResults, userId);

        // Payer
        supplementalResults = new JPAQuery(entityManager)
                .from(patientPayer)
                .where(patientPayer.patient.id.in(patientIdExp))
                .groupBy(patientPayer.payerCode.id)
                .list(patientPayer.payerCode.id, patientPayer.payerCode.id.count());
        saveSupplementalResults(result, supplementalResults, userId);

        // Gender
        //
        // Note: due to oid "2.16.840.1.113883.5.1" not being present in the value set bundle,
        //       it needs to be temporarily converted to a different value.
        // TODO: find a permanent solution
        supplementalResults = new JPAQuery(entityManager)
                .from(patientInfo)
                .where(patientInfo.id.in(patientIdExp))
                .groupBy(patientInfo.genderCode.codeName)
                .list(patientInfo.genderCode.codeName, patientInfo.genderCode.codeName.count());
        saveGenderSupplementalResults(result, supplementalResults, userId);

    }

    private void saveSupplementalResults(Result result, List<Tuple> list, Integer userId) {
        list.stream().filter(countResult -> countResult != null).forEach(countResult -> {
            ResultSupplemental resultSplmntl = new ResultSupplemental();
            Integer codeId = countResult.get(0, Integer.class);
            if (codeId != null) {
				resultSplmntl.setCode(codeRepository.getOne(codeId));
	            resultSplmntl.setResultValue(countResult.get(1, Long.class).intValue());
	            resultSplmntl.setUserCreated(userId);
	            resultSplmntl.setDateCreated(new Date());
	            resultSplmntl.setUserUpdated(userId);
	            resultSplmntl.setDateUpdated(new Date());
	            result.addResultSupplemental(resultSplmntl);
	            resultSupplementalRepository.save(resultSplmntl);
            }
        });
    }

    // Note: due to oid "2.16.840.1.113883.5.1" not being present in the value set bundle,
    //       it needs to be temporarily converted to a different value.
    // TODO: find a permanent solution
    private void saveGenderSupplementalResults(Result result, List<Tuple> supplementalResults, Integer userId) {
        if (MALE_CODE == null) {
            loadAndCacheGenderCodes();
        }
        QTuple qTuple = new QTuple(patientInfo.genderCode.id, patientInfo.genderCode.id.count());
        List<Tuple> converted = supplementalResults.stream().map(suppResult -> {
            String codeName = suppResult.get(0, String.class);
            Integer convertedCode = "M".equals(codeName) ? MALE_CODE : "F".equals(codeName) ? FEMALE_CODE : null;

            Long suppResultValue = suppResult.get(1, Long.class);
            return convertedCode != null ? qTuple.newInstance(convertedCode, suppResultValue) : null;
        }).collect(Collectors.toList());

        saveSupplementalResults(result, converted, userId);

    }

    private Integer MALE_CODE = null;
    private Integer FEMALE_CODE = null;

    private void loadAndCacheGenderCodes() {
        StreamUtils.createStreamFromIterator(valueSetDao.findCodesByValueSetOid("2.16.840.1.113762.1.4.1").iterator()).forEach(valueSetCode -> {
            if ("M".equals(valueSetCode.getCode().getCodeName())) {
                MALE_CODE = valueSetCode.getCode().getId();
            } else if ("F".equals(valueSetCode.getCode().getCodeName())) {
                FEMALE_CODE = valueSetCode.getCode().getId();
            }
        });
    }

}
