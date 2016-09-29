package com.ainq.caliphr.hqmf.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.ainq.caliphr.common.model.security.AuditType;
import com.ainq.caliphr.hqmf.model.HQMFDocument;
import com.ainq.caliphr.hqmf.service.HQMFService;
import com.ainq.caliphr.hqmf.service.impl.GeneratePopulationSqlStatements.PopulationGenerationContext;
import com.ainq.caliphr.hqmf.service.transformation.HQMFTransformer;
import com.ainq.caliphr.hqmf.util.H2PopulateUtil;
import com.ainq.caliphr.hqmf.util.MeasureMetadataUtil;
import com.ainq.caliphr.hqmf.util.StopWatch;
import com.ainq.caliphr.hqmf.util.SystemUtil;
import com.ainq.caliphr.persistence.dao.MeasureDao;
import com.ainq.caliphr.persistence.dao.ProviderDao;
import com.ainq.caliphr.persistence.dao.UserSecurityDao;
import com.ainq.caliphr.persistence.mail.CaliphrMailer;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ApplicationUser;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.Bundle;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.HqmfDocument;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.PracticeAvailableMeasure;
import com.ainq.caliphr.persistence.util.JsonStringUtility;
import ch.qos.logback.classic.Logger;

@Service
public class HQMFServiceImpl implements HQMFService {

    static Logger logger = (Logger) LoggerFactory.getLogger(HQMFServiceImpl.class);

    @Autowired
    private ApplicationContext appCxt;

    @Autowired
    private H2PopulateUtil h2PopulateUtil;

    @Autowired
    private MeasureDao measureDao;
    
    @Autowired
    private MeasureMetadataUtil measureMetadataUtil;

    @Autowired
    private ProviderDao providerDao;

    @Autowired
    private UserSecurityDao userSecurityDao;
    
    @Autowired
    private HQMFTransformer hqmfTransformer;

    private Map<String, HQMFDocument> hqmfDocCache = new HashMap<>();

    @Override
    public HQMFDocument parseHQMF(Resource file) throws ParserConfigurationException, SAXException, IOException {
        HQMFDocument doc = new HQMFDocument();
        MeasurePopulateFromHQMF.populate(doc, file);

        return doc;
    }

    @Override
    public Map<String, PopulationGenerationContext> generateSQL(HQMFDocument hqmfDoc, Integer userId, int bundleId) {
        return appCxt.getBean(GenerateSQLsFromHQMF.class, hqmfDoc, bundleId).generateSQL(userId);
    }

