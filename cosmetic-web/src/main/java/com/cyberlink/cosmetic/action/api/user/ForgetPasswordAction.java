package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.mail.service.MailResetPasswordBCWService;
import com.cyberlink.cosmetic.modules.mail.service.MailResetPasswordService;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.utils.EncrUtil;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/forget-password.action")
public class ForgetPasswordAction extends SignInBCAction {
	@SpringBean("mail.mailResetPasswordService")
	private MailResetPasswordService mailResetPasswordService;
	
	@SpringBean("mail.mailResetPasswordBCWService")
	private MailResetPasswordBCWService mailResetPasswordBCWService;
	
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if (email == null)
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        
    	Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);            
    	if (account == null) {
    		return new ErrorResolution(ErrorDef.InvalidAccount);
    	}
    	
    	Member member = memberDao.findByAccountId(account.getId());
    	if (member == null) {
    		// old users
        	User user = userDao.findById(account.getUserId());
        	if (user != null) {
        		String region = user.getRegion();
        		if (region!= null && region.length() >= 5) {
        			locale = region;
        		}
        	}
    		member = createMember("0", account.getId(), null);
    	}
		if (app != null && app.equalsIgnoreCase("BCW"))
			mailResetPasswordBCWService.send(member.getId());
		else {
			mailResetPasswordService.send(member.getId());
		}
    	return success();
    }

    protected Member createMember(String pass, Long accountId, Long memberId) {
    	Member member = new Member();
    	member.setLocale(locale);
    	member.setAccountId(accountId);
    	member.setActivateCode(null);
    	member.setMemberId(memberId);
    	try {
			member.setPassword(PasswordHashUtil.createHash(pass));
			member.setEncryption(EncrUtil.encrypt(pass));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
		}
    	return memberDao.create(member);
    } 
}
