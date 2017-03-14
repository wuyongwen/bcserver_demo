package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import com.cyberlink.cosmetic.modules.user.dao.*;
import com.cyberlink.cosmetic.modules.user.model.*;
import weibo4j.Users;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.common.service.GeoIPService;
import com.cyberlink.cosmetic.modules.facebook.service.FacebookService;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.sms.repository.PhoneRegistrationRepository;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.qq.connect.QQConnectException;
//import com.qq.connect.api.OpenID;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.RawAPIResponse;
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONObject;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/sign-in.action")
public class SignInAction extends AbstractAction{
    protected AccountSourceType accountSource;
    protected String accountToken;
    protected String apnsType;
    protected String apnsToken;
    protected String uuid = "";
    protected String locale = "en_US";
    protected String userEmail;
    protected String app;
    protected String openId;
    protected String ipAddress;
    
	@SpringBean("user.SessionDao")
	protected SessionDao sessionDao;

    @SpringBean("user.UserDao")
    protected UserDao userDao;

    @SpringBean("user.AccountDao")
    protected AccountDao accountDao;

	@SpringBean("user.DeclineDao")
	protected DeclineDao declineDao;

    @SpringBean("user.AttributeDao")
    protected AttributeDao attributeDao;
    
    @SpringBean("facebook.facebookService")
    private FacebookService facebookService;

    @SpringBean("user.DeviceDao")
    protected DeviceDao deviceDao;
    
    @SpringBean("file.fileDao")
    protected FileDao fileDao;
    
    @SpringBean("common.geoIPService")
    protected GeoIPService geoIPService;
    
    @SpringBean("user.userService")
    protected UserService userService;
    
    @SpringBean("sms.phoneRegistrationRepository")
    protected PhoneRegistrationRepository phoneRegistrationRepository;
    