    @Override
    @Async
    public void calculateMeasures(Integer providerId, Date reportingPeriodStart, Date reportingPeriodEnd, Integer userId) {

        // Find the bundle details from the database
        List<Bundle> activeBundles = measureDao.getActiveBundles();
        if (activeBundles == null) {
            throw new IllegalStateException("Error retrieving the bundle information from the database!");
        } else if (activeBundles.size() != 1) {
            throw new IllegalStateException("There must be only one active bundle in the database for calculations to process!");
        }
        final Integer bundleId = activeBundles.get(0).getId();

        if (userId != null && userId > 0) {
            // Create audit record
            Map<String, Object> requestJson = new HashMap<String, Object>();
            requestJson.put("userId", providerId);
            userSecurityDao.addUserAuditRecord(userId, AuditType.EDIT_REQUEST, HQMFServiceImpl.class.getName(), "calculateMeasures", JsonStringUtility.buildJsonRequest(requestJson));

            // Test if provider belongs to user
            if (! providerDao.checkProviderUserRelationship(providerId, userId)) {
                throw new IllegalStateException("Provider is not associated with the application user!");
            }
        }

        /*
            optional feature - limit which measures are run for a practice
        */
        List<PracticeAvailableMeasure> practiceAvailableMeasures = providerId != null ? measureDao.getAvailableMeasuresForProvider(providerId, bundleId) : null;
        Boolean filterMeasures = (practiceAvailableMeasures != null && practiceAvailableMeasures.size() > 0);

        // only one measure calculation should be taking place at a given time
        // TODO: perhaps refactor to use a message queue
        synchronized (HQMFServiceImpl.class) {
            logger.info("HQMF web process started.");
            try {

                // start with a fresh in-memory database
                h2PopulateUtil.resetToFreshDatabase(providerId, reportingPeriodStart, reportingPeriodEnd);

                StopWatch stopwatch = new StopWatch();
                
                //long start = System.currentTimeMillis();
                measureDao.markActiveMeasuresInactive(providerId, userId);
                //System.out.println("deletions took ms: " + (System.currentTimeMillis() - start));

                ThreadFactory factory = new BasicThreadFactory.Builder()
                	     .priority(Thread.MIN_PRIORITY)
                	     .build();
                
                // as the current version of H2 cannot reliably do multi-threading, we split up the parsing and calculation steps to two different
                // executors.  HQMF parsing can be done in multiple threads, but then the calculations themselves are done in a single thread.
                ExecutorService hqmfParseExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), factory);
                ExecutorService calculationExecutor = Executors.newSingleThreadExecutor(factory);
                
                String epPath = "file:" + measureMetadataUtil.getActiveBundleEpPath();

                Set<Resource> resources = getResources(
                		
                		"dummy*" 

                        , epPath + "CMS122v3/hqmf2.xml"
                        , epPath + "CMS123v3/hqmf2.xml"
                        , epPath + "CMS124v3/hqmf2.xml"
                        , epPath + "CMS125v3/hqmf2.xml"
                        , epPath + "CMS126v3/hqmf2.xml"
                        , epPath + "CMS127v3/hqmf2.xml"
                        , epPath + "CMS129v4/hqmf2.xml"
                        , epPath + "CMS130v3/hqmf2.xml"
                        , epPath + "CMS131v3/hqmf2.xml"
                        , epPath + "CMS132v3/hqmf2.xml"
                        , epPath + "CMS133v3/hqmf2.xml"
                        , epPath + "CMS134v3/hqmf2.xml"
                        , epPath + "CMS135v3/hqmf2.xml"
                        , epPath + "CMS137v3/hqmf2.xml"
                        , epPath + "CMS138v3/hqmf2.xml"
                        , epPath + "CMS139v3/hqmf2.xml"
                        , epPath + "CMS140v3/hqmf2.xml"
                        , epPath + "CMS141v4/hqmf2.xml"
                        , epPath + "CMS142v3/hqmf2.xml"
                        , epPath + "CMS143v3/hqmf2.xml"
                        , epPath + "CMS144v3/hqmf2.xml"
                        , epPath + "CMS145v3/hqmf2.xml"
                        , epPath + "CMS146v3/hqmf2.xml"
                        , epPath + "CMS147v4/hqmf2.xml"
                        , epPath + "CMS148v3/hqmf2.xml"
                        , epPath + "CMS149v3/hqmf2.xml"
                        , epPath + "CMS153v3/hqmf2.xml"
                        , epPath + "CMS154v3/hqmf2.xml"
                        , epPath + "CMS155v3/hqmf2.xml"
                        , epPath + "CMS157v3/hqmf2.xml"
                        , epPath + "CMS158v3/hqmf2.xml"
                        , epPath + "CMS159v3/hqmf2.xml"
                        , epPath + "CMS160v3/hqmf2.xml"
                        , epPath + "CMS161v3/hqmf2.xml"
                        , epPath + "CMS163v3/hqmf2.xml"
                        , epPath + "CMS164v3/hqmf2.xml"
                        , epPath + "CMS165v3/hqmf2.xml"
                        , epPath + "CMS166v4/hqmf2.xml"
                        , epPath + "CMS167v3/hqmf2.xml"
                        , epPath + "CMS169v3/hqmf2.xml"
                        , epPath + "CMS177v3/hqmf2.xml"
                        , epPath + "CMS182v4/hqmf2.xml"
                        , epPath + "CMS22v3/hqmf2.xml"
                        , epPath + "CMS2v4/hqmf2.xml"
                        , epPath + "CMS50v3/hqmf2.xml"
                        , epPath + "CMS52v3/hqmf2.xml"
                        , epPath + "CMS56v3/hqmf2.xml"
                        , epPath + "CMS62v3/hqmf2.xml"
                        , epPath + "CMS65v4/hqmf2.xml"
                        , epPath + "CMS66v3/hqmf2.xml"
                        , epPath + "CMS68v4/hqmf2.xml"
                        , epPath + "CMS69v3/hqmf2.xml"
                        , epPath + "CMS74v4/hqmf2.xml"
                        , epPath + "CMS75v3/hqmf2.xml"
                        , epPath + "CMS77v3/hqmf2.xml"
                        , epPath + "CMS82v2/hqmf2.xml"
                        , epPath + "CMS90v4/hqmf2.xml"
                		
                        // the following measures are not supported in this version, for the reason(s) indicated
                        //, epPath + "CMS128v3/hqmf2.xml" // criteria with field values not implemented yet: [CUMULATIVE_MEDICATION_DURATION]
                        //, epPath + "CMS136v4/hqmf2.xml" // criteria with field values not implemented yet: [CUMULATIVE_MEDICATION_DURATION]
                        //, epPath + "CMS156v3/hqmf2.xml" // criteria with field values not implemented yet: [CUMULATIVE_MEDICATION_DURATION]
                        //, epPath + "CMS179v3/hqmf2.xml" // criteria with field values not implemented yet: [CUMULATIVE_MEDICATION_DURATION].  Also, continuous variable measure
                        //, epPath + "CMS61v4/hqmf2.xml" // COUNT=0 not supported yet
                        //, epPath + "CMS64v4/hqmf2.xml" // COUNT=0 not supported yet
        				//, epPath + "CMS117v3/hqmf2.xml" // Out of Memory error (probably due to >50 occurrences)
                );

                resources.stream().forEach(resource -> {

                    //
                    // optional feature - Specify What Measures to Use Per Practice
                    if (filterMeasures) {
                        Boolean includedMeasure = Boolean.FALSE;
                        for (PracticeAvailableMeasure practiceAvailableMeasure : practiceAvailableMeasures) {
                            if (practiceAvailableMeasure.getCmsId() != null
                                    && resource.toString().toUpperCase().contains(practiceAvailableMeasure.getCmsId().toUpperCase())) {
                                includedMeasure = Boolean.TRUE;
                                break;
                            }
                        }
                        if (! includedMeasure) {
                            return;
                        }
                    }

//                	if (resource.toString().contains("CMS117v3")) {
//                		return;
//                	}

                    hqmfParseExecutor.execute(() -> {
                        String taskId = null;
                        HQMFDocument doc = null;
                        try {
                        	String[] filePathParts = resource.getURL().getPath().split("/");
                        	String fileTaskName = filePathParts[filePathParts.length-2] + "/" + filePathParts[filePathParts.length-1]; 
                        	
                            // reuse parse tree if HQMF doc has already been parsed once since last JVM restart
                            doc = hqmfDocCache.get(resource.getURL().toString());
                            if (doc == null) {
	                            taskId = "parsing " + fileTaskName;
	                            stopwatch.start(taskId);
	
	                            doc = parseHQMF(resource);
	                            hqmfDocCache.put(resource.getURL().toString(), doc);
	
	                            long elapsed = stopwatch.stop(taskId);
	                            System.out.println(taskId + " took " + ((double) elapsed / 1000) + " seconds");
                            }
                            else {
                            	// reset the id so a new doc record would be created
                            	doc.setId(null);
                            }

//                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                            System.out.println(gson.toJson(doc));

                            // reset the reporting period
                            FastDateFormat formatter = FastDateFormat.getInstance("yyyyMMdd");
                            doc.getMeasurePeriod().getLow().setValue(formatter.format(reportingPeriodStart) + "0000");
                            doc.getMeasurePeriod().getHigh().setValue(formatter.format(reportingPeriodEnd) + "2359");

                            HqmfDocument docJPA = hqmfTransformer.transformAndSave(doc, providerId, userId, bundleId);

                            doc.setId(docJPA.getId().toString());
                            
                            triggerCalculation(calculationExecutor, userId, bundleId, stopwatch, fileTaskName, doc);

                        } catch (Exception e) {
                        	logger.error(taskId + "**FAILED thread with exception", e);
                            if (taskId != null) {
                                stopwatch.stop(taskId, e.getMessage() + ": " + e);
                            }
                            
                            // eject the SQL from the cache in case SQL was not fully generated when exception occurred
                            if (doc != null && doc.getCmsId() != null) {
                            	HQMFGenerationHelper.removeFromSqlCache(doc.getCmsId());
                            }
                            
                            throw new RuntimeException(e);
                        }
                    });
                });

                try {
                	hqmfParseExecutor.shutdown();
                    hqmfParseExecutor.awaitTermination(24, TimeUnit.HOURS);
                    
                    calculationExecutor.shutdown();
                	calculationExecutor.awaitTermination(24, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("\n" + stopwatch.outputByElapsedTime());
                System.out.println(stopwatch.outputByTaskName());

                System.out.println(h2PopulateUtil.outputByElapsedTime());
                System.out.println(h2PopulateUtil.outputByTaskName());
                
                SystemUtil.outputMemoryInfo();

                // reset in-memory database to release memory resources
                h2PopulateUtil.dropAllObjects();
                
                SystemUtil.outputMemoryInfo();

                // Send user email regarding process completion
                notifyUserByEmail(userId);

            } catch (Exception e) {

                //
                // Notify the IT support team for HQMF errors
                notifySupportTeamErrorEmail(e);

                logger.error(null, e);
            }
        }
        logger.info("HQMF web process completed.");
    }

