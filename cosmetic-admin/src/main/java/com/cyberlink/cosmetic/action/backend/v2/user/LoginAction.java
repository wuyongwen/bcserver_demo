package com.cyberlink.cosmetic.action.backend.v2.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.v2.IndexAction;
import com.cyberlink.cosmetic.action.backend.UserAccessControl;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.restfb.json.JsonObject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/v2/user/login.action")
public class LoginAction extends AbstractAction{
	@SpringBean("user.SessionDao")
    private SessionDao sessionDao;

    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("user.AccountDao")
    private AccountDao accountDao;

    @SpringBean("user.MemberDao")
    private MemberDao memberDao;

    @SpringBean("user.userService")
    protected UserService userService;
    
	private String email;
	private String password;
    
	@DefaultHandler
    public Resolution route() {
		try {
			findCookieEmail();
		} catch (Exception e){
		}
		
        return forward();
    }

    public Resolution login() {
        String accountToken = "";
        
        Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);        
        if (account != null) {
        	Member member = memberDao.findByAccountId(account.getId());
        	if (member == null) {
        		//return new ErrorResolution(400, "Invalid password");
        	} else {
        		try {
        			if (!PasswordHashUtil.validatePassword(password, member.getPassword())) {
        				return new ErrorResolution(400, "Invalid password");
        			} else {
        				accountToken = "OK";
        			}
        		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        			return new ErrorResolution(400, "Invalid password");
        		}
        	}

        } else {
        	return new ErrorResolution(400, "Invalid account");
        }
        if (accountToken.length() == 0) {
        	return new ErrorResolution(400, "Invalid account");
        }

        User user = userDao.findById(account.getUserId());
        setCurrentUser(user);
        UserAccessControl accessControl = getAccessControl();
        if (!getCurrentUserAdmin() && (accessControl == null || accessControl.getAccessMap() == 0)) {
        	return new ErrorResolution(400, "Invalid account");
        }
        
        String token;
        if (user == null) {
        	User newUser = createNewUser();
            account.setUserId(newUser.getId());
            accountDao.update(account);
            token = userService.getToken(newUser.getId(), newUser.getUserType());
            // return new ErrorResolution(400, "Invalid user");
        } else {
        	token = userService.getToken( user.getId(), user.getUserType());
        }
        getContext().getRequest().getSession().setAttribute("token", token);      
        Cookie c = new Cookie("email", email);
        c.setMaxAge(24*60*60);
        getContext().getResponse().addCookie(c);  // response is an instance of type HttpServletReponse
        return new RedirectResolution(IndexAction.class, "route");
    }
    
    public void findCookieEmail() {
    	Cookie[] cookies = getContext().getRequest().getCookies();     // request is an instance of type 
    	if (cookies == null)
    		return;
    	for(int i = 0; i < cookies.length; i++) { 
    		Cookie c = cookies[i];
    		if (c.getName().equals("email")) {
    			try {
					email = URLDecoder.decode(c.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					email = c.getValue();
				}
    		}
    	}     
    }
    
    private User createNewUser() {
        User user = new User();
        user.setObjVersion(0);
        user.setUserType(UserType.Normal);
        return userDao.create(user);
    }

    public JsonObject loginCSE() throws Exception {
        Map<String, String> param = new HashMap<String, String>();
		param.put("email", email);
		param.put("password", password);
        
    	HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://cse.cyberlink.com/cse/service/signin");

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

    public Resolution cancel() {/* (3) */
    	return new RedirectResolution(IndexAction.class, "route");
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
