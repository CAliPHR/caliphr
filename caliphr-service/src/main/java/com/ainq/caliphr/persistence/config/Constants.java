package com.ainq.caliphr.persistence.config;

/**
 * Created by mmelusky on 8/24/2015.
 */
public class Constants {

    public static final Integer PASSWORD_TOKEN_EXPIRATION_DAYS = 1;

    public class ApplicationUser {
        public final static int ADMINISTRATIVE_USER_ID = 1;
    }

    public class PracticeGroup {
        public final static int UNKNOWN_SENDING_GROUP_ID = 1;
    }

    public class PropertyKey {
        public final static String ALLOW_PROVIDER_WITH_EMPTY_NPI = "ALLOW.PROVIDER.WITH.EMPTY.NPI";
        public final static String ALLOW_PATIENT_WITH_UNKNOWN_SOURCE = "ALLOW.PATIENT.WITH.UNKNOWN.SOURCE";
        public final static String ALLOW_PROVIDER_WITH_UNKNOWN_SOURCE = "ALLOW.PROVIDER.WITH.UNKNOWN.SOURCE";
        public final static String BUNDLE_FILESYSTEM_ROOT = "BUNDLE.FILESYSTEM.ROOT";
        public final static String CALIPHR_PASSWORD_RESET_URL_FORMAT_STRING = "CALIPHR.PASSWORD.RESET.URL.FORMAT.STRING";
        public final static String CLINICAL_DOCUMENT_BACKUP_ROOT = "CLINICAL.DOCUMENT.BACKUP.ROOT";
        public final static String CLINICAL_DOCUMENT_PLAINTEXT_ROOT = "CLINICAL.DOCUMENT.PLAINTEXT.ROOT";
        public final static String DEV_SUPPORT_ERROR_EMAIL = "DEV.SUPPORT.ERROR.EMAIL";
        public final static String EMAIL_SUBJECT_PREFIX = "EMAIL.SUBJECT.PREFIX";
        public final static String EMAIL_LOGO_IMG = "EMAIL.LOGO.IMG";
        public final static String EMAIL_COMPANY_NAME = "EMAIL.COMPANY.NAME";
        public final static String EMAIL_COMPANY_DESCRIPTION = "EMAIL.COMPANY.DESCRIPTION";
        public final static String EMAIL_COPYRIGHT_TXT = "EMAIL.COPYRIGHT.TXT";
        public final static String EXECUTOR_THREAD_POOL_SIZE = "EXECUTOR.THREAD.POOL.SIZE";
    }
    
    public class MainDatasourceProperty {
    	public final static String MAIN_DATASOURCE_JDBC_URL = "MAIN.DATASOURCE.JDBC.URL";
    	public final static String MAIN_DATASOURCE_USERNAME = "MAIN.DATASOURCE.USERNAME";
    	public final static String MAIN_DATASOURCE_ENCRYPTED_PASSWORD = "MAIN.DATASOURCE.ENCRYPTED.PASSWORD";
    	public final static String MAIN_DATASOURCE_PLAINTEXT_PASSWORD = "MAIN.DATASOURCE.PLAINTEXT.PASSWORD";
    }

    public class SmtpProperty {
        public final static String SMTP_FROM = "SMTP.FROM";
        public final static String SMTP_PORT = "SMTP.PORT";
        public final static String SMTP_AUTH = "SMTP.AUTH";
        public final static String SMTP_STARTTLS = "SMTP.STARTTLS";
        public final static String SMTP_PROTOCOL = "SMTP.PROTOCOL";
        public final static String SMTP_DEBUG = "SMTP.DEBUG";
        public final static String SMTP_HOST = "SMTP.HOST";
        public final static String SMTP_USER = "SMTP.USER";
        public final static String SMTP_PASS = "SMTP.PASS";
    }

}
