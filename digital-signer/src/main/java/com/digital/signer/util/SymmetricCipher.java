package com.digital.signer.util;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;



public class SymmetricCipher {

	private SecretKey secretKey;
	private Cipher cipher;

	public SymmetricCipher( SecretKey secretKey, String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.setSecretKey(secretKey);
		setCipher(Cipher.getInstance(transformation));
	}
	
	public  byte[] encryptMessage( String input) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		byte[] clearText = input.getBytes();
		byte[] cipherText = null;
		
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		cipherText = cipher.doFinal(clearText);
		
		return cipherText;
	}
	
	public byte[] encryptMessage(byte[] message) throws Exception {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        return cipher.doFinal(message);
	}
	
 
    public byte[] decryptMessageByte(byte[] encryptedMessage) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedMessage);
    } 
    
	public String decryptMessage(byte[] input) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String output = "";
		
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] clearText = cipher.doFinal(input);
		output = new String(clearText);
		
		return output;
	}
	
	public byte[] encryptObject(Object input) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		byte[] cipherObject = null;
		byte[] clearObject = null;
		
		clearObject = Util.objectToByteArray(input);
		
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		cipherObject = cipher.doFinal(clearObject);
		
		return cipherObject;
	}
	
	public static byte[] objectToByteArray(Object o) throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		
		out.writeObject(o);
		out.close();
		byte[] buffer = bos.toByteArray();
		
		return buffer;
		
	}
	
	public Object decryptObject(byte[] input) throws InvalidKeyException, ClassNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
		
		Object output = null;
		
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] clearObject = cipher.doFinal(input);
		
		output = Util.byteArrayToObject(clearObject);
		
		return output;
	}
	


	
	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public Cipher getCipher() {
		return cipher;
	}

	public void setCipher(Cipher cipher) {
		this.cipher = cipher;
	}
	
	
	
}
