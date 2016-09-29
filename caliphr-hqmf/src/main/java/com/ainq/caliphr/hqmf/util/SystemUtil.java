package com.ainq.caliphr.hqmf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtil {
	
	static Logger logger = LoggerFactory.getLogger(SystemUtil.class);
	private static final int mb = 1024 * 1024; 

	public static void outputMemoryInfo() {
 
		if (logger.isDebugEnabled()) {
			// get Runtime instance
			Runtime instance = Runtime.getRuntime();
			
			logger.debug("** Heap utilization statistics [MB] - Used: {}, Total: {}, Free: {}, Max: {}", 
					(instance.totalMemory() - instance.freeMemory()) / mb,
					instance.totalMemory() / mb,
					instance.freeMemory() / mb,
					instance.maxMemory() / mb);
		}
	}

}
