package com.ainq.caliphr.persistence.model.qrda.cat1.entry;

import lombok.Data;

/**
 * Created by mmelusky on 10/27/2015.
 */
@Data
public class QrdaCat1Entry {

    private String id;
    private String template;
    private String uuid;
    private String negationInd;
    private String description;
    private String low;
    private String high;
    private String codeCode;
    private String codeSystem;
    private String codeDescription;
    private String codeValueSetOid;
    private String valueCode;
    private String valueCodeSystem;
    private String valueDescription;
    private String valueValueSetOid;
    private String resultValue;
    private String resultValueUnit;
    private QrdaCat1Reason reason;
    private QrdaCat1Ordinality ordinality;
    private QrdaCat1Severity severity;

}

