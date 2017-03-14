package com.cyberlink.cosmetic.action.backend.user;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.jets3t.service.ServiceException;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.UserAccessControl;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.gcm.model.GCM;
import com.cyberlink.cosmetic.modules.gcm.model.GCMPayload;
import com.cyberlink.cosmetic.modules.gcm.model.Message;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserNameUpdateEvent;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.BrandType;
import com.cyberlink.cosmetic.modules.user.model.Device;
import com.cyberlink.cosmetic.modules.user.model.DeviceType;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.utils.IdGenerator;
import com.dbay.apns4j.IApnsService;
import com.dbay.apns4j.demo.Apns4jDemo;
import com.dbay.apns4j.impl.ApnsServiceImpl;
import com.dbay.apns4j.model.ApnsConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.json.JsonObject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/userEdit.action")
public class UserEditAction extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.AccountDao")
	private AccountDao accountDao;

    @SpringBean("file.fileService")
	private FileService fileService;

    @SpringBean("file.fileDao")
    private FileDao fileDao;

    @SpringBean("file.storageService")
	private StorageService storageService;

    @SpringBean("user.AttributeDao")
    private AttributeDao attributeDao;

    @SpringBean("post.PostAttributeDao")
    private PostAttributeDao postAttributeDao;

    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;

	@SpringBean("user.DeviceDao")
    private DeviceDao deviceDao;

	@SpringBean("notify.NotifyDao")
	private NotifyDao notifyDao;
    
    static final String editUserInfo = "/user/userEdit-edit.jsp" ;
    
    private PageResult<User> pageResult;
	private User user;
	private Device device;
	private String notifyTitle;
	private String notifyText;
	
	FileBean avatar;
	FileBean cover;

	private String uploadType;

	// search 
	private Long searchId;
	private String searchLocale;
	private GenderType searchGender;
    private UserType searchUserType;
    private String searchEmail;
    private Boolean isSearch = Boolean.FALSE;
    private Long searchUserAccess;
	
	// User 
    private Long userId;
    private String displayName;
    private String avatarUrl;
    private String coverUrl;
    private String websiteUrl;
    private String bgImageUrl;
    private String iconUrl;
    private Long bgImageId;
    private Long iconId;
	private GenderType gender;
    private Date birthDay;
    private UserType userType;
    private String description;
    private String region;
    private BrandType brandType; 
    
    private Boolean isAdmin = false;
	private Boolean isTargetAdmin = false;
	private Boolean isTargetExpert = false;
	private Long promoteScore = Long.valueOf(0);
	private UserAccessControl targetAccessControl = null;    
	
	@DefaultHandler
	public Resolution list() {
		if (isSearch) {
			if(searchId != null){
				PageLimit page = getPageLimit("row");
				pageResult = userDao.findUserByParameters(searchId,
						searchGender, searchUserType, searchLocale, null, null,
						Long.valueOf(page.getStartIndex()),
						Long.valueOf(page.getPageSize()));
			}else if (searchEmail != null && !searchEmail.isEmpty()) {
				PageLimit page = getPageLimit("row");
				pageResult = accountDao.findUserByEmail(searchEmail,
						Long.valueOf(page.getStartIndex()),
						Long.valueOf(page.getPageSize()));
			} else{
				PageLimit page = getPageLimit("row");
				pageResult = userDao.findUserByParameters(searchId,
						searchGender, searchUserType, searchLocale, null, null,
						Long.valueOf(page.getStartIndex()),
						Long.valueOf(page.getPageSize()),searchUserAccess);
			}
			List<User> userList = pageResult.getResults();
			for(User user : userList){
				if(user != null){
					List<Account> accountList = user.getAccountList();
					if(!accountList.isEmpty()){
						for(Account account:accountList){
							List<Member> memberList = account.getMember();
							for(Member member : memberList){
								String encryption = member.getEncryption();
								user.setEncryption(encryption);
							}
						}
					}
					List<Session> sessionList = user.getSessionList();
					if(!sessionList.isEmpty()){
						for(Session session : sessionList){
							String token = session.getToken();
							user.setToken(token);
							break;
						}
					}
				}
			}
		}
		return forward();
	}
    public Resolution edit() {
        if (userId != null && userDao.exists(userId)) {
        	user = userDao.findById(userId);
        } 
        /*if (avatarUrl != null)
        	user.setAvatarUrl(avatarUrl);*/
        if (displayName != null) {
			try {
				user.setDisplayName(URLDecoder.decode(displayName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
        }
        if (description != null) {
            try {
				user.setDescription(URLDecoder.decode(description, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        /*if (coverUrl != null)
            user.setCoverUrl(coverUrl);*/
        if (gender != null)
            user.setGender(gender);
        if (birthDay != null)
            user.setBirthDay(birthDay);
        if (userType != null)
            user.setUserType(userType);
        if (region != null)
            user.setRegion(region);
        isAdmin = getCurrentUserAdmin();
        if (userId != null) {
        	List<Attribute> attr = attributeDao.findByNameAndRefIds(AttributeType.AccessControl, "Access", userId);
            if (attr.size() > 0 && attr.get(0).getAttrValue().equals("Admin")) {
            	isTargetAdmin = true;
            }    
            if (user != null) {
            	isTargetExpert = true;
        		PostAttribute pAttr = postAttributeDao.getPromoteScoreByUserId(userId);
        		if (pAttr != null) {
        			promoteScore = pAttr.getAttrValue();
        		}
            }
        }
        if (user != null) {
        	websiteUrl = user.getWebsiteUrl();
        	bgImageUrl = user.getBgImageUrl();
        	iconUrl = user.getIconUrl();
        	
        	String moreInfo = user.getMoreInfo();
        	JsonObject moreInfoObj = new JsonObject(moreInfo);
        	if (moreInfoObj.has("brandType")) {
        		switch (moreInfoObj.getString("brandType")) {
        			case "LUXURY":
        				brandType = BrandType.LUXURY;
        				break;
        			default:
        				brandType = BrandType.BEAUTY;
        				break;
        		}
        	}
        }

        return forward();
    }

    public Resolution access() {
		if (userId != null) {
			user = userDao.findById(userId);
		}    	
        targetAccessControl = new UserAccessControl(user);
        return forward();
    }

    public Resolution sendNotify() {
		if (userId != null) {
			user = userDao.findById(userId);
			Set<Long> ids = new HashSet<Long>();
			ids.add(userId);
			Map<Long, Device> deviceList = deviceDao.findNotifyDeviceByUserIds(ids);
			device = deviceList.get(userId);
		}    	
        return forward();
    }
    public Resolution send() {
		if (userId != null && notifyTitle != null && notifyText != null) {
			user = userDao.findById(userId);
			Set<Long> ids = new HashSet<Long>();
			ids.add(userId);
			Map<Long, Device> deviceList = deviceDao.findNotifyDeviceByUserIds(ids);
			device = deviceList.get(userId);
			if (device == null) {
				return new RedirectResolution(UserEditAction.class, "list");
			}
			if (device.getDeviceType() == DeviceType.iOS) {
				logger.debug(String.format("Send Apns notify, userId:%s,", userId));
				IApnsService apnsService = getApnsService(device.getApp());
				com.dbay.apns4j.model.Payload payload = new com.dbay.apns4j.model.Payload();
				payload.setAlert(notifyText);	
				payload.setBadge(1);
				payload.addParam("Title", notifyTitle);
				payload.addParam("MsgType", "BC");
			    payload.addParam("Ntype", "Y1");
			    payload.addParam("Link", "ybc://post/131157212784494507");
		        try {
		        	Long nid = IdGenerator.generate(Constants.getShardId());
					payload.addParam("Nid", nid.toString());
			    } catch (Exception e) {
			    	logger.error(e.getMessage());
			    }
				apnsService.sendNotification(device.getApnsToken(), payload);			
			} else {
				logger.debug(String.format("Send GCM notify, userId:%s,", userId));
				GCM gcm = getGCM(device.getApp());
				GCMPayload payload = new GCMPayload();
				Message data = new Message();
				data.setMsg(notifyText);
				data.setTickerText(notifyTitle);
				data.setTitle(notifyTitle);
				data.setLink("ybc://post/131157212784494507");
				data.setNtype("Y1");
		        try {
		           Long nid = IdGenerator.generate(Constants.getShardId());
		           data.setNid(nid.toString());
		        } catch (Exception e) {
		            logger.error(e.getMessage());
		        }
				data.setMsgType("BC");
				payload.setData(data);
				
				List<String> registration_ids = new ArrayList<String>();
				registration_ids.add(device.getApnsToken());
				payload.setRegistration_ids(registration_ids);
				try {
					gcm.push(payload);
				} catch (Exception e) {
					logger.error(String.format("send GCM notify error, userId:%s, tittle:%s, Msg:%s"), userId, notifyTitle, notifyText);
					logger.error(e.getMessage());
				}
			}

		}    	
        return new RedirectResolution(UserEditAction.class, "list");
    }
    private GCM getGCM(String app) {
		if ("YMK".equalsIgnoreCase(app)) {
	    	return new GCM(new DefaultHttpClient(), "AIzaSyA18HpyZW3fRF1iQFaVkNu2LwfCGok5YJ4"); 
		} else if ("YCN".equalsIgnoreCase(app)) {
	    	return new GCM(new DefaultHttpClient(), "AIzaSyCGa8s046e4Fh9JrwOUhsJ_vi4rUZSL_tY");
		} else if ("YBC".equalsIgnoreCase(app)) {
	    	return new GCM(new DefaultHttpClient(), "AIzaSyAQsOLq9EAL_HRgh1GQSjVK2n7rphhrfNE"); 
		} else {
	    	return new GCM(new DefaultHttpClient(), "AIzaSyAfKVawPKwFIcK9l0E3IvV07YAfepZWPtc"); 
		}
    }
    private IApnsService getApnsService(String app) {
        ApnsConfig config = new ApnsConfig();
        InputStream is = null;
        if ("YMK".equalsIgnoreCase(app)) {
        	is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YMK"));
    		if (Constants.getIsApnsDevEnv()) {
    			config.setName("dev-env-ymk");
    		} else {
    			config.setName("product-env-ymk");
    		}
        } else if ("YCN".equalsIgnoreCase(app)) {
        	is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YCN"));
    		if (Constants.getIsApnsDevEnv()) {
    			config.setName("dev-env-ycn");
    		} else {
    			config.setName("product-env-ycn");
    		}
        } else if ("YBC".equalsIgnoreCase(app)) {
        	is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YBC"));
    		if (Constants.getIsApnsDevEnv()) {
    			config.setName("dev-env-ybc");
    		} else {
    			config.setName("product-env-ybc");
    		}
        } else {
        	is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YCP"));
    		if (Constants.getIsApnsDevEnv()) {
    			config.setName("dev-env-ycp");
    		} else {
    			config.setName("product-env-ycp");
    		}
        }
        config.setKeyStore(is);
        config.setDevEnv(Constants.getIsApnsDevEnv());
        config.setPassword("Cl23829868");
        config.setPoolSize(1);
	    return ApnsServiceImpl.createInstance(config);
    }
    
    public Resolution saveAccess() {
        if (userId == null) {
        	return new RedirectResolution(UserEditAction.class, "list");
        } 
        Attribute attr = attributeDao.findOneByRefIdAndName(AttributeType.AccessControl, userId, "AccessMap");
        if (attr == null) {
        	attr = new Attribute();
        }
        attr.setAttrName("AccessMap");
        attr.setAttrValue(Long.toHexString(getTargetAccessControl().getAccessMap()));
        attr.setRefId(userId);
        attr.setRefType(AttributeType.AccessControl);
        attributeDao.update(attr);
        return new RedirectResolution(UserEditAction.class, "list");
    }

    public Resolution save() {
        if (userId != null) {
        	
        	Long avatarId = null;
        	if (user.getAvatarId() != null)
        		avatarId = user.getAvatarId();
        	Long coverId = null;
        	if (user.getCoverId() != null)
        		coverId = user.getCoverId();
        	User oriUser = userDao.findById(userId);
        	String oldName = oriUser.getDisplayName();
        	oriUser.setDisplayName(user.getDisplayName());
        	Boolean isNameUpdated = (user.getDisplayName() != null && !user.getDisplayName().equalsIgnoreCase(oldName)) ? true : false;
        	/*oriUser.setAvatarUrl(user.getAvatarUrl());
        	oriUser.setCoverUrl(user.getCoverUrl());*/
        	oriUser.setRegion(user.getRegion());
        	oriUser.setBirthDay(user.getBirthDay());
        	oriUser.setDescription(user.getDescription());
        	oriUser.setGender(user.getGender());
			PostAttribute pAttr = postAttributeDao
					.getPromoteScoreByUserId(userId);
			if (pAttr == null) {
				try {
					pAttr = new PostAttribute();
					pAttr.setRefType("User");
					pAttr.setRefId(userId);
					pAttr.setAttrType(PostAttrType.PromoteScore);
					pAttr.setAttrValue(promoteScore);
					postAttributeDao.create(pAttr);
				} catch (Exception ex) {

				}
			} else {
				pAttr.setAttrValue(promoteScore);
				postAttributeDao.update(pAttr);
			}

        	oriUser.setUserType(user.getUserType());
        	if (avatarId != null) {
        		oriUser.setAvatarId(avatarId);
            	Long [] ids = new Long[1];
                ids[0] = avatarId;
                List<FileItem> listResults = fileItemDao.findThumbnails(ids, ThumbnailType.Avatar);
                if(listResults.size() > 0)
                	oriUser.setAvatarLink(listResults.get(0).getOriginalUrl());
                else
                	oriUser.setAvatarLink(null);
                
                // avatar detail
                FileItem ori = fileItemDao.findOriginal(avatarId);
                if (ori != null && ori.getIsOriginal())
                	oriUser.setAvatarOri(ori.getOriginalUrl());
                else
                	oriUser.setAvatarOri(null);
        		
        	}
        	if (coverId != null) {
        		oriUser.setCoverId(coverId);
        	}
        	if (websiteUrl != null)
        		oriUser.setStringInAttr("websiteUrl", websiteUrl);
        	if (bgImageId != null) {
        		FileItem bgImage = fileItemDao.findOriginal(bgImageId);
    			bgImageUrl = bgImage.getOriginalUrl();
    			oriUser.setStringInAttr("bgImageUrl", bgImageUrl);
    			bgImageId = null;
        	} else if (bgImageUrl != null){
        		oriUser.setStringInAttr("bgImageUrl", bgImageUrl);
        	}
        	if (iconId != null) {
        		FileItem icon = fileItemDao.findOriginal(iconId);
        		iconUrl = icon.getOriginalUrl();
    			oriUser.setStringInAttr("iconUrl", iconUrl);
    			iconId = null;
        	} else if (iconUrl != null){
        		oriUser.setStringInAttr("iconUrl", iconUrl);
        	}
        	String moreInfo = user.getMoreInfo();
        	JsonObject moreInfoObj = new JsonObject(moreInfo);
        	if (brandType != null) {
        		moreInfoObj.put("brandType", brandType.toString());
        		oriUser.setStringInAttr("moreInfo", moreInfoObj.toString());
        	}
        		
        	userDao.update(oriUser);
        	if (isNameUpdated)
            	publishDurableEvent(new UserNameUpdateEvent(oriUser.getId(), oriUser.getDisplayName()));
        	if (getCurrentUserAdmin()) {
        		List<Attribute> attr = attributeDao.findByNameAndRefIds(AttributeType.AccessControl, "Access", userId);
        		Attribute a;
        		if (attr.size() > 0)
        			a = attr.get(0);
        		else 
        			a = new Attribute();
        		a.setRefId(userId);
        		a.setRefType(AttributeType.AccessControl);
        		a.setAttrName("Access");
        		if (isTargetAdmin != null && isTargetAdmin)
        			a.setAttrValue("Admin");
        		else
        			a.setAttrValue("Normal");
        		attributeDao.update(a);   
        	}
        }
    	return new RedirectResolution(UserEditAction.class, "list");
    }
    public Resolution uploadCover(){
    	uploadType="cover";
    	return upload();
    }
    public Resolution upload(){
        if (uploadType.equals("cover"))
        	avatar = cover;
    	if (avatar == null) {    		
        	try {
        		RedirectResolution r = new RedirectResolution(UserEditAction.class,"edit")
    						.addParameter("avatarUrl", user.getAvatarUrl())
    						.addParameter("userId", userId)
    						.addParameter("birthDay", user.getBirthDay())
    						.addParameter("coverUrl", user.getCoverUrl())
    						.addParameter("gender", user.getGender())
    						.addParameter("region", user.getRegion());
    			if (user.getDescription() != null)
    				r.addParameter("description", URLEncoder.encode(user.getDescription(), "UTF-8"));
    			if (user.getDisplayName() != null)
    				r.addParameter("displayName", URLEncoder.encode(user.getDisplayName(), "UTF-8"));
    			return r;
        	} catch (UnsupportedEncodingException e) {
    			return new RedirectResolution(UserEditAction.class,"edit");
    		}
    	}
    		
        FileItem fileItem = new FileItem();
        
        String metadata = getMetadata();
        fileItem.setMetadata(metadata);
        JsonNode node = fileItem.getMetadataJson();
        JsonNode tempNode;              
        if (node != null) {
            tempNode = node.path("fileSize");
            if (!tempNode.isMissingNode()) fileItem.setFileSize(tempNode.asLong());
            
            tempNode = node.path("md5");
            if (!tempNode.isMissingNode()) fileItem.setMd5(tempNode.asText());

            tempNode = node.path("width");
            if (!tempNode.isMissingNode()) fileItem.setWidth(tempNode.asInt());
            
            tempNode = node.path("height");
            if (!tempNode.isMissingNode()) fileItem.setHeight(tempNode.asInt());
            
            tempNode = node.path("orientation");
            if (!tempNode.isMissingNode()) fileItem.setOrientation(tempNode.asInt());
        }
                      
        try {
            String fileName = avatar.getFileName();
            java.io.File file;                 
            File fileEntity = new File();
            
            fileEntity.setUserId(userId);
            if (uploadType.equals("cover"))
            	fileEntity.setFileType(FileType.Photo);
            else
            	fileEntity.setFileType(FileType.Avatar);
            fileEntity.getFileItems().add(fileItem);
            
            fileItem.setFile(fileEntity);
            fileItem.setFilePath(fileService.getFilePath(userId, fileEntity.getFileType(), fileName));
            fileItem.setFileName(fileName);
            fileItem.setContentType(avatar.getContentType());
            fileItem.setIsOriginal(true);

            file = new java.io.File(fileItem.getLocalFilePath());
            file.getParentFile().mkdirs();
            avatar.save(file);
            getContext().getRequest().setAttribute(Constants.PARAM_CURRENT_USER_ID, userId);
            fileEntity = fileDao.create(fileEntity);
            
            storageService.uploadFile(fileItem);
   
        } catch (IOException | NoSuchAlgorithmException | ServiceException e) {
            e.printStackTrace();
            return new StreamingResolution("text/html", "Upload Failed"); 
        }
        /*if (uploadType.equals("cover"))
        	user.setCoverUrl(fileItem.getOriginalUrl());
        else
        	user.setAvatarUrl(fileItem.getOriginalUrl());*/
        
    	try {
    		RedirectResolution r = new RedirectResolution(UserEditAction.class,"edit")
						.addParameter("avatarUrl", user.getAvatarUrl())
						.addParameter("userId", userId)
						.addParameter("birthDay", user.getBirthDay())
						.addParameter("coverUrl", user.getCoverUrl())
						.addParameter("gender", user.getGender())
						.addParameter("region", user.getRegion());
			if (user.getDescription() != null)
				r.addParameter("description", URLEncoder.encode(user.getDescription(), "UTF-8"));
			if (user.getDisplayName() != null)
				r.addParameter("displayName", URLEncoder.encode(user.getDisplayName(), "UTF-8"));
			return r;
    	} catch (UnsupportedEncodingException e) {
			return new RedirectResolution(UserEditAction.class,"edit");
		}
    }
    private String getMetadata(){
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("fileSize", avatar.getSize());
        InputStream is = null;
        byte[] md5Bytes = null;
        String md5 = "";
        try {
        	is = avatar.getInputStream();
            md5Bytes = DigestUtils.md5(is);
            md5 = Hex.encodeHexString(md5Bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        attributes.put("md5", md5);
        
        try {
			is = avatar.getInputStream();
	        BufferedImage buf = ImageIO.read(is);
	        attributes.put("width", buf.getWidth());
	        attributes.put("height", buf.getHeight());
	        attributes.put("orientation", 0);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return new ObjectMapper().writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
        }
        return "";
    	
    }
    public Resolution cancel() {/* (3) */
    	return new RedirectResolution(UserEditAction.class, "list");
    }

	public PageResult<User> getPageResult() {
		return pageResult;
	}
	public void setPageResult(PageResult<User> pageResult) {
		this.pageResult = pageResult;
	}
    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
    public FileBean getAvatar() {
        return avatar;
    }
    public void setAvatar(FileBean avatar) {
        this.avatar = avatar;
    }
	public FileBean getCover() {
		return cover;
	}
	public void setCover(FileBean cover) {
		this.cover = cover;
	}    
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getCoverUrl() {
		return coverUrl;
	}
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		if (websiteUrl == null)
			this.websiteUrl = "";
		else
			this.websiteUrl = websiteUrl;
	}
	public String getBgImageUrl() {
		return bgImageUrl;
	}
	public void setBgImageUrl(String bgImageUrl) {
		this.bgImageUrl = bgImageUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public Long getBgImageId() {
		return bgImageId;
	}
	public void setBgImageId(Long bgImageId) {
		this.bgImageId = bgImageId;
	}
	public Long getIconId() {
		return iconId;
	}
	public void setIconId(Long iconId) {
		this.iconId = iconId;
	}
	public GenderType getGender() {
		return gender;
	}
	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	public Date getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}
	public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getUploadType() {
		return uploadType;
	}
	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}
    public Boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public Boolean getIsTargetAdmin() {
		return isTargetAdmin;
	}
	public void setIsTargetAdmin(Boolean isTargetAdmin) {
		this.isTargetAdmin = isTargetAdmin;
	}
	public Boolean getIsTargetExpert() {
		return isTargetExpert;
	}
	public void setIsTargetExpert(Boolean isTargetExpert) {
		this.isTargetExpert = isTargetExpert;
	}	
	public Long getPromoteScore() {
		return promoteScore;
	}
	public void setPromoteScore(Long promoteScore) {
		this.promoteScore = promoteScore;
	}	
	public Long getSearchId() {
		return searchId;
	}
	public void setSearchId(Long searchId) {
		this.searchId = searchId;
	}	
	public String getSearchLocale() {
		return searchLocale;
	}
	public void setSearchLocale(String searchLocale) {
		this.searchLocale = searchLocale;
	}
	public GenderType getSearchGender() {
		return searchGender;
	}
	public void setSearchGender(GenderType searchGender) {
		this.searchGender = searchGender;
	}
	public UserType getSearchUserType() {
		return searchUserType;
	}
	public void setSearchUserType(UserType searchUserType) {
		this.searchUserType = searchUserType;
	}
	public String getSearchEmail() {
		return searchEmail;
	}
	public void setSearchEmail(String searchEmail) {
		this.searchEmail = searchEmail;
	}
	public Boolean getIsSearch() {
		return isSearch;
	}
	public void setIsSearch(Boolean isSearch) {
		this.isSearch = isSearch;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public String getNotifyTitle() {
		return notifyTitle;
	}
	public void setNotifyTitle(String notifyTitle) {
		this.notifyTitle = notifyTitle;
	}
	public String getNotifyText() {
		return notifyText;
	}
	public void setNotifyText(String notifyText) {
		this.notifyText = notifyText;
	}
	public BrandType getBrandType() {
		return brandType;
	}
	public void setBrandType(BrandType brandType) {
		this.brandType = brandType;
	}
	
	public Boolean getUserManagerAccess() {
		return getTargetAccessControl().getUserManagerAccess();
	}
	public void setUserManagerAccess(Boolean b) {
		if (b == null)
			b = Boolean.FALSE;
		getTargetAccessControl().setUserManagerAccess(b);
	}
	public Boolean getPostManagerAccess() {
		return getTargetAccessControl().getPostManagerAccess();
	}
	public void setPostManagerAccess(Boolean b) {
		if (b == null)
			b = Boolean.FALSE;
		getTargetAccessControl().setPostManagerAccess(b);
	}
	public Boolean getCircleManagerAccess() {
		return getTargetAccessControl().getCircleManagerAccess();
	}
	public void setCircleManagerAccess(Boolean b) {
		if (b == null)
			b = Boolean.FALSE;
		getTargetAccessControl().setCircleManagerAccess(b);
	}
	public Boolean getProductManagerAccess() {
		return getTargetAccessControl().getProductManagerAccess();
	}
	public void setProductManagerAccess(Boolean b) {
		if (b == null)
			b = Boolean.FALSE;
		getTargetAccessControl().setProductManagerAccess(b);
	}
	public Boolean getReportManagerAccess() {
		return getTargetAccessControl().getReportManagerAccess();
	}
	public void setReportManagerAccess(Boolean b) {
		if (b == null)
			b = Boolean.FALSE;
		getTargetAccessControl().setReportManagerAccess(b);
	}
	public Boolean getReportAuditorAccess() {
        return getTargetAccessControl().getReportAuditorAccess();
    }
    public void setReportAuditorAccess(Boolean b) {
        if (b == null)
            b = Boolean.FALSE;
        getTargetAccessControl().setReportAuditorAccess(b);
    }
    public Boolean getEventManagerAccess() {
        return getTargetAccessControl().getEventManagerAccess();
    }
    public void setEventManagerAccess(Boolean b) {
        if (b == null)
            b = Boolean.FALSE;
        getTargetAccessControl().setEventManagerAccess(b);
    }
    public Boolean getApkManagerAccess() {
        return getTargetAccessControl().getApkManagerAccess();
    }
    public void setApkManagerAccess(Boolean b) {
        if (b == null)
            b = Boolean.FALSE;
        getTargetAccessControl().setApkManagerAccess(b);
    }
    
	public UserAccessControl getTargetAccessControl() {
		if (targetAccessControl == null) {
			if (user == null && userId != null) {
				user = userDao.findById(userId);
			}
			targetAccessControl = new UserAccessControl(user);
		}
		return targetAccessControl;
	}
	public void setTargetAccessControl(UserAccessControl targetAccessControl) {
		if (this.targetAccessControl == null) {		
			this.targetAccessControl = targetAccessControl;
		}	
	}
	public Long getSearchUserAccess() {
		return searchUserAccess;
	}
	public void setSearchUserAccess(Long searchUserAccess) {
		this.searchUserAccess = searchUserAccess;
	}	
}
