package com.daweichang.vcfarm.utils;

import android.content.Context;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * String msg;
 * String txt = "123456";
 * try {
 * AESUtils aesUtils = new AESUtils(this);
 * String encrypt = aesUtils.encrypt(txt);
 * msg = "原文:" + txt + "\n密文" + encrypt;
 * String decrypt = aesUtils.decrypt(encrypt);
 * msg += "\n解密后的原文:" + decrypt;
 * text.setText(msg);
 * } catch (Exception e) {
 * e.printStackTrace();
 * }
 */
public class AESUtils {// 类名不能改变
    // static {
    // System.loadLibrary("hyclibkey");
    // }
    //private native String getKey(Object context);// 方法名不能变

    private Context context;

    public AESUtils(Context context) throws Exception {
        this.context = context;
        // try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String keyStr = Key.getKey(context);
        digest.update(keyStr.getBytes("UTF-8"));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    private final Cipher cipher;
    private final SecretKeySpec key;
    private AlgorithmParameterSpec spec;

    // public static final String SEED_16_CHARACTER =
    // "U1MjU1M0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.QM0FDOUZ.Qz";

    public AlgorithmParameterSpec getIV() {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    /**
     * 加密
     */
    public String encrypt(String plainText) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64.encode(encrypted,
                Base64.DEFAULT), "UTF-8");
        return encryptedText;
    }

    /**
     * 解密
     */
    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");
        return decryptedText;
    }
}
