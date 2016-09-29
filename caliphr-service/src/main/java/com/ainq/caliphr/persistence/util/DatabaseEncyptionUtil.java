package com.ainq.caliphr.persistence.util;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class DatabaseEncyptionUtil {
	
	private String key;
	@Autowired 
	private ApplicationContext ctx;
	private DatabaseEncryptionUtilNoDependencies databaseEncryptionUtilNoDependencies;
	
	
	@Value("#{environment['DATABASE.ENCRYPTION.SYM.KEY.LOCATION']}")
	public void setKeyLocation(String keyLocation) throws IOException {
		setKey(StreamUtils.copyToString(ctx.getResource(keyLocation).getInputStream(), Charset.defaultCharset()));
	}
	
    public void setKey(String key) {
    	this.key = key;
    	databaseEncryptionUtilNoDependencies = new DatabaseEncryptionUtilNoDependencies(key);
    }
    
    public String getKey() {
    	return this.key;
    }
	
	public String decryptPassword(String encryptedPassword) {
        return databaseEncryptionUtilNoDependencies.decryptPassword(encryptedPassword);
    }
	
	public String encryptPassword(String password) {
		return databaseEncryptionUtilNoDependencies.encryptPassword(password);	
	}

}
