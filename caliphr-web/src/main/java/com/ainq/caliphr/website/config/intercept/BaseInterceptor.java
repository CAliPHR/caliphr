package com.ainq.caliphr.website.config.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.utility.SecurityHelper;

public class BaseInterceptor extends HandlerInterceptorAdapter {
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		/*
			Set properties regarding the controller
		 */
		if (handler != null && handler instanceof HandlerMethod) {
			try {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				modelAndView.addObject("controllerName", handlerMethod.getBean()
						.getClass().getSimpleName());
				modelAndView.addObject("controllerAction", handlerMethod
						.getMethod().getName());
			} catch (Exception ex) {
				// ignore
			}
		}

		/*
			Get the logged in user (spring security thymeleaf extensions only returns the Spring Principal object)
		 */
		try {
			if (SecurityHelper.isLoggedIn()) {
				SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
				StringBuilder userFullName = new StringBuilder();
				if (securityUser.getFirstName() != null) {
					userFullName.append(securityUser.getFirstName());
				}
				if (securityUser.getLastName() != null) {
					userFullName.append(" ");
					userFullName.append(securityUser.getLastName());
				}
				modelAndView.addObject("userFullName", userFullName.toString());
			}
		} catch (Exception ex) {
			// Ignore
		}

	}
}