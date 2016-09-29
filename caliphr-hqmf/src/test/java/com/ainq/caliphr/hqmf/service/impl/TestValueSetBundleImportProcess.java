package com.ainq.caliphr.hqmf.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.service.ValueSetBundleImportProcess;

/**
 * Created by mmelusky on 8/23/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestValueSetBundleImportProcess {

    @Autowired
    private ValueSetBundleImportProcess valueSetBundleImportProcess;

    @Test
    public void processBundleValueSets() {
        valueSetBundleImportProcess.importValueSets();
    }
}
