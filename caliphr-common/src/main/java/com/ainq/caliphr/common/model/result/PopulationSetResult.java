package com.ainq.caliphr.common.model.result;

import lombok.Data;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class PopulationSetResult {
    private String submeasureTitle;
    private Integer ippCount;
    private Long ippResultId;
    private Integer numeratorCount;
    private Long numeratorResultId;
    private Integer denominatorCount;
    private Long denominatorResultId;
    private Integer denexCount;
    private Long denexResultId;
    private Integer denexcepCount;
    private Long denexcepResultId;
}
