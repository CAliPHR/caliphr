package com.ainq.caliphr.hqmf.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import org.apache.commons.lang3.time.FastDateFormat;
import org.h2.tools.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;
import com.ainq.caliphr.hqmf.service.HQMFService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestHQMFService {
	
	@Autowired
	private HQMFService service;
	
	@Test
	public void testService() throws SQLException, ParseException, IOException {

		// launch a server so the h2 connections would be available to perform queries from external tools or h2 console
		Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
		Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
		
		FastDateFormat formatter = FastDateFormat.getInstance("yyyy-MM-dd");
		service.calculateMeasures(null, formatter.parse("2013-01-01"), formatter.parse("2013-12-31"), null);
		
//		VerifyMeasureCalculations.outputResults();
		
		System.out.println("h2 server is listening, press <enter> to shut down");
		System.in.read();
		
		System.out.println("shutting down");
		server.shutdown();
		webServer.shutdown();
		
	}

	@Test
	public void testCalculateMeasures() throws ParseException {
		FastDateFormat formatter = FastDateFormat.getInstance("yyyy-MM-dd");
		service.calculateMeasures(null, formatter.parse("2013-01-01"), formatter.parse("2013-12-31"), null);
	}
	
}
