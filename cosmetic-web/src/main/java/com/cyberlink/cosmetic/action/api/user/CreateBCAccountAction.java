package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.cyberlink.model.CyberLinkMemberStatus;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.utils.EncrUtil;

@UrlBinding("/api/user/create-account-BC.action")
public class CreateBCAccountAction extends SignInBCAction{
	private String displayName;
    
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;

        /*if (uuid!= null && uuid.length() > 0 && blockDao.isBlockedUuid(uuid)) {
        	return new ErrorResolution(ErrorDef.DeviceBlocked);
        }*/
        if (locale == null || locale.length() < 5)
        	return new ErrorResolution(ErrorDef.InvalidLocale);
        else if (password == null)
        	return new ErrorResolution(ErrorDef.InvalidPassword);
        else if (email == null)
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        
        final Map<String, Object> createResult = new HashMap<String, Object>();
        isDE = locale.substring(3, 5).equalsIgnoreCase("DE");
        
        Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        if (account != null) {
        	if (account.getUserId() == null) {            	
        		createResult.put("status", CyberLinkMemberStatus.WaitValidate);
        		createResult.put("result", Collections.EMPTY_MAP);
        		return json(createResult);            
        	}
        	
        	Member member = memberDao.findByAccountId(account.getId());
        	if (member == null) {
        		// old users
        		try {
					member = createNewMember(
							PasswordHashUtil.createHash(password),
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
        	doSignIn();
            createResult.put("status", CyberLinkMemberStatus.OK);
            createResult.put("result", this.results);    
            return json(createResult);        
        } else {
        	Member member = null;            
        	if (isDE && bCheckDe) {
        		// Not set userId as WaitValidate status.
            	account = new Account();
                account.setAccount(email);
                account.setAccountSource(AccountSourceType.Email); 
                account.setEmail(email);
                account.setMailStatus(AccountMailStatus.SUBSCRIBE);
                account = accountDao.update(account);
            	try {
					member = createNewMember(
							PasswordHashUtil.createHash(password),
							EncrUtil.encrypt(password), account.getId(), null);
        		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        			return new ErrorResolution(ErrorDef.ServerUnavailable);
        		}
				/*if (sendMail) {
					if (app != null && app.equalsIgnoreCase("BCW"))
						mailActivationBCWService.send(member.getId());
					else
						mailService.send(member.getId());
				}*/
        		createResult.put("status", CyberLinkMemberStatus.WaitValidate);
        		createResult.put("result", Collections.EMPTY_MAP);
        		return json(createResult);
        	}
        	try {
				member = createNewMember(PasswordHashUtil.createHash(password),
						EncrUtil.encrypt(password), doSignIn(), null);
    		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
    			return new ErrorResolution(ErrorDef.InvalidPassword);
    		}
			if (sendMail) {
				/*if (app != null && app.equalsIgnoreCase("BCW"))
					mailSignUpBCWService.send(member.getId());
				else {
					mailSignUpService.send(member.getId());
				}*/
			}
            createResult.put("status", CyberLinkMemberStatus.OK);
            createResult.put("result", this.results);    
            return json(createResult);        
        }        
    }
    
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
