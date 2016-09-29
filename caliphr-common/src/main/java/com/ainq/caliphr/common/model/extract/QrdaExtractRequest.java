package com.ainq.caliphr.common.model.extract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mmelusky on 9/8/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrdaExtractRequest {
    Integer userId;
    Iterable<Long> hqmfIds;
}
