package com.cyberlink.cosmetic.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {
	private static final String UTF8_CHARSET = "UTF-8";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final String SECRET_KEY = "dldTREJNcllDW1xXel1RRkVIclhDUl1U";

	public static String getSignature(Map<String, String> params) {
		return getSignature(params, SECRET_KEY);
	}

	public static String getSignature(Map<String, String> params, String secretKey) {
		SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
		String canonicalQS = canonicalize(sortedParamMap);
		return hmac(canonicalQS, secretKey);
	}

	private static String hmac(String stringToSign, String secretKey) {
		Mac mac = null;
		try {
			byte[] secretyKeyBytes = secretKey.getBytes(UTF8_CHARSET);
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA1_ALGORITHM);
			mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(secretKeySpec);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}

		if (mac == null)
			return null;

		byte[] bytes;
		Formatter formatter = new Formatter();
		bytes = mac.doFinal(stringToSign.getBytes());
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private static String canonicalize(SortedMap<String, String> sortedParamMap) {
		if (sortedParamMap.isEmpty()) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> kvpair = iter.next();
			buffer.append(percentEncodeRfc3986(kvpair.getKey()));
			buffer.append("=");
			buffer.append(percentEncodeRfc3986(kvpair.getValue()));
			if (iter.hasNext()) {
				buffer.append("&");
			}
		}
		String canonical = buffer.toString();
		return canonical;
	}

	private static String percentEncodeRfc3986(String s) {
		String out;
		try {
			out = URLEncoder.encode(s, UTF8_CHARSET)
					.replace("+", "%20")
					.replace("*", "%2A")
					.replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			out = s;
		}
		return out;
	}
}