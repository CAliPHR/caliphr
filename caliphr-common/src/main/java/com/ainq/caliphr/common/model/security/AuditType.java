package com.ainq.caliphr.common.model.security;

/**
 * Created by mmelusky on 9/8/2015.
 */
public enum AuditType {

    EDIT_REQUEST(1),
    QUERY_REQUEST(2),
    LOGIN_REQUEST(3);

    // Instance Data
    private Integer typeId;

    // Constructor
    AuditType(Integer typeId) {
        this.typeId = typeId;
    }

    // Getter
    public Integer getTypeId() {
        return typeId;
    }

    // Static Methods
    public static AuditType fromTypeId(Integer typeId) {
        if (typeId != null) {
            for (AuditType auditType: AuditType.values()) {
                if (auditType.typeId == typeId) {
                    return auditType;
                }
            }
        }
        throw new IllegalArgumentException("No audit with type " + typeId + " found");
    }
}
