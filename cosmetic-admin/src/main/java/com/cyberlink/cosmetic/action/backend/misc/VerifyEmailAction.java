package com.cyberlink.cosmetic.action.backend.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/misc/verifyEmail.action")
public class VerifyEmailAction extends AbstractAction{

	String email = null;
	String domainUrl = null;
	String mxUrl = null;
	Integer stepNum = null;
	Map<String,String> domainResultMap = new HashMap<String,String>();
	
	@DefaultHandler
	public Resolution route() {
	    if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		return forward();
    }

	
    @SuppressWarnings("rawtypes")
	public Resolution getMxList() {
		// Isolate the domain/machine name and get a list of mail exchangers
		ArrayList mxList = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			mxList = getMX(domainUrl);
			// Just because we can send mail to the domain, doesn't mean that the
			// address is valid, but if we can't, it's a sure sign that it isn't
			if (mxList.size() == 0){
				result.put("errorMessage", "MX List size is 0");
			}else{
				result.put("mxList", mxList);
			}
		} catch (NamingException ex) {
			result.put("errorMessage", "[Error] " + ex.getMessage());
		} catch (Exception ex) {
			result.put("errorMessage", "[Error] " + ex.getMessage());
		}
        return json(result);
    }
	
	public Resolution verifyMxUrl() {
    	Long spendTime = 0L;
    	Long startedTime = Calendar.getInstance().getTime().getTime();
		Map<String, Object> result = new HashMap<String, Object>();
		// Now, do the SMTP validation, try each mail exchanger until we get
		// a positive acceptance. It *MAY* be possible for one MX to allow
		// a message [store and forwarder for example] and another [like
		// the actual mail server] to reject it. This is why we REALLY ought
		// to take the preference into account.
		try {
			int res;
			Socket skt = new Socket((String) mxUrl, 25);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));

			res = hear(rdr);
			if (res != 220){
				close(rdr, wtr, skt);
				throw new Exception("Invalid header! Error code:" + res);
			}
			if(stepNum == 1){
				close(rdr, wtr, skt);
				spendTime = Calendar.getInstance().getTime().getTime() - startedTime;
				result.put("message", mxUrl + "  ,  " + "OK!" + " [" + spendTime +" millisecond]");
				logger.info("verify mail message : " + mxUrl + "  , header OK! " + " [" + spendTime +" millisecond]");
				return json(result);
			}
			
			String helo = "HELO " + Constants.getHostSesmail();
			say(wtr, helo);

			res = hear(rdr);
			if (res != 250){
				close(rdr, wtr, skt);
				throw new Exception("Not ESMTP! Error code:" + res);
			}
			if(stepNum == 2){
				close(rdr, wtr, skt);
				spendTime = Calendar.getInstance().getTime().getTime() - startedTime;
				result.put("message", mxUrl + "  ,  " + "OK!" + " [" + spendTime +" millisecond]");
				logger.info("verify mail message : " + mxUrl + "  , ESMTP OK! " + " [" + spendTime +" millisecond]");
				return json(result);
			}
				
			// validate the sender address
			say(wtr, "MAIL FROM: <no-reply@vipmail.perfectcorp.com>");
			res = hear(rdr);
			if (res != 250){
				close(rdr, wtr, skt);
				throw new Exception("Sender rejected! Error code:" + res);
			}
			if(stepNum == 3){
				close(rdr, wtr, skt);
				spendTime = Calendar.getInstance().getTime().getTime() - startedTime;
				result.put("message", mxUrl + "  ,  " + "OK!" + " [" + spendTime +" millisecond]");
				logger.info("verify mail message : " + mxUrl + "  , sender address OK! " + " [" + spendTime +" millisecond]");
				return json(result);
			}

			say(wtr, "RCPT TO: <" + email + ">");
			res = hear(rdr);

			if (res != 250){
				close(rdr, wtr, skt);
				throw new Exception("Address is not valid! Error code:" + res);
			}
			if(stepNum == 4){
				close(rdr, wtr, skt);
				spendTime = Calendar.getInstance().getTime().getTime() - startedTime;
				result.put("message", mxUrl + "  ,  " + "RCPT OK!" + " [" + spendTime +" millisecond]");
				logger.info("verify mail message : " + mxUrl + "  , RCPT OK! " + " [" + spendTime +" millisecond]");
				return json(result);
			}
			close(rdr, wtr, skt);
		} catch (Exception ex) {
			spendTime = Calendar.getInstance().getTime().getTime() - startedTime;
			result.put("errorMessage", mxUrl + "  ,  " + ex.getMessage() + " [" + spendTime +" millisecond]");
			logger.info("verify mail errorMessage : " + mxUrl + "  ,  " + ex.getMessage() + " [" + spendTime +" millisecond]");
		}
        return json(result);
    }
    
    private void close(BufferedReader rdr,BufferedWriter wtr,Socket skt) throws IOException{
		// be polite
		say(wtr, "RSET");
		hear(rdr);
		say(wtr, "QUIT");
		hear(rdr);
		rdr.close();
		wtr.close();
		skt.close();
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
	private static ArrayList getMX(String hostName) throws NamingException {
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
			if(!mailhost.equals(""))
				res.add(mailhost);
		}
		return res;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, String> getDomainResultMap() {
		return domainResultMap;
	}

	public void setDomainResultMap(Map<String, String> domainResultMap) {
		this.domainResultMap = domainResultMap;
	}
	
	public void setDomainUrl(String domainUrl) {
		this.domainUrl = domainUrl;
	}
	
	public void setMxUrl(String mxUrl) {
		this.mxUrl = mxUrl;
	}

	public void setStepNum(Integer stepNum) {
		this.stepNum = stepNum;
	}
}