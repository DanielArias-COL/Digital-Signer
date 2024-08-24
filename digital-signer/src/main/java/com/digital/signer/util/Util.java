package com.digital.signer.util;

import com.digital.signer.common.MensajeRespuestaHTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class Util {

	public static boolean isNull(String valor) {
		return valor == null || valor.trim().length() == 0;
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

	public static String encodingPublicKeyBase64 (byte[] bytes) {
		return Base64.encode(bytes);
	}

	public static void crearArchivoscsv(String[] inputFiles) throws Exception {
	    PrivateKey privateKey1024 = Util.recuperarLLavePrivada("private_key1024.pem");
	    PublicKey publicKey1024 = Util.recuperarLLavePublica("public_key1024.pem");
	    PrivateKey privateKey2048 = Util.recuperarLLavePrivada("private_key2048.pem");
	    PublicKey publicKey2048 = Util.recuperarLLavePublica("public_key2048.pem");
	    SecretKey secretKey = (SecretKey) Util.loadObject("simetric_key.key");
	    
	    
	    StringBuilder cadena = new StringBuilder();
	    String [] spltiArray = null;
	    
	    
	        int contador = 0;
	        String linea = "Tamanio; Encr DES; Desencr DES; Encr RSA 1024; Desencr RSA 1024; Encr RSA 2048; Desencr RSA 2048";
	        cadena.append(linea);
	        cadena.append("\n");
	        

	        while (contador < inputFiles.length ) {
	            linea = "";

	            if (contador == 0) {
	                linea += "1 MB;";
	            }
	            if (contador == 1) {
	                linea += "10 MB;";
	            }
	            if (contador == 2) {
	                linea += "100 MB;";
	            }

	            
	            // Encr DES
	            long startTime = System.nanoTime();
	            SymmetricCipher cipherDES = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");
	            Util.encryptFile(inputFiles[contador], inputFiles[contador] + ".encrypted", secretKey);
	            long processTime = System.nanoTime() - startTime;
	            linea += " " + processTime / 1_000_000.0 + ";";
	            

	            // Dec DES
	            spltiArray = inputFiles[contador].split("\\.");
	            startTime = System.nanoTime();
	            Util.decryptFile(inputFiles[contador] + ".encrypted", spltiArray[0] + ".plain" + "."+spltiArray[1], secretKey);
	            processTime = System.nanoTime() - startTime;
	            linea += " " + processTime / 1_000_000.0 + ";";

	            // Enc RSA1024
	            startTime = System.nanoTime();
	            Util.encryptFileBin(inputFiles[contador], inputFiles[contador] + ".rsa1024", publicKey1024);
	            processTime = System.nanoTime() - startTime;
	            linea += " " + processTime / 1_000_000.0 + ";";
	            

	            // Dec RSA1024
	            spltiArray = inputFiles[contador].split("\\.");
	            startTime = System.nanoTime();
	            Util.decryptFileBin(inputFiles[contador] + ".rsa1024", spltiArray[0] + ".plain1024" + "."+spltiArray[1], privateKey1024);
	            processTime = System.nanoTime() - startTime;
	            linea += " " + processTime / 1_000_000.0 + ";";
	            

	            // Enc RSA2048
	            startTime = System.nanoTime();
	            Util.encryptFileBin(inputFiles[contador], inputFiles[contador] + ".rsa2048", publicKey2048);
	            processTime = System.nanoTime() - startTime;
	            linea += " " + processTime / 1_000_000.0 + ";";

	            // Dec RSA2048
	            spltiArray = inputFiles[contador].split("\\.");
	            startTime = System.nanoTime();
	            Util.decryptFileBin(inputFiles[contador] + ".rsa2048", spltiArray[0] + ".plain2048" + "."+spltiArray[1] , privateKey2048);
	            processTime = System.nanoTime() - startTime;
	            linea += " " + processTime / 1_000_000.0;
	          

	            cadena.append(linea);
	            cadena.append("\n");

	            System.out.println(cadena.toString()); // Mostrar línea en consola para verificar

	            contador++;
	        
	    }
	        
	    saveFile("resultado.csv", cadena.toString());    
	}

	public static byte[][] SplitByteArray (byte[]entrada ,int tamanioFrag) {
		
		byte [][] salida = null;
		
		int tamanioEntrada = entrada.length;
		
		if (tamanioEntrada > tamanioFrag) {
			
			if (tamanioEntrada % tamanioFrag != 0) {
				
				int cantFilasLongFull = tamanioEntrada / tamanioFrag;
				int filaFaltante = tamanioEntrada % tamanioFrag;
				
				int contador = 0;
				
				salida = new byte [cantFilasLongFull+1][];
				
				
				for (int i = 0; i < salida.length-1; i++) {
					salida[i]= new byte[tamanioFrag];
					
					for (int j = 0; j < salida[i].length; j++) {
						
						salida [i][j] = entrada[contador];
					    contador ++; 
					}
					
					
				}
			
				salida[cantFilasLongFull]= new byte[filaFaltante]; //en la fila n+1  con elementos faltantes que son filaFaltante
				for (int i = 0; i < salida[cantFilasLongFull].length; i++) {
					
					salida[cantFilasLongFull][i] = entrada[contador];
					contador++;
				}
				
				
			
			}else {
				
				int cantFilasLongFull = tamanioEntrada / tamanioFrag;
				int contador = 0;
				salida = new byte [cantFilasLongFull][];
				
				
				for (int i = 0; i < salida.length; i++) {
					salida[i]= new byte[cantFilasLongFull];
					
					for (int j = 0; j < salida.length; j++) {
						
						salida [i][j] = entrada[contador];
					    contador ++; 
					}
					
					
				}
			}
			
		}
		
		
		
	return salida;	
		
	}
	
	public static KeyPair generateKeyPair(String algorithm,int wrenchSize) throws NoSuchAlgorithmException{

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
		keyPairGenerator.initialize(wrenchSize);

		return keyPairGenerator.generateKeyPair();

	}

	public static String getHash(byte[] inputBA, String algorithm) throws Exception {
		MessageDigest hasher = MessageDigest.getInstance(algorithm);
		hasher.update(inputBA);
		return Util.byteArrayToHexString(hasher.digest(),"");

	}

	public static byte[] joinByteArray(byte[][] matriz) {
	 
	    int totalLength = 0;
	    for (int i = 0; i < matriz.length; i++) {
	    	for (int j = 0; j < matriz[i].length; j++) {
				
	    		totalLength += 1;
			}
		}

	    
	    byte[] resultado = new byte[totalLength];

	 
	    int contador=0;
	    for (int i = 0; i < matriz.length; i++) {
			
	    	for (int j = 0; j < matriz[i].length; j++) {
				
	    		resultado [contador] = matriz [i][j];
	    		contador++;
			}
		}

	    return resultado;
	}
	
	
	public static String generateRandomString(int length) {
		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
	
	public static byte[] loadBytes(String filename) {
        byte[] bytes = new byte[0];
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String hexString = sb.toString().replaceAll("\\s", ""); // Elimina espacios en blanco
            bytes = hexStringToByteArray(hexString);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return bytes;
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

	  public static PrivateKey recuperarLLavePrivada(String filePath) throws IOException, GeneralSecurityException {
	        StringBuilder keyBuilder = new StringBuilder();
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // Omitir las líneas de encabezado y pie de página del archivo PEM
	                if (!line.startsWith("-----BEGIN") && !line.startsWith("-----END")) {
	                    keyBuilder.append(line);
	                }
	            }
	        }

	        // Decodificar la clave privada en Base64
	        byte[] keyBytes = Base64.decode(keyBuilder.toString());
	        
	        // Construir el objeto PrivateKey desde el byte array
	        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        return kf.generatePrivate(spec);
	    }
	  
	  public static PublicKey recuperarLLavePublica(String filePath) throws IOException, GeneralSecurityException {
	        StringBuilder keyBuilder = new StringBuilder();
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // Omitir las líneas de encabezado y pie de página del archivo PEM
	                if (!line.startsWith("-----BEGIN") && !line.startsWith("-----END")) {
	                    keyBuilder.append(line.trim());
	                }
	            }
	        }

	        // Decodificar la clave pública en Base64
	        byte[] keyBytes = Base64.decode(keyBuilder.toString());
	        
	        // Construir el objeto PublicKey desde el byte array
	        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        return kf.generatePublic(spec);
	    }
	
	public static void guardarLLaveEnDisco(String filePath, String key) throws IOException {
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	            writer.write(key);
	        }
	}
	
	
	public static void saveFile(String filePath, String text) throws IOException {
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	            writer.write(text);
	        }
	}
	
	
	
	public static String imprimirPublicKey ( PublicKey publicKey) {
		
		String key = "";
		
		byte [] bytePublicKey = publicKey.getEncoded();
		String B64publicKey = Base64.encode(bytePublicKey);
		key = B64publicKey;
		
		StringBuilder sb = new StringBuilder();
	    sb.append("-----BEGIN PUBLIC KEY-----\n");

	    int index = 0;
	    while (index < key.length()) {
	            sb.append(key, index, Math.min(index + 64, key.length()));
	            sb.append('\n');
	            index += 64;
	        }
	        
	        sb.append("-----END PUBLIC KEY-----");
	        
	        
		return sb.toString();
		
		
	}
	
	public static String imprimirPrivateKey ( PrivateKey privateKey) {
		
		String key = "";
		
		byte [] bytePublicKey = privateKey.getEncoded();
		String B64publicKey = Base64.encode(bytePublicKey);
		key = B64publicKey;
		
		StringBuilder sb = new StringBuilder();
	    sb.append("-----BEGIN PRIVATE KEY-----\n");
	        
	    int index = 0;
	    while (index < key.length()) {
	            sb.append(key, index, Math.min(index + 64, key.length()));
	            sb.append('\n');
	            index += 64;
	        }
	        
	        sb.append("-----END PRIVATE KEY-----");
	        
	        
		
		
		return sb.toString();
		
	}
	
	public static void encryptFile(String inputFilePath, String outputFilePath, SecretKey secretKey) throws Exception {
		
	    SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");
	    
	    
	    
	    
	    try (FileInputStream fis = new FileInputStream(inputFilePath);
	         BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

	        byte[] buffer = new byte[8]; // DES works with 8-byte blocks
	        int bytesRead;
	        while ((bytesRead = fis.read(buffer)) != -1) {
	            byte[] encryptedBlock = cipher.encryptMessage(buffer);
	            String encodedBlock = Base64.encode(encryptedBlock);
	            bw.write(encodedBlock);
	            bw.newLine();
	        }
	        System.out.println("Archivo encriptado con éxito.");
	    }
	}
	
