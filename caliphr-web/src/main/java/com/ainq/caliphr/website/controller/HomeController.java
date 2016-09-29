package com.ainq.caliphr.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ainq.caliphr.website.utility.SecurityHelper;

@Controller
public class HomeController {
@Autowired private Environment env; 	
    @RequestMapping(method = RequestMethod.GET, value = "/test/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return name + " from the server";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String home() {
System.out.println("env="+env);
System.out.println("" + env.getProperty("spring.messages.basename"));
        if (SecurityHelper.isLoggedIn()) {
            return "site/dashboard";
        } else {
            return "home";
        }
    }

}