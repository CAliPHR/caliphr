package com.ainq.caliphr.persistence.transformation.util;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Created by mmelusky on 5/12/2015.
 */
public final class JaxbUtility {

    static final Logger logger = (Logger) LoggerFactory.getLogger(JaxbUtility.class);

    public static String jaxbToString(Object jaxbObject, Class<?> classInstance) {
        try {
            final JAXBContext context = JAXBContext.newInstance(classInstance);

            // Create the marshaller, this is the nifty little thing that will actually transform the object into XML
            final Marshaller marshaller = context.createMarshaller();

            // Create a stringWriter to hold the XML
            final StringWriter stringWriter = new StringWriter();

            // Marshal the javaObject and write the XML to the stringWriter
            marshaller.marshal(jaxbObject, stringWriter);

            return stringWriter.toString();
        } catch (Exception ex) {
            logger.error("JAXB Parse Exception: ", ex);
        }
        return StringUtils.EMPTY;
    }
    
    public static void jaxbToOutputStream(Object jaxbObject, Class<?> classInstance, OutputStream os) {
        try {
            final JAXBContext context = JAXBContext.newInstance(classInstance);

            // Create the marshaller, this is the nifty little thing that will actually transform the object into XML
            final Marshaller marshaller = context.createMarshaller();

            // Marshal the javaObject and write the XML to the stringWriter
            marshaller.marshal(jaxbObject, os);

        } catch (Exception ex) {
            logger.error("JAXB Parse Exception: ", ex);
        }
    }

}
