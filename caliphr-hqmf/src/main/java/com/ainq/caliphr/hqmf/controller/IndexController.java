package com.ainq.caliphr.hqmf.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mmelusky on 7/13/2015.
 */
@RestController
public class IndexController {

    @RequestMapping(method = RequestMethod.GET, value= "/")
    public String index() {
        return "HQMF application landing page.";
    }

}
