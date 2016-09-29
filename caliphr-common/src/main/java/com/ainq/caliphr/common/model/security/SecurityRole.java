package com.ainq.caliphr.common.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mmelusky on 1/4/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityRole {
    /** Field mapping. */
    private Integer id;
    /** Field mapping. */
    private String roleName;
}
