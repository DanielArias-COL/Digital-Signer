package com.digital.signer.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PublicKeyCipher {

    private static PublicKeyCipher instance;

    private Cipher cipher;

    private PublicKeyCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance("RSA");
    }

    public static synchronized PublicKeyCipher getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (instance == null) {
            instance = new PublicKeyCipher();
        }
        return instance;
    }

    public synchronized byte[] encrypBlockByte(byte[] input, Key key)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    public synchronized byte[] decrypBlockByte(byte[] input, Key key)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
}

