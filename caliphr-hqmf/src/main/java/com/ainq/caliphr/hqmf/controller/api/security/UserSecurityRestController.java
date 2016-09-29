package com.ainq.caliphr.hqmf.controller.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.web.bind.annotation.*;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.request.PasswordResetRequest;
import com.ainq.caliphr.hqmf.model.projection.security.ApplicationUserDetails;
import com.ainq.caliphr.hqmf.model.projection.security.ApplicationUserPasswordHistoryDetails;
import com.ainq.caliphr.persistence.service.UserSecurityService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mmelusky on 8/25/2015.
 */
@RestController
public class UserSecurityRestController {

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private ProjectionFactory projectionFactory;

    @RequestMapping(value="/api/security/login", method = RequestMethod.POST)
    private ApplicationUserDetails findByEmailAddress(@RequestParam String emailAddress) {
        return projectionFactory.createProjection(ApplicationUserDetails.class, userSecurityService.findUserByEmail(emailAddress));
    }

    @RequestMapping(value="/api/security/password/reset/request", method = RequestMethod.POST)
    private JsonResponse sendPasswordResetLink(@RequestParam String emailAddress) {
        return userSecurityService.sendPasswordResetLink(emailAddress);
    }

    @RequestMapping(value="/api/security/password/reset/token", method = RequestMethod.POST)
    private ApplicationUserDetails findUserByPasswordRequestToken(@RequestParam String token) {
        return projectionFactory.createProjection(ApplicationUserDetails.class, userSecurityService.findUserByPasswordRequestToken(token));
    }

    @RequestMapping(value="/api/security/password/reset/submit", method = RequestMethod.POST)
    private JsonResponse submitPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest) {
        return userSecurityService.submitPasswordRequest(passwordResetRequest);
    }

    @RequestMapping(value="/api/security/password/history/check", method = RequestMethod.POST)
    private List<ApplicationUserPasswordHistoryDetails> validateUserPasswordHistory(@RequestParam Integer userId) {
        return userSecurityService.findUserPasswordHistories(userId).stream()
                .map(record -> projectionFactory.createProjection(ApplicationUserPasswordHistoryDetails.class, record))
                .collect(Collectors.toList());
    }
}
