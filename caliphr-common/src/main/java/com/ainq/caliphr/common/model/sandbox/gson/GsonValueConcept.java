package com.ainq.caliphr.common.model.sandbox.gson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GsonValueConcept {
    private Boolean black_list;
    private String code;
    private String code_system;
    private String code_system_name;
    private String code_system_version;
    private String display_name;
    private Boolean white_list;
}
