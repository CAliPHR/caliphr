package com.ainq.caliphr.persistence.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ainq.caliphr.hqmf.config.ApplicationConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class TestCaliphrMailer {
	
	@Autowired
    private ApplicationContext appCxt;
	
	@Test
    public void testCaliphrMailer() throws MessagingException {
		
		List<String> to = new ArrayList<>();
        to.add("test@test.com");
		
		CaliphrMailer caliphrMailer = appCxt.getBean(CaliphrMailer.class);
        caliphrMailer.setTo(to);
        caliphrMailer.setSubject("CAliPHR - Test Subject");
        caliphrMailer.setHtmlHeading("CAliPHR - Test Heading");
        caliphrMailer.setTextContent("This is test message content.");
        caliphrMailer.setHtmlContent("This is test message content.");
        caliphrMailer.generateAndSendEmail();
		
	}

}