	private void triggerCalculation(ExecutorService calculationExecutor, Integer userId, final Integer bundleId, StopWatch stopwatch, String fileTaskName, final HQMFDocument doc) {
		
		// trigger the calculations to be performed single-threaded
		calculationExecutor.execute(() -> {
			
			String taskId = "generating " + fileTaskName;
			stopwatch.start(taskId);
			
			try {
	
			    // Generate SQL from HQMF document
			    Map<String, PopulationGenerationContext> genCtxs = generateSQL(doc, userId, bundleId);
	
			    long elapsed = stopwatch.stop(taskId, "success");
			    System.out.println(taskId + " took " + ((double) elapsed / 1000) + " seconds");
	
//	                            appCxt.getBean(VerifyMeasureCalculations.class).verify(doc, genCtxs);
			    
			} catch (Exception e) {
				logger.error(taskId + "**FAILED thread with exception", e);
			    stopwatch.stop(taskId, e.getMessage() + ": " + e);
			    
			    // eject the SQL from the cache in case SQL was not fully generated when exception occurred
			    if (doc.getCmsId() != null) {
			    	HQMFGenerationHelper.removeFromSqlCache(doc.getCmsId());
			    }
			    
			    throw new RuntimeException(e);
			}
		});
	}

    private Set<Resource> getResources(String... paths) throws IOException {
        Set<Resource> resources = new LinkedHashSet<Resource>();
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

        for (String path : paths) {
            resources.addAll(Arrays.asList(resolver.getResources(path)));
        }

        return resources;
    }

