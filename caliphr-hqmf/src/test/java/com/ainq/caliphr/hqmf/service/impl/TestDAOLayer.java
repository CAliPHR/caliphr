package com.ainq.caliphr.hqmf.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeAvailableMeasure;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestDAOLayer {

    @Autowired
    private MeasureDao measureDao;

    @Test
    public void testActiveProviderMeasures() {
        List<PracticeAvailableMeasure> measureList = measureDao.getAvailableMeasuresForProvider(1, 1);
        Assert.assertNull(measureList);
    }
    
    @Test
    public void testJPA() {
    	
    }

}
