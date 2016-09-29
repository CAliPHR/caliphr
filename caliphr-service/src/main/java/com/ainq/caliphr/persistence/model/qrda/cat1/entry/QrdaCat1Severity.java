package com.ainq.caliphr.persistence.model.qrda.cat1.entry;

import lombok.Data;

/**
 * Created by mmelusky on 11/17/2015.
 */
@Data
public class QrdaCat1Severity {

    private String id;
    private String severityCode;
    private String severityCodeSystem;
    private String severityCodeDescription;
    private String severityValueSetOid;

}
