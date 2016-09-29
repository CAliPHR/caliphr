package com.ainq.caliphr.hqmf.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.service.CcdaClasspathImportProcess;

/**
 * Created by mmelusky on 8/24/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestCcdaImportProcess {

    @Autowired
    private CcdaClasspathImportProcess ccdaClasspathImportProcess;

    @Test
    public void importDirectoryCcda() {
        ccdaClasspathImportProcess.processDirectoryCcda();
    }

}