    private void notifyUserByEmail(Integer userId) {
        if (userId != null && userId > 0) {

            ApplicationUser applicationUser = userSecurityDao.findUserById(userId);
            if (applicationUser == null || applicationUser.getEmailAddress() == null) {
                logger.error(String.format("Error in sending HQMF email. User ID %s not found.", userId));
                return;
            }

            List<String> to = new ArrayList<>();
            to.add(applicationUser.getEmailAddress());
            try {
                CaliphrMailer caliphrMailer = appCxt.getBean(CaliphrMailer.class);
                caliphrMailer.setTo(to);
                caliphrMailer.setSubject("CAliPHR - Measure Calculation Complete");
                caliphrMailer.setHtmlHeading("CAliPHR - Measure Calculation Complete");
                caliphrMailer.setTextContent("The calculation process has been completed and your measures are ready for viewing.");
                caliphrMailer.setHtmlContent("The calculation process has been completed and your measures are ready for viewing.");
                caliphrMailer.generateAndSendEmail();
            } catch (MessagingException e) {
                logger.error(String.format("Error in sending HQMF email to user %s", userId), e);
            }
        }
    }

    private void notifySupportTeamErrorEmail(Exception ex) {
        List<String> to = new ArrayList<>();
        CaliphrMailer caliphrMailer = appCxt.getBean(CaliphrMailer.class);
        to.add(caliphrMailer.getDevSupportEmailAddress());
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(ExceptionUtils.getStackTrace(ex));
        try {
            caliphrMailer.setTo(to);
            caliphrMailer.setSubject("Caliphr - HQMF Service Errors");
            caliphrMailer.setTextContent(errorMessage.toString());
            caliphrMailer.setHtmlContent(errorMessage.toString());
            caliphrMailer.generateAndSendEmail();
        } catch (MessagingException e) {
            logger.error(String.format("Error in sending out the HQMF process error email."), e);
        }
    }

}
