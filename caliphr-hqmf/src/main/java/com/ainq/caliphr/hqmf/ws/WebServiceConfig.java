package com.ainq.caliphr.hqmf.ws;

import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

import com.ainq.caliphr.hqmf.ws.intercept.SimpleMustUnderstandEndpointInterceptor;

import java.util.List;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new SimpleMustUnderstandEndpointInterceptor());
    }

    @Bean
    public SaajSoapMessageFactory soap12MessageFactory() {
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        factory.setSoapVersion(SoapVersion.SOAP_12);
        return factory;
    }

    @Bean
    public AxiomSoapMessageFactory axiomSoapMessageFactory() {
        AxiomSoapMessageFactory factory = new AxiomSoapMessageFactory();
        factory.setSoapVersion(SoapVersion.SOAP_12);
        factory.setPayloadCaching(false);
        return factory;
    }

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        servlet.setMessageFactoryBeanName("soap12MessageFactory");
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "XDSb")
    public DefaultWsdl11Definition xcpdDefaultXcpdWsdl11Definition(
            XsdSchemaCollection xdsbSchemaCollection) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setCreateSoap11Binding(false);
        wsdl11Definition.setCreateSoap12Binding(true);
        wsdl11Definition.setPortTypeName("XDSb");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition
                .setTargetNamespace(environment.getProperty(WebServiceConstants.TARGET_NAMESPACE_URL));
        wsdl11Definition.setSchemaCollection(xdsbSchemaCollection);
        return wsdl11Definition;
    }

    @Bean
    public Resource[] xdsbSchemaResources() {
        Resource[] schema = {
                new ClassPathResource(
                        "schema/service/XDSb.Support.Materials.v10/schema/HL7V3/NE2008/coreschemas/NarrativeBlock.xsd"),
                new ClassPathResource(
                        "schema/service/XDSb.Support.Materials.v10/schema/HL7V3/NE2008/coreschemas/datatypes-base.xsd"),
                new ClassPathResource(
                        "schema/service/XDSb.Support.Materials.v10/schema/HL7V3/NE2008/coreschemas/infrastructureRoot.xsd"),
                new ClassPathResource(
                        "schema/service/XDSb.Support.Materials.v10/schema/IHE/XDS.b_DocumentRepository.xsd")};
        return schema;
    }

    @Bean
    public XsdSchemaCollection xdsbSchemaCollection() {
        CommonsXsdSchemaCollection collection = new CommonsXsdSchemaCollection();
        collection.setUriResolver(new DefaultURIResolver());
        collection.setInline(true);
        collection.setXsds(xdsbSchemaResources());
        return collection;
    }
}