public static void encryptFileBin(String inputFilePath, String outputFilePath, PublicKey publicKey) throws Exception {
		
	
		
		String algorithm = "RSA";
		PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
		
		RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
		int tamanioLLave = rsaPublicKey.getModulus().bitLength();
		
		int fragmentos = 0;
	    
	    if (tamanioLLave == 1024) {
			fragmentos = 117;
		}
	    if (tamanioLLave == 2048) {
			fragmentos = 245;
		}
		
	    
	    try (FileInputStream fis = new FileInputStream(inputFilePath);
	         BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

	        byte[] buffer = new byte[fragmentos]; // DES works with 8-byte blocks
	        int bytesRead;
	        while ((bytesRead = fis.read(buffer)) != -1) {
	        	if (buffer.length > fragmentos) {
					System.out.println("Entra a partir cadenas");
	        		byte [][] partes = SplitByteArray(buffer, fragmentos);
	        		
	        		String encodedBlock ="";
	        		for (byte[] bs : partes) {
	        			byte[] encryptedBlock = cipher.encrypBlockByte(bs, publicKey);
	    	            encodedBlock += Base64.encode(encryptedBlock);
	    	            
					}
	        		bw.write(encodedBlock);
    	            bw.newLine();
				}else {
					
					byte[] encryptedBlock = cipher.encrypBlockByte(buffer, publicKey);
    	            String encodedBlock = Base64.encode(encryptedBlock);
    	            bw.write(encodedBlock);
    	            bw.newLine();
				}
	        	
	            
	        }
	        //System.out.println("Archivo encriptado con éxito.");
	    }
	}

