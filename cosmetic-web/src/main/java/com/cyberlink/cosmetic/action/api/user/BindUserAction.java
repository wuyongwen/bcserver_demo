package com.cyberlink.cosmetic.action.api.user;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.cyberlink.model.CyberLinkMember;
import com.cyberlink.cosmetic.modules.cyberlink.service.CyberLinkService;
import com.cyberlink.cosmetic.modules.facebook.service.FacebookService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/bind-user.action")
public class BindUserAction extends AbstractAction {
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;

    @SpringBean("user.AccountDao")
    private AccountDao accountDao;

    @SpringBean("facebook.facebookService")
    private FacebookService facebookService;
    
    @SpringBean("cyberlink.cyberlinkService")
    private CyberLinkService cyberlinkService;

    private AccountSourceType accountSource;
    private String accountToken;
    
    @DefaultHandler
    public Resolution route() {
    	RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
    	
    	if (!authenticate()) {
    		return new ErrorResolution(authError); 
    	} else if (getSession().getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
        
        String accountRef = null;
        String userEmail = null;
        if (accountSource == AccountSourceType.Email) {
        	CyberLinkMember clMember = cyberlinkService.fetchCyberLinkMember(accountToken);
        	if (clMember != null && clMember.getResultCode() == 0) {
        		accountRef = clMember.getEmail();
        		userEmail = accountRef;
        	} else {
        		return new ErrorResolution(ErrorDef.InvalidAccountToken);
        	}
        } else if (accountSource == AccountSourceType.Facebook){
        	try {
        		FacebookClient fbc = new DefaultFacebookClient(accountToken);
        		com.restfb.types.User fbu = facebookService.findUserByClientAndUid(
                    fbc, "me");
        		accountRef = fbu.getId();
        		try {
            		userEmail = fbu.getEmail();
        		} catch (Exception e)  {        			
        		}
        	} catch (FacebookOAuthException e){
        		return new ErrorResolution(ErrorDef.InvalidAccountToken);
        	}
        } else if (accountSource == AccountSourceType.Weibo){
        	weibo4j.Account am = new weibo4j.Account(accountToken);
    		try {
    			accountRef = am.getUid().getString("uid");
    		} catch (WeiboException e) {
    			return new ErrorResolution(ErrorDef.InvalidAccountToken);
    		} catch (JSONException e) {
    			return new ErrorResolution(ErrorDef.InvalidAccountToken);
    		}
        } else {
        	return new ErrorResolution(ErrorDef.InvalidAccountSource);
        }
        Account account = accountDao.findBySourceAndReference(accountSource, accountRef);
        
        // already bind the account
        if (account != null) {
            // account is bind already by other user
            if (account.getUserId() != getSession().getUserId())
                return new ErrorResolution(ErrorDef.InvalidToken);
            return success();
        }
        account = new Account();
        account.setAccount(accountRef);
        account.setAccountSource(accountSource);
        account.setUserId(getSession().getUserId());
        account.setEmail(userEmail);
        account.setMailStatus(AccountMailStatus.SUBSCRIBE);
        accountDao.create(account);
        return success();
    }

    public AccountSourceType getAccountSource() {
        return accountSource;
    }
    public void setAccountSource(AccountSourceType accountSource) {
        this.accountSource = accountSource;
    }
    public String getAccountToken() {
        return accountToken;
    }
    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }
    
}
