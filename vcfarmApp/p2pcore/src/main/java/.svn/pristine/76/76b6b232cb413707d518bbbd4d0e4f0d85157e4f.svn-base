package com.p2p.core.utils;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;


import android.util.Log;

public class DES {
	static byte[] key=new byte[]{(byte) 0x9c, (byte) 0xae, 0x6a, 0x5a, (byte) 0xe1,(byte) 0xfc,(byte) 0xb0, (byte) 0x82};
	public static byte[] des(byte[] str,int type)
			throws Exception {
		if (type == 0) {
			return desEncrypt(str,key);
		} else {
			return desDecrypt(str,key);
		}
	}
	
	public static byte[] desEncrypt(byte[] source, byte rawKeyData[])
            throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeySpec key = new SecretKeySpec(rawKeyData, "DES");
//        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] ss=cipher.doFinal(source);
        return ss;
    }
	
	public static byte[] desDecrypt(byte[] data, byte rawKeyData[])
            throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeySpec key = new SecretKeySpec(rawKeyData, "DES");
//        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] ss=cipher.doFinal(data);
        return ss;
    }
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder(""); 
	    stringBuilder.append("[");
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv); 
	        if(i!=src.length-1){
	         stringBuilder.append(", ");
	        }
	    } 
	    stringBuilder.append("]");
	    return stringBuilder.toString();  
	}  
}
