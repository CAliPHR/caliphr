package com.ainq.caliphr.website;

/**
 * Created by mmelusky on 8/21/2015.
 */
public class Constants {

    /*
        Property file keys
     */
    public class PropertyKey {
        public static final String API_ROOT = "API.ROOT";
        public static final String PASSWORD_EXPIRATION_MONTHS = "PASSWORD.EXPIRATION.MONTHS";
        public static final String PASSWORD_LENGTH_MIN = "PASSWORD.LENGTH.MIN";
        public static final String PASSWORD_LENGTH_MAX = "PASSWORD.LENGTH.MAX";
        public static final String SESSION_TIMEOUT_SECONDS = "SESSION.TIMEOUT.SECONDS";
    }

    /*
        API Url
     */
    public class ApiUri {

        // HQMF
        public static final String PROCESS_HQMF = "/api/measures/calculate";

        // PROVIDER
        public static final String ORGANIZATIONS_ALL = "/api/provider/organizations/all";
        public static final String PRACTICES_ALL = "/api/provider/practices/all";
        public static final String PROVIDERS_ALL = "/api/provider/providers/all";

        // MEASURE
        public static final String PATIENT_RESULT_INFO = "/api/patient_results";
        public static final String MEASURE_ATTRIBUTES_FOR_DOCUMENT = "/api/measure_attributes";
        public static final String MEASURES_ACTIVE_FOR_PROVIDER = "/api/all_active_measures";

        // QRDA EXPORT
        public static final String QRDA_CAT1_EXPORT = "/api/qrda_cat1/export";
        public static final String QRDA_CAT3_EXPORT = "/api/qrda_cat3/export";
        public static final String QRDA_CAT1_IMPORT = "/api/qrda_cat1/import";

        // SECURITY
        public static final String SECURITY_USER_LOGIN = "/api/security/login";
        public static final String SECURITY_USER_PASSWORD_RESET_REQUEST = "/api/security/password/reset/request";
        public static final String SECURITY_USER_PASSWORD_RESET_TOKEN = "/api/security/password/reset/token";
        public static final String SECURITY_USER_PASSWORD_RESET_SUBMIT = "/api/security/password/reset/submit";
        public static final String SECURITY_USER_PASSWORD_HISTORY_CHECK = "/api/security/password/history/check";
    }

}
