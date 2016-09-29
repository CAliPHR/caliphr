package com.ainq.caliphr.common.model.sandbox.gson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GsonByMeasureResult {
    private int DENOM;
    private int NUMER;
}
