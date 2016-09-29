package com.ainq.caliphr.hqmf.model.projection.security;

import org.springframework.data.rest.core.config.Projection;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserSecurity;

import java.util.Date;

/**
 * Created by mmelusky on 1/5/2016.
 */
@Projection(types = ApplicationUserSecurity.class)
public interface ApplicationUserSecurityDetails {
    SecurityRoleDetails getRole();
    Date getDateDisabled();
}
