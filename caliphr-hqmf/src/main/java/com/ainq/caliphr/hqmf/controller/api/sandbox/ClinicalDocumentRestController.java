package com.ainq.caliphr.hqmf.controller.api.sandbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ainq.caliphr.common.model.json.JsonResponse;
import com.ainq.caliphr.common.model.json.JsonStatus;
import com.ainq.caliphr.hqmf.service.CcdaClasspathImportProcess;
import com.ainq.caliphr.persistence.service.ProviderService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class ClinicalDocumentRestController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private CcdaClasspathImportProcess ccdaClasspathImportProcess;

    /*
        Processes C-CDA files in a hard-coded directory (usually C:\ccda-files\)
     */
    @RequestMapping(value = "/api/ccda/directory", method = RequestMethod.POST)
    public JsonResponse processDirectoryClinicalDocuments() {
        ccdaClasspathImportProcess.processDirectoryCcda();
        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.OK);
        response.setMessage("Process triggered.");
        return response;
    }

    /*
        Decrypts C-CDA files and writes the output in plaintext to the filesystem.
     */
    @RequestMapping(value = "/api/ccda/decrypt", method = RequestMethod.POST)
    public ResponseEntity<JsonResponse> decryptClinicalDocuments(@RequestParam("groupId") String groupId
            , @RequestParam("startDateRange") String startDateRange       // yyyyMMdd - format
            , @RequestParam("endDateRange") String endDateRange) {        // yyyyMMdd - format

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.OK);

        //
        //  Attempt to parse the group
        if (groupId == null || groupId.isEmpty()) {
            response.setMessage("Group ID is required to decrypt C-CDA documents.");
            response.setStatus(JsonStatus.FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //
        //  Attempt to parse the start date
        Date startDate = null;
        if (startDateRange == null || startDateRange.isEmpty()) {
            response.setMessage("Start date in format <yyyyMMdd> is required to decrypt C-CDA documents.");
            response.setStatus(JsonStatus.FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            try {
                startDate = formatter.parse(startDateRange);
            } catch (ParseException e) {
                response.setMessage("Start date in format <yyyyMMdd> is invalid to decrypt C-CDA documents.");
                response.setStatus(JsonStatus.FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        //
        //  Attempt to parse the end date
        Date endDate = null;
        if (endDateRange == null || endDateRange.isEmpty()) {
            response.setMessage("End date in format <yyyyMMdd> is required to decrypt C-CDA documents.");
            response.setStatus(JsonStatus.FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            try {
                endDate = formatter.parse(endDateRange);
            } catch (ParseException e) {
                response.setMessage("End date in format <yyyyMMdd> is invalid to decrypt C-CDA documents.");
                response.setStatus(JsonStatus.FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        //
        //  Loop through the filesystem and reprocess the HL7 messages
        providerService.decryptClinicalDocuments(groupId, startDate, endDate);
        response.setMessage("Process submitted.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Reprocesses encrypted C-CDA files into the application database.
     */
    @RequestMapping(value = "/api/ccda/reprocess", method = RequestMethod.POST)
    public ResponseEntity<JsonResponse> reprocessClinicalDocuments(@RequestParam("groupId") String groupId
            , @RequestParam("startDateRange") String startDateRange       // yyyyMMdd - format
            , @RequestParam("endDateRange") String endDateRange) {        // yyyyMMdd - format

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        JsonResponse response = new JsonResponse();
        response.setStatus(JsonStatus.OK);

        //
        //  Attempt to parse the group
        if (groupId == null || groupId.isEmpty()) {
            response.setMessage("Group ID is required to reprocess C-CDA documents.");
            response.setStatus(JsonStatus.FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //
        //  Attempt to parse the start date
        Date startDate = null;
        if (startDateRange == null || startDateRange.isEmpty()) {
            response.setMessage("Start date in format <yyyyMMdd> is required to reprocess C-CDA documents.");
            response.setStatus(JsonStatus.FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            try {
                startDate = formatter.parse(startDateRange);
            } catch (ParseException e) {
                response.setMessage("Start date in format <yyyyMMdd> is invalid to reprocess C-CDA documents.");
                response.setStatus(JsonStatus.FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        //
        //  Attempt to parse the end date
        Date endDate = null;
        if (endDateRange == null || endDateRange.isEmpty()) {
            response.setMessage("End date in format <yyyyMMdd> is required to reprocess C-CDA documents.");
            response.setStatus(JsonStatus.FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            try {
                endDate = formatter.parse(endDateRange);
            } catch (ParseException e) {
                response.setMessage("End date in format <yyyyMMdd> is invalid to reprocess C-CDA documents.");
                response.setStatus(JsonStatus.FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        providerService.reprocessClinicalDocuments(groupId, startDate, endDate);
        response.setMessage("Process submitted.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
