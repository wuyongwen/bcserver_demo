package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.utils.EncrUtil;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/change-password.action")
public class ChangePasswordAction extends AbstractAction{
	@SpringBean("user.MemberDao")
	protected MemberDao memberDao;

	private String oldPassword;
	private String newPassword;
	
	@DefaultHandler
    public Resolution route() {
		if (oldPassword == null || newPassword == null)
    		return new ErrorResolution(ErrorDef.InvalidPassword);
		
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticate())
    		return new ErrorResolution(authError); 
    	List<Account> accountList = getSession().getUser().getAccountList();
    	Account account = null;
    	if (accountList.size() > 0) {
        	account = accountList.get(accountList.size()-1);
        } else {
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        }

    	List<Member> memberList = account.getMember();
        Member member = null;
        if (memberList.size() > 0) {
        	member = memberList.get(0);
        } else {
        	return new ErrorResolution(ErrorDef.InvalidAccount);
        }
    	
		try {
			if (PasswordHashUtil.validatePassword(oldPassword, member.getPassword())) {
				member.setPassword(PasswordHashUtil.createHash(newPassword));
				member.setEncryption(EncrUtil.encrypt(newPassword));
				member = memberDao.update(member);
			} else {
				return new ErrorResolution(ErrorDef.InvalidPassword);
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
		}
		return success();
	}
	
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
