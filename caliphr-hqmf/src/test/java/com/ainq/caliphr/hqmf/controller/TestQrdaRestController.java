package com.ainq.caliphr.hqmf.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.controller.api.sandbox.QrdaRestController;

/**
 * Created by mmelusky on 10/20/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestQrdaRestController {

    @Autowired
    private QrdaRestController qrdaRestController;

    @Test
    public void testProcessQrdaCat1Export() {

    }
    
    @Test
    public void testProcessQrdaCat3Import() throws IOException, JAXBException {
    	InputStream is = new FileInputStream("c:/dev/Test_5661cafe6379700561000000._qrda.zip");
    	MultipartFile file = new MockMultipartFile("file","test.zip","", is);
    	qrdaRestController.processQrdaCat1Import(file, 1);
    }

}
