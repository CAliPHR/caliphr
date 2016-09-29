package com.ainq.caliphr.website.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by mmelusky on 9/18/2015.
 */
@Controller
public class SiteController {

    @RequestMapping(method = RequestMethod.GET, value = "/web/site/getting-started")
    public String gettingStarted() {
        return "site/getting-started";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/site/about-us")
    public String aboutUs() {
        return "site/about-us";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/site/help")
    public String help() {
        return "site/help";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/site/faq")
    public String faq() {
        return "site/faq";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/web/site/account")
    public String account() {
        return "site/account";
    }
}
