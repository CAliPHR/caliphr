package com.ainq.caliphr.hqmf.controller.api.hqmf;

import ch.qos.logback.classic.Logger;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.hqmf.service.HQMFService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mmelusky on 7/13/2015.
 */
@RestController
public class HqmfProcessController {

    static Logger logger = (Logger) LoggerFactory.getLogger(HqmfProcessController.class);

    static final String DATE_FORMAT = "yyyyMMdd";

    @Autowired
    private HQMFService hqmfService;

    @RequestMapping("/api/measures/calculate")
    public JsonResponse processHqmf(@RequestParam("providerId") Integer providerId, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam(value = "userId", required = false) Integer userId) {

        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.FAIL);

        if (providerId == null) {
            response.setMessage("Provider Id cannot be null");
        }
        if (startDate == null) {
            response.setMessage("Start Date cannot be null");
        } else if (endDate == null) {
            response.setMessage("End Date cannot be null");
        } else {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            Date reportingPeriodStart = null;
            try {
                reportingPeriodStart = df.parse(startDate);
            } catch (ParseException e) {
                // e.printStackTrace();
            }
            Date reportingPeriodEnd = null;
            try {
                reportingPeriodEnd = df.parse(endDate);
            } catch (ParseException e) {
                // e.printStackTrace();
            }

            if (reportingPeriodStart == null || reportingPeriodEnd == null) {
                response.setMessage(String.format("please enter dates in format %s", DATE_FORMAT));
            } else {
                hqmfService.calculateMeasures(providerId, reportingPeriodStart, reportingPeriodEnd, userId);
                response.setMessage("Process triggered.  Please check logs for additional details.");
                response.setStatus(JsonStatus.OK);
            }
        }

        return response;
    }
}
