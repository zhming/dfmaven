// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TrustedAuthenticatorUtils.java

package com.gihow.dfc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfPreferences;
import com.gihow.util.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ResourceBundle;

public final class TrustedAuthenticatorUtils
{

	private static final String ENCRYPT_ALGORITHM = "TripleDES";
	private static final int KEY_SIZE = 168;
	private static final String KEYSTORE_TYPE = "JCEKS";
	private static final String KEYSTORE_USE_DFC_CONFIG_DIR = "use_dfc_config_dir";
	private static final String KEYSTORE_FILE_FILENAME = "keystore.file.filename";
	private static final String KEYSTORE_FILE_LOCATION = "keystore.file.location";
	private static final String KEYSTORE_FILE_PWD = "@password12";
	private static final String KEYSTORE_SK_ALIASNAME = "wdksecretkey";
	private static final String KEYSTORE_SK_PWD = "@password34";
	private static final String KEYSTORE_CREDENTIALS = "com.gihow.dfc.KeystoreCredentials";
	private static ResourceBundle g_resourceBundle = ResourceBundle.getBundle("com.gihow.dfc.KeystoreCredentials");

	public TrustedAuthenticatorUtils()
	{
	}

	public static final String encrypt(String str)
	{
		String strEncrypted = null;
		if (str != null)
		{
			char key[] = TrustedAuthenticatorUtils.class.getName().toCharArray();
			char buf[] = str.toCharArray();
			StringBuffer bufEncrypted = new StringBuffer(buf.length * 2);
			int nIdx = 0;
			int nKey = key.length - 1;
			for (; nIdx < buf.length; nIdx++)
			{
				bufEncrypted.append(Integer.toHexString(buf[nIdx] + key[nKey]));
				nKey = ((key.length + nKey) - 1) % key.length;
			}

			strEncrypted = bufEncrypted.toString();
		}
		return strEncrypted;
	}

	public static final String decrypt(String str)
	{
		String strDecrypted = null;
		if (str != null)
		{
			char key[] = TrustedAuthenticatorUtils.class.getName().toCharArray();
			char buf[] = str.toCharArray();
			StringBuffer bufDecrypted = new StringBuffer(buf.length / 2);
			int nIdx = 0;
			int nKey = key.length - 1;
			for (; nIdx < buf.length; nIdx += 2)
			{
				char c = (char)Integer.parseInt(new String(buf, nIdx, 2), 16);
				bufDecrypted.append((char)(c - key[nKey]));
				nKey = ((key.length + nKey) - 1) % key.length;
			}

			strDecrypted = bufDecrypted.toString();
		}
		return strDecrypted;
	}

	public static String encryptByDES(String str)
		throws GeneralSecurityException, IOException
	{
		String encryptedText = null;
		SecretKey secretKey = getSecretKey(true);
		Cipher aesCipher = Cipher.getInstance("TripleDES");
		aesCipher.init(1, secretKey);
		byte plainBytes[] = str.getBytes();
		byte encryptedBytes[] = aesCipher.doFinal(plainBytes);
		encryptedText = Base64.encodeBytes(encryptedBytes);
		return encryptedText;
	}

	public static String decryptByDES(String str)
		throws GeneralSecurityException, IOException
	{
		String decryptedText = null;
		SecretKey secretKey = getSecretKey(true);
		if (secretKey != null)
		{
			Cipher aesCipher = Cipher.getInstance("TripleDES");
			aesCipher.init(2, secretKey);
			byte encryptedBytes[] = Base64.decodeBytes(str);
			byte decryptedBytes[] = aesCipher.doFinal(encryptedBytes);
			decryptedText = new String(decryptedBytes);
		}
		return decryptedText;
	}

	private static SecretKey getSecretKey(boolean doGenerateKey)
		throws GeneralSecurityException, IOException
	{
		SecretKey secretKey;
		String keystoreFile;
		KeyStore ks;
		BufferedInputStream bis;
		secretKey = null;
		keystoreFile = getKeystoreFileName();
		File file = new File(keystoreFile);
		if (!file.exists())
			return secretKey;
		ks = KeyStore.getInstance("JCEKS");
		bis = null;
		bis = new BufferedInputStream(new FileInputStream(keystoreFile));
		ks.load(bis, "@password12".toCharArray());
		IOUtils.closeQuietly(bis);
		KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry)ks.getEntry("wdksecretkey", new KeyStore.PasswordProtection("@password34".toCharArray()));
		if (skEntry != null)
			secretKey = skEntry.getSecretKey();
		if (secretKey == null && doGenerateKey)
			secretKey = generateSecretKey();
		return secretKey;
	}

	private static SecretKey generateSecretKey()
		throws GeneralSecurityException, IOException
	{
		SecretKey secretKey;
		KeyStore ks;
		String keystoreFilename;
		BufferedOutputStream bos;
		KeyGenerator keyGen = KeyGenerator.getInstance("TripleDES");
		keyGen.init(168);
		secretKey = keyGen.generateKey();
		ks = KeyStore.getInstance("JCEKS");
		keystoreFilename = getKeystoreFileName();
		createFolderIfNotExist(keystoreFilename);
		bos = null;
		bos = new BufferedOutputStream(new FileOutputStream(keystoreFilename));
		ks.load(null, "@password12".toCharArray());
		KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKey);
		ks.setEntry("wdksecretkey", skEntry, new KeyStore.PasswordProtection("@password34".toCharArray()));
		ks.store(bos, "@password12".toCharArray());
		IOUtils.closeQuietly(bos);
		return secretKey;
	}

	private static String getKeystoreFileName()
		throws IOException, GeneralSecurityException
	{
		StringBuffer strBuffer = new StringBuffer(512);
		String useDfcConfigDir = g_resourceBundle.getString("use_dfc_config_dir");
        System.out.println("useDfcConfigDir11: " + useDfcConfigDir);
		if (useDfcConfigDir != null && useDfcConfigDir.equalsIgnoreCase("true"))
			try
			{
				DfPreferences prefs = (DfPreferences) DfcUtils.getClientX().getLocalClient32().getClientConfig();
				strBuffer.append(prefs.getConfigDir());
			}
			catch (DfException e)
			{
				throw new GeneralSecurityException("Unable to get the location of Keystore file");
			}
		else
			strBuffer.append(g_resourceBundle.getString("keystore.file.location"));
		strBuffer.append(File.separator).append(g_resourceBundle.getString("keystore.file.filename"));
		File file = new File(strBuffer.toString());
		String fileName = file.getCanonicalPath();
		return fileName;
	}

	private static void createFolderIfNotExist(String fileFullPath)
	{
		String folderPath = fileFullPath.substring(0, fileFullPath.lastIndexOf(File.separator));
		File folder = new File(folderPath);
		if (!folder.exists())
			folder.mkdirs();
	}

}
