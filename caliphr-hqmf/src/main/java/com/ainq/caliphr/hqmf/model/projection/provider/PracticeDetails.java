package com.ainq.caliphr.hqmf.model.projection.provider;

import org.springframework.data.rest.core.config.Projection;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;

/**
 * Created by mmelusky on 8/10/2015.
 */
@Projection(types = PracticeGroup.class)
public interface PracticeDetails {

    Integer getId();

    String getGroupName();
}
