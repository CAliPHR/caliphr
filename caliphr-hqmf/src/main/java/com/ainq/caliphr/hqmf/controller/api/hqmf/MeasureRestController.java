package com.ainq.caliphr.hqmf.controller.api.hqmf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.common.model.result.Measure;
import com.ainq.caliphr.common.model.result.PopulationSetResult;
import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.dao.UserSecurityDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfAttribute;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfPopulationSet;
import com.ainq.caliphr.persistence.util.JsonStringUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class MeasureRestController {

    @Autowired
    private MeasureDao measureDao;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @RequestMapping(value = "/api/all_active_measures", method = RequestMethod.POST)
    public List<Measure> getAllActiveMeasures(@RequestParam("providerId") Integer providerId, @RequestParam(value = "userId", required = false) Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("providerId", providerId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.QUERY_REQUEST, MeasureRestController.class.getName(), "getAllActiveMeasures", JsonStringUtility.buildJsonRequest(requestJson));
        }
        return StreamUtils.createStreamFromIterator(measureDao.getAllActiveHqmfDocuments(providerId, userId).iterator()).map(hqmfDoc -> {
            Measure dto = new Measure();
            dto.setHqmfDocumentId(hqmfDoc.getId());
            dto.setCmsId(hqmfDoc.getCmsId());
            dto.setTitle(hqmfDoc.getTitle());
            dto.setDescription(hqmfDoc.getDescription());
            dto.setDateCreated(hqmfDoc.getDateCreated());
            dto.setReportingPeriodStart(hqmfDoc.getMeasurePeriod().getLow());
            dto.setReportingPeriodEnd(hqmfDoc.getMeasurePeriod().getHigh());
            if (hqmfDoc.getDomain() != null) {
                dto.setDomainName(hqmfDoc.getDomain().getName());
                dto.setDomainId(hqmfDoc.getDomain().getId());
            }

            // get the max index and create that number of MeasureDTO.PopulationSetResult objects
            int maxIndex = hqmfDoc.getHqmfPopulationSets().stream().map(HqmfPopulationSet::getIndex).max(Integer::compare).get();
            IntStream.rangeClosed(0, maxIndex).forEach(i -> dto.getPopulationSetResults().add(new PopulationSetResult()));

            hqmfDoc.getHqmfPopulationSets().forEach(popSet -> {

                if ("title".equals(popSet.getKey())) {
                    dto.getPopulationSetResults().get(popSet.getIndex()).setSubmeasureTitle(popSet.getValue());
                    return;
                }

                popSet.getResults().stream().forEach(popResult -> {
                    PopulationSetResult popSetResult = dto.getPopulationSetResults().get(popSet.getIndex());

                    if ("IPP".equals(popSet.getKey())) {
                        popSetResult.setIppCount(popResult.getResultValue());
                        popSetResult.setIppResultId(popResult.getId());
                    } else if ("DENOM".equals(popSet.getKey())) {
                        popSetResult.setDenominatorCount(popResult.getResultValue());
                        popSetResult.setDenominatorResultId(popResult.getId());
                    } else if ("DENEX".equals(popSet.getKey())) {
                        popSetResult.setDenexCount(popResult.getResultValue());
                        popSetResult.setDenexResultId(popResult.getId());
                    } else if ("NUMER".equals(popSet.getKey())) {
                        popSetResult.setNumeratorCount(popResult.getResultValue());
                        popSetResult.setNumeratorResultId(popResult.getId());
                    } else if ("DENEXCEP".equals(popSet.getKey())) {
                        popSetResult.setDenexcepCount(popResult.getResultValue());
                        popSetResult.setDenexcepResultId(popResult.getId());
                    }
                });
            });

            //
            // Remove any "Population 0" from the submeasure titles for population set results of cardinality 1.  (mmelusky 9-21)
            if (dto != null && dto.getPopulationSetResults() != null && dto.getPopulationSetResults().size() == 1) {
                dto.getPopulationSetResults().get(0).setSubmeasureTitle(StringUtils.EMPTY);
            }

            return dto;

        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/measure_attributes", method = RequestMethod.POST)
    public Map<String, String> getMeasureAttributes(@RequestParam("hqmfDocId") Long hqmfDocId, @RequestParam(value = "userId", required = false) Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("hqmfDocId", hqmfDocId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.QUERY_REQUEST, MeasureRestController.class.getName(), "getMeasureAttributes", JsonStringUtility.buildJsonRequest(requestJson));
        }
        return StreamUtils.createStreamFromIterator(measureDao.getHqmfAttributesByHqmfDocId(hqmfDocId).iterator())
                .collect(Collectors.toMap(HqmfAttribute::getAttributeName, HqmfAttribute::getValueObjJson));
    }

    @RequestMapping(value = "/api/patient_results", method = RequestMethod.POST)
    public List<Patient> getPatientResult(@RequestParam("resultId") Long resultId, @RequestParam(value = "userId", required = false) Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("resultId", resultId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.QUERY_REQUEST, MeasureRestController.class.getName(), "getPatientResult", JsonStringUtility.buildJsonRequest(requestJson));
        }
        return measureDao.getPatientByActiveResultPatientForResultId(resultId, userId);
    }

}