public static void encryptFile(String inputFilePath, String outputFilePath, PublicKey publicKey) throws Exception {
    String algorithm = "RSA";
    PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
    
    RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
	int tamanioLLave = rsaPublicKey.getModulus().bitLength();
	
	int fragmentos = 0;
    
    if (tamanioLLave == 1024) {
		fragmentos = 117;
	}
    if (tamanioLLave == 2048) {
		fragmentos = 245;
	}

    // Usamos try-with-resources para asegurarnos de que los recursos se cierren automáticamente
    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
         BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

        String line;
        while ((line = br.readLine()) != null) {
            // Encriptar la línea
            byte[] encryptedBlock = cipher.encryptMessage(line, publicKey);

            // Codificar el resultado en Base64
            String encodedBlock = Base64.encode(encryptedBlock);

            // Escribir la línea codificada en el archivo de salida
             // Para depuración
            bw.write(encodedBlock);
            bw.newLine();
        }
    }
}


public static void decryptFileBin(String inputFilePath, String outputFilePath, PrivateKey privateKey) throws Exception {
    String algorithm = "RSA";
    PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

    RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
    int keySize = rsaPrivateKey.getModulus().bitLength();
    int fragmentSize = 0;

    if (keySize == 1024) {
        fragmentSize = 128; // RSA 1024 encryption block size
    } else if (keySize == 2048) {
        fragmentSize = 256; // RSA 2048 encryption block size
    }

    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
         FileOutputStream fos = new FileOutputStream(outputFilePath)) {

        String line;
        while ((line = br.readLine()) != null) {
        	byte[] encryptedBlock = Base64.decode(line);
        	if (encryptedBlock.length > fragmentSize) {
				byte [][] partes = SplitByteArray(encryptedBlock, fragmentSize);
				byte[] decryptedBlock;
				for (byte[] bs : partes) {
					System.out.println(fragmentSize);
					System.out.println(bs.length);
					decryptedBlock = cipher.decrypBlockByte(bs, privateKey);
					fos.write(decryptedBlock);
				}
			}else {
				
				byte[] decryptedBlock = cipher.decrypBlockByte(encryptedBlock, privateKey);
				fos.write(decryptedBlock);
			}
            
            
        }
    }
    //System.out.println("Archivo desencriptado con éxito.");
}
	
	    
    public static void decryptFile(String inputFilePath, String outputFilePath, PrivateKey privateKey) throws Exception {
        String algorithm = "RSA";
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

        BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));

        String line;
        while ((line = br.readLine()) != null) {
            // Decodificar la línea de Base64
            byte[] encryptedBlock = Base64.decode(line);

            // Desencriptar la línea
            String decryptedLine = new String( cipher.decryptMessage(encryptedBlock, privateKey));

            // Escribir la línea desencriptada en el archivo de salida
            bw.write(decryptedLine);
            bw.newLine();
        }

        br.close();
        bw.close();
    }
    
    
    
    
    
	
	public static void decryptFile(String inputFilePath, String outputFilePath, SecretKey secretKey) throws Exception {
	    SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");

	    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
	         FileOutputStream fos = new FileOutputStream(outputFilePath)) {

	        String line;
	        while ((line = br.readLine()) != null) {
	            byte[] encryptedBlock = Base64.decode(line);
	            byte[] decryptedBlock = cipher.decryptMessageByte(encryptedBlock);
	            fos.write(decryptedBlock);
	        }
	        System.out.println("Archivo desencriptado con éxito.");
	    }
	}
	
	
	
	
	
	
	
	
	
	public static void encriptTextFile(String filePath , SymmetricCipher cipher) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		
 
    //String filePath = "file.txt"; // Ruta del archivo de texto
    String encryptedFilePath = "encryptedFile.txt.encrypted"; // Ruta del archivo encriptado
	
	
	   try (BufferedReader br = new BufferedReader(new FileReader(filePath));
	             BufferedWriter bw = new BufferedWriter(new FileWriter(encryptedFilePath))) {

	            String linea;
	            while ((linea = br.readLine()) != null) {
	                // Procesar la línea leída
	                System.out.println(linea);

	                byte[] encryptedLine = cipher.encryptMessage(linea);
	                String lineB64 = Base64.encode(encryptedLine);

	                // Escribir la línea encriptada en el archivo encriptado
	                bw.write(lineB64);
	                bw.newLine(); // Agregar un salto de línea después de cada línea encriptada
	            }
	            System.out.println("¡Se escribió en el archivo encriptado con éxito!");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
	}
	
	public static void decriptTextFile(String encryptedFilePath, SymmetricCipher cipher) throws ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		int index = encryptedFilePath.lastIndexOf(".encrypted");
		String parteAntes = encryptedFilePath.substring(0, index);
		String parteDespues = encryptedFilePath.substring(index + ".encrypted".length());
		String decryptedFilePath = parteAntes + ".plain" + parteDespues + ".txt";;
		
	    
		
		
		   try (BufferedReader br = new BufferedReader(new FileReader(encryptedFilePath));
		             BufferedWriter bw = new BufferedWriter(new FileWriter(decryptedFilePath))) {

		            String lineaB64;
		            
		           while ((lineaB64 = br.readLine()) != null) {
		                // Procesar la línea leída
		                System.out.println(lineaB64);



		                 byte[] lineaByte = Base64.decode(lineaB64);
		        		 String lineaDesencriptada = cipher.decryptMessage(lineaByte);
			              

		                // Escribir la línea encriptada en el archivo encriptado
		                bw.write(lineaDesencriptada);
		                bw.newLine(); // Agregar un salto de línea después de cada línea encriptada
		            }
		            System.out.println("¡Se desencripta el archivo con éxito!");
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		   
		   
	}
	
	
	
	
	public static String byteArrayToHexString(byte[] bytes, String separator) {
	String result = "";
	for (int i=0; i<bytes.length; i++) {
			result += String.format("%02x", bytes[i]) + separator;
	}
	
	//result = addLineBreaks(result, 140);
	return result.toString();
	}

	
    public static String addLineBreaks(String input, int lineLength) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < input.length(); i++) {
            result.append(input.charAt(i));
            if ((i + 1) % lineLength == 0) {
                result.append(System.lineSeparator());
            }
        }

        return result.toString();
    }
	public static void saveObject(Object o, String fileName) throws IOException {
		// TODO Auto-generated method stub
		
		FileOutputStream fileOut;
		ObjectOutputStream out;
		
		fileOut = new FileOutputStream(fileName);
		out = new ObjectOutputStream(fileOut);
		
		out.writeObject(o);
		
		out.flush();
		out.close();
		
		
		
	}
	
	public static byte[] objectToByteArray (Object o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(o);
		out.close();
		byte[] buffer = bos.toByteArray();
		
		return buffer;
		
		
	}
	
	public static Object  byteArrayToObject(byte [] byteArray) throws IOException, ClassNotFoundException {
		
		ObjectInputStream in = new ObjectInputStream(new  ByteArrayInputStream(byteArray));
		Object o = in.readObject();
		in.close();
		
		return o;
		
	}

	public static Object loadObject(String fileName) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		FileInputStream fileIn;
		ObjectInputStream in;
		
		fileIn = new FileInputStream(fileName);
		in = new ObjectInputStream(fileIn);
		
		Thread.sleep(100);
		
		Object o = in.readObject();
		
		fileIn.close();
		in.close();
		
		return o;
	}
	
	public static void imprimirMatrizBytes(byte [][] matriz) {
		for (int x=0; x < matriz.length; x++) {
			  System.out.print("|");
			  for (int y=0; y < matriz[x].length; y++) {
			    System.out.print (matriz[x][y]);
			    if (y!=matriz[x].length-1) System.out.print("\t");
			  }
			  System.out.println("|");
			}
	}
	
	public class Base64 {
		public static String encode(byte[] raw) {
			StringBuffer encoded = new StringBuffer();

			for (int i = 0; i < raw.length; i += 3) {
				encoded.append(encodeBlock(raw, i));
			}
			return encoded.toString();
		}

		protected static char[] encodeBlock(byte[] raw, int offset) {
			int block = 0;
			int slack = raw.length - offset - 1;
			int end = (slack >= 2) ? 2 : slack;

			for (int i = 0; i <= end; i++) {
				byte b = raw[offset + i];
				int neuter = (b < 0) ? b + 256 : b;
				block += neuter << (8 * (2 - i));
			}

			char[] base64 = new char[4];

			for (int i = 0; i < 4; i++) {
				int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
				base64[i] = getChar(sixbit);
			}

			if (slack < 1)
				base64[2] = '=';
			if (slack < 2)
				base64[3] = '=';

			return base64;
		}

		protected static char getChar(int sixBit) {
			if (sixBit >= 0 && sixBit <= 25)
				return (char) ('A' + sixBit);

			if (sixBit >= 26 && sixBit <= 51)
				return (char) ('a' + (sixBit - 26));

			if (sixBit >= 52 && sixBit <= 61)
				return (char) ('0' + (sixBit - 52));

			if (sixBit == 62)
				return '+';

			if (sixBit == 63)
				return '/';

			return '?';
		}

		public static byte[] decode(String base64) {
			int pad = 0;

			for (int i = base64.length() - 1; base64.charAt(i) == '='; i--) {
				pad++;
			}

			int length = base64.length() * 6 / 8 - pad;
			byte[] raw = new byte[length];
			int rawIndex = 0;

			for (int i = 0; i < base64.length(); i += 4) {
				int block = (getValue(base64.charAt(i)) << 18) + (getValue(base64.charAt(i + 1)) << 12)
						+ (getValue(base64.charAt(i + 2)) << 6) + (getValue(base64.charAt(i + 3)));

				for (int j = 0; j < 3 && rawIndex + j < raw.length; j++)
					raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);

				rawIndex += 3;
			}

			return raw;
		}

		protected static int getValue(char c) {
			if (c >= 'A' && c <= 'Z')
				return c - 'A';
			if (c >= 'a' && c <= 'z')
				return c - 'a' + 26;
			if (c >= '0' && c <= '9')
				return c - '0' + 52;
			if (c == '+')
				return 62;
			if (c == '/')
				return 63;
			if (c == '=')
				return 0;

			return -1;
		}
	}
	
	
}
