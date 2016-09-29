package com.ainq.caliphr.hqmf.ws;

/**
 * Created by mmelusky on 8/18/2015.
 */
public class WebServiceConstants {

    public static final String TARGET_NAMESPACE_URL = "TARGET_NAMESPACE_URL";

    public class XdsbResponseType {
        public static final String SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        public static final String PARTIAL_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:PartialSuccess";
        public static final String FAILURE = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
    }

    public class XdsbErrorSeverityType {
        public static final String WARNING = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Warning";
        public static final String ERROR = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error";
    }

    public class XdsbRegistryErrorCode {
        //
        // More error codes in "ErrorTable.doc" found in "documentation" (root of Caliphr project)
        public static final String MISSING_DOCUMENT = "XDSMissingDocument";
        public static final String INTERNAL_ERROR = "XDSRegistryError";
    }

}
