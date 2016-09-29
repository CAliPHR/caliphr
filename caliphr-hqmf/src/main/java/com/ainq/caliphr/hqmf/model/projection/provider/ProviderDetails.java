package com.ainq.caliphr.hqmf.model.projection.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;

/**
 * Created by mmelusky on 8/10/2015.
 */
@Projection(types = Provider.class)
public interface ProviderDetails {

    String getFirstName();

    Integer getId();

    String getLastName();

    String getMiddleName();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

}
