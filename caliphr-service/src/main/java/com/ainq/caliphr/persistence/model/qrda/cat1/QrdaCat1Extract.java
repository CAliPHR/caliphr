package com.ainq.caliphr.persistence.model.qrda.cat1;

import lombok.Data;

import java.util.List;

import com.ainq.caliphr.persistence.model.qrda.cat1.entry.QrdaCat1Entry;
import com.ainq.caliphr.persistence.model.qrda.cat1.patient.QrdaCat1Patient;
import com.ainq.caliphr.persistence.model.qrda.cat1.provider.QrdaCat1Measure;
import com.ainq.caliphr.persistence.model.qrda.cat1.provider.QrdaCat1Provider;

/**
 * Created by mmelusky on 10/20/2015.
 */
@Data
public class QrdaCat1Extract {

    private String uuid;
    private String effectiveTime;
    private QrdaCat1Patient patient;
    private QrdaCat1Provider provider;
    private QrdaCat1ReportingParameters reportingParameters;
    private List<QrdaCat1Measure> measures;
    private List<QrdaCat1Entry> entries;

}
