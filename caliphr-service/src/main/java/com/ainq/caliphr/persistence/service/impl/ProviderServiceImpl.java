package com.ainq.caliphr.persistence.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.persistence.concurrent.ClinicalDocumentExecutor;
import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.dao.ProviderDao;
import com.ainq.caliphr.persistence.dao.UserSecurityDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Organization;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeGroup;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Provider;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.service.ProviderService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;
import com.ainq.caliphr.persistence.util.DatabaseEncyptionUtil;
import com.ainq.caliphr.persistence.util.JsonStringUtility;

import ch.qos.logback.classic.Logger;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * Created by mmelusky on 8/6/2015.
 */

@Service
public class ProviderServiceImpl implements ProviderService {

    static Logger logger = (Logger) LoggerFactory.getLogger(ProviderServiceImpl.class);

    @Autowired
    private ClinicalDocumentExecutor clinicalDocumentExecutor;

    @Autowired
    private Environment environment;

    @Autowired
    private DatabaseEncyptionUtil databaseEncyptionUtil;

    @Autowired
    private ProviderDao providerDao;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Override
    public List<Provider> getAllProviders(Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("userId", userId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.QUERY_REQUEST, ProviderServiceImpl.class.getName(), "getAllProviders", JsonStringUtility.buildJsonRequest(requestJson));
        }
        return providerDao.getAllProviders(userId);
    }

    @Override
    public List<PracticeGroup> getAllPracticeGroups(Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("userId", userId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.QUERY_REQUEST, ProviderServiceImpl.class.getName(), "getAllPracticeGroups", JsonStringUtility.buildJsonRequest(requestJson));
        }
        return providerDao.getAllPracticeGroups(userId);
    }

    @Override
    public List<Organization> getAllOrganizations(Integer userId) {
        if (userId != null && userId > 0) {
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("userId", userId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.QUERY_REQUEST, ProviderServiceImpl.class.getName(), "getAllOrganizations", JsonStringUtility.buildJsonRequest(requestJson));
        }
        return providerDao.getAllOrganizations(userId);
    }

    @Override
    @Transactional
    public PracticeGroup findGroupBySenderOid(String senderOid) {
        return providerDao.findGroupBySenderOid(senderOid);
    }

    @SuppressWarnings("rawtypes")
	@Override
    @Async
    public void decryptClinicalDocuments(String groupId, Date startDate, Date endDate) {

        //
        //  Get all of the C-CDA files in the directory.
        String directoryRoot = String.format("%s", environment.getProperty(Constants.PropertyKey.CLINICAL_DOCUMENT_BACKUP_ROOT));
        final File folder = new File(directoryRoot);
        for (final File groupDirectory : sorted(folder.listFiles())) {
            if (groupDirectory != null && groupDirectory.isDirectory()) {

                //
                //  Check to see if the group ID matches the name of the directory
                if (groupDirectory.getName() != null && (groupDirectory.getName().startsWith(String.format("%s_", groupId))
                        || groupDirectory.getName().equalsIgnoreCase(groupId))) {   // NOTFOUND group ID

                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime(startDate);
                    Calendar endCal = Calendar.getInstance();
                    endCal.setTime(endDate);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    while (startCal.compareTo(endCal) <= 0) {

                        // Attempt to open the directory
                        String dateRoot = String.format("%s/%s/%s"
                                , environment.getProperty(Constants.PropertyKey.CLINICAL_DOCUMENT_BACKUP_ROOT)
                                , groupDirectory.getName()
                                , formatter.format(startCal.getTime()));
                        try {
                            final File dateDirectory = new File(dateRoot);
                            if (dateDirectory.isDirectory()) {
                                for (final File encryptedFile : sorted(dateDirectory.listFiles())) {
                                    try {
                                        ZipFile zipFile = new ZipFile(encryptedFile);
                                        if (zipFile.isEncrypted()) {
                                            zipFile.setPassword(databaseEncyptionUtil.getKey());
                                        }
                                        List fileHeaderList = zipFile.getFileHeaders();
                                        for (Object aFileHeaderList : fileHeaderList) {
                                            FileHeader fileHeader = (FileHeader) aFileHeaderList;
                                            StringWriter sw = new StringWriter();
                                            IOUtils.copy(zipFile.getInputStream(fileHeader), sw, StandardCharsets.UTF_8);
                                            String xml = sw.toString();
                                            writePlaintextCcdaFile(xml, encryptedFile.getName(), groupDirectory.getName(), formatter.format(startCal.getTime()));
                                        }
                                    } catch (Exception e) {
                                        logger.error("C-CDA decrypt errors -> ", e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("C-CDA decrypt errors -> ", e);
                        }

                        // i++
                        startCal.add(Calendar.DATE, 1);
                    }
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
	@Override
    @Async
    public void reprocessClinicalDocuments(String groupId, Date startDate, Date endDate) {

        //
        //  Get all of the C-CDA files in the directory.
        String directoryRoot = String.format("%s", environment.getProperty(Constants.PropertyKey.CLINICAL_DOCUMENT_BACKUP_ROOT));
        final File folder = new File(directoryRoot);
        for (final File groupDirectory : sorted(folder.listFiles())) {
            if (groupDirectory != null && groupDirectory.isDirectory()) {

                //
                //  Check to see if the group ID matches the name of the directory
                if (groupDirectory.getName() != null && (groupDirectory.getName().startsWith(String.format("%s_", groupId))
                        || groupDirectory.getName().equalsIgnoreCase(groupId))) {   // NOTFOUND group ID

                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime(startDate);
                    Calendar endCal = Calendar.getInstance();
                    endCal.setTime(endDate);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    while (startCal.compareTo(endCal) <= 0) {

                        // Attempt to open the directory
                        String dateRoot = String.format("%s/%s/%s"
                                , environment.getProperty(Constants.PropertyKey.CLINICAL_DOCUMENT_BACKUP_ROOT)
                                , groupDirectory.getName()
                                , formatter.format(startCal.getTime()));
                        try {
                            final File dateDirectory = new File(dateRoot);
                            if (dateDirectory.isDirectory()) {
                                for (final File encryptedFile : sorted(dateDirectory.listFiles())) {
                                    try {
                                        ZipFile zipFile = new ZipFile(encryptedFile);
                                        if (zipFile.isEncrypted()) {
                                            zipFile.setPassword(databaseEncyptionUtil.getKey());
                                        }
                                        List fileHeaderList = zipFile.getFileHeaders();
                                        for (Object aFileHeaderList : fileHeaderList) {
                                            FileHeader fileHeader = (FileHeader) aFileHeaderList;
                                            StringWriter sw = new StringWriter();
                                            IOUtils.copy(zipFile.getInputStream(fileHeader), sw, StandardCharsets.UTF_8);
                                            String xml = sw.toString();

                                            //
                                            //  Reprocess the ccda file
                                            ClinicalDocumentProcessTask task = new ClinicalDocumentProcessTask();
                                            task.setLogToFileSystem(Boolean.FALSE);
                                            task.setClinicalDocumentService(clinicalDocumentService);
                                            task.setDocumentName(fileHeader.getFileName());
                                            task.setXml(xml);
                                            clinicalDocumentExecutor.processClinicalDocument(task);
                                            logger.info(String.format("CCDA file %s/%s submitted for processing.",
                                                    encryptedFile.getAbsolutePath(), encryptedFile.getName()));
                                        }
                                    } catch (Exception e) {
                                        logger.error("C-CDA reprocess errors -> ", e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("C-CDA reprocess errors -> ", e);
                        }

                        // i++
                        startCal.add(Calendar.DATE, 1);
                    }
                }
            }
        }
    }

    /*
        Private method to write the plaintext xml to the filesystem.
     */
    private void writePlaintextCcdaFile(String xmlContent, String fileName, String practiceDirectory, String currentDirectory) {

        String directoryRoot = String.format("%s/%s/%s/"
                , environment.getProperty(Constants.PropertyKey.CLINICAL_DOCUMENT_PLAINTEXT_ROOT)
                , practiceDirectory
                , currentDirectory);

        // Remove trailing "zip" extension if found
        if (fileName.endsWith("zip")) {
            fileName = FilenameUtils.removeExtension(fileName);
        }
        String filePath = String.format("%s/%s", directoryRoot, fileName);

        // Create the directory structure if not present
        File directoryInstance = new File(directoryRoot);
        if (!directoryInstance.exists()) {
            directoryInstance.mkdirs();
        }

        // Write the xml to the directory
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(xmlContent);
        } catch (IOException ex) {
            // Ignore this
        }
    }
    
    private File[] sorted(File[] files) {
        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }
}
