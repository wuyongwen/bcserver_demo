package com.cyberlink.cosmetic.modules.notify.service.impl;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.gcm.model.GCM;
import com.cyberlink.cosmetic.modules.gcm.model.GCMPayload;
import com.cyberlink.cosmetic.modules.gcm.model.Message;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyEventDao;
import com.cyberlink.cosmetic.modules.notify.model.Notify;
import com.cyberlink.cosmetic.modules.notify.model.NotifyEvent;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Attachment;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.result.AttachmentWrapper;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.FriendshipDao;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Device;
import com.cyberlink.cosmetic.modules.user.model.DeviceType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.utils.CosmeticWorkQueue;
import com.dbay.apns4j.IApnsService;
import com.dbay.apns4j.demo.Apns4jDemo;
import com.dbay.apns4j.impl.ApnsServiceImpl;
import com.dbay.apns4j.model.ApnsConfig;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

public class NotifyServiceImpl extends AbstractService implements NotifyService {

    private UserDao userDao;
    private PostDao postDao;
    private NotifyDao notifyDao;
    private NotifyEventDao notifyEventDao;
	private CircleDao circleDao;
	private FriendshipDao friendshipDao;
	private SubscribeDao subscribeDao;
    private CommentDao commentDao;
    private DeviceDao deviceDao;
    private URLContentReader urlContentReader;
    private TransactionTemplate transactionTemplate;
    
    private RunnableUpdateUserDevice runableUpdateUser;
	private Thread executeThread;
    Queue<UrlRequest> urlQueue = new LinkedList<UrlRequest>();
    CosmeticWorkQueue workQueue = new CosmeticWorkQueue(1, "NotifyService");
    CosmeticWorkQueue eventWorkQueue = new CosmeticWorkQueue(1, "SendEventNotify");
    
    private GCM YCPGcm;
    private GCM YCNGcm;
    private GCM YMKGcm;
    private GCM YBCGcm;
    ApnsConfig YCPConfig;
    ApnsConfig YCNConfig;
    ApnsConfig YMKConfig;
    ApnsConfig YBCConfig;
	IApnsService YCPApns = null;
	IApnsService YCNApns = null;
    IApnsService YMKApns = null;
    IApnsService YBCApns = null;
    
    private static Long dropCount = 0L;
    private static Boolean bWriteNotifyEvent = Boolean.FALSE;
    
	private class UrlRequest {
		Map<String, String> params;
		String url;
		
		public Map<String, String> getParams() {
			return params;
		}
		
		public String getUrl() {
			return url;
		}
		
		UrlRequest(String url, Map<String, String> params) {
			this.params = params;
			this.url = url;
		}
	}

    private class RunnableUpdateUserDevice implements Runnable {
        private Boolean isRunning = Boolean.FALSE;
        
		public void stop() {
        	isRunning = Boolean.FALSE;
        }
        
		public void run() {
			if (isRunning)
				return;

			isRunning = Boolean.TRUE;
			UrlRequest p = null;
			do {
				try {		
					p = urlQueue.poll();
					if (p == null)
						break;
					urlContentReader.post(p.getUrl(), p.getParams());

				} catch(Exception e) {
				}
			} while(p != null);
			executeThread = null;
			isRunning = Boolean.FALSE;
		}
		   
		public void start() {
			if (executeThread == null) {
				executeThread = new Thread (this, "UpdateDevice");
				executeThread.start ();
			}
		}

		public Boolean getIsRunning() {
			return isRunning;
		}
	}
    
	private class RunnableAddFriendNotify implements Runnable {
		private String notifyType;
		private User sender;
		private Long refId;
		private String content;
		private String iconUrl;
		private List<Long> receiverList;
		private String accountSource;
		//private Thread t;
		private AttachmentWrapper postCover;
		
		RunnableAddFriendNotify(String notifyType, User sender,
				Long refId, String content,  Set<Long> receiverList, String accountSource, String iconUrl, AttachmentWrapper postCover){
			this.notifyType = notifyType;
			this.sender = sender;
	    	sender.getDisplayName();
	    	sender.getAvatarId();
			this.refId = refId;
			this.content = content;
			this.iconUrl = iconUrl;
			this.receiverList = new ArrayList<Long>(receiverList);
			this.accountSource = accountSource;
			this.postCover = postCover;
		}
		
		public void run() {		    
		    for(UserType ut : UserType.values()) {
		        if(UserType.getReceiveNotifyType().contains(ut))
		            continue;
		        List<Long> nonValidReceiver = userDao.findIdByUserType(ut, null); 
		        receiverList.removeAll(nonValidReceiver);
		    }
		    int maxCount = 100;
            while (receiverList.size() > 0) {
                int count = receiverList.size() > maxCount ? maxCount : receiverList.size();
                Set<Long> tmpSet = new HashSet<Long>(receiverList.subList(0, count));
                doFriendNotifyByType(notifyType, sender, refId, content, tmpSet, accountSource, iconUrl, postCover);
                receiverList.removeAll(tmpSet);
            }
		}
		   
		/*public void start () {
			if (t == null) {
				t = new Thread (this, "AddNotify-"+ accountSource + sender.getId());
		        t.start ();
			}
		}*/
	}
	
	private class RunnableSendEventNotify implements Runnable {
		List<EventUser> eventUserList;
		BrandEvent brandEvent;
		
		RunnableSendEventNotify(List<EventUser> eventUserList, BrandEvent brandEvent){
			this.eventUserList = eventUserList;
			this.brandEvent = brandEvent;
		}
		
		public void run() {
			doSendEventNotify(eventUserList, brandEvent);
		}
	}
	
	private class RunnableSendSOWNotify implements Runnable {
		List<Long> userIds;
		String locale;
		
		RunnableSendSOWNotify(List<Long> userIds, String locale) {
			this.userIds = userIds;
			this.locale = locale;
		}
		public void run() {
			doSendSOWNotify(userIds, locale);
		}
	}
	
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public URLContentReader getUrlContentReader() {
		return urlContentReader;
	}

	public void setUrlContentReader(URLContentReader urlContentReader) {
		this.urlContentReader = urlContentReader;
	}
	
