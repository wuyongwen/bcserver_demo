package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.cyberlink.model.CyberLinkMemberStatus;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.mail.service.MailActivationService;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.utils.EncrUtil;

@UrlBinding("/api/user/change-account-email.action")
public class ChangeEmailAction extends SignInBCAction {
	@SpringBean("mail.mailActivationService")
	protected MailActivationService mailService;

	private String displayName;
    
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticate())
    		return new ErrorResolution(authError); 
    	else if (locale == null || locale.length() < 5)
        	return new ErrorResolution(ErrorDef.InvalidLocale);
        else if (password == null)
        	return new ErrorResolution(ErrorDef.InvalidPassword);
        else if (email == null)
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        
    	final Map<String, Object> createResult = new HashMap<String, Object>();
    	isDE = locale.substring(3, 5).equalsIgnoreCase("DE");
        
        Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
        if (account != null) {
        	// this email is already been used by other account
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        }     
        account = null;
    	
        Long userId = getSession().getUserId();
    	
    	// find account that need delete
    	List<Account> accountList = accountDao.findByUserId(userId);
    	for (Account a :  accountList) {
    		if (a.getAccountSource() == AccountSourceType.Email && a.getAccount() == null) {
    			account = a;
    			break;
    		}
    	}    	
    	if (account == null)
    		return new ErrorResolution(ErrorDef.InvalidToken);
    	
    	Member member = memberDao.findByAccountId(account.getId());
    	if (member != null) {
    		member.setIsDeleted(Boolean.TRUE);
    		memberDao.update(member);
    	}
    	member = null;
    	
    	account.setAccount(email);
    	accountDao.update(account);
    	try {
			member = createNewMember(PasswordHashUtil.createHash(password),
					EncrUtil.encrypt(password), doSignIn(account), null);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return new ErrorResolution(ErrorDef.InvalidPassword);
		}
    	mailService.send(member.getId());
        createResult.put("status", CyberLinkMemberStatus.OK);
        createResult.put("result", this.results);    
        return json(createResult);        
    }	

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
