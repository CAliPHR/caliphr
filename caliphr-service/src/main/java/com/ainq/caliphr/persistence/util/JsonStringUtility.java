package com.ainq.caliphr.persistence.util;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by mmelusky on 11/3/2015.
 */
public class JsonStringUtility {

    static Logger logger = (Logger) LoggerFactory.getLogger(JsonStringUtility.class);

    public static String buildJsonRequest(Map<String, Object> requestJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(requestJson);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return StringUtils.EMPTY;
    }
}
