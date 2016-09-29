package com.ainq.caliphr.website.service.security;

import java.util.List;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.request.PasswordResetRequest;
import com.ainq.caliphr.common.model.security.ApplicationUser;
import com.ainq.caliphr.common.model.security.ApplicationUserPasswordHistory;

/**
 * Created by mmelusky on 8/25/2015.
 */
public interface UserSecurityService {
    ApplicationUser findUserByEmail(String emailAddress);

    JsonResponse sendPasswordResetLink(String emailAddress);

    ApplicationUser findUserByPasswordRequestToken(String token);

    JsonResponse resetUserPassword(PasswordResetRequest passwordResetRequest);

    List<ApplicationUserPasswordHistory> findUserPasswordHistories(Integer userId);
}
