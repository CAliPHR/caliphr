package com.ainq.caliphr.website.controller.secure.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.service.security.UserSecurityService;
import com.ainq.caliphr.website.utility.RedirectHelper;
import com.ainq.caliphr.website.utility.SecurityHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mmelusky on 9/8/2015.
 */
@Controller
public class UserAuthController {

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private Environment environment;

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/confirm")
    public String authConfirm() {

        Integer passwordExpirationMonths = Integer.parseInt(environment.getProperty(Constants.PropertyKey.PASSWORD_EXPIRATION_MONTHS));

        // Test if the user needs to reset password
        SecurityUser user = (SecurityUser) SecurityHelper.getUserDetails();
        Calendar expiredDateCalendar = Calendar.getInstance();
        expiredDateCalendar.setTime(new Date());
        expiredDateCalendar.add(Calendar.MONTH, passwordExpirationMonths);
        if (user.getDateLastLogin() == null || user.getDateLastLogin().after(expiredDateCalendar.getTime())) {

            //
            //  Invalidate the user's session and log out immediately.
            SecurityContextHolder.clearContext();

            //
            // User needs to reset their password.  Email token.
            if (user.getEmailAddress() != null) {
                JsonResponse response = userSecurityService.sendPasswordResetLink(user.getEmailAddress());
                if (response != null && response.getStatus() != null && response.getStatus() != JsonStatus.OK) {
                    return RedirectHelper.redirect(RedirectHelper.PASSWORD_REQUEST_FAILURE);
                } else {
                    return RedirectHelper.redirect(RedirectHelper.PASSWORD_REQUEST_SUCCESS);
                }
            } else {
                return RedirectHelper.redirect(RedirectHelper.PASSWORD_REQUEST_FAILURE);
            }
        } else {

            //
            // All systems go.
            return RedirectHelper.redirect(RedirectHelper.HOME);
            // return "site/dashboard";
        }
    }
}
