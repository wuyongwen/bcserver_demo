package com.cyberlink.cosmetic.action.api.user;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/unbind-user.action")
public class UnBindAction extends AbstractAction{
    @SpringBean("user.AccountDao")
    private AccountDao accountDao;

    private Long accountId;
    
    @DefaultHandler
    public Resolution route() {
    	RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticate())
    		return new ErrorResolution(authError); 
        else if (getSession().getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
        
        Account account = accountDao.findById(accountId);
        if (accountId == null) {
            return new ErrorResolution(ErrorDef.InvalidAccount);
        }
            
        account.setIsDeleted(Boolean.TRUE);
        accountDao.update(account);
        return success();
    }
    
    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
