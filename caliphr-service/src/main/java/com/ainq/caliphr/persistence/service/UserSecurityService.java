package com.ainq.caliphr.persistence.service;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.request.PasswordResetRequest;
import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUser;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserAudit;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserPasswordHistory;

import java.util.List;

/**
 * Created by mmelusky on 8/25/2015.
 */
public interface UserSecurityService {
    ApplicationUser findUserByEmail(String emailAddress);

    ApplicationUserAudit addUserAuditRecord(Integer userId, AuditType auditType, String className, String methodName, String jsonResponse);

    JsonResponse sendPasswordResetLink(String emailAddress);

    ApplicationUser findUserByPasswordRequestToken(String token);

    JsonResponse submitPasswordRequest(PasswordResetRequest passwordResetRequest);

    List<ApplicationUserPasswordHistory> findUserPasswordHistories(Integer userId);
}
