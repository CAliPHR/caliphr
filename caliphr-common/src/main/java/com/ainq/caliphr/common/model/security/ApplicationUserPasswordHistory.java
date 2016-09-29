package com.ainq.caliphr.common.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mmelusky on 10/13/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationUserPasswordHistory {
    Integer id;
    String passwordHash;
}
