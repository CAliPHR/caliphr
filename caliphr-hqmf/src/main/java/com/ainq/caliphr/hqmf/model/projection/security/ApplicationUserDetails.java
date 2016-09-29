package com.ainq.caliphr.hqmf.model.projection.security;

import org.springframework.data.rest.core.config.Projection;

import com.ainq.caliphr.common.model.security.ApplicationUser;
import java.util.Date;
import java.util.Set;

/**
 * Created by mmelusky on 9/22/2015.
 */
@Projection(types = ApplicationUser.class)
public interface ApplicationUserDetails {
    Date getDateCreated();
    Date getDateDisabled();
    Date getDateLastLogin();
    Date getDatePasswordChanged();
    Date getDateUpdated();
    String getEmailAddress();
    String getFirstName();
    Integer getId();
    String getLastLoginIpAddress();
    String getLastName();
    String getPasswordHash();
    Integer getUserCreated();
    Integer getUserUpdated();
    Set<ApplicationUserSecurityDetails> getApplicationUserSecurities();
}
