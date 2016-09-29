package com.ainq.caliphr.website.service.security;

/**
 * Created by mmelusky on 1/4/2016.
 */
public enum SecurityRole {

    //
    //  This enum should mirror the application.security_role database table.

    ROLE_ADMIN(1, "System Administrator", "ADMIN"),
    ROLE_USER(2, "Application User", "USER"),
    ROLE_DEVELOPER(3, "Application Developer", "DEV"),
    ROLE_TESTER(4, "Application Tester", "TEST");

    public static final String ROLE_PREFIX = "ROLE_";

    private final Integer recordId;
    private final String roleName;
    private final String springSecurityKey;

    SecurityRole(Integer recordId, String roleName, String springSecurityKey) {
        this.recordId = recordId;
        this.roleName = roleName;
        this.springSecurityKey = springSecurityKey;
    }

    /*  Getters */

    public Integer getRecordId() {
        return recordId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getSpringSecurityKey() {
        return springSecurityKey;
    }
}
