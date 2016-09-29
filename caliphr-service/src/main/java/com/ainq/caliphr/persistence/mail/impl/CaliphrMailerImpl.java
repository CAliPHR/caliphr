package com.ainq.caliphr.persistence.mail.impl;

import static com.ainq.caliphr.persistence.config.Constants.PropertyKey.DEV_SUPPORT_ERROR_EMAIL;
import static com.ainq.caliphr.persistence.config.Constants.PropertyKey.EMAIL_COMPANY_DESCRIPTION;
import static com.ainq.caliphr.persistence.config.Constants.PropertyKey.EMAIL_COMPANY_NAME;
import static com.ainq.caliphr.persistence.config.Constants.PropertyKey.EMAIL_COPYRIGHT_TXT;
import static com.ainq.caliphr.persistence.config.Constants.PropertyKey.EMAIL_LOGO_IMG;
import static com.ainq.caliphr.persistence.config.Constants.PropertyKey.EMAIL_SUBJECT_PREFIX;
import static com.ainq.caliphr.persistence.config.Constants.SmtpProperty.SMTP_FROM;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.ainq.caliphr.persistence.mail.CaliphrMailer;

import ch.qos.logback.classic.Logger;

/**
 * Created by mmelusky on 9/1/2015.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CaliphrMailerImpl implements CaliphrMailer {

    @Autowired
    private Environment environment;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    // Instance Data
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String textContent;
    private String htmlHeading;
    private String htmlContent;

    // Static Members
    static Logger logger = (Logger) LoggerFactory.getLogger(CaliphrMailerImpl.class);

    // Constants
    private static final String TEMPLATE_NAME = "email.html";
    private static final String HTML_HEADING_CONTEXT = "htmlHeading";
    private static final String HTML_CONTENT_CONTEXT = "htmlContent";
    private static final String IMAGE_CONTEXT = "imageResourceName";
    private static final String IMAGE_NAME = "Caliphr_logo_transparent.png";

    @Override
    public void setTo(List<String> to) {
        this.to = to;
    }

    @Override
    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    @Override
    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public void setTextContent(String content) {
        this.textContent = content;
    }

    @Override
    public void setHtmlHeading(String content) {
        this.htmlHeading = content;
    }

    @Override
    public void setHtmlContent(String content) {
        this.htmlContent = content;
    }

    @Override
    public void generateAndSendEmail() throws MessagingException, IllegalStateException {
        // Validate
        if (to == null || to.size() == 0) {
            throw new IllegalStateException("<To> field is required with email message");
        }
        if (subject == null || subject.length() == 0) {
            throw new IllegalStateException("Subject is required with email message");
        }
        if ((textContent == null || textContent.length() == 0) && (htmlContent == null || htmlContent.length() == 0)) {
            throw new IllegalStateException("Message body is required with email message");
        }

        // Subject prepending
        String subjectPrefix = environment.getProperty(EMAIL_SUBJECT_PREFIX);
        if (subjectPrefix != null && subjectPrefix.length() > 0) {
            subject = String.format("(%s) %s", subjectPrefix, subject);
        }

        // Format HTML string.
        final Context ctx = new Context(LocaleContextHolder.getLocale());
        if (htmlHeading != null) {
            ctx.setVariable(HTML_HEADING_CONTEXT, htmlHeading);
        }
        ctx.setVariable(HTML_CONTENT_CONTEXT, htmlContent);
        ctx.setVariable(IMAGE_CONTEXT, IMAGE_NAME);
        ctx.setVariable(EMAIL_COMPANY_NAME, environment.getProperty(EMAIL_COMPANY_NAME));
        ctx.setVariable(EMAIL_COMPANY_DESCRIPTION, environment.getProperty(EMAIL_COMPANY_DESCRIPTION));
        ctx.setVariable(EMAIL_COPYRIGHT_TXT, environment.getProperty(EMAIL_COPYRIGHT_TXT));

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(subject);
        message.setFrom(environment.getProperty(SMTP_FROM));
        message.setTo(to.toArray(new String[to.size()]));
        if (to != null) {
            message.setTo(to.toArray(new String[to.size()]));
        }
        if (cc != null) {
            message.setCc(cc.toArray(new String[cc.size()]));
        }
        if (bcc != null) {
            message.setBcc(bcc.toArray(new String[bcc.size()]));
        }
        message.setText(textContent, this.templateEngine.process(TEMPLATE_NAME, ctx));

        // Add image of logo to message
        String logoImg = environment.getProperty(EMAIL_LOGO_IMG);
        if (logoImg != null) {
        	final Resource imageResource = new ClassPathResource(logoImg);
        	message.addInline(IMAGE_NAME, imageResource);
        }

        this.mailSender.send(mimeMessage);
    }

    @Override
    public String getDevSupportEmailAddress() {
        return environment.getProperty(DEV_SUPPORT_ERROR_EMAIL);
    }
}
