package com.cyberlink.cosmetic.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;


public class Tool {
	
	private static String USER_AGENT = "Mozilla/5.0";
	
	public static int sendGet(String url, StringBuffer msg) {
		 
		int status = 404;
		msg.delete(0,  msg.length());		
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
	 
			// add request header
			request.addHeader("User-Agent", USER_AGENT);
			request.setHeader("Content-Type", "utf-8");
	 
			HttpResponse response = client.execute(request);
			status = response.getStatusLine().getStatusCode();
	 
			//System.out.println("\nSending 'GET' request to URL : " + url);
			//System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			
			BufferedReader rd = new BufferedReader(
	                       new InputStreamReader(response.getEntity().getContent(), "utf-8")); 
			
			String line = "";
			while ((line = rd.readLine()) != null) {
				msg.append(line);
			}
		} catch (Exception e) {}
 
		return status; 
	}
 
	// HTTP POST request
	public static int sendPost(String url, List<NameValuePair> param, StringBuffer msg) {
 
		msg.delete(0,  msg.length());
		int status = 404;

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
 
		// add header
		post.setHeader("User-Agent", USER_AGENT);
 
		try {
		    MultipartEntity mpEntity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
			for (NameValuePair kv:  param) {
				if (kv.getName().equalsIgnoreCase("file")) {
			    	File file = new File(kv.getValue());
			    	mpEntity.addPart( "fileBean", new FileBody((( File ) file ), "image/jpeg" ));
				}
				else
					mpEntity.addPart( kv.getName(), new StringBody( kv.getValue(), "text/plain", Charset.forName( "UTF-8" )));
			}
			post.setEntity(mpEntity);
			//Debug.dprintf("URL: %s", url);
			HttpResponse response = client.execute(post);
			status = response.getStatusLine().getStatusCode();
			BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent(), "utf-8"));
			 
			String line = "";
			while ((line = rd.readLine()) != null) {
				msg.append(line);
			}	 
		} catch (Exception e) {}
		
		return status;		 
	}		
	
	private String canonicalize(SortedMap<String, String> sortedParamMap) {
		if (sortedParamMap.isEmpty()) {
			return "";
			}
		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, String>> iter =
		sortedParamMap.entrySet().iterator();
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
			out = URLEncoder.encode(s, "UTF-8")
					.replace("+", "%20")
					.replace("*", "%2A")
					.replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			out = s;
		}
		return out;
	}
	
	public static void writeStringToFile(String filePath, String data, boolean append){
		if (data == null)
			return;
		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, append), "utf-8"));
            out.write(data);
            out.close();
        }
        catch (Exception e){}
        return ;
	}
	
	public static String readFileToString(String filePath) {
		StringBuffer sb = new StringBuffer();
		if (Tool.isFileExist(filePath)) {
			try {
				BufferedReader brin = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
				String line = null;
				while ((line = brin.readLine()) != null) {
					sb.append(line);
				}	
				brin.close();	
			} catch (Exception e) {}
		}
		return sb.toString();
	}
	
	public static ArrayList<String> readFileToStringArray(String filePath) {
		ArrayList<String> tmp = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		if (Tool.isFileExist(filePath)) {
			try {
				BufferedReader brin = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
				String line = null;
				while ((line = brin.readLine()) != null) {
					tmp.add(line);
				}	
				brin.close();	
			} catch (Exception e) {}
		}
		return tmp;
	}
	
	public static boolean isFileExist(String sFilePath) {
		File f = new File(sFilePath);
		return f.exists();		
	}
	
	public static void movFile(String src, String dst) {
		File afile =new File(src);
		 
 	    if(afile.renameTo(new File(dst))){
 			//System.out.println("File is moved successful!");
 	    }else{
 			//System.out.println("File is failed to move!");
 	    }
	}
	
	public static void delFile(String sFilePat) {
		try{
			File file = new File(sFilePat);
			if(file.exists()){
				file.delete();
			}
		}catch(Exception e){}
	}
	
	public static void delAllFiles(File path) {
		try{
	        if (!path.exists()) {
	            return;
	        }
	        if (path.isFile()) {
	            path.delete();
	            return;
	        }
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	        	delAllFiles(files[i]);
	        }
	        path.delete();
		} catch(Exception e){}
    }
	
	
	public static boolean makeDir(String sFilePath) {
		File f = new File(sFilePath);
		if (!f.exists()) {
			return f.mkdirs();
		}	
		return false;
	}
	
	public static void downloadFileFromURL(String urlString, String destination) {    
        try {
            URL website = new URL(urlString);
            ReadableByteChannel rbc;
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(new File(destination));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
	
	public static String pathJoin(final String ... pathElements)
    {
		if ( pathElements.length == 0)
		  {
		    return "";
		  }

		  File combined = new File(pathElements[0]);

		  int i = 1;
		  while ( i < pathElements.length)
		  {
		    combined = new File(combined, pathElements[i]);
		    ++i;
		  }

		  return combined.getPath();
    }
	
	public static String getMD5Checksum(String filename) throws Exception {
		   InputStream fis =  new FileInputStream(filename);

	       byte[] buffer = new byte[1024];
	       MessageDigest complete = MessageDigest.getInstance("MD5");
	       int numRead;

	       do {
	           numRead = fis.read(buffer);
	           if (numRead > 0) {
	               complete.update(buffer, 0, numRead);
	           }
	       } while (numRead != -1);

	       fis.close();
	       
	       byte[] b = complete.digest();
	       String result = "";

	       for (int i=0; i < b.length; i++) {
	           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	       }
	       return result;
   }	
	
	public static void printCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
	}
	
	public static void writeMessageToFile(String filePath, String message, boolean append){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String funcName = new Exception().getStackTrace()[1].getMethodName();
		String dateMsg = String.format("%s [%s] ", dateFormat.format(cal.getTime()), funcName);
		String data = String.format("%s %s \r\n",dateMsg,message);
		Tool.writeStringToFile(filePath, data, append);
	}
}