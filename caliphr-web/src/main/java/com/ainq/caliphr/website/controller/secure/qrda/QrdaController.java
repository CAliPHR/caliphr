package com.ainq.caliphr.website.controller.secure.qrda;

import com.ainq.caliphr.website.model.security.SecurityUser;
import com.ainq.caliphr.website.service.qrda.QrdaService;
import com.ainq.caliphr.website.utility.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmelusky on 9/7/2015.
 */
@Controller
public class QrdaController {

    @Autowired
    private QrdaService qrdaService;

    private Integer userId;

    @RequestMapping(value = "/extract/qrda_cat1/export", method = RequestMethod.POST)
    public HttpEntity<byte[]> processQrdaCat1Export(@RequestParam(value="hqmfId") String[] hqmfArray) {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        List<Long> hqmfIds = new ArrayList<>();
        for(String s : hqmfArray) {
            hqmfIds.add(Long.valueOf(s));
        }
        byte[] documentBody = qrdaService.processQrdaCat1Export(hqmfIds, userId);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "zip"));
        header.set("Content-Disposition",
                "attachment; filename=" + "qrdaCat1Export.zip");
        header.setContentLength((documentBody == null) ? 0 : documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);
    }

    @RequestMapping(value = "/extract/qrda_cat3/export", method = RequestMethod.POST)
    public HttpEntity<byte[]> processQrdaCat3Export(@RequestParam(value="hqmfId") String[] hqmfArray) {
        if (SecurityHelper.isLoggedIn()) {
            SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
            userId = securityUser.getId();
        } else {
            userId = null;
        }
        List<Long> hqmfIds = new ArrayList<>();
        for(String s : hqmfArray) {
            hqmfIds.add(Long.valueOf(s));
        }
        byte[] documentBody = qrdaService.processQrdaCat3Export(hqmfIds, userId).getBytes(StandardCharsets.UTF_8);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "xml"));
        header.set("Content-Disposition",
                "attachment; filename=" + "qrdaCat3Export.xml");
        header.setContentLength((documentBody == null) ? 0 : documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);
    }

}




