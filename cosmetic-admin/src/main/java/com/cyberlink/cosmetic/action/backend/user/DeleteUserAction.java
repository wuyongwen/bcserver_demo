package com.cyberlink.cosmetic.action.backend.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.post.service.DeleteUserService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.BlockDeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.BlockDevice;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.service.UserService;

@UrlBinding("/user/deleteUser.action")
public class DeleteUserAction extends AbstractAction {	
	@SpringBean("user.AccountDao")
    private AccountDao accountDao;

    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.DeviceDao")
    private DeviceDao deviceDao;
    
    @SpringBean("user.BlockDeviceDao")
    private BlockDeviceDao blockDao;
    
    @SpringBean("delete.DeleteUserService")
    private DeleteUserService deleteUserService;
    
    @SpringBean("user.MemberDao")
    private MemberDao memberDao;
    
    @SpringBean("user.userService")
    protected UserService userService;
    
    private Long userId;
	private String email;
	private String password;
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}


	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
		
        return forward();
    }
	
	public Resolution delete() {
		
		if(!isExternalLoginUserAdmin())
		{
			if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()) {
	            return new ErrorResolution(403, "Need to login");
	        }	
			if (!userDao.exists(userId))
				return new StreamingResolution("text/html", "User doesn't exist");
		}
		// block device
		List<String> uuidList = deviceDao.findDistinctByUserId(userId);
		for (String uuid : uuidList) {
			List<BlockDevice> b = blockDao.findByUuid(uuid);
			if (b.size() > 0) {
				b.get(0).setIsDeleted(Boolean.FALSE);
				blockDao.update(b.get(0));
			} else {
				BlockDevice d = new BlockDevice();
				d.setShardId(userId);
				d.setUuid(uuid);
				blockDao.create(d);
			}
		}
		
		deleteUserService.pushUser(userId);
		deleteUserService.startAutoPostThread();
		
		User user = userDao.findById(userId);
		user.setIsDeleted(Boolean.TRUE);
		user = userDao.update(user);
        
		// delete account
        for (Account account : accountDao.findByUserId(user.getId())) {
            accountDao.delete(account);            
        }
        // delete session
        userService.deleteSessionByUser(user.getId());
        
        userDao.refresh(user);
   
        return new StreamingResolution("text/html", "delete user success");
    }
	
	private Boolean isExternalLoginUserAdmin(){
		if (email == null || email.isEmpty() || password == null || password.isEmpty())
			return Boolean.FALSE;
		
        Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);        
        if (account != null) {
        	Member member = memberDao.findByAccountId(account.getId());
        	if (member == null) {
        		return Boolean.FALSE;
        	} else {
        		try {
        			if (!PasswordHashUtil.validatePassword(password, member.getPassword())) {
        				return Boolean.FALSE;
        			}
        		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        			return Boolean.FALSE;
        		}
        	}
        } else {
        	return Boolean.FALSE;
        }
        
    	List<Attribute> attr = attributeDao.findByNameAndRefIds(AttributeType.AccessControl, "Access", account.getUserId());
        if (attr.size() > 0 && attr.get(0).getAttrValue().equals("Admin")) {
            return Boolean.TRUE;
        }  
		
        return Boolean.FALSE;
	}
}