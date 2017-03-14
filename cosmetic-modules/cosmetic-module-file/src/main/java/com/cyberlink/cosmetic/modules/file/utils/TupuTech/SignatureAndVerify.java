package com.cyberlink.cosmetic.modules.file.utils.TupuTech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SignatureAndVerify {

    public static String Signature(PrivateKey privateKey, String sign_string) {
		try {
			Signature signer = Signature.getInstance("SHA256WithRSA");
			signer.initSign(privateKey);
			signer.update(sign_string.getBytes());
			byte[] signed = signer.sign();
			return new String(Base64.encode(signed));
		} catch (Exception e) {
			return "err";
		}
	}

	public static boolean Verify(PublicKey pubKey, String signature, String json) {
		try {
			Signature signer = Signature.getInstance("SHA256WithRSA");
			signer.initVerify(pubKey);
			signer.update(json.getBytes());
			return signer.verify(Base64.decode(signature));
		} catch (Exception e) {
			return false;
		}
	}

	public static String ReadKey(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String readLine = null;
		StringBuilder sb = new StringBuilder();
		while ((readLine = br.readLine()) != null) {
			if (readLine.charAt(0) == '-') {
				continue;
			} else {
				sb.append(readLine);
				sb.append('\r');
			}
		}
		return sb.toString();
	}
}