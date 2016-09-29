package com.ainq.caliphr.hqmf.ws.repository;

import org.springframework.ws.context.MessageContext;

import com.ainq.caliphr.hqmf.schema.service.ProvideAndRegisterDocumentSetRequestType;
import com.ainq.caliphr.hqmf.schema.service.RegistryResponseType;

/**
 * Created by mmelusky on 8/18/2015.
 */
public interface XdsbRepository {

    RegistryResponseType provideAndRegisterDocumentSetRequest(ProvideAndRegisterDocumentSetRequestType requestType, MessageContext context);

}
