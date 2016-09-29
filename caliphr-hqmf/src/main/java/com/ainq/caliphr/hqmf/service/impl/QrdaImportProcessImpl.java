package com.ainq.caliphr.hqmf.service.impl;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.hqmf.service.QrdaImportProcess;
import com.ainq.caliphr.persistence.concurrent.ClinicalDocumentExecutor;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;

import java.io.IOException;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Component
public class QrdaImportProcessImpl implements QrdaImportProcess {

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Autowired
    private ClinicalDocumentExecutor clinicalDocumentExecutor;

    static Logger logger = (Logger) LoggerFactory.getLogger(QrdaImportProcessImpl.class);

    private String[] qrda = new String[]{

            // Cypress
            "sample_cypress_qrda",

            // BCH
            "sample_qrda_cat_i/*/*",

            // More Cypress samples
            "sample_qrda_cat_i/ProjectCypress/QRDA_Cat_I/bundle_2_6_0/MasterPatientList/xml/ep"

    };

    @Async
    @Override
    public void importClasspathQrdaCat1() {

        //
        //  Metrics
        Integer numFiles = 0;
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        try {
            for (String path : qrda) {
                Resource[] resources = null;
                try {
                    resources = resolver.getResources(String.format("classpath*:%s/*.xml", path));
                } catch (IOException ex) {
                    logger.error("QRDA Process Error -> ", ex);
                }
                for (Resource resource : resources) {
                    ClinicalDocumentProcessTask task = new ClinicalDocumentProcessTask();
                    task.setResource(resource);
                    task.setLogToFileSystem(Boolean.TRUE);
                    task.setClinicalDocumentService(clinicalDocumentService);
                    task.setDocumentName(resource.getFilename());
                    clinicalDocumentExecutor.processClinicalDocument(task);
                    numFiles++;
                }
            }
        } catch (Exception ex) {
            logger.error("QRDA Process Error -> ", ex);
        }

        logger.info(String.format(
                "*** QRDA CAT I DATA LOAD PROCESS COMPLETED (scheduled %s files for processing) ***"
                , numFiles)
        );
    }

}
