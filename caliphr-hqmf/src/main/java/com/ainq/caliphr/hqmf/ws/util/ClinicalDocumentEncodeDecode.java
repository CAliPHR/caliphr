package com.ainq.caliphr.hqmf.ws.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Created by mmelusky on 8/18/2015.
 */
@Component
public class ClinicalDocumentEncodeDecode {

    /*
        Clinical documents are received in the XDS.b endpoint as base64 encoded strings.
     */

    // UTF8 -> BASE64
    public String encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    // BASE64 -> UTF8
    public String decode(byte[] bytes) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(bytes));
    }

    //  EXTRACT STRING FROM DECODED VALUE (JAXB)
    public String toUtf8String(byte[] bytes) {
        return StringUtils.newStringUtf8(bytes);
    }

}
