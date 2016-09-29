package com.ainq.caliphr.persistence.model.qrda.cat1.entry;

import lombok.Data;

/**
 * Created by mmelusky on 11/17/2015.
 */
@Data
public class QrdaCat1Ordinality {

    private String id;
    private String ordinalityCode;
    private String ordinalityCodeSystem;
    private String ordinalityCodeDescription;
    private String ordinalityValueSetOid;

}
