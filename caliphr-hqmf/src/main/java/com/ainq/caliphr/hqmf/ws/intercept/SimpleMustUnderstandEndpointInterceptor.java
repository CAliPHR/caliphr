package com.ainq.caliphr.hqmf.ws.intercept;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;

/*
 * 	The purpose of this class is to intercept the SOAP headers and accept any action.  If Intel passes attributes such as "mustUnderstand",
 * 	Spring croaks.  --melusky
 */
public class SimpleMustUnderstandEndpointInterceptor implements
		SoapEndpointInterceptor {
	private final String SAMPLE_NS = "http://www.w3.org/2005/08/addressing";

	@Override
	public boolean understands(SoapHeaderElement header) {
		if (header.getName().getNamespaceURI().equals(SAMPLE_NS)
				&& (header.getName().getLocalPart().equals("Action") || header.getName().getLocalPart().equals("To"))) {
			return true;
		}

		return false;
	}

	@Override
	public boolean handleRequest(MessageContext messageContext, Object endpoint)
			throws Exception {
		return true;
	}

	@Override
	public boolean handleResponse(MessageContext messageContext, Object endpoint)
			throws Exception {
		return true;
	}

	@Override
	public boolean handleFault(MessageContext messageContext, Object endpoint)
			throws Exception {
		return true;
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Object endpoint,
			Exception ex) throws Exception {
	}
}