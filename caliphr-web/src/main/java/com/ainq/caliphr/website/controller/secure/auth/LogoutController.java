package com.ainq.caliphr.website.controller.secure.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by mmelusky on 9/10/2015.
 */
@Controller
public class LogoutController {
    @RequestMapping(method = RequestMethod.GET, value = "/web/auth/logout/success")
    public String logoutSuccess()
    {
        return "auth/logout-success";
    }
}
