package com.cyberlink.cosmetic.utils;

import org.apache.commons.codec.binary.Base64;

public class EncrUtil {
	private static final String KEY = "BeautyCircle";

	public static String encrypt(String str) {
		if (str == null || str.isEmpty())
			return null;
		
		try {
			return base64Encode(xorWithKey(str.getBytes(), KEY.getBytes()));			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String str) {
		if (str == null || str.isEmpty())
			return null;
		
		try {
			return new String(xorWithKey(base64Decode(str), KEY.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] xorWithKey(byte[] str, byte[] key) {
		byte[] out = new byte[str.length];
		for (int i = 0; i < str.length; i++) {
			out[i] = (byte) (str[i] ^ key[i % key.length]);
		}
		return out;
	}

	private static byte[] base64Decode(String str) {
		return Base64.decodeBase64(str);
	}

	private static String base64Encode(byte[] bytes) {
		return Base64.encodeBase64String(bytes).replaceAll("\\s", "");

	}
}