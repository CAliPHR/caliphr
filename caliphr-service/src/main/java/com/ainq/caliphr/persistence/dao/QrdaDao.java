package com.ainq.caliphr.persistence.dao;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDocument;
import com.ainq.caliphr.persistence.model.qrda.cat1.QrdaCat1Extract;

import java.util.List;

/**
 * Created by mmelusky on 10/20/2015.
 */
public interface QrdaDao {
    List<Patient> findPatientsForHqmfDocument(Long hqmfDocumentId, Integer userId);

    QrdaCat1Extract retrieveQrdaCat1Extract(Patient patient, HqmfDocument hqmfDocument);
}
