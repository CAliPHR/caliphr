package com.ainq.caliphr.hqmf.controller.api.sandbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.hqmf.service.ValueSetBundleImportProcess;

import javax.xml.bind.JAXBException;
import java.net.URISyntaxException;

@RestController
public class ValueSetRestController {

    @Autowired
    private ValueSetBundleImportProcess valueSetBundleImportProcess;

    @RequestMapping(value = "/api/value-sets", method = RequestMethod.POST)
    public JsonResponse processValueSets() throws JAXBException, URISyntaxException {
        valueSetBundleImportProcess.importValueSets();
        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.OK);
        response.setMessage("Process triggered.");
        return response;
    }
}
