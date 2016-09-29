package com.ainq.caliphr.hqmf.controller.api.sandbox;

import ch.qos.logback.classic.Logger;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ainq.caliphr.common.model.extract.QrdaExtractRequest;
import com.ainq.caliphr.common.model.extract.cat1.QrdaCat1XmlFile;
import com.ainq.caliphr.common.model.extract.cat1.QrdaCat1ZipFile;
import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.hqmf.service.QrdaImportProcess;
import com.ainq.caliphr.persistence.concurrent.ClinicalDocumentExecutor;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.service.QrdaService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
public class QrdaRestController {
	
	static Logger logger = (Logger) LoggerFactory.getLogger(QrdaRestController.class);

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Autowired
    private QrdaImportProcess qrdaImportProcess;

    @Autowired
    private QrdaService qrdaService;

    @Autowired
    private ClinicalDocumentExecutor clinicalDocumentExecutor;

    @RequestMapping(value = "/api/qrda", method = RequestMethod.POST)
    public JsonResponse processQrda() throws JAXBException, URISyntaxException {
        qrdaImportProcess.importClasspathQrdaCat1();
        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.OK);
        response.setMessage("Process triggered.");
        return response;
    }

    @RequestMapping(value = "/api/qrda_cat1/export", method = RequestMethod.POST, produces="application/zip")
    public byte[] processQrdaCat1Export(@RequestBody QrdaExtractRequest request) throws IOException {

        Iterable<Long> hqmfIds = null;
        if (request != null && request.getHqmfIds() != null) {
            hqmfIds = request.getHqmfIds();
        }
        Integer userId = null;
        if (request != null && request.getUserId() != null) {
            userId = request.getUserId();
        }

        //
        //  Create an outer zip file to hold the zip files for each measure
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        List<QrdaCat1ZipFile> measureZipFiles = qrdaService.exportQrdaCategory1(hqmfIds, userId);
        for (QrdaCat1ZipFile zipFile : measureZipFiles) {

            //
            //  Create a zip file to hold each measure patients
            if (zipFile.getFileName() != null) {
                ByteArrayOutputStream measureBaos = new ByteArrayOutputStream();
                ZipOutputStream measureZos = new ZipOutputStream(measureBaos);

                //
                //  Add all XML files to the contents of this zip file.
                if (zipFile.getZipFileContents() != null) {
                    for (QrdaCat1XmlFile xmlFile : zipFile.getZipFileContents()) {
                        if (xmlFile.getFileName() != null && xmlFile.getXmlConent() != null) {
                            //
                            //  Create the XML file from the content
                            ByteArrayOutputStream xmlBos = new ByteArrayOutputStream();
                            xmlBos.write(xmlFile.getXmlConent().getBytes(StandardCharsets.UTF_8));

                            //
                            // Create entry in the measure zip file
                            ZipEntry xmlEntry = new ZipEntry(xmlFile.getFileName());
                            xmlEntry.setSize(xmlBos.size());
                            measureZos.putNextEntry(xmlEntry);
                            measureZos.write(xmlBos.toByteArray());
                            measureZos.closeEntry();
                        }
                    }

                    measureZos.close();

                    //
                    //  Add the measure zip file to the outer zip file
                    ZipEntry xmlEntry = new ZipEntry(zipFile.getFileName());
                    xmlEntry.setSize(measureBaos.size());
                    zos.putNextEntry(xmlEntry);
                    zos.write(measureBaos.toByteArray());
                    zos.closeEntry();
                }
            }
        }

        zos.close();
        return baos.toByteArray();
    }

    @RequestMapping(value = "/api/qrda_cat3/export", method = RequestMethod.POST)
    public String processQrdaCat3Export(@RequestBody QrdaExtractRequest request) {
        Iterable<Long> hqmfIds = null;
        if (request != null && request.getHqmfIds() != null) {
            hqmfIds = request.getHqmfIds();
        }
        Integer userId = null;
        if (request != null && request.getUserId() != null) {
            userId = request.getUserId();
        }
        return qrdaService.exportQrdaCategory3(hqmfIds, userId);
    }

    @RequestMapping(value = "/api/qrda_cat1/import", method = RequestMethod.POST)
    public String processQrdaCat1Import(
    		@RequestParam("file") MultipartFile file,
    		@RequestParam(value = "userId", required = false) Integer userId) throws JAXBException, IOException {

    	String filename = file.getOriginalFilename();
        Integer numFiles = 0;
    	logger.debug("processing file " + filename);

		if (filename.endsWith(".xml")) {
            ClinicalDocumentProcessTask task = new ClinicalDocumentProcessTask();
            task.setInputStream(file.getInputStream());
            task.setLogToFileSystem(Boolean.TRUE);
            task.setClinicalDocumentService(clinicalDocumentService);
            task.setDocumentName(filename);
            clinicalDocumentExecutor.processClinicalDocument(task);
            numFiles++;
    	}
    	else if (filename.endsWith(".zip")) {

    		ZipInputStream zis = new ZipInputStream(file.getInputStream());
    		ZipEntry zipEntry;
    		while((zipEntry=zis.getNextEntry())!=null){
    			logger.debug("reading entry " + zipEntry.getName());
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			IOUtils.copy(zis, baos);

                ClinicalDocumentProcessTask task = new ClinicalDocumentProcessTask();
                task.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
                task.setLogToFileSystem(Boolean.TRUE);
                task.setClinicalDocumentService(clinicalDocumentService);
                task.setDocumentName(zipEntry.getName());
                clinicalDocumentExecutor.processClinicalDocument(task);
                numFiles++;
    		}

    	}
    	else {
    		throw new IllegalArgumentException("unrecognized file type: " + filename);
    	}

		// Output statistics
		String processingStats = String.format(
                "*** QRDA CAT I DATA LOAD PROCESS COMPLETED (scheduled %s files to process) ***"
                , numFiles);
        logger.info(processingStats);
        return processingStats;

    }

}





