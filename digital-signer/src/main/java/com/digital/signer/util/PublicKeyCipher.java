package com.digital.signer.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class PublicKeyCipher {

    private Cipher cipher;

    public PublicKeyCipher (String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {

        setCipher(Cipher.getInstance(algorithm));

    }
    public byte[] encrypBlockByte( byte[] input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherByte = cipher.doFinal(input);
        return cipherByte;
    }

    public byte[] decrypBlockByte( byte[] input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] cipherByte = cipher.doFinal(input);
        return cipherByte;
    }
    public byte[] encryptObject(Object input, Key key) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] clearObject = Util.objectToByteArray(input);
        byte [] cipherObject = cipher.doFinal(clearObject);

        return cipherObject;
    }


    public Object decryotObject (byte[] input, Key key) throws ClassNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] clearText = cipher.doFinal(input);
        Object output = Util.byteArrayToObject(clearText);


        return output;
    }

    public byte[] encryptMessage ( String input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] cipherText = null;
        byte [] clearText = input.getBytes();

        cipher.init(Cipher.ENCRYPT_MODE,key);
        cipherText = cipher.doFinal(clearText);

        return cipherText;


    }


    public String decryptMessage (byte [] input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        String output = "";

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte [] clearText = cipher.doFinal(input);
        output = new String(clearText);

        return output;

    }
    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

}

