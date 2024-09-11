package com.hurontg.tiger.core;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

  public static Map<String, byte[]>  encrypt(String password) {
    Map<String, byte[]> map = new HashMap<>();

    try {
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[256];
      random.nextBytes(salt);

      PBEKeySpec pbKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1324, 256);
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

      SecureRandom ivRandom = new SecureRandom();
      byte[] iv = new byte[16];
      ivRandom.nextBytes(iv);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
      byte[] encrypted = cipher.doFinal(password.getBytes());

      map.put("salt", salt);
      map.put("iv", iv);
      map.put("encrypted", encrypted);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return map;
  }

  public static String decrypt(String password, Map<String, byte[]> map) {
    String decryptedPassword;
    try {
      byte[] salt = map.get("salt");
      byte[] iv = map.get("iv");
      byte[] encrypted = map.get("encrypted");

      PBEKeySpec pbKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1324, 256);
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      byte[] decrypted = cipher.doFinal(encrypted);
      decryptedPassword = new String(decrypted);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return decryptedPassword;
  }
}
