package com.ainq.caliphr.common.model.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Practice {
    Integer id;
    String groupName;
    Integer organizationId;
}
