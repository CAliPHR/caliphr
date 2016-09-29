package com.ainq.caliphr.hqmf.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.controller.api.sandbox.ClinicalDocumentRestController;

/**
 * Created by mmelusky on 3/2/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestClinicalDocumentRestController {

    @Autowired
    private ClinicalDocumentRestController clinicalDocumentRestController;

    @Test
    public void testDecryptProcess() {
        String groupId = "6";
        String startDate = "20160225";
        String endDate = "20160226";
        clinicalDocumentRestController.decryptClinicalDocuments(groupId, startDate, endDate);
    }
}
