package com.ainq.caliphr.common.model.sandbox.gson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GsonMeasure {
    private String id;
    private String name;
}
