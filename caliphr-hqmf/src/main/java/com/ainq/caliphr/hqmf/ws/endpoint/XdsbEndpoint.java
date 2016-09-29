package com.ainq.caliphr.hqmf.ws.endpoint;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.ainq.caliphr.hqmf.schema.service.*;
import com.ainq.caliphr.hqmf.ws.repository.XdsbRepository;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Endpoint
public class XdsbEndpoint {

    // Logger
    static Logger logger = (Logger) LoggerFactory.getLogger(XdsbEndpoint.class);

    // Namespace
    private static final String XDSB_NAMESPACE_URI = "urn:ihe:iti:xds-b:2007";

    // Response Root
    private static final String XDSB_RESPONSE_ROOT = "RegistryResponse";

    // Request Roots
    private static final String XDSB_ACCOBJ_REQUEST_ROOT = "AcceptObjectsRequest";
    private static final String XDSB_ADHOC_REQUEST_ROOT = "AdhocQueryRequest";
    private static final String XDSB_APPOBJ_REQUEST_ROOT = "ApproveObjectsRequest";
    private static final String XDSB_PARDSR_REQUEST_ROOT = "ProvideAndRegisterDocumentSetRequest";
    private static final String XDSB_DEPOBJ_REQUEST_ROOT = "DeprecateObjectsRequest";
    private static final String XDSB_REG_REQUEST_ROOT = "RegistryRequest";
    private static final String XDSB_RELOBJ_REQUEST_ROOT = "RelocateObjectsRequest";
    private static final String XDSB_REMOBJ_REQUEST_ROOT = "RemoveObjectsRequest";
    private static final String XDSB_RETOBJ_REQUEST_ROOT = "RetrieveDocumentSetRequest";
    private static final String XDSB_SUBOBJ_REQUEST_ROOT = "SubmitObjectsRequest";
    private static final String XDSB_UNDOBJ_REQUEST_ROOT = "UndeprecateObjectsRequest";
    private static final String XDSB_UPDOBJ_REQUEST_ROOT = "UpdateObjectsRequest";

    // Instance Data

    @Autowired
    private XdsbRepository xdsbRepository;

    /*
     *  Payload Methods
	 */

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_ACCOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> acceptObjectsRequest(
            @RequestPayload JAXBElement<AcceptObjectsRequest> request) {
        throw new NotImplementedException("<AcceptObjectsRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_ADHOC_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> adhocQueryRequest(
            @RequestPayload JAXBElement<AdhocQueryRequest> request) {
        throw new NotImplementedException("<AdhocQueryRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_APPOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> approveObjectsRequest(
            @RequestPayload JAXBElement<ApproveObjectsRequest> request) {
        throw new NotImplementedException("<ApproveObjectsRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_DEPOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> deprecateObjectsRequest(
            @RequestPayload JAXBElement<DeprecateObjectsRequest> request) {
        throw new NotImplementedException("<DeprecateObjectsRequest> not implemented at this XDS.b endpoint!");
    }


    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_PARDSR_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> provideAndRegisterDocumentSetRequest(
            @RequestPayload JAXBElement<ProvideAndRegisterDocumentSetRequestType> request, MessageContext context) {

        logger.debug("Entering XDS.b -> <ProvideAndRegisterDocumentSetRequest> request");
        RegistryResponseType response = xdsbRepository.provideAndRegisterDocumentSetRequest(request.getValue(), context);
        QName qname = new QName(XDSB_NAMESPACE_URI, XDSB_RESPONSE_ROOT);
        JAXBElement<RegistryResponseType> jaxbResponse = new JAXBElement<RegistryResponseType>(qname, RegistryResponseType.class, response);
        logger.debug("Exiting XDS.b -> <ProvideAndRegisterDocumentSetRequest> request");
        return jaxbResponse;

    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_REG_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> registryRequest(
            @RequestPayload JAXBElement<RegistryRequestType> request) {
        throw new NotImplementedException("<RegistryRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_RELOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> relocateObjectsRequest(
            @RequestPayload JAXBElement<RelocateObjectsRequest> request) {
        throw new NotImplementedException("<RelocateObjectsRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_REMOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> removeObjectsRequest(
            @RequestPayload JAXBElement<RemoveObjectsRequest> request) {
        throw new NotImplementedException("<RemoveObjectsRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_RETOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> retrieveDocumentSetRequest(
            @RequestPayload JAXBElement<RetrieveDocumentSetRequestType> request) {
        throw new NotImplementedException("<RetrieveDocumentSetRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_SUBOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> submitObjectsRequest(
            @RequestPayload JAXBElement<SubmitObjectsRequest> request) {
        throw new NotImplementedException("<SubmitObjectsRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_UNDOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> undeprecateObjectsRequest(
            @RequestPayload JAXBElement<UndeprecateObjectsRequest> request) {
        throw new NotImplementedException("<UndeprecateObjectsRequest> not implemented at this XDS.b endpoint!");
    }

    @PayloadRoot(namespace = XDSB_NAMESPACE_URI, localPart = XDSB_UPDOBJ_REQUEST_ROOT)
    @ResponsePayload
    public JAXBElement<RegistryResponseType> updateObjectsRequest(
            @RequestPayload JAXBElement<UpdateObjectsRequest> request) {
        throw new NotImplementedException("<UpdateObjectsRequest> not implemented at this XDS.b endpoint!");
    }

}
