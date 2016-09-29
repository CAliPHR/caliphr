package com.ainq.caliphr.common.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * Created by mmelusky on 1/4/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationUserSecurity {
    /** Field mapping. */
    private Integer id;
    /** Field mapping. */
    private SecurityRole role;
    /** Field mapping. */
    private Date dateDisabled;
}
