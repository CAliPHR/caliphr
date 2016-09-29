package com.ainq.caliphr.hqmf.model.projection.security;

import org.springframework.data.rest.core.config.Projection;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUserPasswordHistory;

/**
 * Created by mmelusky on 9/22/2015.
 */
@Projection(types = ApplicationUserPasswordHistory.class)
public interface ApplicationUserPasswordHistoryDetails {
    Integer getId();
    String getPasswordHash();
}
