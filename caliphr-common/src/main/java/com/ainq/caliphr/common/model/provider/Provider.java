package com.ainq.caliphr.common.model.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Provider {
    Integer id;
    Integer groupId;
    String npi;
    String firstName;
    String middleName;
    String lastName;
    String fullName;
}
