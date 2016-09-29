package com.ainq.caliphr.website.controller.api.hqmf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.service.hqmf.HqmfProcessService;
import com.ainq.caliphr.website.utility.SecurityHelper;

/**
 * Created by mmelusky on 7/13/2015.
 */
@RestController
public class HqmfProcessController {

    @Autowired
    private HqmfProcessService hqmfProcessService;

    private Integer userId;

    @RequestMapping("/api/measures/calculate")
    public JsonResponse processHqmf(@RequestParam Integer providerId, @RequestParam String startDate, @RequestParam String endDate) {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        return hqmfProcessService.processHqmf(providerId, startDate, endDate, userId);
    }
}
