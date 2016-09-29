package com.ainq.caliphr.hqmf.service.impl;

import ch.qos.logback.classic.Logger;

import com.ainq.caliphr.common.model.sandbox.gson.GsonValue;
import com.ainq.caliphr.common.util.format.DateTimeFormat;
import com.ainq.caliphr.hqmf.Constants;
import com.ainq.caliphr.hqmf.service.ValueSetBundleImportProcess;
import com.ainq.caliphr.persistence.dao.ValueSetDao;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Bundle;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ValueSet;
import com.ainq.caliphr.persistence.service.ValueSetService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by mmelusky on 8/21/2015.
 */
@Component
public class ValueSetBundleImportProcessImpl implements ValueSetBundleImportProcess {

    static Logger logger = (Logger) LoggerFactory.getLogger(ValueSetBundleImportProcessImpl.class);

    // Instance data and constants
    private final String valueSetDir = "value_sets/json";

    public static Integer THREAD_POOL_SIZE = 4;
    
    @Autowired
    private Environment environment;

    @Autowired
    private ValueSetService valueSetService;
    
    @Autowired
    private ValueSetDao valueSetDao;
    
    private static final int NUM_RECORDS_PER_THREAD = 20;

    @Async
    @Override
    public void importValueSets() {
        // Set the Gson libraries
        Long processStartTime = System.currentTimeMillis();
        
        // Load the measures into a hash map (two measure dirs)
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
       	     .priority(Thread.MIN_PRIORITY)
       	     .build();

        // Create a thread pool
         ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, threadFactory);
//        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {

            // Load the bundles
            List<Bundle> bundleList = valueSetService.getAllBundles();
            for (Bundle bundle : bundleList) {

                String bundleDir = String.format("file:%s/%s/%s/*.json"
                        , environment.getProperty(Constants.PropertyKey.BUNDLE_FILESYSTEM_ROOT)
                        , bundle.getBundleVersion()
                        , valueSetDir);
                logger.info(String.format("Reading from bundle directory -> %s", bundleDir));
                Resource[] resources = resolver.getResources(bundleDir);

                for (Resource resource : resources) {
                    Long resourceStartTime = System.currentTimeMillis();
 
                    Gson myGson = new Gson();
                    JsonParser jsonParser = new JsonParser();
                    JsonReader jsonReader = new JsonReader(new InputStreamReader(
                            resource.getInputStream()));
                    JsonObject resultObject = jsonParser.parse(jsonReader)
                            .getAsJsonObject();
                    GsonValue record = myGson.fromJson(resultObject,
                            GsonValue.class);
                    
                    final ValueSet valueSet = saveValueSet(bundle, record);

                    // assign different record ranges to multiple threads
                    for (int i = 0; i < record.getConcepts().size(); i += NUM_RECORDS_PER_THREAD) {
                    	final int j = i;
	                    executorService.execute(() -> {
                        	int toIndex = Math.min(j + NUM_RECORDS_PER_THREAD, record.getConcepts().size());
	                        try {
	                        	valueSetService.processBundleValueSetRecords(record.getConcepts().subList(j, toIndex), valueSet);
	                        } catch (Exception ex) {
	                            logger.error("Value Set Process Error -> %s", ex);
	                        } finally {
	                            logger.info(String.format(
	                                    "+++ Processed File %s records %s - %s of %s (Total Time: %s).", resource.getFilename(), j+1, toIndex, record.getConcepts().size(), DateTimeFormat.humanElapsedTime(resourceStartTime)));
	                        }
	                    });
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Value Set Process Error -> %s", ex);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(24, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info(String.format(
                "*** VALUE SET PROCESS COMPLETED (Total Time: %s).", DateTimeFormat.humanElapsedTime(processStartTime)));
    }

	private ValueSet saveValueSet(Bundle bundle, GsonValue record) {
		ValueSet valueSet = new ValueSet();
		valueSet.setValueSetName(record.getDisplay_name());
		valueSet.setHl7Oid(record.getOid());
		valueSet.setDateCreated(new Date());
		valueSet.setDateUpdated(new Date());
		valueSet.setBundle(bundle);
		valueSet.setUserCreated(com.ainq.caliphr.persistence.config.Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
		valueSet.setUserUpdated(com.ainq.caliphr.persistence.config.Constants.ApplicationUser.ADMINISTRATIVE_USER_ID);
		valueSet = this.valueSetDao.saveValueSet(valueSet);
		return valueSet;
	}
}
