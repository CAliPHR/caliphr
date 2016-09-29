package com.ainq.caliphr.website.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.ainq.caliphr.website.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private Environment environment;

    public LoginSuccessHandler() {
    }

    public LoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {

        // configure session timeout here
        Integer sessionTimeout = Integer.parseInt(environment.getProperty(Constants.PropertyKey.SESSION_TIMEOUT_SECONDS));
        request.getSession().setMaxInactiveInterval(sessionTimeout);

        super.onAuthenticationSuccess(request, response, authentication);
    }

}