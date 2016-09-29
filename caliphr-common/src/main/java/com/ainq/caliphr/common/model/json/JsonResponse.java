package com.ainq.caliphr.common.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mmelusky on 8/25/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResponse {

    // Instance Data
    private JsonStatus status;
    private String message;

}
