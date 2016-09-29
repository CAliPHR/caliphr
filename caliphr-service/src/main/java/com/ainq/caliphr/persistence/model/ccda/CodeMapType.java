package com.ainq.caliphr.persistence.model.ccda;

/**
 * Created by mmelusky on 2/15/2016.
 */
public enum CodeMapType {

    PATIENT_PROBLEM_MAPPING(1),
    PATIENT_PROCEDURE_MAPPING(2),
    PATIENT_MEDICATION_MAPPING(3),
    PATIENT_PROCEDURE_MAPPING_UNKNOWN_VALUE(4),
    PATIENT_RESULT_VALUE_MAPPING(5);

    private final Integer typeId;

    // Constants
    public static final String UNKNOWN_VALUE = "unknown";

    // Constructor
    CodeMapType(Integer typeId) {
        this.typeId = typeId;
    }

    // Getters
    public Integer getTypeId() {
        return typeId;
    }

    public static CodeMapType fromType(Integer type) {
        if (type != null) {
            for (CodeMapType codeMapType : CodeMapType.values()) {
                if (type.equals(codeMapType.typeId)) {
                    return codeMapType;
                }
            }
        }
        throw new IllegalArgumentException("No codemap with type " + type + " found");
    }
}
