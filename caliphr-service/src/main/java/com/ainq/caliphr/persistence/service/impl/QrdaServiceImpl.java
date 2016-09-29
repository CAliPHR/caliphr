package com.ainq.caliphr.persistence.service.impl;

import lombok.Data;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.ainq.caliphr.common.model.extract.cat1.QrdaCat1XmlFile;
import com.ainq.caliphr.common.model.extract.cat1.QrdaCat1ZipFile;
import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.dao.QrdaDao;
import com.ainq.caliphr.persistence.dao.UserSecurityDao;
import com.ainq.caliphr.persistence.dao.ValueSetDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfPopulationSet;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Result;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ResultSupplemental;
import com.ainq.caliphr.persistence.model.qrda.cat1.QrdaCat1Extract;
import com.ainq.caliphr.persistence.service.QrdaService;
import com.ainq.caliphr.persistence.util.JsonStringUtility;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mmelusky on 6/3/2015.
 */
@Service
public class QrdaServiceImpl implements QrdaService {

    @Autowired
    private MeasureDao measureDao;

    @Autowired
    private ValueSetDao valueSetDao;

    @Autowired
    private QrdaDao qrdaDao;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Transactional
    @Override
    public List<QrdaCat1ZipFile> exportQrdaCategory1(Iterable<Long> hqmfIds, Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("hqmfIds", hqmfIds);
            userSecurityDao.addUserAuditRecord(userId, AuditType.EDIT_REQUEST, QrdaServiceImpl.class.getName(), "exportQrdaCategory1", JsonStringUtility.buildJsonRequest(requestJson));
        }

        List<QrdaCat1ZipFile> response = new ArrayList<>();
        Iterable<HqmfDocument> hqmfDocs = measureDao.getHqmfDocumentsById(hqmfIds);
        for (HqmfDocument hqmfDocument : hqmfDocs) {

            if (hqmfDocument.getCmsId() != null && hqmfDocument.getCmsId().length() > 0
                    && hqmfDocument.getId() != null && hqmfDocument.getId() > 0) {
                QrdaCat1ZipFile zipFile = new QrdaCat1ZipFile();

                zipFile.setFileName(String.format("%s.zip", hqmfDocument.getCmsId()));
                zipFile.setZipFileContents(new ArrayList<>());

                List<Patient> patients = qrdaDao.findPatientsForHqmfDocument(hqmfDocument.getId(), userId);
                for (Patient patient : patients) {

                    // Query for the extract data
                    QrdaCat1Extract extract = qrdaDao.retrieveQrdaCat1Extract(patient, hqmfDocument);

                    // prepare the context
                    final Context ctx = new Context();
                    ctx.setVariable("cat1", extract);

                    // create the body using ThymeLeaf
                    String xmlContent = templateEngine.process("qrda_cat1/show.cat1.xml", ctx);

                    // And output the contents to an XML file
                    QrdaCat1XmlFile xmlFile = new QrdaCat1XmlFile();
                    StringBuilder xmlFileName = new StringBuilder(String.format("%s", patient.getId()));
                    if (patient.getFirstName() != null) {
                        xmlFileName.append(String.format("_%s", patient.getFirstName()));
                    }
                    if (patient.getLastName() != null) {
                        xmlFileName.append(String.format("_%s", patient.getLastName()));
                    }
                    xmlFileName.append(".xml");
                    xmlFile.setFileName(xmlFileName.toString());
                    xmlFile.setXmlConent(xmlContent);
                    zipFile.getZipFileContents().add(xmlFile);

                }

                response.add(zipFile);
            }
        }

