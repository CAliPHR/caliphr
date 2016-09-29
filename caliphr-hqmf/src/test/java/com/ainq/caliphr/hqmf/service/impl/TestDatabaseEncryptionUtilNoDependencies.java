package com.ainq.caliphr.hqmf.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.persistence.util.AES256KeyGeneratorNoDependencies;
import com.ainq.caliphr.persistence.util.DatabaseEncryptionUtilNoDependencies;

/**
 * Created by mmelusky on 9/23/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestDatabaseEncryptionUtilNoDependencies {

    @Test
    public void generateAESkey() {
        System.out.println(AES256KeyGeneratorNoDependencies.generateKey());
    }

    @Test
    public void generateEncryptedKey() {

        // Change these
        //
        String pass = "suVKp9CpTwhqeeB";
        String key = "OKMEva27jbkW9gd/un36zqUNOtE6c8nHWz22tGJ33ps=";

        //
        //


        // Key
        DatabaseEncryptionUtilNoDependencies deu = new DatabaseEncryptionUtilNoDependencies(key);

        // Password
        System.out.println("encrypted password=" + deu.encryptPassword(pass));
    }

}
