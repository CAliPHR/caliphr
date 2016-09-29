package com.ainq.caliphr.website.controller.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.service.qrda.QrdaService;
import com.ainq.caliphr.website.utility.SecurityHelper;

/**
 * Created by mmelusky on 1/5/2016.
 */
@Controller
public class CdaImportController {

    private static final String VIEW_NAME = "util/cda-import";

    @Autowired
    private QrdaService qrdaService;

    @RequestMapping(path = "/util/cda-import", method = RequestMethod.GET)
    public String viewCdaImport(Model model) {
        model.addAttribute("message", null);
        return VIEW_NAME;
    }

    @RequestMapping(path = "/util/cda-import", method = RequestMethod.POST)
    public String processQrdaCat3Import(Model model, @RequestParam("file") MultipartFile file) {

        String message = null;
        Integer userId = null;
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        }
        if (!file.isEmpty()) {
            try {
                qrdaService.processQrdaCat1Import(file.getOriginalFilename(), file.getBytes(), userId);
            }catch (Exception e) {
                message = "ERRORS: " + e.getMessage();
            }
        } else {
            message = "File cannot be empty.";
        }

        if (message == null) {
            message = "Upload successfully processed.";
        }

        model.addAttribute("message", message);
        return VIEW_NAME;
    }


}
