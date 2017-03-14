package com.cyberlink.cosmetic.action.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

public abstract class AbstractMsrAction extends AbstractAction {
    
    @SpringBean("user.SessionDao")
    protected SessionDao sessionDao;
    
    @SpringBean("user.AccountDao")
    protected AccountDao accountDao;
    
    protected User getUserByToken(String token) throws Exception {
        if(token == null)
            throw new Exception("Invalid token");
        Session session = sessionDao.findByToken(token);
        if(session == null)
            throw new Exception("Invalid token");
        User user = session.getUser();
        if(user == null)
            throw new Exception("Invalid user");
        return user;
    }
    
    protected InputStream doPost(String sURL, Map<String, String> params, String cookie, String referer, String charset) { 
        java.io.BufferedWriter wr = null; 
        InputStream result = null;
        String data = "";
        for(String key : params.keySet()) {
            data += key + "=" + params.get(key) + "&";
        }
        try { 
            URL url = new URL(sURL); 
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
            conn.setDoOutput(true); 
            conn.setDoInput(true); 
            conn.setRequestMethod("POST"); 
            conn.setUseCaches(false); 
            conn.setAllowUserInteraction(true);  
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");  
            if (cookie != null) 
                conn.setRequestProperty("Cookie", cookie); 
            if (referer != null) 
                conn.setRequestProperty("Referer", referer); 
      
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length)); 
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            java.io.DataOutputStream dos = new java.io.DataOutputStream(conn.getOutputStream()); 
            dos.writeBytes(data); 
            result = conn.getInputStream();
        } catch (java.io.IOException e) {   
            e.printStackTrace();
        } finally { 
            if (wr != null) { 
                try { 
                    wr.close(); 
                } catch (java.io.IOException ex) { 
                } 
                wr = null; 
            } 
        } 
          
        return result; 
    } 
    
    protected User getUserByEmail(String email) throws Exception {
        if (email == null)
            throw new Exception("Invalid email");
        Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        if (account == null)
            throw new Exception("Invalid email");
        
        User user = account.getUser();
        if (user == null) {
            throw new Exception("Invalid account");
        }
        
        return user;
    }
    
    protected class MsrApiResult {
        private Map<String, Object> result = new LinkedHashMap<String, Object>();
        private Boolean success = true;
        private String error;
        
        public MsrApiResult() {
            
        }
        
        public Resolution getResult() {
            result.put("success", success);
            if(error != null)
                result.put("error", error);
            return json(result);
        }

        public Resolution getResult(Class<?> serializationView) {
            result.put("success", success);
            if(error != null)
                result.put("error", error);
            return json(result, serializationView);
        }
        
        public void Add(String key, Object value) {
            result.put(key, value);
        }
        
        public void setError(String error) {
            this.error = error;
            this.success = false;
        }

    }
}