        return response;
    }

    @Transactional
    @Override
    public String exportQrdaCategory3(Iterable<Long> hqmfIds, Integer userId) {

        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("hqmfIds", hqmfIds);
            userSecurityDao.addUserAuditRecord(userId, AuditType.EDIT_REQUEST, QrdaServiceImpl.class.getName(), "exportQrdaCategory3", JsonStringUtility.buildJsonRequest(requestJson));
        }

        // TemplateEngine templateEngine = ThymeleafUtil.getTemplateEngine();

        val hqmfDocs = measureDao.getHqmfDocumentsById(hqmfIds);
        LinkedHashMap<Long, Map<String, PopulationResultEntry>> resultMap = new LinkedHashMap<>();

        // prepare the context
        final Context ctx = new Context();
        ctx.setVariable("hqmfDocs", hqmfDocs);
        ctx.setVariable("results", resultMap);
        ctx.setVariable("helper", new Helper());

        for (HqmfDocument hqmfDoc : hqmfDocs) {

            val measureResults = new LinkedHashMap<String, PopulationResultEntry>();
            resultMap.put(hqmfDoc.getId(), measureResults);

            // get the max index
            int maxIndex = hqmfDoc.getHqmfPopulationSets().stream().map(HqmfPopulationSet::getIndex).max(Integer::compare).get();
            for (int i = 0; i <= maxIndex; i++) {
                String stratId = i > 0 ? getPopulationSetField("stratification", hqmfDoc, i) : null;
                String stratTitle = stratId != null ? getPopulationSetField("title", hqmfDoc, i) : null;

                processPopulationSet("IPP", hqmfDoc, measureResults, i, stratId, stratTitle);
                processPopulationSet("DENOM", hqmfDoc, measureResults, i, stratId, stratTitle);
                processPopulationSet("DENEX", hqmfDoc, measureResults, i, stratId, stratTitle);
                processPopulationSet("NUMER", hqmfDoc, measureResults, i, stratId, stratTitle);
                processPopulationSet("DENEXCEP", hqmfDoc, measureResults, i, stratId, stratTitle);
            }
        }

        // create the body using ThymeLeaf
        String htmlContent = templateEngine.process("qrda_cat3/show.cat3.xml", ctx);
        System.out.println(htmlContent);
        return htmlContent;
    }

    private String getPopulationSetField(String key, HqmfDocument hqmfDoc, final int index) {
        try {
            return hqmfDoc.getHqmfPopulationSets().stream()
                    .filter(popSet -> popSet.getIndex() == index && key.equals(popSet.getKey()))
                    .map(HqmfPopulationSet::getValue).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private void processPopulationSet(String popId, HqmfDocument hqmfDoc,
                                      Map<String, PopulationResultEntry> measureResults, final int index,
                                      final String stratId, String stratTitle) {

        for (HqmfPopulationSet popSet : hqmfDoc.getHqmfPopulationSets()) {
            if (popSet.getIndex() == index && popId.equals(popSet.getKey()) && popSet.getDateDisabled() == null) {

                popSet.getResults().stream().filter(result -> result.getDateDisabled() == null).forEach(result -> {
                    PopulationResultEntry entry = measureResults.get(popSet.getValue());
                    if (entry == null) {
                        measureResults.put(popSet.getValue(), new PopulationResultEntry(result));
                    } else if (stratId != null) {
                        entry.getStratifications().add(new StratificationEntry(stratId, stratTitle, result));
                    }
                });
            }
        }
    }

    public static
    @Data
    class PopulationResultEntry {
        private Result result;
        private List<StratificationEntry> stratifications = new ArrayList<StratificationEntry>();

        public PopulationResultEntry(Result result) {
            this.result = result;
        }

    }

    public static
    @Data
    class StratificationEntry {
        private String stratId;
        private String stratTitle;
        private Result result;

        public StratificationEntry(String stratId, String stratTitle, Result result) {
            this.stratId = stratId;
            this.stratTitle = stratTitle;
            this.result = result;
        }
    }

    public class Helper {

        private Map<String, Set<Integer>> codes = new HashMap<String, Set<Integer>>();

        public Set<ResultSupplemental> getRaceSupplementalData(Set<ResultSupplemental> resultSupp) {
            return filterSupplementalData(resultSupp, "2.16.840.1.114222.4.11.836");
        }

        public Set<ResultSupplemental> getEthnicitySupplementalData(Set<ResultSupplemental> resultSupp) {
            return filterSupplementalData(resultSupp, "2.16.840.1.114222.4.11.837");
        }

        public Set<ResultSupplemental> getPayerSupplementalData(Set<ResultSupplemental> resultSupp) {
            return filterSupplementalData(resultSupp, "2.16.840.1.114222.4.11.3591");
        }

        public Set<ResultSupplemental> getSexSupplementalData(Set<ResultSupplemental> resultSupp) {
            return filterSupplementalData(resultSupp, "2.16.840.1.113762.1.4.1");
        }

        private Set<ResultSupplemental> filterSupplementalData(Set<ResultSupplemental> resultSupp, String valueSetOid) {
            final Set<Integer> matchingCodes;
            if (!codes.containsKey(valueSetOid)) {
                matchingCodes = StreamUtils.createStreamFromIterator(valueSetDao.findCodesByValueSetOid(valueSetOid).iterator())
                        .map(v -> v.getCode().getId()).collect(Collectors.toSet());
                codes.put(valueSetOid, matchingCodes);
            } else {
                matchingCodes = codes.get(valueSetOid);
            }
            return resultSupp.stream().filter(result -> matchingCodes.contains(result.getCode().getId())).collect(Collectors.toSet());
        }
    }

}
