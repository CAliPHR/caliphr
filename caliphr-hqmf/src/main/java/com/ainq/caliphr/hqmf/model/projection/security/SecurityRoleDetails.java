package com.ainq.caliphr.hqmf.model.projection.security;

import org.springframework.data.rest.core.config.Projection;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.SecurityRole;

/**
 * Created by mmelusky on 1/5/2016.
 */
@Projection(types = SecurityRole.class)
public interface SecurityRoleDetails {
    Integer getId();
    String getRoleName();
}
