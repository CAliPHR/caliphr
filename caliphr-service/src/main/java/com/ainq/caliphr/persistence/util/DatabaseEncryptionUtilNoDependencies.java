package com.ainq.caliphr.persistence.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.Base64Utils;

public class DatabaseEncryptionUtilNoDependencies {
	
	private String key;
	
	public DatabaseEncryptionUtilNoDependencies(String key) {
		this.key = key;
	}

	public String decryptPassword(String encryptedPassword) {
        try {
        	byte[] encryptedTextBytes = Base64Utils.decodeFromString(encryptedPassword);
        	Cipher cipher = getCipher();
			cipher.init(Cipher.DECRYPT_MODE, getSecret());
			byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(e);
		}
    }
	
	public String encryptPassword(String password) {
		
        Cipher cipher = getCipher();
        try {
			cipher.init(Cipher.ENCRYPT_MODE, getSecret());
			return Base64Utils.encodeToString(cipher.doFinal(password.getBytes()));
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(e);
		}
		
	}

    private SecretKeySpec getSecret() {
		PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), "randomSaltValue9$50&46!49x".getBytes(), 10000, 128);
		SecretKey secretKey;
		try {
			secretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return new SecretKeySpec(secretKey.getEncoded(), "AES");
	}
	
	private Cipher getCipher() {
		try {
			return Cipher.getInstance("AES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("usage: DatabaseEncryptionUtilNoDependencies <passwordTxt> <keyTxt>");
			System.exit(-1);
		}
		DatabaseEncryptionUtilNoDependencies deu = new DatabaseEncryptionUtilNoDependencies(args[1]);
		System.out.println("encrypted password=" + deu.encryptPassword(args[0]));

	}

}
