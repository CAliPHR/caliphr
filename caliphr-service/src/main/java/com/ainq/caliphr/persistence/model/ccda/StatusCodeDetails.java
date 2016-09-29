package com.ainq.caliphr.persistence.model.ccda;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.StatusCode;

/**
 * Created by mmelusky on 5/21/2015.
 */
public class StatusCodeDetails {

    private StatusCode statusCode;
    private String statusCodeName;

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCodeName() {
        return statusCodeName;
    }

    public void setStatusCodeName(String statusCodeName) {
        this.statusCodeName = statusCodeName;
    }
}
