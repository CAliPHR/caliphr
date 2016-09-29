package com.ainq.caliphr.hqmf.service.impl;

import com.ainq.caliphr.common.model.extract.QrdaExtractRequest;
import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.controller.api.sandbox.QrdaRestController;
import com.ainq.caliphr.hqmf.service.QrdaImportProcess;
import com.ainq.caliphr.persistence.dao.QrdaDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.QHqmfDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.HqmfDocumentRepository;
import com.ainq.caliphr.persistence.service.QrdaService;
import com.google.common.primitives.Longs;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.util.StreamUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class QrdaServiceTest {

    @Autowired
    private QrdaService qrdaService;

    @Autowired
    private QrdaImportProcess qrdaImportProcess;

    @Autowired
    private HqmfDocumentRepository hqmfDocumentRepository;

    @Autowired
    private QrdaDao qrdaDao;

    @Autowired
    private QrdaRestController qrdaRestController;

    @Test
    public void testProcessQrdaCategory1() {
        qrdaImportProcess.importClasspathQrdaCat1();
    }

    @Test
    public void findPatientForHqmfDOcument() {

        // Test patient queries
        for (Long i : new long[]{24, 9, 32, 27}) {
            List<Patient> result = qrdaDao.findPatientsForHqmfDocument(i, 1);
            System.out.println(result.size());
        }

    }

    @Test
    public void testCat1Extract() throws IOException {
        QrdaExtractRequest request = new QrdaExtractRequest();
        request.setUserId(1);
        // request.setHqmfIds(Ints.asList(new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57}));
        request.setHqmfIds(Longs.asList(new long[]{39}));
        byte[] output = qrdaRestController.processQrdaCat1Export(request);
        FileUtils.writeByteArrayToFile(new File("C:\\test\\output.zip"), output);
    }

    @Test
    public void testCat3ExtractFile() throws IOException {
        QrdaExtractRequest request = new QrdaExtractRequest();
        request.setUserId(1);
        request.setHqmfIds(Longs.asList(new long[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57}));
        // request.setHqmfIds(Ints.asList(new int[]{42}));
        String output = qrdaRestController.processQrdaCat3Export(request);
        FileUtils.write(new File("C:\\test\\output.xml"), output);
    }

    @Test
    public void testCat3Extract() {

        Iterable<HqmfDocument> docs = hqmfDocumentRepository.findAll(
                QHqmfDocument.hqmfDocument.dateDisabled.isNull()
                //.and(QHqmfDocument.hqmfDocument.cmsId.eq("CMS62v3"))
        );
        List<Long> hqmfIds = StreamUtils.createStreamFromIterator(docs.iterator())
                .map(HqmfDocument::getId)
                .collect(Collectors.toList());

        qrdaService.exportQrdaCategory3(hqmfIds, null);
    }

}
