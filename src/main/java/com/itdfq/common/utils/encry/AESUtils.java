package com.itdfq.common.utils.encry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: QianMo
 * @date: 2022/2/10 14:31
 * @mark:
 */
public class AESUtils {
    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String AES_CODE = "AES";

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

}
