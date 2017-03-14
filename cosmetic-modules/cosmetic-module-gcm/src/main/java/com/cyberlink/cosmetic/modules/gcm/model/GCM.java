package com.cyberlink.cosmetic.modules.gcm.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GCM implements java.io.Serializable {
	private static final long serialVersionUID = 8148921467126084770L;
	private HttpClient httpclient;
	public HttpClient getHttpclient() {
		return httpclient;
	}

	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	private String apiKey;

	public GCM(HttpClient httpclient, String apiKey){
		this.httpclient = httpclient;
		this.apiKey = apiKey;
	}
	
	public GCM(){
	}	
	
	public GCMResult push(GCMPayload payload) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		HttpPost post = null;
		String jsonContent = null;
		try {
			post = new HttpPost("https://android.googleapis.com/gcm/send");
			post.setHeader("Authorization", "key=" + apiKey);
			post.setHeader("Content-Type", "application/json");

			jsonContent = mapper.writeValueAsString(payload);
			StringEntity input = new StringEntity(jsonContent, "UTF-8");
			input.setContentType("application/json");
			post.setEntity(input);
			HttpResponse response = httpclient.execute(post);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			GCMResult result = mapper.readValue(body, GCMResult.class);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}
}
