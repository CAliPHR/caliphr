package com.ainq.caliphr.hqmf.ws.repository.impl;

import ch.qos.logback.classic.Logger;

import com.ainq.caliphr.hqmf.schema.service.ObjectFactory;
import com.ainq.caliphr.hqmf.schema.service.ProvideAndRegisterDocumentSetRequestType;
import com.ainq.caliphr.hqmf.schema.service.RegistryError;
import com.ainq.caliphr.hqmf.schema.service.RegistryResponseType;
import com.ainq.caliphr.hqmf.ws.WebServiceConstants;
import com.ainq.caliphr.hqmf.ws.repository.XdsbRepository;
import com.ainq.caliphr.hqmf.ws.util.ClinicalDocumentEncodeDecode;
import com.ainq.caliphr.persistence.concurrent.ClinicalDocumentExecutor;
import com.ainq.caliphr.persistence.service.ClinicalDocumentService;
import com.ainq.caliphr.persistence.transformation.ClinicalDocumentProcessTask;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.mutable.MutableInt;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmelusky on 8/18/2015.
 */
@Repository
public class XdsbRepositoryImpl implements XdsbRepository {

    // Logger
    static Logger logger = (Logger) LoggerFactory.getLogger(XdsbRepositoryImpl.class);

    @Autowired
    private ClinicalDocumentEncodeDecode clinicalDocumentEncodeDecode;

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Autowired
    private ClinicalDocumentExecutor clinicalDocumentExecutor;

    /*
        Implemented Methods
     */

    @Override
    public RegistryResponseType provideAndRegisterDocumentSetRequest(ProvideAndRegisterDocumentSetRequestType requestType, MessageContext context) {

        // Create an outbound object
        ObjectFactory objectFactory = new ObjectFactory();
        RegistryResponseType responseType = objectFactory.createRegistryResponseType();
        responseType.setStatus(WebServiceConstants.XdsbResponseType.SUCCESS);   // Change this below if errors

        // Create a JAXB context
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
        }
        catch (Exception ex) {
            addRegistryError(responseType, "Unable to obtain JAXB context.  Please try request again later."
                    , WebServiceConstants.XdsbRegistryErrorCode.INTERNAL_ERROR);
            return responseType;
        }

        // Create JAXB unmarshaller
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = jc.createUnmarshaller();
        }
        catch (Exception ex) {
            addRegistryError(responseType, "Unable to obtain JAXB unmarshaller.  Please try request again later."
                    , WebServiceConstants.XdsbRegistryErrorCode.INTERNAL_ERROR);
            return responseType;
        }

        // Search SOAP message for attachments.
        List<Attachment> attachmentList = new ArrayList<>();
        if (context != null && context.getRequest() != null
                && context.getRequest() instanceof SaajSoapMessage) {
            SaajSoapMessage saajSoapMessage = (SaajSoapMessage) context.getRequest();
            if (saajSoapMessage.getAttachments() != null) {
                saajSoapMessage.getAttachments().forEachRemaining(attachmentList::add);
            }
        }

        // Determine how to parse the clinical document
        MutableInt filesProcessed = new MutableInt(0);
        if (attachmentList.size() > 0) {

            //
            //  Find instances of clinical document from MTOM attachment
            for (Attachment attachment : attachmentList) {
                byte[] attachmentInputStream = null;
                try {
                    attachmentInputStream = ByteStreams.toByteArray(attachment.getInputStream());
                } catch (IOException e) {
                    // Ignore exception.  Empty attachment was found.
                }
                if (attachmentInputStream != null) {
                    unmarshallAndProcessClinicalDocument(responseType, attachmentInputStream, unmarshaller, filesProcessed);
                }
            }

        } else if (requestType.getDocument() != null) {

            //
            //  Find instances of clinical document from SOAP body
            for (ProvideAndRegisterDocumentSetRequestType.Document document : requestType.getDocument()) {
                if (document.getValue() != null) {
                    unmarshallAndProcessClinicalDocument(responseType, document.getValue(), unmarshaller, filesProcessed);
                }
            }
        }

        if (filesProcessed != null && filesProcessed.intValue() == 0) {
            addRegistryError(responseType, "No documents found in the request (<Document> element or MIME attachments) were processed."
                    , WebServiceConstants.XdsbRegistryErrorCode.MISSING_DOCUMENT);
        }

        return responseType;
    }

    /*
        Private Methods
     */

    private void unmarshallAndProcessClinicalDocument(RegistryResponseType responseType, byte[] bytes, Unmarshaller unmarshaller, MutableInt filesProcessed) {

        // Attempt to unmarshall byte array
        JAXBElement<?> root = null;
        String documentXml = null;
        try {
            documentXml = clinicalDocumentEncodeDecode.toUtf8String(bytes);
            root = (JAXBElement<?>) unmarshaller.unmarshal(new StringReader(documentXml));
        } catch (Exception ex) {
            // Invalid byte array sent in attachment or message body.  Ignore exception.
        }

        // Attempt to save clinical document
        if (root != null && root.getValue() != null && root.getValue() instanceof POCDMT000040ClinicalDocument) {
            try {
                ClinicalDocumentProcessTask task = new ClinicalDocumentProcessTask();
                task.setLogToFileSystem(Boolean.TRUE);
                task.setClinicalDocumentService(clinicalDocumentService);
                task.setDocumentName("XDS.b ENDPOINT DOCUMENT");            // TODO put something in here!!!!
                task.setXml(documentXml);
                clinicalDocumentExecutor.processClinicalDocument(task);
                filesProcessed.increment();
            }
            catch (Exception ex) {
                addRegistryError(responseType
                        , String.format("Internal error processing clinical document [%s]", ex.getMessage())
                        , WebServiceConstants.XdsbRegistryErrorCode.INTERNAL_ERROR);
            }
        }
    }

    private void addRegistryError(RegistryResponseType responseType, String errorMessage, String errorCode) {
        ObjectFactory objectFactory = new ObjectFactory();
        if (responseType.getRegistryErrorList() == null) {
            responseType.setRegistryErrorList(objectFactory.createRegistryErrorList());
        }
        RegistryError registryError = objectFactory.createRegistryError();
        registryError.setErrorCode(errorCode);
        registryError.setValue(errorMessage);
        registryError.setSeverity(WebServiceConstants.XdsbErrorSeverityType.ERROR);
        responseType.getRegistryErrorList().getRegistryError().add(registryError);
        responseType.setStatus(WebServiceConstants.XdsbResponseType.FAILURE);
    }

}
