package com.ainq.caliphr.website.controller.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ainq.caliphr.website.config.WebConfig;

import static org.junit.Assert.assertTrue;

/**
 * Created by mmelusky on 5/11/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
public class BundleRestControllerTest {
    /*
        TODO please populate this with workable test cases
     */

    @Test
    public void alwaysPassTestCase() {
        assertTrue(true);
    }
}

