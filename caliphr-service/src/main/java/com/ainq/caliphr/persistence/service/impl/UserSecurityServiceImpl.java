package com.ainq.caliphr.persistence.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.common.model.request.PasswordResetRequest;
import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.UserSecurityDao;
import com.ainq.caliphr.persistence.mail.CaliphrMailer;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUser;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserAudit;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserPasswordHistory;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserPasswordRequest;
import com.ainq.caliphr.persistence.service.UserSecurityService;

import javax.mail.MessagingException;
import java.util.*;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Service
public class UserSecurityServiceImpl implements UserSecurityService {

    @Autowired
    private ApplicationContext appCxt;

    @Autowired
    private Environment environment;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @Override
    public ApplicationUser findUserByEmail(String emailAddress) {
        return userSecurityDao.findUserByEmail(emailAddress);
    }

    @Override
    public ApplicationUserAudit addUserAuditRecord(Integer userId, AuditType auditType, String className, String methodName, String jsonResponse) {
        return userSecurityDao.addUserAuditRecord(userId, auditType, className, methodName, jsonResponse);
    }

    @Override
    public JsonResponse sendPasswordResetLink(String emailAddress) {

        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.FAIL);

        // Find the matching user
        if (emailAddress == null || emailAddress.length() == 0) {
            response.setMessage("Email address is required.");
        } else {
            ApplicationUser user = userSecurityDao.findUserByEmail(emailAddress);
            if (user == null) {
                response.setMessage("User not found from email address.");
            } else {

                //
                //  Password tokens need an expiration date
                UUID uuid = UUID.randomUUID();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, Constants.PASSWORD_TOKEN_EXPIRATION_DAYS);

                ApplicationUserPasswordRequest applicationUserPasswordRequest = new ApplicationUserPasswordRequest();
                applicationUserPasswordRequest.setUser(user);
                applicationUserPasswordRequest.setUuid(uuid.toString());
                applicationUserPasswordRequest.setDateUuidExpiration(cal.getTime());
                applicationUserPasswordRequest.setUserCreated(user.getId());
                applicationUserPasswordRequest.setDateCreated(new Date());
                applicationUserPasswordRequest.setUserUpdated(user.getId());
                applicationUserPasswordRequest.setDateUpdated(new Date());
                userSecurityDao.addApplicationUserPasswordRequest(applicationUserPasswordRequest);

                String resetUrl = String.format(environment.getProperty(Constants.PropertyKey.CALIPHR_PASSWORD_RESET_URL_FORMAT_STRING), uuid.toString());
                
                StringBuilder textContent = new StringBuilder();
                textContent.append(String.format("Please reset your password using the following link: %s", resetUrl));
                
                StringBuilder htmlContent = new StringBuilder();
                htmlContent.append(String.format("Please reset your password using the following link: <br/><br/> <a href=\"%s\">%s</a>", resetUrl, resetUrl));
                
                List<String> to = new ArrayList<>();
                to.add(user.getEmailAddress());

                try {
                    CaliphrMailer caliphrMailer = appCxt.getBean(CaliphrMailer.class);
                    caliphrMailer.setTo(to);
                    caliphrMailer.setSubject("Application Password Reset Link");
                    caliphrMailer.setHtmlHeading("Application Password Reset Link");
                    caliphrMailer.setTextContent(textContent.toString());
                    caliphrMailer.setHtmlContent(htmlContent.toString());
                    caliphrMailer.generateAndSendEmail();
                } catch (MessagingException e) {
                    response.setMessage(e.getMessage());
                    return response;
                }

                response.setStatus(JsonStatus.OK);
                response.setMessage("Email successfully sent.");
            }
        }

        return response;
    }

    @Override
    public ApplicationUser findUserByPasswordRequestToken(String token) {
        ApplicationUser applicationUser = userSecurityDao.findUserByPasswordRequestToken(token);
        if (applicationUser == null) {
            return new ApplicationUser();
        }
        return applicationUser;
    }

    @Override
    public JsonResponse submitPasswordRequest(PasswordResetRequest passwordResetRequest) {

        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.FAIL);

        // Find the application user
        if (passwordResetRequest == null || passwordResetRequest.getUserId() == null || passwordResetRequest.getToken() == null || passwordResetRequest.getPasswordHash() == null) {
            response.setMessage("User ID is required");
            return response;
        }

        ApplicationUser foundUser = userSecurityDao.findUserById(passwordResetRequest.getUserId());
        if (foundUser == null) {
            response.setMessage("User not found");
            return response;
        }

        // Update user password details
        foundUser.setPasswordHash(passwordResetRequest.getPasswordHash());
        foundUser.setDateUpdated(new Date());
        foundUser.setUserUpdated(passwordResetRequest.getUserId());
        foundUser.setDateLastLogin(new Date());
        userSecurityDao.updateUserDetails(foundUser);

        // Find the token record and disable it
        ApplicationUserPasswordRequest request = userSecurityDao.findUserPasswordRequestByToken(passwordResetRequest.getToken());
        request.setDateUpdated(new Date());
        request.setUserUpdated(passwordResetRequest.getUserId());
        request.setDateDisabled(new Date());
        userSecurityDao.updateUserPasswordRequest(request);

        // Create the history record as well
        ApplicationUserPasswordHistory passwordHistory = new ApplicationUserPasswordHistory();
        passwordHistory.setUser(foundUser);
        passwordHistory.setPasswordHash(passwordResetRequest.getPasswordHash());
        passwordHistory.setDateCreated(new Date());
        passwordHistory.setUserCreated(passwordResetRequest.getUserId());
        passwordHistory.setDateUpdated(new Date());
        passwordHistory.setUserUpdated(passwordResetRequest.getUserId());
        userSecurityDao.addUserPasswordHistoryRecord(passwordHistory);

        response.setStatus(JsonStatus.OK);
        response.setMessage("Password successfully updated.");
        return response;
    }

    @Override
    public List<ApplicationUserPasswordHistory> findUserPasswordHistories(Integer userId) {
        return userSecurityDao.findUserPasswordHistories(userId);
    }
}
