package com.ainq.caliphr.website.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AboutController {

	@RequestMapping(method = RequestMethod.GET, value = "/web/site/about")
	public String about() {
		return "about";
	}

}