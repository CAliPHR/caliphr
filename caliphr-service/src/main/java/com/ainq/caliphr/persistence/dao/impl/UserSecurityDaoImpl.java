package com.ainq.caliphr.persistence.dao.impl;

import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.dao.UserSecurityDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.*;
import com.ainq.caliphr.persistence.util.predicate.security.UserSecurityPredicate;
import com.mysema.query.jpa.impl.JPAQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUserPasswordHistory.applicationUserPasswordHistory;
import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QApplicationUserPasswordRequest.applicationUserPasswordRequest;

import java.util.Date;
import java.util.List;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Repository
public class UserSecurityDaoImpl implements UserSecurityDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditTypeRepository auditTypeRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ApplicationUserAuditRepository applicationUserAuditRepository;

    @Autowired
    private ApplicationUserPasswordRequestRepository applicationUserPasswordRequestRepository;

    @Autowired
    private ApplicationUserPasswordHistoryRepository applicationUserPasswordHistoryRepository;

    @Override
    @Transactional
    public ApplicationUser findUserByEmail(String emailAddress) {
        return applicationUserRepository.findOne(UserSecurityPredicate.findUserByEmail(emailAddress));
    }

    @Override
    @Transactional
    public ApplicationUserAudit addUserAuditRecord(Integer userId, AuditType auditTypeEnum, String className, String methodName, String jsonResponse) {

        // Find the user
        ApplicationUser applicationUser = applicationUserRepository.findOne(userId);

        // Find the audit type
        com.ainq.caliphr.persistence.model.obj.caliphrDb.AuditType auditType = auditTypeRepository.findOne(auditTypeEnum.getTypeId());

        // Save the record
        ApplicationUserAudit applicationUserAudit = new ApplicationUserAudit();
        applicationUserAudit.setUser(applicationUser);
        applicationUserAudit.setAuditType(auditType);
        applicationUserAudit.setClassName(className);
        applicationUserAudit.setMethodName(methodName);
        applicationUserAudit.setJsonRequest(jsonResponse);
        applicationUserAudit.setDateCreated(new Date());
        applicationUserAudit.setUserCreated(userId);
        applicationUserAudit.setDateUpdated(new Date());
        applicationUserAudit.setUserUpdated(userId);
        return applicationUserAuditRepository.saveAndFlush(applicationUserAudit);

    }

    @Override
    @Transactional
    public ApplicationUser findUserById(Integer userId) {
        return applicationUserRepository.findOne(userId);
    }

    @Override
    @Transactional
    public ApplicationUserPasswordRequest addApplicationUserPasswordRequest(ApplicationUserPasswordRequest applicationUserPasswordRequest) {
        return applicationUserPasswordRequestRepository.saveAndFlush(applicationUserPasswordRequest);
    }

    @Override
    @Transactional
    public ApplicationUser findUserByPasswordRequestToken(String token) {
        return new JPAQuery(entityManager).from(applicationUserPasswordRequest)
                .where(applicationUserPasswordRequest.dateDisabled.isNull()
                        , applicationUserPasswordRequest.uuid.eq(token)
                        , applicationUserPasswordRequest.dateUuidExpiration.gt(new Date()))
                .singleResult(applicationUserPasswordRequest.user);
    }

    @Override
    @Transactional
    public ApplicationUser updateUserDetails(ApplicationUser foundUser) {
        return applicationUserRepository.saveAndFlush(foundUser);
    }

    @Override
    @Transactional
    public ApplicationUserPasswordRequest updateUserPasswordRequest(ApplicationUserPasswordRequest request) {
        return applicationUserPasswordRequestRepository.saveAndFlush(request);
    }

    @Override
    @Transactional
    public ApplicationUserPasswordRequest findUserPasswordRequestByToken(String token) {
        return applicationUserPasswordRequestRepository.findOne(UserSecurityPredicate.findPasswordRequestByToken(token));
    }

    @Override
    public ApplicationUserPasswordHistory addUserPasswordHistoryRecord(ApplicationUserPasswordHistory passwordHistory) {
        return applicationUserPasswordHistoryRepository.saveAndFlush(passwordHistory);
    }

    @Override
    public List<ApplicationUserPasswordHistory> findUserPasswordHistories(Integer userId) {
        return new JPAQuery(entityManager).from(applicationUserPasswordHistory).where(
                applicationUserPasswordHistory.user.id.eq(userId)
                , applicationUserPasswordHistory.dateDisabled.isNull())
                .list(applicationUserPasswordHistory);
    }
}
