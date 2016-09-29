package com.ainq.caliphr.common.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationUser {
    /** Field mapping. */
    private Date dateCreated;
    /** Field mapping. */
    private Date dateDisabled;
    /** Field mapping. */
    private Date dateLastLogin;
    /** Field mapping. */
    private Date datePasswordChanged;
    /** Field mapping. */
    private Date dateUpdated;
    /** Field mapping. */
    private String emailAddress;
    /** Field mapping. */
    private String firstName;
    /** Field mapping. */
    private Integer id;
    /** Field mapping. */
    private String lastLoginIpAddress;
    /** Field mapping. */
    private String lastName;
    /** Field mapping. */
    private String passwordHash;
    /** Field mapping. */
    private Integer userCreated;
    /** Field mapping. */
    private Integer userUpdated;
    /** Field mapping. */
    private Set<ApplicationUserSecurity> applicationUserSecurities;
}