    public DeviceDao getDeviceDao() {
		return deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	public CommentDao getCommentDao() {
		return commentDao;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public SubscribeDao getSubscribeDao() {
		return subscribeDao;
	}

	public void setSubscribeDao(SubscribeDao subscribeDao) {
		this.subscribeDao = subscribeDao;
	}

	public FriendshipDao getFriendshipDao() {
		return friendshipDao;
	}

	public void setFriendshipDao(FriendshipDao friendshipDao) {
		this.friendshipDao = friendshipDao;
	}

	public CircleDao getCircleDao() {
		return circleDao;
	}

	public void setCircleDao(CircleDao circleDao) {
		this.circleDao = circleDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public PostDao getPostDao() {
		return postDao;
	}

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	public NotifyDao getNotifyDao() {
		return notifyDao;
	}

	public void setNotifyDao(NotifyDao notifyDao) {
		this.notifyDao = notifyDao;
	}

	public void setNotifyEventDao(NotifyEventDao notifyEventDao) {
		this.notifyEventDao = notifyEventDao;
	}

	public GCM getYCPGcm() {
		return YCPGcm;
	}

	public void setYCPGcm(GCM yMPGcm) {
		YCPGcm = yMPGcm;
	}

	public GCM getYCNGcm() {
		return YCNGcm;
	}

	public void setYCNGcm(GCM yCNGcm) {
		YCNGcm = yCNGcm;
	}

	public GCM getYMKGcm() {
		return YMKGcm;
	}

	public void setYMKGcm(GCM yMKGcm) {
		YMKGcm = yMKGcm;
	}

	public GCM getYBCGcm() {
		return YBCGcm;
	}

	public void setYBCGcm(GCM yBCGcm) {
		YBCGcm = yBCGcm;
	}

	public ApnsConfig getYCPConfig() {
		return YCPConfig;
	}

	public void setYCPConfig(ApnsConfig yCPConfig) {
		YCPConfig = yCPConfig;
		if (YCPApns == null) {
			InputStream is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YCP"));
			YCPConfig.setKeyStore(is);
			YCPConfig.setDevEnv(Constants.getIsApnsDevEnv());
			if (YCPConfig.isDevEnv()) {
				YCPConfig.setName("dev-env-ycp");
			} else {
				YCPConfig.setName("product-env-ycp");
			}
			YCPConfig.setPassword(Constants.getCertPass());
			YCPConfig.setPoolSize(1);
			YCPApns = ApnsServiceImpl.createInstance(YCPConfig);
		}
	}

	public ApnsConfig getYCNConfig() {
		return YCNConfig;
	}

	public void setYCNConfig(ApnsConfig yCNConfig) {
		YCNConfig = yCNConfig;
		if (YCNApns == null) {
			InputStream is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YCN"));
			YCNConfig.setKeyStore(is);
			YCNConfig.setDevEnv(Constants.getIsApnsDevEnv());
			if (YCNConfig.isDevEnv()) {
				YCNConfig.setName("dev-env-ycn");
			} else {
				YCNConfig.setName("product-env-ycn");
			}
			YCNConfig.setPassword(Constants.getCertPass());
			YCNConfig.setPoolSize(1);
			YCNApns = ApnsServiceImpl.createInstance(YCNConfig);
		}
	}

	public ApnsConfig getYMKConfig() {
		return YMKConfig;
	}

	public void setYMKConfig(ApnsConfig yMKConfig) {
		YMKConfig = yMKConfig;
		if (YMKApns == null) {
			InputStream is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YMK"));
			YMKConfig.setKeyStore(is);
			YMKConfig.setDevEnv(Constants.getIsApnsDevEnv());
			if (YMKConfig.isDevEnv()) {
				YMKConfig.setName("dev-env-ymk");
			} else {
				YMKConfig.setName("product-env-ymk");
			}
			YMKConfig.setPassword(Constants.getCertPass());
			YMKConfig.setPoolSize(1);
			YMKApns = ApnsServiceImpl.createInstance(YMKConfig);
		}	
	}
	
	public ApnsConfig getYBCConfig() {
		return YBCConfig;
	}

	public void setYBCConfig(ApnsConfig yBCConfig) {
		YBCConfig = yBCConfig;
		if (YBCApns == null) {
			InputStream is = Apns4jDemo.class.getClassLoader().getResourceAsStream(Constants.getCert("YBC"));
			YBCConfig.setKeyStore(is);
			YBCConfig.setDevEnv(Constants.getIsApnsDevEnv());
			if (YBCConfig.isDevEnv()) {
				YBCConfig.setName("dev-env-ybc");
			} else {
				YBCConfig.setName("product-env-ybc");
			}
			YBCConfig.setPassword(Constants.getCertPass());
			YBCConfig.setPoolSize(1);
			YBCApns = ApnsServiceImpl.createInstance(YBCConfig);
		}	
	}

	private class CircleData implements Serializable{
		private static final long serialVersionUID = 8267464774299855684L;
		private String name;

		@JsonView(Views.Public.class)
		public String getName() {
			return name;
		}

		private String iconUrl;
		
		@JsonView(Views.Public.class)		
		public String getIconUrl() {
			return iconUrl;
		}

		public CircleData(String name, String iconUrl) {
			this.name = name; 
			this.iconUrl = iconUrl;
		}
	}
	
	@Override
	public void addNotifyByType(String notifyType, Long receiverId,
			Long senderId, Long refId, String content) {
		addNotifyByType(notifyType, receiverId, senderId, refId, content, null);
	}
	
	@Override
	public void addNotifyByType(String notifyType, Long receiverId,
			Long senderId, Long refId, String content, String iconUrl) {
		if (bWriteNotifyEvent)
			addNotifyEvent(notifyType, receiverId, senderId, refId, content, iconUrl);
		
		if (notifyType.equals(NotifyType.ReplyComment.toString())) {
			createReplyCommentNotify(senderId, receiverId, refId, content);
			return;
		}
		
		Notify notify = new Notify();
		String refContent = null;
    	List<AttachmentWrapper> files = new ArrayList<AttachmentWrapper>();
    	Post post = null;
    	
    	// post related
		if (notifyType.equals(NotifyType.LikePost.toString()) || notifyType.equals(NotifyType.CommentPost.toString()) || 
				notifyType.equals(NotifyType.CircleInPost.toString())) {
			post = postDao.findById(refId);
			receiverId = post.getCreatorId();
		}

		if (receiverId == null)
			return;
		else if (receiverId.longValue() == senderId)
			return;
		else if ( NotifyType.getYouWithoutCommentType().contains(notifyType) 
				&& !NotifyType.CircleInPost.toString().equalsIgnoreCase(notifyType)
				&& notifyDao.findIsGrouped(receiverId, refId, senderId, notifyType)){
    		return;
    	}

		Boolean isGroupBySender = NotifyType.getSenderGroupType().contains(notifyType);
    	
		User receiver = userDao.findById(receiverId);
		if(!UserType.getReceiveNotifyType().contains(receiver.getUserType()))
		    return;
		
    	User sender = userDao.findById(senderId);
    	String displayName = sender.getDisplayName();
    	
    	// Group Notify
    	Notify group = fiendGroupByType(receiverId, senderId, refId, notifyType);

    	Long groupNum = Long.valueOf(1);
    	String groupIds = "";
    	Date firstCreated = null;
    	if (group != null) {
    		groupNum = group.getGroupNum();
			groupIds = getGroupIds(group, groupNum, isGroupBySender);
			firstCreated = group.getFirstCreated();
    		if ((notifyType.equals(NotifyType.CommentPost.toString()) ||  notifyType.equals(NotifyType.CircleInPost.toString())) && 
    				notifyDao.findIsGrouped(receiverId, refId, senderId, notifyType)){
    			// not add group num
    		} else {
    			groupNum += 1;
    		}
    		group.setGroupNum(Long.valueOf(0));
    		//notifyDao.update(group);
    	}

    	// Set Attributes
		String mapAsJson = "";
    	final Map<String, Object> attributes = new HashMap<String, Object>();
    	if (notifyType.equals(NotifyType.CommentPost.toString())) {
    		addInAttributeList("commandList", content, Long.valueOf(5), group, attributes);
    	} 
    	if (!NotifyType.getFollowType().contains(notifyType) && !isGroupBySender) {
        	String name = displayName;
    		if (name == null)
    			name = "";
    		if ((notifyType.equals(NotifyType.CommentPost.toString()) || notifyType.equals(NotifyType.CircleInPost.toString())) 
    				&& group != null && group.getSenderId() == senderId.longValue())
    			name = null;
    		addInAttributeList("nameList", name, Long.valueOf(5), group, attributes);
    	}
    	try {
    		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
    	} catch (JsonProcessingException e) {
    	}
    	
    	// Set Reference Content
    	if (NotifyType.getPostType().contains(notifyType)) {
			if (group == null || notifyType.equals(NotifyType.AddPost.toString())) {
        		if (post == null)
        			post = postDao.findById(refId);
            	AttachmentWrapper postCover =  getPostCover(post);    	
            	//if (postCover != null) {
            	files.add(postCover);
            	//}			
			}
			
    		if (notifyType.equals(NotifyType.AddPost.toString())) {
    			refContent = getReferenceContent(files, group);
    			
    		} else {
    			if (group != null) {
    				refContent = group.getRefContent();
    			} else {
    				refContent = getReferenceContent(files, null);
    			}
    		}
    	} else if (NotifyType.getCircleType().contains(notifyType)) {
    		if (notifyType.equals(NotifyType.FollowCircle.toString()))
    			refContent = getReferenceContent(content, iconUrl, null);
    		else	
    			refContent = getReferenceContent(content, iconUrl, group);
    	} else {
    		refContent = getReferenceContent(displayName, group);
    	}
    	if(refContent != null){
	    	notify.setAttribute(mapAsJson);    	
	    	notify.setSenderName(displayName);
	    	notify.setGroupNum(groupNum);
	    	notify.setGroupIds(groupIds);
	    	notify.setNotifyType(notifyType);
	    	notify.setReceiverId(receiverId);
	    	notify.setAvatarId(sender.getAvatarId());
	    	notify.setSenderId(senderId);
	    	notify.setRefContent(refContent);
	    	notify.setRefId(refId);
	    	notify.setRegion(Constants.getNotifyRegion());
	    	if (firstCreated != null)
				notify.setFirstCreated(firstCreated);
	    	notifyDao.create(notify);	
	    	if (group != null) {
	    		notifyDao.deleteById(group.getId());
	    	}
    	}
	}
	
	private Notify createNewNotify(Long receiverId, User sender,
			String notifyType, Long refId, Long groupNum, String groupIds,
			String refContent, String mapAsJson, Date firstCreated, Notify group, Boolean bCreate) {
		if (refContent != null) {
			Notify notify = new Notify();
			notify.setAttribute(mapAsJson);
			notify.setSenderName(sender.getDisplayName());
			notify.setGroupNum(groupNum);
			notify.setGroupIds(groupIds);
			notify.setNotifyType(notifyType);
			notify.setReceiverId(receiverId);
			notify.setAvatarId(sender.getAvatarId());
			notify.setSenderId(sender.getId());
			notify.setRefContent(refContent);
			notify.setRefId(refId);
			notify.setRegion(Constants.getNotifyRegion());
			if (firstCreated != null)
				notify.setFirstCreated(firstCreated);
			if (bCreate)
				notifyDao.create(notify);
			if (group != null) {
				notifyDao.deleteById(group.getId());
			}
			return notify;
		}
		return null;
	}
	
	private Boolean checkReciver(Long receiverId, Long senderId) {
		if (receiverId == null || receiverId.longValue() == senderId)
			return false;
		User receiver = userDao.findById(receiverId);
		if(!UserType.getReceiveNotifyType().contains(receiver.getUserType()))
			return false;		
		return true;
	}
	
	private Boolean checkReciver(User receiver, Long senderId) {
		if (receiver == null || receiver.getId() == null || receiver.getId().longValue() == senderId)
			return false;
		if(!UserType.getReceiveNotifyType().contains(receiver.getUserType()))
			return false;		
		return true;
	}
	
	private void createReplyCommentNotify(Long senderId, Long refParentId, Long refId, String content) {
		Boolean bSendToPostOwner = Boolean.FALSE;
		Boolean bSendToCommentOwner = Boolean.FALSE;
		Long postOwnerId = null;
		Long commentOwnerId = null;	
		Post post = null;
		User postOwner = null;
		try {
			Comment parentComment = commentDao.findById(refParentId);
			if (!PostTargetType.POST.equals(parentComment.getRefType()))
				return;
			post = postDao.findById(parentComment.getRefId());
			commentOwnerId = parentComment.getCreatorId();
			postOwner = post.getCreator();
			postOwnerId = postOwner.getId();
		} catch (Exception e) {
		}
		if (postOwnerId == null || commentOwnerId == null)
			return;
		
		bSendToPostOwner = checkReciver(postOwner, senderId);
		bSendToCommentOwner = checkReciver(commentOwnerId, senderId);		
		if (!bSendToPostOwner && !bSendToCommentOwner)
			return;
		
		User sender = userDao.findById(senderId);
    	Long groupNum = Long.valueOf(1);
    	String groupIds = "";
    	Date firstCreated = null;
    	
    	String mapAsJson = "";
    	final Map<String, Object> attributes = new HashMap<String, Object>();
    	List<String> nameList = new ArrayList<String>(Arrays.asList(postOwner.getDisplayName()));
    	List<String> commandList = new ArrayList<String>(Arrays.asList(content));
    	attributes.put("commandList", commandList);
    	attributes.put("nameList", nameList);
    	attributes.put("commentId", refId);
    	try {
    		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
    	} catch (JsonProcessingException e) {
    	}
    	
    	AttachmentWrapper postCover =  getPostCover(post);    	
    	List<AttachmentWrapper> files = new ArrayList<AttachmentWrapper>(Arrays.asList(postCover));
		String refContent = getReferenceContent(files, null);
		
		if (bSendToPostOwner)
			createNewNotify(postOwnerId, sender, NotifyType.ReplyToPostOwner.toString(), refParentId, 
				groupNum, groupIds,	refContent, mapAsJson, firstCreated, null, true);
		if (bSendToCommentOwner)
			createNewNotify(commentOwnerId, sender, NotifyType.ReplyToCommentOwner.toString(), refParentId, 
				groupNum, groupIds,	refContent, mapAsJson, firstCreated, null, true);
	}
	
	@Override
	public void addFriendNotifyByType(String notifyType, Long senderId,
			Long refId, String content) {
		addFriendNotifyByType(notifyType, senderId, refId, content, null);
	
	}
	@Override
	public void addFriendNotifyByType(String notifyType, Long senderId,
			Long refId, String content, String iconUrl) {
		if (bWriteNotifyEvent)
			addNotifyEvent(notifyType, null, senderId, refId, content, iconUrl);
		if (NotifyType.JoinBCFromFB.toString().equalsIgnoreCase(notifyType) 
				|| NotifyType.JoinBCFromWeibo.toString().equalsIgnoreCase(notifyType))
			notifyType = NotifyType.JoinBC.toString();
		
		if (workQueue.getTaskCount() >= 5000) {
			workQueue.dropTask();
			if (dropCount.compareTo(Long.MAX_VALUE) < 0)
				dropCount++;
		}
		
    	User sender = userDao.findById(senderId);
    	Post post = null;
    	AttachmentWrapper postCover = null;    

    	if (NotifyType.getPostType().contains(notifyType)) {
			post = postDao.findById(refId);
	    	postCover = getPostCover(post);    

    	}     	
    	// Group Notify
    	Set<Long> followerList = new HashSet<Long>(subscribeDao.findBySubscribee(senderId, SubscribeType.User));
    	if (followerList.size() > 0) {
    		if (!notifyType.equalsIgnoreCase(NotifyType.JoinBC.toString())) {
    			RunnableAddFriendNotify rFollow = new RunnableAddFriendNotify(notifyType, sender, refId, content, 
    					followerList, "BC", iconUrl, postCover);
    			workQueue.execute(rFollow);
    			//rFollow.start();
    		}
    	}
    	
    	List<String> accountSource = new ArrayList<String>();
    	accountSource.add(AccountSourceType.Facebook.toString());
    	Set<Long> receiverList = friendshipDao.findUserIdByAccountSource(senderId, accountSource);    	
    	receiverList.removeAll(followerList);
    	if (receiverList.size() > 0) {
        	if (notifyType.equalsIgnoreCase(NotifyType.JoinBC.toString())) {
    			RunnableAddFriendNotify rFB = new RunnableAddFriendNotify(NotifyType.JoinBCFromFB.toString(), sender, refId, content, 
    					receiverList, AccountSourceType.Facebook.toString(), iconUrl, postCover);
    			workQueue.execute(rFB);
    			//rFB.start();
        	} else {
    			RunnableAddFriendNotify rFB = new RunnableAddFriendNotify(notifyType, sender, refId, content, 
    					receiverList, AccountSourceType.Facebook.toString(), iconUrl, postCover);
    			workQueue.execute(rFB);
    			//rFB.start();
        	}
    	}
    	accountSource = new ArrayList<String>();
    	accountSource.add(AccountSourceType.Weibo.toString());
    	receiverList = friendshipDao.findUserIdByAccountSource(senderId, accountSource);  
    	receiverList.removeAll(followerList);
    	if (receiverList.size() > 0) {
    		if (notifyType.equalsIgnoreCase(NotifyType.JoinBC.toString())) {
    			RunnableAddFriendNotify rWeibo = new RunnableAddFriendNotify(NotifyType.JoinBCFromWeibo.toString(), sender, refId, content, 
    					receiverList, AccountSourceType.Weibo.toString(), iconUrl, postCover);
    			workQueue.execute(rWeibo);
    			//rWeibo.start();
    		} else {
    			RunnableAddFriendNotify rWeibo = new RunnableAddFriendNotify(notifyType, sender, refId, content, 
    					receiverList, AccountSourceType.Weibo.toString(), iconUrl, postCover);
    			//rWeibo.start();
    			workQueue.execute(rWeibo);
    		}
    	}
	}

	@Override
	public void doGroupYouNotify(Date checkTime) {
		
	}
	
	private void addNotifyEvent(String notifyType, Long receiverId, Long senderId, Long refId, String content, String iconUrl) {
		if (senderId == null)
			return;
		
		User sender = userDao.findById(senderId);
		Boolean isGroupBySender = NotifyType.getSenderGroupType().contains(notifyType);
		// post related
		Post post = null;
		if (NotifyType.getPostType().contains(notifyType) && postDao.exists(refId)) {
			post = postDao.findById(refId);
		}
		
		String displayName = null;
		Long avatarId = null;
		String attrJson = null;
		String refContent = null;
		
		if (NotifyType.getYouType().contains(notifyType)) {
			if (post != null) {
				receiverId = post.getCreatorId();
			}
			
			if (receiverId == null || receiverId.equals(senderId))
				return;			
			if (userDao.exists(receiverId)){
				User receiver = userDao.findById(receiverId);
				if (!UserType.getReceiveNotifyType().contains(receiver.getUserType()))
					return;
			} else
				return;
			
			avatarId = sender.getAvatarId();
	    	displayName = sender.getDisplayName();
	    	// get Attribute
	    	final Map<String, Object> attribute = new HashMap<String, Object>();
	    	if (notifyType.equals(NotifyType.CommentPost.toString())) {
	    		attribute.put("command", content);
	    	}
	    	if (!NotifyType.getFollowType().contains(notifyType) && !isGroupBySender) {
	    		String name = displayName;
	    		if (name == null)
	    			name = "";
	    		attribute.put("name", name);
	    	}
	    	try {
	    		attrJson = new ObjectMapper().writeValueAsString(attribute);
	    	} catch (JsonProcessingException e) {
	    	}
	    	// get Reference Content
	    	refContent = getContent(notifyType, post, displayName, content, iconUrl);
	    		
		} else if (NotifyType.getFriendType().contains(notifyType)){
			String accountSource = null;
	    	avatarId = sender.getAvatarId();
	    	// get Display Name
	    	if (notifyType.equals(NotifyType.JoinBCFromFB.toString())) {
	    		accountSource = AccountSourceType.Facebook.toString();
	    		displayName = friendshipDao.findName(senderId, accountSource);
	    	}
	    	else if (notifyType.equals(NotifyType.JoinBCFromWeibo.toString())) {
	    		accountSource = AccountSourceType.Weibo.toString();
	    		displayName = friendshipDao.findName(senderId, accountSource);
	    	}
	    	else
	    		accountSource = "BC";
	    	if (displayName == null || displayName.isEmpty())
	    		displayName = sender.getDisplayName();
	    	// get Attribute
        	final Map<String, Object> attribute = new HashMap<String, Object>();
        	attribute.put("accountSource", accountSource);	
        	try {
        		attrJson = new ObjectMapper().writeValueAsString(attribute);
        	} catch (JsonProcessingException e) {
        	}
        	// get Reference Content
        	refContent = getContent(notifyType, post, displayName, content, iconUrl);
		}
		
		if (refContent != null) {
			NotifyEvent nEvent = new NotifyEvent();
			nEvent.setReceiverId(receiverId);
			nEvent.setSenderId(senderId);
			nEvent.setAvatarId(avatarId);
			nEvent.setSenderName(displayName);
			nEvent.setNotifyType(notifyType);
			nEvent.setRefId(refId);
			nEvent.setRefContent(refContent);
			nEvent.setAttribute(attrJson);
			notifyEventDao.create(nEvent);
		}
	}
	
	private String getContent(String notifyType, Post post, String displayName, String content, String iconUrl) {
		String refContent = null;
		if (NotifyType.getPostType().contains(notifyType)) {
    		if (post != null) {
        		AttachmentWrapper postCover = getPostCover(post);
        		try {
					refContent = new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(postCover);
				} catch (JsonProcessingException e) {
				}
    		}
    	} else if (NotifyType.getCircleType().contains(notifyType)) {
    		try {
				refContent = new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(new CircleData(content, iconUrl));
			} catch (JsonProcessingException e) {
			}
    	} else
    		refContent = displayName;
    	return refContent;
	}
	
	private void doFriendNotifyByType(String notifyType, User sender,
			Long refId, String content, Set<Long> receiverList, String accountSource, String iconUrl, AttachmentWrapper postCover) {

		//Post post = null;
		List<AttachmentWrapper> files;
		String refContent = null;
    	Boolean isGroupBySender = NotifyType.getSenderGroupType().contains(notifyType);
		
		String displayName = null;
		if (!"BC".equalsIgnoreCase(accountSource)) {
			displayName = friendshipDao.findName(sender.getId(), accountSource);
		} 
		if (displayName == null) {
    		displayName = sender.getDisplayName();
    	}
    	Map<Long, Notify> groupMap = fiendGroupsByType(receiverList, sender.getId(), refId, notifyType);
		//List<Notify> createList = new ArrayList<Notify>();
		//List<Long> deleteList = new ArrayList<Long>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -10);
		
    	for (Long receiverId : receiverList) {
        	// Group Notify
        	if ((NotifyType.FriendLikePost.toString().equalsIgnoreCase(notifyType) ||  NotifyType.FriendFollowCircle.toString().equalsIgnoreCase(notifyType)) 
        			&& notifyDao.findIsGrouped(receiverId, refId, sender.getId(), notifyType)){
        		continue;
        	}
    		else if (receiverId == null || receiverId.longValue() == sender.getId())
    			continue;

        	//Notify group = fiendGroupByType(receiverId, sender.getId(), refId, notifyType);

        	Notify group;
        	if (groupMap.containsKey(receiverId))
        		group = groupMap.get(receiverId);
        	else
        		group = null;

        	Long groupNum = Long.valueOf(1);
        	String groupIds = "";
        	Date firstCreated = null;
        	if (group != null) {
        		groupNum = group.getGroupNum();
        		groupIds = getGroupIds(group, groupNum, isGroupBySender);
        		firstCreated = group.getFirstCreated();
        		groupNum += 1;
        		//group.setGroupNum(Long.valueOf(0));
        		//notifyDao.update(group);
        		if (NotifyType.FriendLikePost.toString().equalsIgnoreCase(notifyType)) {
        			if (group.getCreatedTime().after(cal.getTime())){
        				continue;
        			}
        		}
        	}

        	// Set Attributes
    		String mapAsJson = "";
        	final Map<String, Object> attributes = new HashMap<String, Object>();
        	attributes.put("accountSource", accountSource);	
        	if (!NotifyType.getFollowType().contains(notifyType) && !isGroupBySender) {
            	String name = displayName;
        		if (name == null)
        			name = "";
        		addInAttributeList("nameList", name, Long.valueOf(5), group, attributes);
        	}
        	try {
        		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
        	} catch (JsonProcessingException e) {
        	}
        	
        	// Set Reference Content
        	if (NotifyType.getPostType().contains(notifyType)) {
        		files = new ArrayList<AttachmentWrapper>();  
    			if (group == null || NotifyType.getSenderGroupType().contains(notifyType)) {
                	files.add(postCover);
    			}
    			
        		if (NotifyType.getSenderGroupType().contains(notifyType)) {
        			refContent = getReferenceContent(files, group);
        			
        		} else {
        			if (group != null) {
        				refContent = group.getRefContent();
        			} else {
        				refContent = getReferenceContent(files, null);
        			}
        		}
        	} else if (NotifyType.getCircleType().contains(notifyType)) {
        		if (notifyType.equals(NotifyType.FollowCircle.toString()))
        			refContent = getReferenceContent(content, iconUrl, null);
        		else	
        			refContent = getReferenceContent(content, iconUrl, group);
        	} else {
        		refContent = getReferenceContent(displayName, group);
        	}
           	if(refContent != null){
           		Notify notify = new Notify();        	
	        	notify.setAttribute(mapAsJson);    	
	        	notify.setSenderName(displayName);
	        	notify.setGroupNum(groupNum);
	        	notify.setGroupIds(groupIds);
	        	notify.setNotifyType(notifyType);
	        	notify.setReceiverId(receiverId);
	        	notify.setAvatarId(sender.getAvatarId());
	        	notify.setSenderId(sender.getId());
	        	notify.setRefContent(refContent);
	        	notify.setRefId(refId);
	        	notify.setRegion(Constants.getNotifyRegion());
	        	if (firstCreated != null)
	        		notify.setFirstCreated(firstCreated);
	        	notifyDao.create(notify);	    		
	        	//createList.add(notify);
	        	if (group != null) {
	            	//deleteList.add(group.getId());
	        		notifyDao.deleteById(group.getId());
	        	}
	        	try {
	        		Thread.sleep(100);
	        	} catch (Exception e) {        		
	        	}
           	}
    	}
    	/*Boolean isDeleteComplete = Boolean.TRUE;
    	if (deleteList.size() > 0) {
    		isDeleteComplete = notifyDao.batchDelete(deleteList);
    	}
    	if (createList.size() > 0 && isDeleteComplete) {
    		notifyDao.batchInsert(createList);
    	}*/
	}
	
	private void addInAttributeList(String attributeName, String content, Long maxSize, Notify group, Map<String, Object> attributes) {
		List<String> contentList = new ArrayList<String>();
		if (content != null)
			contentList.add(content);
		if (group != null && group.getAttribute() != null && group.getAttribute().length() > 0) {
			JsonObject object = new JsonObject(group.getAttribute());
    		JsonArray list = object.getJsonArray(attributeName);
			for (int i = 0; contentList.size() < maxSize && i < list.length(); i++) {
    			try {
    				contentList.add(list.getString(i));
    			} catch (Exception e) {
    				continue;
    			}
			}
		}
		attributes.put(attributeName, contentList);		
	}
	
	private AttachmentWrapper getPostCover(Post post) {
		AttachmentWrapper postCoverOri = null;
		for (Attachment at : post.getAttachments()) {
        	try {
        		if(at.getTarget() instanceof File) {
        			File file = (File)at.getTarget();        			

        			if (file.getId() != 0) {
                    	FileType fileType = file.getFileType();
                    	if (fileType != FileType.Photo && fileType != FileType.PostCover && fileType != FileType.PostCoverOri)
                    		continue;
        				FileItem fileItem = null;
                		if (file.getListItems().size() != 0) {
                			fileItem = file.getListItems().get(0);
                		} else if (file.getQualityItems().size() != 0) {
                			fileItem = file.getQualityItems().get(0);
                		} else if (file.getFileItems().size() != 0) {
                			fileItem = file.getFileItems().get(0);
                		} 
                        if (fileItem != null) {
                        	fileType = file.getFileType();
                        	if(fileType == FileType.Photo) {
                        		return new AttachmentWrapper(file.getId(), "Photo", 
                        				Long.valueOf(0), (ObjectNode)fileItem.getMetadataJson(), fileItem.getOriginalUrl());
                        	}
                        	else if(fileType == FileType.PostCover) {
                        		return new AttachmentWrapper(file.getId(), "PostCover", 
                        				Long.valueOf(0), (ObjectNode)fileItem.getMetadataJson(), fileItem.getOriginalUrl());
                        	}
                        	else if(fileType == FileType.PostCoverOri) {
                        		postCoverOri = new AttachmentWrapper(file.getId(), "PostCoverOri", 
                        				Long.valueOf(0), (ObjectNode)fileItem.getMetadataJson(), fileItem.getOriginalUrl());
                        	}
                        }                		
        			}
        				
        		} 
        	} catch (Exception e) {
        	} 
        }
        if(postCoverOri != null) {
        	return postCoverOri;
        }     
		return null;
	}
	
	private String getReferenceContent(String content, Notify group) {
		String refContent = null;
		List<String> nameList = new ArrayList<String>();
		if (content == null)
			nameList.add("");
		else
			nameList.add(content);
		if (group != null && group.getRefContent() != null) {
			JsonArray list = new JsonArray(group.getRefContent());
			for (int i = 0; nameList.size() < 10 && i < list.length(); i++) {
				try {
					nameList.add(list.getString(i));
				} catch (Exception e) {
					continue;
				}
			}
		}
    	try {
    		refContent = new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(nameList);
    		do{
    			if(refContent == null || refContent.length() <= 2048){
    				break;
    			}
    			try{
	    			JsonArray newList = new JsonArray(refContent);
	    			newList.remove(newList.length()-1);
	    			refContent = newList.toString();
    			} catch (Exception e){
    				logger.error("NotifyServiceImpl getReferenceContent(String content, Notify group) fail,refContent : " + refContent + " message : " + e.getMessage());
    				refContent = null;
    				break;
    			}
    		}while(true);
		} catch (JsonProcessingException e) {
			refContent = null;
		}
    	if (refContent != null && refContent.equalsIgnoreCase("null"))
    		refContent = null;
		return refContent;
	}

	private String getReferenceContent(List<AttachmentWrapper> files, Notify group) {
		String refContent = null;
    	if (group!= null && group.getRefContent() != null) {
			JsonArray list = new JsonArray(group.getRefContent());
			for (int i = 0; files.size() < 5 && i < list.length(); i++) {
				try {
					JsonObject obj = list.getJsonObject(i);
					files.add(new AttachmentWrapper(obj.getLong("fileId"), obj.getString("fileType"), 
							obj.getLong("downloadCount"), obj.getString("metadata")));
				} catch (Exception e) {
					files.add(null);
					continue;
				}
			}
		}
    	try {
    		refContent = new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(files);
    		do{
    			if(refContent == null || refContent.length() <= 2048){
    				break;
    			}
    			try{
	    			JsonArray newList = new JsonArray(refContent);
	    			newList.remove(newList.length()-1);
	    			refContent = newList.toString();
    			} catch (Exception e){
    				logger.error("NotifyServiceImpl getReferenceContent(List<AttachmentWrapper> files, Notify group) fail,refContent : " + refContent + " message : " + e.getMessage());
    				refContent = null;
    				break;
    			}
    		}while(true);
		} catch (JsonProcessingException e) {
			refContent = null;
		}
    	if (refContent != null && refContent.equalsIgnoreCase("null"))
    		refContent = null;
		return refContent;
	}

	private String getReferenceContent(String circleName, String circleIcon, Notify group) {
		String refContent = null;
		List<CircleData> circles = new ArrayList<CircleData>();
		circles.add(new CircleData(circleName, circleIcon));
    	if (group!= null && group.getRefContent() != null) {
			JsonArray list = new JsonArray(group.getRefContent());
			for (int i = 0; circles.size() < 5 && i < list.length(); i++) {
				try {
					JsonObject obj = list.getJsonObject(i);
					circles.add(new CircleData(obj.getString("name"), obj.getString("iconUrl")));
				} catch (Exception e) {
					continue;
				}
			}
		}
    	try {
    		refContent = new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(circles);
    		do{
    			if(refContent == null || refContent.length() <= 2048){
    				break;
    			}
    			try{
	    			JsonArray newList = new JsonArray(refContent);
	    			newList.remove(newList.length()-1);
	    			refContent = newList.toString();
    			} catch (Exception e){
    				logger.error("NotifyServiceImpl getReferenceContent(String circleName, String circleIcon, Notify group) fail,refContent : " + refContent + " message : " + e.getMessage());
    				refContent = null;
    				break;
    			}
    		}while(true);
		} catch (JsonProcessingException e) {
			refContent = null;
		}
    	if (refContent != null && refContent.equalsIgnoreCase("null"))
    		refContent = null;
		return refContent;
	}
	
	private Notify fiendGroupByType(Long receiverId, Long senderId, Long refId, String notifyType) {
    	Notify group = null;
    	if (NotifyType.CommentPost.toString().equalsIgnoreCase(notifyType)) {
    		return null;
    	} else if (NotifyType.getSenderGroupType().contains(notifyType)) {
    		group = notifyDao.findNotifyGroupByFirstCreated(receiverId, senderId, notifyType, "senderId");
    	} else {
    		if (NotifyType.getFollowType().contains(notifyType)) {
    			group = notifyDao.findNotifyGroupByFirstCreated(receiverId, receiverId, notifyType, "receiverId");
    		} else {
    			group = notifyDao.findNotifyGroupByFirstCreated(receiverId, refId, notifyType, "refId");
    		}
    	}
		return group;
	}
	private Map<Long, Notify> fiendGroupsByType(Set<Long> receiverId, Long senderId, Long refId, String notifyType) {
		Map<Long, Notify> group = null;
    	if (NotifyType.getSenderGroupType().contains(notifyType)) {
    		group = notifyDao.findNotifyGroupsByFirstCreated(receiverId, senderId, notifyType, "senderId");
    	} else {
    		if (NotifyType.getFollowType().contains(notifyType)) {
    			group = notifyDao.findNotifyGroupsByFirstCreated(receiverId, null, notifyType, "receiverId");
    		} else {
    			group = notifyDao.findNotifyGroupsByFirstCreated(receiverId, refId, notifyType, "refId");
    		}
    	}
		return group;
	}
	
	private String getGroupIds(Notify group, Long groupNum, Boolean isGroupBySender) {
		String groupIds = "";
		if (groupNum == 1) {
			if (isGroupBySender) {
				groupIds = group.getRefId().toString();
			} else {
				groupIds = group.getSenderId().toString();
			}
		} else if (groupNum < 100000) {
			if (isGroupBySender) {
				groupIds = group.getRefId().toString() + "," + group.getGroupIds();
			} else {
				groupIds = group.getSenderId().toString() + "," + group.getGroupIds();
			}
		} else if (groupNum >= 100000) {
			int lastIndex = group.getGroupIds().lastIndexOf(",");
			if (isGroupBySender) {
				groupIds = group.getRefId().toString() + "," + group.getGroupIds().substring(0, lastIndex);
			} else {
				groupIds = group.getSenderId().toString() + "," + group.getGroupIds().substring(0, lastIndex);
			}
		}
		return groupIds;
	}

	@Override
	public void updateByDeleteComment(Long commentId) {
		Comment comment = commentDao.findById(commentId);
		if (comment.getRefType().equals("Post")) {
			Long postId = comment.getRefId();
			Post p = postDao.findById(postId);
			Long receiverId = p.getCreatorId();
			Long senderId = comment.getCreator().getId();
			notifyDao.updateByDeleteComment(postId, receiverId, senderId, comment.getCommentText());
		}
	}

	@Override
	public void updateNotifyDevice(Long userId, String apnsToken,
			String apnsType, String uuid, String app) {
		if (runableUpdateUser == null) {
			runableUpdateUser = new RunnableUpdateUserDevice();
		}
		//runableUpdateUser.start();
		
		Map<String, String> params = new HashMap<String, String>();
    	params.put("method", "GET");
        params.put("apnsToken", apnsToken);
        params.put("apnsType", apnsType);
        params.put("userId", userId.toString());
        params.put("ap", app);
        params.put("uuid", uuid);

        urlQueue.offer(new UrlRequest("http://" + Constants.getWebsiteWrite() + "/api/user/update-device.action", params));
		runableUpdateUser.start();
        return;
	}
	
	private class RunnableUpdateAvatar implements Runnable {
		private Long userId; 
		private Long avatarId;		
		RunnableUpdateAvatar(Long userId, Long avatarId){
			this.userId = userId;
			this.avatarId = avatarId;
		}
		
		public void run() {
			notifyDao.updateSenderAvatar(userId, avatarId);
		}
	}
	
	public void updateSenderAvatar(Long userId, Long avatarId) {
		//workQueue.execute(new RunnableUpdateAvatar(userId, avatarId));
	}
	
	@Override
	public void sendEventNotify(List<EventUser> eventUserList, BrandEvent brandEvent) {
		workQueue.execute(new RunnableSendEventNotify(eventUserList, brandEvent));
	}
	
	private void doSendEventNotify(final List<EventUser> eventUserList, final BrandEvent brandEvent) {
		String locale = brandEvent.getLocale();
		final User sender = getOfficial(locale);
		
		Set<Long> receiveIds = new HashSet<Long>();		
		for (EventUser eventUser : eventUserList) {
			receiveIds.add(eventUser.getUserId());
		}
		final Map<Long, Device> deviceMap = deviceDao.findNotifyDeviceByUserIds(receiveIds);
		
		
		// create Notify
		Map<Long, Notify> notifyMap = transactionTemplate.execute(new TransactionCallback<Map<Long, Notify>>() {
			@Override
			public Map<Long, Notify> doInTransaction(TransactionStatus status) {
				Map<Long, Notify> map = new HashMap<Long, Notify>();				
				for (EventUser eventUser : eventUserList) {
					Notify notify = new Notify();
					Long groupNum = Long.valueOf(1);
			    	String groupIds = "";
			    	String mapAsJson = "";
		        	notify.setAttribute(mapAsJson);    	
		        	notify.setSenderName(sender.getDisplayName());
		        	notify.setGroupNum(groupNum);
		        	notify.setGroupIds(groupIds);
		        	if (brandEvent.getServiceType().equals(ServiceType.FREE_SAMPLE))
		        		notify.setNotifyType(NotifyType.FreeSample.toString());
		        	if (brandEvent.getServiceType().equals(ServiceType.CONSULTATION))
		        		notify.setNotifyType(NotifyType.Consultation.toString());
		        	notify.setReceiverId(eventUser.getUserId());
		        	notify.setAvatarId(sender.getAvatarId());
		        	notify.setSenderId(sender.getId());
					notify.setRefContent("[\""+ brandEvent.getTitle() + "\"]");
		        	notify.setRefId(brandEvent.getId());
		        	notify.setRegion(brandEvent.getLocale());
		        	if (deviceMap.containsKey(eventUser.getUserId()))
		        		notify.setSendTarget(deviceMap.get(eventUser.getUserId()).getApp());
		        	else
		        		notify.setSendTarget("NoToken");
		        	notifyDao.create(notify);
		        	map.put(notify.getReceiverId(), notify);
				}
				return map;
			}      		
    	});
		
		if (notifyMap == null || notifyMap.isEmpty()) {
			return;
		}
		
		pushNotification(deviceMap, notifyMap);
	}
	
	@Override
	public void sendSOWNotify(List<Long> userIds, String locale) {
		workQueue.execute(new RunnableSendSOWNotify(userIds, locale));
	}
	
	private void doSendSOWNotify(List<Long> userIds, final String locale) {
		final User sender = getOfficial(locale);
		final List<User> userList = userDao.findByIds(userIds.toArray(new Long[userIds.size()]));
		final Map<Long, Device> deviceMap = deviceDao.findNotifyDeviceByUserIds(new HashSet<Long>(userIds));
		
		// create Notify
		Map<Long, Notify> notifyMap = transactionTemplate.execute(new TransactionCallback<Map<Long, Notify>>() {
			@Override
			public Map<Long, Notify> doInTransaction(TransactionStatus status) {
				Map<Long, Notify> map = new HashMap<Long, Notify>();				
				for (User user : userList) {
					Long groupNum = Long.valueOf(1);
			    	String groupIds = "";
			    	String mapAsJson = "";
			    	String refContent = LanguageCenter.getNotifyLang(locale).getStarOfWeekMessage();
			    	Notify notify = createNewNotify(user.getId(), sender, NotifyType.StarOfWeek.toString(), null, 
							groupNum, groupIds,	refContent, mapAsJson, null, null, false);
			    	if (notify == null)
			    		continue;
			    	
		        	if (deviceMap.containsKey(user.getId()))
		        		notify.setSendTarget(deviceMap.get(user.getId()).getApp());
		        	else
		        		notify.setSendTarget("NoToken");
		        	notifyDao.create(notify);
		        	map.put(notify.getReceiverId(), notify);
				}
				return map;
			}      		
    	});
		
		if (notifyMap == null || notifyMap.isEmpty()) {
			return;
		}
		
		pushNotification(deviceMap, notifyMap);
	}
	
	private User getOfficial(String locale) {
		PageResult<User> userResult = userDao.findByUserType(Arrays.asList(UserType.CL), Arrays.asList(locale), (long)0, (long)1);
		if (userResult == null || userResult.getResults().isEmpty()) {
			logger.error("[EventNotify] Can't find the CL user");
			return null;
		}
		return userResult.getResults().get(0);
	}
	
	private void pushNotification(Map<Long, Device> deviceMap, Map<Long, Notify> notifyMap) {
		Iterator<Long> keys = notifyMap.keySet().iterator();
		if (keys == null) {
			logger.error("[EventNotify] Push notification fail");
			return;
		}
		
		while(keys.hasNext()) {
			Long receiveId = (Long)keys.next();
			if (receiveId == null) 
				continue;
			Notify notify = notifyMap.get(receiveId);
			Device device = deviceMap.get(receiveId);
			if (notify == null || device == null) 
				continue;
			
			if (device.getDeviceType() == DeviceType.iOS) {
				logger.debug(String.format("Send Apns notify, Nid:%s,", notify.getId().toString()));
				com.dbay.apns4j.model.Payload payload = new com.dbay.apns4j.model.Payload();
				String message = notify.getMessage();
				try {
					Boolean isTruncate = false;
					if (message.length() >= 66) {
						message = message.substring(0, 66);
						isTruncate = true;
					}
					byte[] utf8Bytes = message.getBytes("UTF-8");
					if (utf8Bytes.length > 66) {
						message = message.substring(0, 22);
						isTruncate = true;
					}
					if (isTruncate)
						message += "...";
				} catch (Exception e) {
					if (message.length() >= 22)
						message = message.substring(0, 22) + "...";
				}
				payload.setAlert(message);	
				payload.setBadge(1);
				payload.addParam("Title", "Beauty Circle");
				payload.addParam("MsgType", "BC");
			    payload.addParam("Ntype", notify.getTypeIndex());
			    payload.addParam("Link", notify.getDeepLink());
			    payload.addParam("Nid", notify.getId().toString());
			    if ("YMK".equalsIgnoreCase(device.getApp())) {
			    	YMKApns.sendNotification(device.getApnsToken(), payload);
			    } else if ("YCN".equalsIgnoreCase(device.getApp())) {
			    	YCNApns.sendNotification(device.getApnsToken(), payload);
			    } else if ("YBC".equalsIgnoreCase(device.getApp())) {
			    	YBCApns.sendNotification(device.getApnsToken(), payload);
			    } else {
			    	YCPApns.sendNotification(device.getApnsToken(), payload);
			    }
			} else {
				logger.debug(String.format("Send GCM notify, Nid:%s,", notify.getId().toString()));
				GCMPayload payload = new GCMPayload();
				Message data = new Message();
				data.setMsg(notify.getMessage());
				data.setTickerText(notify.getMessage());
				data.setTitle("Beauty Circle");
				data.setLink(notify.getDeepLink());
				data.setNtype(notify.getTypeIndex());
		        data.setNid(notify.getId().toString());
				data.setMsgType("BC");
				payload.setData(data);
				
				List<String> registration_ids = new ArrayList<String>();
				registration_ids.add(device.getApnsToken());
				payload.setRegistration_ids(registration_ids);
				try {
					if ("YMK".equalsIgnoreCase(device.getApp())) {
						YMKGcm.push(payload);
					} else if ("YCN".equalsIgnoreCase(device.getApp())) {
						YCNGcm.push(payload);
					} else if ("YBC".equalsIgnoreCase(device.getApp())) {
						YBCGcm.push(payload);
					} else {
						YCPGcm.push(payload);
					}
				} catch (Exception e) {
					logger.error("send GCM notify error");
					logger.error(String.format("ReceiveId:%s, Msg:%s, Link%s: Ntype:%s, Nid:%s, GCMToken:%s",
							notify.getReceiverId().toString(), notify.getMessage(), notify.getDeepLink(), notify.getTypeIndex(), notify.getId().toString(), device.getApnsToken()));
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	public void deleteOldNotify(){
		Boolean hasOldData = Boolean.FALSE;
		do {
			try {
				hasOldData = transactionTemplate.execute(new TransactionCallback<Boolean>()  {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						Boolean hasOldDataTransaction = Boolean.TRUE;
						for(int runTimes = 0 ; runTimes < 5 ; runTimes++ ){
							if(notifyDao.deleteOldNotify() == 0){
								hasOldDataTransaction = Boolean.FALSE;
								break;
							}
						}
						return hasOldDataTransaction;
					}
				});
			} catch (Exception e) {
				hasOldData = Boolean.FALSE;
				logger.error("deleteOldNotify fail. message:" + e.getMessage());
			}
		} while (hasOldData);
	}
	
	
	public void setNotifyIsRead(Long receiverId,Long time,String type){
		Boolean hasOldData = Boolean.FALSE;
		final String ftype = type;
		final Long fReceiverId = receiverId;
		final Long fTime = time;
		do {
			try {
				hasOldData = transactionTemplate.execute(new TransactionCallback<Boolean>()  {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						Boolean hasOldDataTransaction = Boolean.TRUE;
						//Update 200 at one time.If the number is less than 100 update, which represents the work done.
						if(notifyDao.setIsReaded(fReceiverId,fTime,ftype) < 100){
							hasOldDataTransaction = Boolean.FALSE;
						}
						return hasOldDataTransaction;
					}
				});
			} catch (Exception e) {
				hasOldData = Boolean.FALSE;
				logger.error("NotifyServiceImpl setNotifyIsRead fail. receiverId:" + receiverId + "  message:" + e.getMessage());
			}
		} while (hasOldData);
	}
	
	@Override
	public Map<Integer, Boolean> getWorkerStatus() {
	    return workQueue.getWorkerStatus();
	}
	
	@Override
	public void wakeUpWorker() {
	    workQueue.initWorker();
	}
	
	@Override
	public Integer getTaskCount() {
	    return workQueue.getTaskCount();
	}
	
	@Override
	public void clearAllTask() {
	    workQueue.clearAllTask();
	}
	
	@Override
	public Long getDropCount() {
		return dropCount;
	}

	@Override
	public void setWriteEvent(Boolean bWrite) {
		bWriteNotifyEvent = bWrite;
	}
	
	@Override
	public Boolean getWriteEvent() {
		return bWriteNotifyEvent;
	}
}
