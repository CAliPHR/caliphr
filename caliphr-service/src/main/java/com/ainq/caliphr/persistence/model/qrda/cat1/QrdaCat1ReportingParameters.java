package com.ainq.caliphr.persistence.model.qrda.cat1;

import lombok.Data;

/**
 * Created by mmelusky on 10/27/2015.
 */
@Data
public class QrdaCat1ReportingParameters {

    private String extension;
    private String reportingPeriod;
    private String startDateNumeric;
    private String endDateNumeric;

    public static String reportingPeriodHeader(String startDate, String endDate) {
        return String.format("Reporting period: %s - %s", startDate, endDate);
    }
}
