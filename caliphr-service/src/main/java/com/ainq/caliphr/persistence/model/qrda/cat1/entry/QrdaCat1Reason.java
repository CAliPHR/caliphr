package com.ainq.caliphr.persistence.model.qrda.cat1.entry;

import lombok.Data;

/**
 * Created by mmelusky on 11/13/2015.
 */
@Data
public class QrdaCat1Reason {

    private String id;
    private String effectiveTime;
    private String reasonCode;
    private String reasonCodeSystem;
    private String reasonCodeDescription;
    private String reasonCodeValueSetOid;

}
