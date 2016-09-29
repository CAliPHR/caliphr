package com.ainq.caliphr.persistence.transformation.cda;

/**
 * Created by mmelusky on 8/31/2015.
 */
public enum ParseStatus {

    SUCCESS(1),
    FAILED_VALIDATION(2),
    PARSING_ERRORS(3);

    private final Integer typeId;

    ParseStatus(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getTypeId() { return typeId; }
}
