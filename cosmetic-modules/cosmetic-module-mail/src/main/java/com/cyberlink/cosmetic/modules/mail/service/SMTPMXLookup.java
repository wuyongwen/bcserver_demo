package com.cyberlink.cosmetic.modules.mail.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.cyberlink.cosmetic.Constants;

public class SMTPMXLookup {
	
	public static class SocketVerifyException extends Exception {
		
		private static final long serialVersionUID = 5422544379427492761L;

		public SocketVerifyException(String message) {
			super(message);
		}
	}

	private static int hear(BufferedReader in) throws IOException {
		String line = null;
		int res = 0;

		while ((line = in.readLine()) != null) {
			String pfx = line.substring(0, 3);
			try {
				res = Integer.parseInt(pfx);
			} catch (Exception ex) {
				res = -1;
			}
			if (line.length() <= 3 || line.charAt(3) != '-')
				break;
		}
		return res;
	}

	private static void say(BufferedWriter wr, String text) throws IOException {
		wr.write(text + "\r\n");
		wr.flush();
		return;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getMX(String hostName) throws NamingException {
		// Perform a DNS lookup for MX records in the domain
		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		Attribute attr = attrs.get("MX");

		// if we don't have an MX record, try the machine itself
		if ((attr == null) || (attr.size() == 0)) {
			attrs = (Attributes) ictx.getAttributes(hostName,
					new String[] { "A" });
			attr = (Attribute) attrs.get("A");
			if (attr == null)
				throw new NamingException("No match for name '" + hostName
						+ "'");
		}

		// Huzzah! we have machines to try. Return them as an array list
		// NOTE: We SHOULD take the preference into account to be absolutely
		// correct. This is left as an exercise for anyone who cares.
		ArrayList res = new ArrayList();
		NamingEnumeration en = attr.getAll();

		while (en.hasMore()) {
			String mailhost;
			String x = (String) en.next();
			String f[] = x.split(" ");
			// THE fix *************
			if (f.length == 1)
				mailhost = f[0];
			else if (f[1].endsWith("."))
				mailhost = f[1].substring(0, (f[1].length() - 1));
			else
				mailhost = f[1];
			// THE fix *************
			res.add(mailhost);
		}
		return res;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getMXOnly(String hostName) throws NamingException {
		// Perform a DNS lookup for MX records in the domain
		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		Attribute attr = attrs.get("MX");

		if (attr == null)
			throw new NamingException("No match for name '" + hostName + "'");

		// Huzzah! we have machines to try. Return them as an array list
		// NOTE: We SHOULD take the preference into account to be absolutely
		// correct. This is left as an exercise for anyone who cares.
		ArrayList res = new ArrayList();
		NamingEnumeration en = attr.getAll();

		while (en.hasMore()) {
			String mailhost;
			String x = (String) en.next();
			String f[] = x.split(" ");
			// THE fix *************
			if (f.length == 1)
				mailhost = f[0];
			else if (f[1].endsWith("."))
				mailhost = f[1].substring(0, (f[1].length() - 1));
			else
				mailhost = f[1];
			// THE fix *************
			res.add(mailhost);
		}
		return res;
	}

	@SuppressWarnings("deprecation")
	public static boolean validateEmailAddress(String address, ArrayList mxList) throws SocketVerifyException {
		// Now, do the SMTP validation, try each mail exchanger until we get
		// a positive acceptance. It *MAY* be possible for one MX to allow
		// a message [store and forwarder for example] and another [like
		// the actual mail server] to reject it. This is why we REALLY ought
		// to take the preference into account.
		int socketFailCount = 0;
		
		for (int mx = 0; mx < mxList.size(); mx++) {
			boolean valid = false;
			Boolean isSocketVerify = Boolean.FALSE;
			try {
				int res;
				//
				isSocketVerify = Boolean.TRUE;
				Socket skt = new Socket((String) mxList.get(mx), 25);
				isSocketVerify = Boolean.FALSE;
				
				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						skt.getInputStream()));
				BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(
						skt.getOutputStream()));

				res = hear(rdr);
				if (res != 220)
					throw new Exception("Invalid header");
				String helo = "HELO " + Constants.getHostSesmail();
				say(wtr, helo);

				res = hear(rdr);
				if (res != 250)
					throw new Exception("Not ESMTP");

				// validate the sender address
				say(wtr, "MAIL FROM: <no-reply@vipmail.perfectcorp.com>");
				res = hear(rdr);
				if (res != 250)
					throw new Exception("Sender rejected");

				say(wtr, "RCPT TO: <" + address + ">");
				res = hear(rdr);

				// be polite
				say(wtr, "RSET");
				hear(rdr);
				say(wtr, "QUIT");
				hear(rdr);
				if (res != 250)
					throw new Exception("Address is not valid!");

				valid = true;
				rdr.close();
				wtr.close();
				skt.close();
			} catch (Exception ex) {
				// Do nothing but try next host
				if (isSocketVerify)
					socketFailCount++;
				ex.printStackTrace();
			} finally {
				if (valid)
					return true;
			}
		}
		
		if (socketFailCount == mxList.size())
			throw new SocketVerifyException("MX domain connect fail, the fail count: " + socketFailCount);
		
		return false;
	}

}