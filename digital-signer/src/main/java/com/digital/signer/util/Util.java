package com.digital.signer.util;

import com.digital.signer.common.MensajeRespuestaHTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;


public class Util {

    public static PublicKeyCipher cipherClass;

    static {
        try {
            cipherClass = PublicKeyCipher.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNull(String valor) {
        return valor == null || valor.trim().length() == 0;
    }

    public static byte[] encrypBlockByte(byte[] input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        return cipherClass.encrypBlockByte(input, key);
    }

    public static byte[] decrypBlockByte(byte[] input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        return cipherClass.decrypBlockByte(input, key);
    }

    public static ResponseEntity<Object> getResponseSuccessful(Object body) {
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    public static ResponseEntity<Object> getResponseBadRequest(String bussinesMessage) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeRespuestaHTTP(bussinesMessage));
    }

    public static ResponseEntity<Object> getResponseError(String metodo, String error) {
        if (error == null || error.trim().length() == 0) {
            error = "Exception lanzada por NullPointerException.";
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MensajeRespuestaHTTP(metodo + error));
    }

    public static String encodingPublicKeyBase64(byte[] bytes) {
        return Base64.encode(bytes);
    }

    public static byte[][] SplitByteArray(byte[] entrada, int tamanioFrag) {

        byte[][] salida = null;

        int tamanioEntrada = entrada.length;

        if (tamanioEntrada > tamanioFrag) {

            if (tamanioEntrada % tamanioFrag != 0) {

                int cantFilasLongFull = tamanioEntrada / tamanioFrag;
                int filaFaltante = tamanioEntrada % tamanioFrag;

                int contador = 0;

                salida = new byte[cantFilasLongFull + 1][];


                for (int i = 0; i < salida.length - 1; i++) {
                    salida[i] = new byte[tamanioFrag];

                    for (int j = 0; j < salida[i].length; j++) {

                        salida[i][j] = entrada[contador];
                        contador++;
                    }


                }

                salida[cantFilasLongFull] = new byte[filaFaltante]; //en la fila n+1  con elementos faltantes que son filaFaltante
                for (int i = 0; i < salida[cantFilasLongFull].length; i++) {

                    salida[cantFilasLongFull][i] = entrada[contador];
                    contador++;
                }


            } else {

                int cantFilasLongFull = tamanioEntrada / tamanioFrag;
                int contador = 0;
                salida = new byte[cantFilasLongFull][];


                for (int i = 0; i < salida.length; i++) {
                    salida[i] = new byte[cantFilasLongFull];

                    for (int j = 0; j < salida.length; j++) {

                        salida[i][j] = entrada[contador];
                        contador++;
                    }


                }
            }

        }


        return salida;

    }

    public static KeyPair generateKeyPair(String algorithm, int wrenchSize) throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(wrenchSize);

        return keyPairGenerator.generateKeyPair();

    }

    public static String getHash(byte[] inputBA, String algorithm) throws Exception {
        MessageDigest hasher = MessageDigest.getInstance(algorithm);
        hasher.update(inputBA);
        return Util.byteArrayToHexString(hasher.digest(), "");

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes, String separator) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += String.format("%02x", bytes[i]) + separator;
        }

        //result = addLineBreaks(result, 140);
        return result.toString();
    }

    public static byte[] objectToByteArray(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(o);
        out.close();
        byte[] buffer = bos.toByteArray();

        return buffer;
    }

    public static Object byteArrayToObject(byte[] byteArray) throws IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        Object o = in.readObject();
        in.close();

        return o;

    }

    public static String decodeKeyDto(byte[] privateKeyFileBytes) throws IOException {
        // Convertir los bytes del archivo en una cadena
        String pemString = new String(privateKeyFileBytes);

        // Quitar las cabeceras y pies del formato PEM
        pemString = pemString.replace("-----BEGIN PRIVATE KEY-----", "");
        pemString = pemString.replace("-----END PRIVATE KEY-----", "");

        // Eliminar cualquier espacio en blanco adicional, incluyendo saltos de línea
        pemString = pemString.replaceAll("\\s+", "");

        return pemString; // Esta es la clave en formato Base64
    }
}
