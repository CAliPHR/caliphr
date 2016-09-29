package com.ainq.caliphr.website.controller.api.hqmf;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.common.model.result.Measure;
import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.service.hqmf.MeasureService;
import com.ainq.caliphr.website.utility.SecurityHelper;

@RestController
public class MeasureRestController {

    @Autowired
    private MeasureService measureService;

    private Integer userId;

    @RequestMapping(value = "/api/all_active_measures/{providerId}", method = RequestMethod.POST)
    public List<Measure> getAllActiveMeasures(@PathVariable("providerId") Integer providerId) {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return measureService.getAllActiveMeasures(providerId, userId);
    }

    @RequestMapping(value = "/api/measure_attributes/{hqmfDocId}", method = RequestMethod.POST)
    public Map<String, String> getMeasureAttributes(@PathVariable("hqmfDocId") Integer hqmfDocId) {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return measureService.getMeasureAttributes(hqmfDocId, userId);
    }

    @RequestMapping(value = "/api/patient_results/{resultId}", method = RequestMethod.POST)
    public List<Patient> getPatientResult(@PathVariable("resultId") Integer resultId) {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return measureService.getPatientResult(resultId, userId);
    }

}
