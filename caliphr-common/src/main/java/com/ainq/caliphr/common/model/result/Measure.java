package com.ainq.caliphr.common.model.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure {
    private Long hqmfDocumentId;
    private String cmsId;
    private Date dateCreated;
    private String title;
    private String description;
    private String domainName;
    private Integer domainId;
    private String reportingPeriodStart;
    private String reportingPeriodEnd;
    private List<PopulationSetResult> populationSetResults = new ArrayList<PopulationSetResult>();
}
