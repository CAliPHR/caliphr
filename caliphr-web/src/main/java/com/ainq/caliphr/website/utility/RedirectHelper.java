package com.ainq.caliphr.website.utility;

/**
 * Created by mmelusky on 9/8/2015.
 */
public class RedirectHelper {

    // Home
    public static String HOME = "/";

    // Password request
    public static String PASSWORD_REQUEST_RESET = "/web/auth/password/reset";
    public static String PASSWORD_REQUEST_SUCCESS = "/web/auth/password/success";
    public static String PASSWORD_REQUEST_FAILURE = "/web/auth/password/failure";

    // Success or fail if the password reset was good
    public static String PASSWORD_RESET_SUCCESS = "/web/auth/password-reset/success";
    public static String PASSWORD_RESET_FAILURE = "/web/auth/password-reset/failure";

    public static String redirect(String url) {
        return String.format("redirect:%s", url);
    }
}
