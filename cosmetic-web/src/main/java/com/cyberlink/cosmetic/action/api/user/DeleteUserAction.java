package com.cyberlink.cosmetic.action.api.user;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.post.service.DeleteUserService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/delete.action")
public class DeleteUserAction extends AbstractAction{
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;

    @SpringBean("user.AccountDao")
    private AccountDao accountDao;

    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("delete.DeleteUserService")
    private DeleteUserService deleteUserService;
    
    @SpringBean("user.userService")
    protected UserService userService;
    
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticate())
    		return new ErrorResolution(authError); 
        else if (getSession().getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
    	deleteUserService.pushUser(getSession().getUserId());
    	deleteUserService.startAutoPostThread();
        
    	User user = userDao.findById(getSession().getUserId());
        user.setIsDeleted(Boolean.TRUE);
        user = userDao.update(user);
        
        // delete account
        for (Account account : accountDao.findByUserId(user.getId())) {
            accountDao.delete(account);            
        }
        // delete session
        userService.deleteSessionByUser(user.getId());
        
        userDao.refresh(user);
        
        return success();
    }
}
