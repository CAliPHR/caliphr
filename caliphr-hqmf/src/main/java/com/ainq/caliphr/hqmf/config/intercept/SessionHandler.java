package com.ainq.caliphr.hqmf.config.intercept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ainq.caliphr.hqmf.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mmelusky on 9/10/2015.
 */
@Component
public class SessionHandler extends HandlerInterceptorAdapter {

    @Autowired
    private Environment environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer sessionTimeout = Integer.parseInt(environment.getProperty(Constants.PropertyKey.SESSION_TIMEOUT_SECONDS));
        request.getSession().setMaxInactiveInterval(sessionTimeout);
        return true;
    }
}