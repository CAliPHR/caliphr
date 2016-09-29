package com.ainq.caliphr.persistence.mail;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created by mmelusky on 9/1/2015.
 */
public interface CaliphrMailer {

    void setTo(List<String> to);
    void setCc(List<String> cc);
    void setBcc(List<String> bcc);
    void setSubject(String subject);
    void setTextContent(String content);
    void setHtmlHeading(String content);
    void setHtmlContent(String content);
    void generateAndSendEmail() throws MessagingException;
    String getDevSupportEmailAddress();

}
