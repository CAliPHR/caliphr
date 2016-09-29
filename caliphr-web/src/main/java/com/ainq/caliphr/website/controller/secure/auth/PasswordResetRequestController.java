package com.ainq.caliphr.website.controller.secure.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ainq.caliphr.common.model.security.ApplicationUser;
import com.ainq.caliphr.website.service.security.UserSecurityService;
import com.ainq.caliphr.website.utility.RedirectHelper;

/**
 * Created by mmelusky on 9/8/2015.
 */
@Controller
public class PasswordResetRequestController {

    @Autowired
    private UserSecurityService userSecurityService;

    /*
        Send Password Reset
     */

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/password/reset")
    public String passwordResetForm(Model model) {
        model.addAttribute("user", new ApplicationUser());
        return "auth/password-reset";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/web/auth/password/reset")
    public String passwordResetSubmit(@ModelAttribute ApplicationUser user, Model model) {
        model.addAttribute("user", user);
        if (user != null && user.getEmailAddress() != null) {
            // TODO handle errors here???
            userSecurityService.sendPasswordResetLink(user.getEmailAddress());
            return RedirectHelper.redirect(RedirectHelper.PASSWORD_REQUEST_RESET);
        } else {
            return "auth/password-reset";
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/password/success")
    public String passwordResetSuccess() {
        return "auth/password-reset-success";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/password/failure")
    public String passwordResetFailure() {
        return "auth/password-reset-failure";
    }

}
