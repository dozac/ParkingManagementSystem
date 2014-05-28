package com.oracle.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class EncryptDecrypt 

{
	//default Constructor
	public EncryptDecrypt()
	{
		
	}
	
   // Encrypt password hash function (with block size SHA-512) using 64-bit words
   public String encrypt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
	 { 
	   // get the Message Digest object to perform one way hashing
	   MessageDigest md = MessageDigest.getInstance("SHA-512");	
	     // processed the input data or password
	     md.update(password.getBytes());
	     // complete the hash computation
	     byte hash[] = md.digest();
	  // convert the byte to the String
		return toHex(hash);													
	}
   // Method to convert the byte array to the String
   private String toHex(byte[] array) throws NoSuchAlgorithmException {
	   // buffer to hold the string data
	   StringBuffer hexString = new StringBuffer();						
		for (int i = 0; i < array.length; i++) {
			String hex = Integer.toHexString(0xff & array[i]);
			if (hex.length() == 1)
				hexString.append('0');
			// append each string to the buffer
			hexString.append(hex);											
		}
		// convert buffer to the string
		return hexString.toString();										
	}
}

