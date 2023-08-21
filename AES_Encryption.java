import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

public class AES_Encryption {
	private SecretKey key;
	private int[] KEY_SIZE = {128, 192, 256};
	private int tagLength = 128; //{128, 120, 112, 104, 96};
	private String encryptType = "AES";
	private Cipher encrypt;
	private byte[] output;
	private String message;
	private byte[] iv;
	
	//This method will create the symmetric key
	public void createKey() throws Exception {
		KeyGenerator generate = KeyGenerator.getInstance(encryptType);
		int randomKey = new Random().nextInt(KEY_SIZE.length);
		generate.init(KEY_SIZE[randomKey]);
		key = generate.generateKey();
	}
	
	//This method will encrypt the file
	public File encryptFile(File inputFile, File outputFile) throws Exception{		
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		byte[] fileInBytes = new byte[(int) inputFile.length()];

		encrypt = Cipher.getInstance(encryptType + "/GCM/NoPadding");
		iv = encrypt.getIV();
		encrypt.init(Cipher.ENCRYPT_MODE, key);
		inputStream.read(fileInBytes);
		output = encrypt.doFinal(fileInBytes);
		message = Base64.getEncoder().encodeToString(output);
		outputStream.write(message.getBytes(Charset.forName("UTF-8")));
			
		inputStream.close();
		outputStream.close();	
		
		return outputFile;
		
		//For multiple part encryption
//		int readBytes;
//		while((readBytes = inputStream.read(fileInBytes)) != -1) {
//			byte[] output = encrypt.update(fileInBytes, 0, readBytes);
//			if(output !=  null) {
//				outputStream.write(output);
//			}
//		}
	}
	
	public File decryptFile(File encryptedFile, File decryptedFile) throws Exception{
		FileOutputStream outputStream = new FileOutputStream(decryptedFile);
		byte[] fileInBytes = Base64.getDecoder().decode(message.getBytes(Charset.forName("UTF-8")));
		Cipher decrypt = Cipher.getInstance(encryptType + "/GCM/NoPadding");
		GCMParameterSpec spec = new GCMParameterSpec(tagLength, encrypt.getIV());
		decrypt.init(Cipher.DECRYPT_MODE, key, spec);
		byte[] decryptBytes = decrypt.doFinal(fileInBytes);
		outputStream.write(decryptBytes);
		outputStream.close();

		return decryptedFile;
	}
	
	public static void main(String[] args) {
		AES_Encryption test = new AES_Encryption();
		File inputFile = new File("C:\\Users\\kcbro\\OneDrive\\Documents\\GSU Files\\Secret Message.txt");
		File encryptedFile = new File("encryptedFile.txt");
		File decryptedFile = new File("decryptedFile.txt");
	    
		try {
			test.createKey();
			test.encryptFile(inputFile, encryptedFile);
			test.decryptFile(encryptedFile, decryptedFile);
		} catch(Exception error) {
			System.out.println("Unable to perform task.");
			error.printStackTrace();
		}
	}
}