package com.ainq.caliphr.persistence.transformation.util;

import java.util.Date;
import org.hl7.v3.ED;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.Code;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.TemplateRoot;

/**
 * Created by mmelusky on 4/27/2016.
 */
public final class CdaUtility {

    public static Boolean isSameCode(Code code1, Code code2, String codeDescription1, String codeDescription2) {
    	if (code1 == null && code2 == null) {
			return isSameValue(codeDescription1, codeDescription2);
		}
        return  (code1 != null && code2 != null && code1.getId() != null && code1.getId().equals(code2.getId()));
    }

    public static Boolean isSameTemplate(TemplateRoot t1, TemplateRoot t2) {
        return (t1 == null && t2 == null) || t1 != null && t2 != null && t1.getId() != null &&  t1.getId().equals(t2.getId());
    }

    public static Boolean isSameValue(Object value1, Object value2) {
    	return (value1 == null && value2 == null) || (value1 != null && value1.equals(value2));
    }
    
    @SuppressWarnings("deprecation")
	public static Boolean isSameTime(Date time1, Date time2) {
    	return 	
    		(time1 == null && time2 == null) ? true :
            (time1 == null || time2 == null) ? false :
            time1.getDate() == time2.getDate() && 
    		time1.getMonth() == time2.getMonth() && 
    		time1.getYear() == time2.getYear() &&
    		time1.getHours() == time2.getHours() &&
    		time1.getMinutes() == time2.getMinutes();
    }
    
    public static String getOriginalText(ED ed) {
    	StringBuilder stVal = new StringBuilder();
		ed.getContent().stream().filter(content -> content instanceof String).forEach(stVal::append);
		return stVal.toString();
    }

}
