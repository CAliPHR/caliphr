package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUser;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserAudit;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserPasswordHistory;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserPasswordRequest;

import java.util.List;

/**
 * Created by mmelusky on 8/25/2015.
 */
public interface UserSecurityDao {
    ApplicationUser findUserByEmail(String emailAddress);

    ApplicationUserAudit addUserAuditRecord(Integer userId, AuditType auditTypeEnum, String className, String methodName, String jsonResponse);

    ApplicationUser findUserById(Integer userId);

    ApplicationUserPasswordRequest addApplicationUserPasswordRequest(ApplicationUserPasswordRequest applicationUserPasswordRequest);

    ApplicationUser findUserByPasswordRequestToken(String token);

    ApplicationUser updateUserDetails(ApplicationUser foundUser);

    ApplicationUserPasswordRequest updateUserPasswordRequest(ApplicationUserPasswordRequest request);

    ApplicationUserPasswordRequest findUserPasswordRequestByToken(String token);

    ApplicationUserPasswordHistory addUserPasswordHistoryRecord(ApplicationUserPasswordHistory passwordHistory);

    List<ApplicationUserPasswordHistory> findUserPasswordHistories(Integer userId);
}