    protected class Friend {
    	public String name;
    	public String id;
    	Friend(String id, String name) {
    		this.name = name;
    		this.id = id;
    	}
    }
    @DefaultHandler
    public Resolution route() {
    	RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        /*if (uuid!= null && uuid.length() > 0 && blockDao.isBlockedUuid(uuid)) {
        	return new ErrorResolution(ErrorDef.DeviceBlocked);
        }*/
        
        final Map<String, Object> results = new HashMap<String, Object>();
        String accountRef = null;

        Facebook facebook = null;
        List<String> accountRefList = null;
        List<Friend> friendList = null;
        String name = "";

        if (accountSource == AccountSourceType.Email) {
        	return new ErrorResolution(ErrorDef.InvalidAccountSource);
        } else if (accountSource == AccountSourceType.Facebook){
        	try {
        		facebook = new FacebookFactory().getInstance();
        		facebook.setOAuthAppId("", "");
        		facebook.setOAuthAccessToken(new AccessToken(accountToken));
        		
        		RawAPIResponse resMe = facebook.callGetAPI("me");
        		JSONObject jsonObject = resMe.asJSONObject();
        		accountRef = jsonObject.getString("id");
        		name = jsonObject.getString("name");
            	try {
            		friendList = getFBFriend(facebook);
            	} catch (Exception e)  {        			
            	}
        		try {
        			userEmail =  jsonObject.getString("email");
        		} catch (Exception e)  {        			
        		}
        	} catch (Exception e){
        		return new ErrorResolution(ErrorDef.InvalidAccountToken);
        	}
        } else if (accountSource == AccountSourceType.Weibo){
        	weibo4j.Account am = new weibo4j.Account(accountToken);
    		try {
    			accountRef = am.getUid().getString("uid");
    			Users um = new Users(accountToken);
    			try {
    				weibo4j.model.User user = um.showUserById(accountRef);
    				name = user.getName();
    				friendList = getWeiboFriend(accountToken, accountRef);
    			} catch (WeiboException e) {
    			}
    		} catch (WeiboException e) {
    			return new ErrorResolution(ErrorDef.InvalidAccountToken);
    		} catch (JSONException e) {
    			return new ErrorResolution(ErrorDef.InvalidAccountToken);
    		}
        	
        } else if (accountSource == AccountSourceType.Qq || accountSource == AccountSourceType.Wechat || accountSource == AccountSourceType.Twitter){
        	if (openId == null || openId.isEmpty())
        		return new ErrorResolution(ErrorDef.InvalidAccountToken);
        	accountRef = openId;
        } else if (accountSource == AccountSourceType.Phone) {
        	accountRef = phoneVerify(uuid, accountToken);
        	if (accountRef == null || accountRef.isEmpty())
        		return new ErrorResolution(ErrorDef.InvalidAccountToken);
        } else {
        	return new ErrorResolution(ErrorDef.InvalidAccountSource);
        }
        	
        Account account = accountDao.findBySourceAndReference(accountSource, accountRef);
        User user = null;
        Long userId;
        
        if (account == null) {
    		if (accountSource == AccountSourceType.Facebook) {
    			if (facebook == null)  {
    				facebook = new FacebookFactory().getInstance();
            		facebook.setOAuthAppId("", "");
            		facebook.setOAuthAccessToken(new AccessToken(accountToken));    			
    			}
    			accountRefList = new ArrayList<String>();
				try {
					RawAPIResponse res = facebook.callGetAPI("/v2.0/me/ids_for_business");
	    			JSONObject jsonObject = res.asJSONObject();
	    			JSONArray jsonArray = jsonObject.getJSONArray("data");
	    			for (int i = 0; i < jsonArray.length(); i++) {
	    				JSONObject jObject = jsonArray.getJSONObject(i);
	    				String id = jObject.getString("id");
	    				if (!accountRef.equalsIgnoreCase(id)) {
	    					accountRefList.add(id);
	    				}
	    			}
	    			account = accountDao.findBySourceAndReference(accountSource, accountRefList.toArray(new String[accountRefList.size()]));
	    			if (account != null) {
	    				accountRefList = new ArrayList<String>();
	    				accountRefList.add(accountRef);
	    			}
				} catch (Exception e) {
					e.printStackTrace();
				}        		
    		} else if (accountSource == AccountSourceType.Wechat || accountSource == AccountSourceType.Weibo) {
    			account = getAccountFromDevice(uuid, accountSource, accountRef);
    		}
        }
        
    	if (account == null) {
            account = new Account();
            account.setAccount(accountRef);
            account.setAccountSource(accountSource);      
            account.setEmail(userEmail);
            account.setMailStatus(AccountMailStatus.SUBSCRIBE);
        } else {
        	userId = account.getUserId();
        	if (userDao.exists(userId))
        		user = userDao.findById(userId);
        	if (userEmail != null && account.getEmail() == null) {
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
            results.put("token", userService.getToken(userId, user.getUserType()));
            results.put("userInfo", user);
            if (friendList != null && friendList.size() > 0) {
            	updateFriendship(userId, accountRef, accountSource, friendList, name);
            }
        } else {
        	if (apnsType != null) {
        		if (apnsType.equalsIgnoreCase("gcm")) {
        			user.setOs("Android");
        		} else if (apnsType.equalsIgnoreCase("apns")) {
        			user.setOs("iOS");
        		}
        	} 
            user.setApp(app); 
            user.setIpAddress(ipAddress);
            user = userDao.update(user);


            userId = user.getId();
			Decline decline = declineDao.findByTypeAndDeclineid("uid",String.valueOf(userId));

			if (decline != null){
				return new ErrorResolution(ErrorDef.UserBlocked);
			}
            if (friendList != null && friendList.size() > 0) {
            	updateFriendship(userId, accountRef, accountSource, friendList, name);
            }

            results.put("token", userService.getToken(userId,user.getUserType()));
            String mapAsJson = "{}";
            if (user.hasKeyInAttr("userAttr")) {
            	String userAttr = user.getStringInAttr("userAttr");
            	if (userAttr.length() > 0) {
            		mapAsJson = userAttr;
            	}
            } else {
            	final Map<String, Object> attributes = new HashMap<String, Object>();
            	for (Attribute attr : user.getAttributeList()) {
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
        if (accountSource == AccountSourceType.Facebook && accountRefList != null && accountRefList.size() > 0) {
        	for (String ref : accountRefList) {
                account = new Account();
                account.setAccount(ref);
                account.setAccountSource(accountSource);   
                account.setUserId(userId);
                account.setEmail(userEmail);
                account.setMailStatus(AccountMailStatus.SUBSCRIBE);
                account = accountDao.update(account);
        	}
        }
        //publishDurableEvent(new UserSignInEvent(userId, user.getRegion()));
        return json(results);    
    }
    
    protected void updateFriendship(Long userId, String accountRef, AccountSourceType accountSource, 
    		List<Friend> friendList, String name) {
    	/*List<String> reference = new ArrayList<String>();
    	Map<String, String> nameMap = new HashMap<String, String>();
    	for (Friend f: friendList) {
    		reference.add(f.id);
    		nameMap.put(f.id, f.name);
    	}
    	friendshipDao.updateBySourceId(accountRef, accountSource.toString(), userId, name);
    	Map<String, Long> accountMap = accountDao.findUserIdBySourceAndReference(accountSource, reference);
    	if (accountMap.keySet().size() > 0)
    		friendshipDao.createByUserIdsAndSourceId(new ArrayList<Long>(accountMap.values()), accountSource.toString(), accountRef, name, userId);
    	
    	friendshipDao.createByUserIdAndSourceIds(userId, accountSource.toString(), reference, nameMap, accountMap);*/
    }
    
    private List<Friend> getWeiboFriend(String accountToken, String id) {
    	List<Friend> friendList = new ArrayList<Friend>();
    	weibo4j.Friendships fm = new weibo4j.Friendships(accountToken);
		try {
			weibo4j.model.UserWapper users = fm.getFriendsByID(id, Long.valueOf(0),Long.valueOf(50) );
	    	for(weibo4j.model.User u : users.getUsers()){
	    		friendList.add(new Friend(u.getId(), u.getName()));
	    		//System.out.println(u.getId() + " : " + u.getName());
	    	}

			Long cursor = Long.valueOf(friendList.size());//users.getNextCursor();
			Long totalNum = users.getTotalNumber();
			for (;cursor < totalNum && cursor > 0; cursor = Long.valueOf(friendList.size())) {
				users = fm.getFriendsByID(id, cursor, Long.valueOf(50));
		    	for(weibo4j.model.User u : users.getUsers()){
		    		friendList.add(new Friend(u.getId(), u.getName()));
		    		//System.out.println(u.getId() + " : " + u.getName());
		    	}
		    	if (users.getUsers().size() == 0) {
		    		break;
		    	}
			}
		} catch (WeiboException e) {
			//e.printStackTrace();
		}
		return friendList;
    }    
    protected List<Friend> getFBFriend(Facebook facebook) {
		if (facebook == null)  {
			facebook = new FacebookFactory().getInstance();
    		facebook.setOAuthAppId("", "");
    		facebook.setOAuthAccessToken(new AccessToken(accountToken));    			
		}
		List<Friend> friendList = new ArrayList<Friend>();
		try {
			JSONArray data = null;
			String api = "/v2.0/me/friends";
			do {
				RawAPIResponse res = facebook.callGetAPI(api);
				JSONObject jsonObject = res.asJSONObject();
				data = jsonObject.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject jObject;
					try {
						jObject = data.getJSONObject(i);
						String id = jObject.getString("id");
						String name = jObject.getString("name");
						friendList.add(new Friend(id, name));
					} catch (facebook4j.internal.org.json.JSONException e) {
					}
				}
				api = "";
				try {
					api = jsonObject.getJSONObject("paging").getString("next");
					if (api.startsWith("https://graph.facebook.com")) {
						api = api.substring("https://graph.facebook.com".length());
					}
				}catch (Exception e) {
				}
			} while(data != null && data.length() > 0 && api.length() > 0);
		}  catch (Exception e) {
			
		}
		return friendList;
    }
        
    protected void updateUserDevice(Long userId) {
    	DeviceType deviceType = DeviceType.Android;
    	String apnsToken = "";
    	if (this.apnsToken != null)
    		apnsToken = this.apnsToken;
    	String uuid = "";
    	if (this.uuid != null)
    		uuid  = this.uuid;
    	
    	if ((apnsToken.length() == 0 || apnsType == null) && (uuid == null || uuid.length() == 0))
    		return;
    	
    	if (apnsType != null) {
    		if (apnsType.equalsIgnoreCase("gcm")) {
    			deviceType = DeviceType.Android;
    		} else if (apnsType.equalsIgnoreCase("apns")) {
    			deviceType = DeviceType.iOS;
    		} else {
    			apnsToken = "";
    		}
    	} else {
    		apnsToken = "";
    	}
    	Device device = deviceDao.findDeviceInfo(userId, uuid, deviceType, app);
    	if (device == null) {
    		device = new Device();
    		device.setShardId(userId);
    		device.setApnsToken(apnsToken);
    		device.setUserId(userId);
    		device.setDeviceType(deviceType);
    		device.setUuid(uuid);
    		device.setApp(app);
        	deviceDao.create(device);
    	} else {
    		Calendar cal = Calendar.getInstance();
    		cal.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    		device.setApnsToken(apnsToken);
    		device.setLastModified(cal.getTime());
    		device.setApp(app);
    		device.setIsDeleted(Boolean.FALSE);
        	deviceDao.update(device);
    	}
    }
    
	protected User createNewUser() {
		User user = new User();
		// user.setCoverFile(getDefaultCover());
		user.setUserType(UserType.Normal);
		user.setRegion(locale);
		user.setObjVersion(0);
		if (apnsType != null) {
			if (apnsType.equalsIgnoreCase("gcm")) {
				user.setOs("Android");
			} else if (apnsType.equalsIgnoreCase("apns")) {
				user.setOs("iOS");
			}
		}
		user.setApp(app);		
		user.setIpAddress(ipAddress);
		user = userDao.create(user);
		return user;
	}
    
    protected File getDefaultCover() {
        List<File> fileList =new ArrayList<File>();
        Long offset = Long.valueOf(0);
        Long limit = Long.valueOf(20);
        int totalSize = 0;
    	PageResult<File> pageResult = fileDao.findByFileType(FileType.DefaultUserCover, offset, limit);
    	fileList.addAll(pageResult.getResults());
    	totalSize = pageResult.getTotalSize();
    	for (offset = limit; offset < totalSize; offset += 20) {
    		pageResult = fileDao.findByFileType(FileType.DefaultUserCover, offset, limit);
    		fileList.addAll(pageResult.getResults());
    	}
    	if (fileList.size() > 0) {
        	int n = 0;
    		if (fileList.size() > 1) {
    			Random rand = new Random();
    			n = rand.nextInt(fileList.size());
    		}
    		return fileList.get(n);
    	}
    	return null;
    }
    
    private Account getAccountFromDevice(String uuid, AccountSourceType accountSource, String accountRef) {
    	if (uuid == null || accountSource == null ||  accountRef == null)
    		return null;
    	Device deivce = deviceDao.findDeviceInfo(uuid);
    	if (deivce == null)
    		return null;
    	
    	Long userId = deivce.getUserId();
    	List<Account> list = accountDao.findByUserId(userId);
    	if (list == null || list.isEmpty())
    		return null;
    	for(Account ac : list) {
    		if (accountSource.equals(ac.getAccountSource())) {
    			ac.setAccount(accountRef);
    			accountDao.update(ac);
    		}
    	}
    	return list.get(0);
    }
    
    private String phoneVerify(String uuid, String accountToken) {
    	try {
    		Map<String, String> register = phoneRegistrationRepository.getRegistration(uuid);
    		if (!register.get("accountToken").equals(accountToken))
    			return null;
    		
    		return register.get("countryCode") + register.get("phoneNumber");
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    	}
    	return null;
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
    
    public String getApnsType() {
        return apnsType;
    }
    
    public void setApnsType(String apnsType) {
        this.apnsType = apnsType;
    }
    
    public String getApnsToken() {
        return apnsToken;
    }
    
    public void setApnsToken(String apnsToken) {
        this.apnsToken = apnsToken;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
