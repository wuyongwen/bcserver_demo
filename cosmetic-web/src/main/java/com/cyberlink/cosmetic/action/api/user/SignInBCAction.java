package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.event.user.UserSignInEvent;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.cyberlink.cosmetic.utils.EncrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/sign-in-BC.action")
public class SignInBCAction extends SignInAction {
	@SpringBean("user.MemberDao")
	protected MemberDao memberDao;

    @SpringBean("user.userService")
    protected UserService userService;
	
	protected String email;
    protected String password;
    protected boolean isDE = false;
    protected boolean bCheckDe = Boolean.TRUE;
    protected Boolean sendMail = true;
	protected Map<String, Object> results = new HashMap<String, Object>();

	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        /*if (uuid!= null && uuid.length() > 0 && blockDao.isBlockedUuid(uuid)) {
        	return new ErrorResolution(ErrorDef.DeviceBlocked);
        } else*/ if (email== null || password == null) {
        	return new ErrorResolution(ErrorDef.InvalidPassword);
        }
        
        isDE = locale.substring(3, 5).equalsIgnoreCase("DE");
        
        Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        if (account == null) {
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        } else if (account.getUserId() == null) {            	
    		return new ErrorResolution(ErrorDef.WaitingValidate);
    	}
        
        List<Member> memberList = account.getMember(); //memberDao.findByAccountId(account.getId());       
        Member member = null;
        if (memberList.size() > 0) {
        	member = memberList.get(0);
        }
		if (member == null) {
			// old users
			try {
				member = createNewMember(PasswordHashUtil.createHash(password),
						EncrUtil.encrypt(password), account.getId(), null);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				return new ErrorResolution(ErrorDef.ServerUnavailable);
			}
		}
    	try {
        	if (!PasswordHashUtil.validatePassword(password, member.getPassword())) {
        		return new ErrorResolution(ErrorDef.InvalidPassword);
        	}
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        	return new ErrorResolution(ErrorDef.InvalidPassword);
        }
    	doSignIn(account);
    	return json(results); 	
    }
	
    protected Long doSignIn() {
    	return doSignIn(null);
    }
    
    protected Long doSignIn(Account account){
        if (account == null)
        	account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        User user = null;
        Long userId;
        if (account == null) {
        	account = new Account();
            account.setAccount(email);
            account.setEmail(email);
            account.setAccountSource(AccountSourceType.Email);
            account.setMailStatus(AccountMailStatus.SUBSCRIBE);
        } else {
        	userId = account.getUserId();
        	//if (userDao.exists(userId))
        	user = account.getUser();
        	if (email != null && account.getEmail() == null) {
        		account.setEmail(userEmail);
        		account = accountDao.update(account);
        	}
        }    
        if (user == null) {
            user = createNewUser();
            user.setAttribute("{}");
            userId = user.getId();
            account.setUserId(userId);
            account = accountDao.update(account);
            results.put("token", userService.getToken(userId,user.getUserType()));
            results.put("userInfo", user);
        } else {
        	if (apnsType != null) {
        		if (apnsType.equalsIgnoreCase("gcm")) {
        			user.setOs("Android");
        		} else if (apnsType.equalsIgnoreCase("apns")) {
        			user.setOs("iOS");
        		}
        	} 
            if (getContext() != null && getContext().getRequest() != null)
            	user.setIpAddress(ipAddress);
            	//user.setIpAddress(getContext().getRequest().getRemoteAddr());
        	user.setApp(app);            
            user = userDao.update(user);

        	userId = user.getId();
            results.put("token", userService.getToken(userId,user.getUserType()));
            String mapAsJson = "{}";
            
            if (user.hasKeyInAttr("userAttr")) {
            	String userAttr = user.getStringInAttr("userAttr");
            	if (userAttr.length() > 0) {
            		mapAsJson = userAttr;
            	}
            } else {
            	final Map<String, Object> attributes = new HashMap<String, Object>();
            	for (Attribute attr : attributeDao.findByRefId(AttributeType.User, userId)) {
            		attributes.put(attr.getAttrName(), attr.getAttrValue());
            	}
            	try {
            		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
            	} catch (JsonProcessingException e) {
            	}
            }
            user.setAttribute(mapAsJson);
            results.put("userInfo", user);
        }
        //publishDurableEvent(new UserSignInEvent(account.getUserId(), user.getRegion()));
        return account.getId();
    }   
    protected Member createNewMember(String pass, String encrStr, Long accountId, Long memberId) {
    	Member member = new Member();
    	member.setLocale(locale);
    	member.setAccountId(accountId);
    	if (isDE && bCheckDe)
    		member.setActivateCode(UUID.randomUUID().toString());
    	else
    		member.setActivateCode(null);
    	member.setMemberId(memberId);
    	member.setPassword(pass);
    	member.setEncryption(encrStr);
    	return memberDao.create(member);
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


	public Map<String, Object> getResults() {
		return results;
	}

	public void setResults(Map<String, Object> results) {
		this.results = results;
	}

	public boolean isDE() {
		return isDE;
	}

	public void setDE(boolean isDE) {
		this.isDE = isDE;
	}

	public boolean isbCheckDe() {
		return bCheckDe;
	}

	public void setbCheckDe(boolean bCheckDe) {
		this.bCheckDe = bCheckDe;
	}

	public Boolean getSendMail() {
		return sendMail;
	}

	public void setSendMail(Boolean sendMail) {
		this.sendMail = sendMail;
	}

}
