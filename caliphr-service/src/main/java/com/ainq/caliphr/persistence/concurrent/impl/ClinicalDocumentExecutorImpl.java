package com.ainq.caliphr.persistence.concurrent.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.concurrent.ClinicalDocumentExecutor;
import com.ainq.caliphr.persistence.config.Constants;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.ClinicalDocument;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ch.qos.logback.classic.Logger;

/**
 * Created by mmelusky on 5/17/2016.
 */
@Component
public class ClinicalDocumentExecutorImpl implements ClinicalDocumentExecutor, SmartLifecycle {

    // Class Members

    // Logger
    private final static Logger logger = (Logger) LoggerFactory.getLogger(ClinicalDocumentExecutorImpl.class);
    
    // Instance Members

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;
       
    @Autowired
    private Environment environment;
    
    // hold a counting semaphore to limit the number of jobs to be placed on the queue
    private Semaphore semaphore;

    // distribute jobs based on the hash code of patient_id, so all CCDs for a given patient would be processed in sequence
    private ExecutorService[] executors;
    
    boolean running = false;
    
    @Override
	public void start() {

        //
        //  Thread Executor.  Run at low priority so web traffic has higher/normal priority
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("cda-executor-thread-%d")
                .setPriority(Thread.MIN_PRIORITY)
                .build();
        
        executors = new ExecutorService[Integer.parseInt(environment.getProperty(Constants.PropertyKey.EXECUTOR_THREAD_POOL_SIZE))];
        for (int i = 0; i < executors.length; i++) {
        	executors[i] = Executors.newSingleThreadExecutor(threadFactory);
        }

        //
        //  Semaphore.  Limit the number of jobs to hold in memory at once to the configured number of threads * 3
        //  once all those slots are taken, block until one of the CCDs finish processing and another slot opens
        semaphore = new Semaphore(executors.length * 3);
        
        running = true;
    }

    @Override
    public void processClinicalDocument(ClinicalDocumentProcessTask task) {
    	ClinicalDocument clinicalDocument = clinicalDocumentService.createClinicalDocument(task);
		if (clinicalDocument != null && clinicalDocument.getPatient() != null) {
	        try {
	        	// if needed, wait for a slot to open up
	            semaphore.acquire();
	            try {
	            	// run the job on the executor determined by the hash code of the patient id
	            	int hash = Math.abs(clinicalDocument.getPatient().getId().hashCode());
	                executors[hash % executors.length].execute(() -> {
	                    try {
	                        task.run();
	                    } finally {
	                        semaphore.release();
	                    }
	                });
	                logger.debug("submitted for processing: " + task.getDocumentName());
	            } catch (RejectedExecutionException e) {
	                semaphore.release();
	                throw e;
	            }
	        } catch (Exception ex) {
	            logger.error("CCDA Executor Process Error ->", ex);
	            throw new IllegalStateException("Clinical Document Process Error -> ", ex);
	        }
    	}
    }    	
    
    
    /**
     * When spring shuts down, this bean should be shut down first to ensure all threads are finished executing 
     * before other beans are destroyed.
     */
	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		for (int i = 0; i < executors.length; i++) {
    		executors[i].shutdown();
    	}
        try {
        	for (int i = 0; i < executors.length; i++) {
        		executors[i].awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        	}
        } catch (InterruptedException ex) {
            logger.error("CCDA Executor Process Error ->", ex);
        }
        logger.debug("executors shut down");
        running = false;
        callback.run();
	}

}
