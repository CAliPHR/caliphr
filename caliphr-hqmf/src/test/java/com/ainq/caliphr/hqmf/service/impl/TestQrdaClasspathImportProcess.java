package com.ainq.caliphr.hqmf.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.service.QrdaImportProcess;

/**
 * Created by mmelusky on 8/24/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestQrdaClasspathImportProcess {

    @Autowired
    private QrdaImportProcess qrdaImportProcess;

    @Test
    public void importCat1Qrda() {
        qrdaImportProcess.importClasspathQrdaCat1();
    }

}
