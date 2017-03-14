package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import com.restfb.json.JsonObject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/api/user/get-accountToken-CL.action")
public class GetCLAccountTokenAction extends AbstractAction{
    private String email = "";
    private String password = "";
    private String SIGN_IN_URL = "https://cse.cyberlink.com/cse/service/signin";
    
    @DefaultHandler
    public Resolution route() {
        final Map<String, Object> results = new HashMap<String, Object>();
        String accountToken = "";
        try {
        	accountToken = loginCSE().getString("accessToken");
		} catch (Exception e1) {
			return new ErrorResolution(ErrorDef.InvalidPassword);
		}
        
        if (accountToken.length() == 0) {
        	return new ErrorResolution(ErrorDef.InvalidPassword);
        }
        results.put("accountToken", accountToken);
        return json(results);    
    }

    public JsonObject loginCSE() throws Exception {
        Map<String, String> param = new HashMap<String, String>();
		param.put("email", email);
		param.put("password", password);
        
    	HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(SIGN_IN_URL);

        post.setHeader("User-Agent", "");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry: param.entrySet()) {
             urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        post.setEntity(new UrlEncodedFormEntity(urlParameters, "utf-8"));

        HttpResponse response = client.execute(post); 
        BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent(), "utf-8"));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return new JsonObject(result.toString());
    }    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
