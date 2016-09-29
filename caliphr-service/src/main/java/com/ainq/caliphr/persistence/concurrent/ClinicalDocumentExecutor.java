package com.ainq.caliphr.persistence.concurrent;

import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;

/**
 * Created by mmelusky on 5/17/2016.
 */
public interface ClinicalDocumentExecutor {
    void processClinicalDocument(ClinicalDocumentProcessTask task);
}
