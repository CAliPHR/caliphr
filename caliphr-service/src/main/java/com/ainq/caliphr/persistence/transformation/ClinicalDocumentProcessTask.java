package com.ainq.caliphr.persistence.transformation;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.ainq.caliphr.common.util.format.DateTimeFormat;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;

import ch.qos.logback.classic.Logger;
import lombok.Data;

/**
 * Created by mmelusky on 5/9/2016.
 */
@Data
public class ClinicalDocumentProcessTask implements Runnable {

    static Logger logger = (Logger) LoggerFactory.getLogger(ClinicalDocumentProcessTask.class);
    
    // Instance
    private Boolean logToFileSystem;
    private ClinicalDocumentService clinicalDocumentService;
    private File fileEntry;
    private Resource resource;
    private InputStream inputStream;
    private String documentName;
    private JAXBElement<?> document;
    private String xml;
    private Float fileSize;
    private ClinicalDocument clinicalDocument;
    private String medicalRecordNum;
    
    

    @Override
    public void run() {

        // Process each C-CDA file
        Long resourceStartTime = System.currentTimeMillis();

        try {
            
            // Process the data
            clinicalDocumentService.processClinicalDocumentRecord(document, logToFileSystem, clinicalDocument.getId(), medicalRecordNum);

        } catch (Exception ex) {
            logger.error("Clinical Document Process Error -> ", ex);
        }

        // Output statistics
        logger.info(String.format(
                "+ Clinical Document File <%s> (%s KB) processed.  (Total Time: %s).",
                documentName, fileSize,
                DateTimeFormat.humanElapsedTime(resourceStartTime)));

        // Attempt to delete the file
        if (fileEntry != null) {
            try {
                fileEntry.setWritable(true);
                if (!fileEntry.delete()) {
                    logger.info("Unable to delete file %s", fileEntry.getName());
                }
            } catch (Exception ex) {
                logger.info("File deletion exception ", ex);
            }
        }
    }
}
