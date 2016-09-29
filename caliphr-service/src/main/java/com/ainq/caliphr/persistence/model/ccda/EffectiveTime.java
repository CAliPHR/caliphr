package com.ainq.caliphr.persistence.model.ccda;

import java.util.Date;

/**
 * Created by mmelusky on 5/21/2015.
 */
public class EffectiveTime {

    private Date effectiveTimeStart;
    private Date effectiveTimeEnd;

    public Date getEffectiveTimeStart() {
        return effectiveTimeStart;
    }

    public void setEffectiveTimeStart(Date effectiveTimeStart) {
        this.effectiveTimeStart = effectiveTimeStart;
    }

    public Date getEffectiveTimeEnd() {
        return effectiveTimeEnd;
    }

    public void setEffectiveTimeEnd(Date effectiveTimeEnd) {
        this.effectiveTimeEnd = effectiveTimeEnd;
    }
}
