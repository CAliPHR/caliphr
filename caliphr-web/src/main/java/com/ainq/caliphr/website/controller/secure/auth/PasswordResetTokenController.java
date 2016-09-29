package com.ainq.caliphr.website.controller.secure.auth;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.common.model.request.PasswordResetRequest;
import com.ainq.caliphr.common.model.security.ApplicationUser;
import com.ainq.caliphr.common.model.security.ApplicationUserPasswordHistory;
import com.ainq.caliphr.website.Constants;
import com.ainq.caliphr.website.service.security.UserSecurityService;
import com.ainq.caliphr.website.utility.RedirectHelper;
import com.ainq.caliphr.website.utility.SecurityHelper;
import com.google.common.base.Joiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmelusky on 9/8/2015.
 */
@Controller
@SessionAttributes("passwordResetRequest")
public class PasswordResetTokenController {

    private static String FORM_ERRORS = "formErrors";

    @Autowired
    private Environment environment;

    @Autowired
    private UserSecurityService userSecurityService;

    /*
        Received Password Reset Email
     */
    @RequestMapping(method = RequestMethod.POST, value = "/web/auth/password/token")
    // Remove all bean validation into the controller itself
    // public String resetPasswordUsingToken(@Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest passwordResetRequest, BindingResult bindingResult, Model model) {
    public String resetPasswordUsingToken(@ModelAttribute("passwordResetRequest") PasswordResetRequest passwordResetRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Form errors (basic validation)
            return "auth/password-reset-token";
        } else {
            List<String> passwordErrors = validatePasswords(passwordResetRequest.getNewPassword(), passwordResetRequest.getNewPasswordConfirm());
            if (passwordErrors != null && passwordErrors.size() > 0) {
                model.addAttribute(FORM_ERRORS, Joiner.on(",").join(passwordErrors));
                return "auth/password-reset-token";
            } else {

                //
                //  Set new password and save changes
                List<ApplicationUserPasswordHistory> passwordHistories = userSecurityService.findUserPasswordHistories(passwordResetRequest.getUserId());
                if (hasExistingPasswordHistories(passwordResetRequest.getNewPassword(), passwordHistories)) {
                    model.addAttribute(FORM_ERRORS, "Password must not be in your password history.");
                    return "auth/password-reset-token";
                } else {
                    passwordResetRequest.setPasswordHash(SecurityHelper.createPasswordHash(passwordResetRequest.getNewPassword()));
                    JsonResponse resetPasswordResponse = userSecurityService.resetUserPassword(passwordResetRequest);
                    if ( resetPasswordResponse == null || resetPasswordResponse.getStatus() != JsonStatus.OK ){
                        return RedirectHelper.redirect(RedirectHelper.PASSWORD_RESET_FAILURE);
                    } else {
                        return RedirectHelper.redirect(RedirectHelper.PASSWORD_RESET_SUCCESS);
                    }
                }
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/password/t/{token}")
    public String findByPasswordResetToken(Model model, @PathVariable("token") String token) {

        // Find user by password token
        ApplicationUser applicationUser = userSecurityService.findUserByPasswordRequestToken(token);
        if (applicationUser == null || applicationUser.getId() == null) {

            //
            //  user not found or token expired
            return RedirectHelper.redirect(RedirectHelper.PASSWORD_RESET_FAILURE);
        } else {

            //
            //  Permit reset
            PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
            passwordResetRequest.setUserId(applicationUser.getId());
            passwordResetRequest.setNewPassword(new String());
            passwordResetRequest.setNewPasswordConfirm(new String());
            passwordResetRequest.setToken(token);
            model.addAttribute("passwordResetRequest", passwordResetRequest);
            return "auth/password-reset-token";
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/password-reset/success")
    public String passwordResetTokenSuccess() {
        return "auth/password-reset-token-success";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/password-reset/failure")
    public String passwordResetTokenFailure() {
        return "auth/password-reset-token-failure";
    }

    private List<String> validatePasswords(String newPassword, String newPasswordConfirm) {

        List<String> errors = new ArrayList<>();

        // Constants
        Integer minPasswordLength = Integer.parseInt(environment.getProperty(Constants.PropertyKey.PASSWORD_LENGTH_MIN));
        Integer maxPasswordLength = Integer.parseInt(environment.getProperty(Constants.PropertyKey.PASSWORD_LENGTH_MAX));

        // Null check
        if (newPassword == null || newPassword.length() == 0) {
            errors.add("Password may not be blank");
        } else {

            // Equality
            if (newPassword != null && newPasswordConfirm != null && (! newPassword.equals(newPasswordConfirm))) {
                errors.add("Passwords must equal each other");
            }

            // Min check/Max Check
            if (newPassword.length() < minPasswordLength) {
                errors.add(String.format("Password must be at least %s characters long", minPasswordLength));
            } else if (newPassword.length() > maxPasswordLength) {
                errors.add(String.format("Password must be no more than %s characters long", maxPasswordLength));
            }

            // Upper and lower case
            Boolean hasUppercase = !newPassword.equals(newPassword.toLowerCase());
            Boolean hasLowercase = !newPassword.equals(newPassword.toUpperCase());
            if (! (hasUppercase && hasLowercase)) {
                errors.add("Password must be mixed upper and lower case");
            }

            // Has at least one digit
            Integer digitCount = newPassword.replaceAll("\\D", "").length();
            if (! (digitCount > 0)) {
                errors.add("Password must contain one or more numbers");
            }

            // Has at least one letter
            Integer letterCount = newPassword.replaceAll("[a-zA-Z]", "").length();
            if (! (letterCount > 0)) {
                errors.add("Password must contain one or more letters");
            }
        }

        return errors;
    }

    /**
     * Check if a password was in the set of recently used user passwords.
     *
     * @param newPassword - password to check against
     * @param passwordHistories - collection of passwords
     * @return T/F
     */
    private Boolean hasExistingPasswordHistories(String newPassword, List<ApplicationUserPasswordHistory> passwordHistories) {
        if (passwordHistories == null || passwordHistories.size() == 0) {
            return false;
        } else {
            for (ApplicationUserPasswordHistory passwordHistory : passwordHistories) {
                if (SecurityHelper.passwordHasMatches(newPassword, passwordHistory.getPasswordHash())) {
                    return true;
                }
            }
        }

        return false;
    }

}
