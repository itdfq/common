package com.itdfq.common.utils.encry;

import com.itdfq.common.constant.EncodeConstant;
import com.itdfq.common.pojo.Result;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @author: QianMo
 * @date: 2022/2/10 14:31
 * @mark:
 */
public class AESUtils {
    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String AES_CODE = "AES";
    private static final String AES_SYS_KEY = "AES_SYS_KEY";

    /**
     * 获取 cipher
     */
    private static Cipher getCipher(byte[] key, int model) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES_CODE);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(model, secretKeySpec);
        return cipher;
    }

    /**
     * AES加密
     */
    public static byte[] encrypt(byte[] data, byte[] key)
            throws Exception {
        Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);
        return cipher.doFinal(data);
    }

    /**
     * AES解密
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);
        return cipher.doFinal(data);
    }

    /**
     * 根据密钥对指定的密文cipherText进行解密.
     *
     * @param cipherText 密文
     * @param key        秘钥
     * @return 解密后的明文.
     */
    public static Result decrypt(String cipherText, String key) {
        Key secretKey = null;
        try {
            secretKey = getKey(key);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_CODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] c = decoder.decodeBuffer(cipherText);
            byte[] result = cipher.doFinal(c);
            return Result.success(new String(result, EncodeConstant.UTF_8));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据密钥对指定的明文plainText进行加密.
     *
     * @param plainText 明文
     * @param key       秘钥
     * @return 加密后的密文.
     */
    public static Result encrypt(String plainText, String key) {
        Key secretKey = null;
        try {
            secretKey = getKey(key);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_CODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] p = plainText.getBytes(EncodeConstant.UTF_8);
            byte[] result = cipher.doFinal(p);
            BASE64Encoder encoder = new BASE64Encoder();
            return Result.success(encoder.encode(result));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public static Key getKey(String keySeed) throws Exception {
        if (keySeed == null) {
            keySeed = System.getenv(AES_SYS_KEY);
        }
        if (keySeed == null) {
            keySeed = System.getProperty(AES_SYS_KEY);
        }
        if (keySeed == null || keySeed.trim().length() == 0) {
            keySeed = "abcd1234!@#$";
        }

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(keySeed.getBytes());
        KeyGenerator generator = KeyGenerator.getInstance(AES_CODE);
        generator.init(secureRandom);
        return generator.generateKey();

    }


}
