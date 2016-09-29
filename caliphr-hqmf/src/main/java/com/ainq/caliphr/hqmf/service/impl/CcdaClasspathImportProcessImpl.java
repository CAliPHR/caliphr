package com.ainq.caliphr.hqmf.service.impl;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.hqmf.service.CcdaClasspathImportProcess;
import com.ainq.caliphr.persistence.concurrent.ClinicalDocumentExecutor;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Component
public class CcdaClasspathImportProcessImpl implements CcdaClasspathImportProcess {

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Autowired
    private ClinicalDocumentExecutor clinicalDocumentExecutor;

    static Logger logger = (Logger) LoggerFactory.getLogger(CcdaClasspathImportProcessImpl.class);

    private static final File CCDA_DIRECTORY = new File("C:/c-cda-files/");

    @Async
    @Override
    public void processDirectoryCcda() {

        //
        //  Metrics
        Integer numFiles = 0;
        try {
        	File[] fileList = CCDA_DIRECTORY.listFiles();
            Arrays.sort(fileList, Comparator.comparing(File::getName));
            for (final File fileEntry : fileList) {
                if (fileEntry.isFile()) {
                    ClinicalDocumentProcessTask task = new ClinicalDocumentProcessTask();
                    task.setLogToFileSystem(Boolean.TRUE);
                    task.setClinicalDocumentService(clinicalDocumentService);
                    task.setFileEntry(fileEntry);
                    task.setDocumentName(fileEntry.getName());
                    clinicalDocumentExecutor.processClinicalDocument(task);
                    numFiles++;
                }
            }
        } catch (Exception ex) {
            logger.error("CCDA Directory Process error -> ", ex);
        }

        logger.info(String.format(
                "*** CLINICAL DOCUMENT DATA LOAD PROCESS COMPLETED (scheduled %s files for processing) ***"
                , numFiles)
        );
    }

}
