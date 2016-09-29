package com.ainq.caliphr.common.model.sandbox.gson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GsonValue {
    private String bundle_id;
    private List<GsonValueConcept> concepts;
    private String display_name;
    private String oid;
    private String user_id;
    private String version;
}
