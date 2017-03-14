package com.cyberlink.cosmetic.modules.notify.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.post.result.AttachmentWrapper;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.fasterxml.jackson.annotation.JsonView;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

@Entity
@Table(name = "BC_NOTIFY")
@DynamicUpdate
public class Notify extends AbstractCoreEntity<Long> implements Comparable<Notify>{
	private static final long serialVersionUID = 8331529892827613402L;

	private Long receiverId; 
    private Long senderId; 
    private Long avatarId; 
    private String senderName;
    
	private String notifyType;
    private Long refId; 
    private String refContent;
	private Boolean isRead = Boolean.FALSE;
    
	Long groupNum;
	String groupIds;
	
	private Date sendTime;
    private String sendTarget;
    private String attribute;
    
    private String region = Constants.getNotifyRegion();
    
    private Date firstCreated = Calendar.getInstance().getTime();
    		
	@Transient
    private Long sortValue = Long.valueOf(0);

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Transient    
    @JsonView(Views.Public.class)   
    public String getAvatarUrl() {
    	if (getAvatarId() != null)
    		return "http://" + Constants.getWebsiteDomain() + "/api/file/download-file.action?getFile&fileId="+ getAvatarId() +"&thumbnailType=Avatar";
    	else
    		return null;
	}
    
    @Column(name = "RECEIVER_ID")
    @JsonView(Views.Public.class)
    public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	
	@Column(name = "SENDER_ID")
	@JsonView(Views.Public.class)
	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	@Column(name = "SENDER_NAME")
	@JsonView(Views.Public.class)
	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Column(name = "SENDER_AVATAR")
	public Long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(Long avatarId) {
		this.avatarId = avatarId;
	}


	@Column(name = "NOTIFY_TYPE")
	@JsonView(Views.Public.class)
	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	
	@Column(name = "REF_ID")
	@JsonView(Views.Public.class)
	public Long getRefId() {
		return refId;
	}

	public void setRefId(Long refId) {
		this.refId = refId;
	}
	
	@Column(name = "REF_CONTENT")	
    public String getRefContent() {
		return refContent;
	}

	public void setRefContent(String refContent) {
		this.refContent = refContent;
	}

	@Column(name = "IS_READ")
	@JsonView(Views.Public.class)
	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
	@Column(name = "GROUP_NUM")
	@JsonView(Views.Public.class)
	public Long getGroupNum() {
		return groupNum;
	}

    public void setGroupNum(Long groupNum) {
		this.groupNum = groupNum;
	}

    @Column(name = "GROUP_IDS")
	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
	
    @Transient    
    @JsonView(Views.Public.class)   
    public List<Long> getIdList() {
    	List<Long> list = new ArrayList<Long>();
    	if (getGroupIds() != null && getGroupIds().length() > 0) {
    		String ids = groupIds.replaceAll(" ", "");
    		String [] alist = ids.split(",");
    		for (String id: alist) {
    			list.add(Long.valueOf(id));
    			if (list.size() > 20)
    				break;
    		}
    	}
    	return list;
    }
    
    @Transient    
    public Long getIdListSize() {
    	if (getGroupIds() != null && getGroupIds().length() > 0) {
    		String ids = groupIds.replaceAll(" ", "");
    		String [] alist = ids.split(",");
    		return Long.valueOf(alist.length + 1);
    	}
    	return Long.valueOf(1);
    }
    

    @Transient    
    public PageResult<Long> getIdListWithOffset(Long offset, Long limit) {
    	PageResult<Long> page = new PageResult<Long>();
    	Set<Long> list = new LinkedHashSet<Long>();
    	list.add(getRefId());
    	if (getGroupIds() != null && getGroupIds().length() > 0) {
    		String ids = groupIds.replaceAll(" ", "");
    		String [] alist = ids.split(",");
    		for (String id: alist)
    			list.add(Long.valueOf(id));
    	}
    	page.setTotalSize(list.size());
    	if (offset >= list.size() || offset < 0) {
    		page.setResults(new ArrayList<Long>());
    		return page;
    	} else {
    		List<Long> result = new ArrayList<Long>(list);
    		page.setResults( result.subList(offset.intValue(), Long.valueOf(Math.min(result.size(), offset+limit)).intValue()));
    		return page;
    	}
    }

    @Column(name = "SEND_TIME")
	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	@Column(name = "SEND_TARGET")
	public String getSendTarget() {
		return sendTarget;
	}

	public void setSendTarget(String sendTarget) {
		this.sendTarget = sendTarget;
	}
	
	@Column(name = "ATTRIBUTE")
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Column(name = "REGION")
	public String getRegion() {
		if (region == null) {
			return Constants.getNotifyRegion();
		}
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FIRST_CREATED")
	public Date getFirstCreated() {
		return firstCreated;
	}

	public void setFirstCreated(Date firstCreated) {
		this.firstCreated = firstCreated;
	}

	@Transient   
	@JsonView(Views.Public.class)   
    public Date getCreateTime() {
        return getCreatedTime();
    }

	@Transient   
	@JsonView(Views.Public.class)   
    public List<String> getFriendNames() {
		List<String> list = new ArrayList<String>();
		if (NotifyType.getCommentType().contains(getNotifyType())) {
			list.add(getSenderName());
		} else if (NotifyType.getFollowType().contains(getNotifyType())) {
			if (getRefContent() == null)
				return list;
			try {
				JsonArray array = new JsonArray(getRefContent());
				for (int i =0;i < array.length();i++) {
					list.add(array.getString(i));
				}
			} catch (Exception e) {
			}
			
		} else if (!NotifyType.getSenderGroupType().contains(getNotifyType()) && getAttribute() != null){
			try {
				JsonObject obj = new JsonObject(attribute);
				JsonArray array = obj.getJsonArray("nameList");
				for (int i =0;i < array.length();i++) {
					list.add(array.getString(i));
				}
			} catch (Exception e) {
				return list;
			}
		}
		return list;
    }
	
	@Transient   
	@JsonView(Views.Public.class)   
    public List<String> getComments() {
		List<String> list = new ArrayList<String>();
		if (!NotifyType.getCommentType().contains(getNotifyType()) || getAttribute() == null)
			return list;
		try {
			JsonObject obj = new JsonObject(attribute);
			JsonArray array = obj.getJsonArray("commandList");
			for (int i =0;i < array.length();i++) {
				list.add(array.getString(i));
			}
		} catch (Exception e) {
				return list;
		}
		return list;
	}
	
	@Transient   
	@JsonView(Views.Public.class)   
    public String getAccountSource() {
		if (!NotifyType.getFriendType().contains(getNotifyType()))
			return null;
		try {
			JsonObject obj = new JsonObject(attribute);
			return obj.getString("accountSource");
		} catch (Exception e) {
		}
		return AccountSourceType.Facebook.toString();
	}
	
	@Transient   
	@JsonView(Views.Public.class)   
    public List<String> getCircleNames() {
		List<String> list = new ArrayList<String>();
		if (!NotifyType.getCircleType().contains(getNotifyType()) || getRefContent() == null) {
			return list;
		} else {
			try {
				JsonArray array = new JsonArray(getRefContent());
				for (int i =0;i < array.length();i++) {
					try {
						JsonObject obj = array.getJsonObject(i);
						list.add(obj.getString("name"));
					} catch (Exception e) {
						list.add(array.getString(i));
					}
				}
			} catch (Exception e) {
			}
		}
		return list;
    }

	@Transient   
	@JsonView(Views.Public.class)   
    public List<String> getCircleIcons() {
		List<String> list = new ArrayList<String>();
		if (!NotifyType.getCircleType().contains(getNotifyType()) || getRefContent() == null) {
			return list;
		} else {
			try {
				JsonArray array = new JsonArray(getRefContent());
				for (int i =0;i < array.length();i++) {
					try {
						JsonObject obj = array.getJsonObject(i);
						String url = obj.getString("iconUrl");
						if (url != null && url.length() > 0 && !url.equalsIgnoreCase("null"))
							list.add(obj.getString("iconUrl"));
						else 
							list.add("");
					} catch (Exception e) {
						list.add("");
					}
				}
			} catch (Exception e) {
			}
		}
		return list;
    }
	
	@Transient
	@JsonView(Views.Public.class)  
	public List<AttachmentWrapper> getFiles() {
		List<AttachmentWrapper> list = new ArrayList<AttachmentWrapper>();
		if (!(NotifyType.getPostType().contains(getNotifyType()) || NotifyType.getCommentType().contains(getNotifyType())) 
				|| getRefContent() == null) {
			return list;
		}
		try {
			JsonArray array = new JsonArray(getRefContent());
			for (int i =0;i < array.length();i++) {
				try {
					JsonObject obj = array.getJsonObject(i);
					list.add(new AttachmentWrapper(obj.getLong("fileId"), obj.getString("fileType"), 
							obj.getLong("downloadCount"), obj.getString("metadata")));
				} catch (Exception e){
					list.add(null);
					continue;
				}
			}
		} catch (Exception e) {
		}
		
		return list;
	}
	
	@Transient    
	public String getDisplayName(){
		String names = "";
		if (getGroupNum() > 1 && getFriendNames().size() >= 2 && !NotifyType.getSenderGroupType().contains(getNotifyType())) {
			if (getSenderName() == null || getSenderName().length() == 0)
				names = "Someone, ";
			else 
				names = "<b>" + getSenderName() + "</b>";
			
			String name = getFriendNames().get(1);
			if (name == null || name.length() == 0 /*|| getGroupNum() - 2 == 1*/)
				names += (", and " + String.valueOf(getGroupNum() - 1) + " others");
			else if (getGroupNum() - 2 == 1)
				names += (", <b>" + name + "</b>, and " +  String.valueOf(getGroupNum() - 2) + " other");
			else if (getGroupNum() - 2 > 1)
				names += (", <b>" + name + "</b>, and " +  String.valueOf(getGroupNum() - 2) + " others");
			else 
				names += (" and <b>" + name + "</b>");
			return names;
		} else {
			if (getSenderName() == null || getSenderName().length() == 0)
				if (NotifyType.getSenderGroupType().contains(getNotifyType())) {
					return "Your friend";
				} else if (NotifyType.getFriendType().contains(getNotifyType())) {
					return "";
				} else {
					return "Someone";
				}
			else 
				return "<b>" + getSenderName() + "</b>";			
		}
	}

	@Transient    
	public String getTypeIndex(){
		String type = getNotifyType();
		if (NotifyType.CommentPost.toString().equalsIgnoreCase(type)) {
			return "Y1";
		} else if (NotifyType.FollowUser.toString().equalsIgnoreCase(type)) {
			return "Y2";
		} else if (NotifyType.FollowCircle.toString().equalsIgnoreCase(type)) {
			return "Y3";
		} else if (NotifyType.FreeSample.toString().equalsIgnoreCase(type)) {
			return "Y4";
		} else if (NotifyType.Consultation.toString().equalsIgnoreCase(type)) {
			return "Y5";
		}else if (NotifyType.JoinBCFromFB.toString().equalsIgnoreCase(type)) {
			return "P4";
		} else if (NotifyType.JoinBCFromWeibo.toString().equalsIgnoreCase(type)) {
			return "P4";
		} else if (NotifyType.AddPost.toString().equalsIgnoreCase(type)) {
			return "P5";
		} else if (NotifyType.CreateCircle.toString().equalsIgnoreCase(type)) {
			return "P6";
		} else if (NotifyType.LikePost.toString().equalsIgnoreCase(type)) {
			return "Y7";
		} else if (NotifyType.CircleInPost.toString().equalsIgnoreCase(type)) {
			return "Y8";
		} else if (NotifyType.FriendLikePost.toString().equalsIgnoreCase(type)) {
			return "P9";
		} else if (NotifyType.FriendFollowCircle.toString().equalsIgnoreCase(type)) {
			return "P10";
		} else if (NotifyType.ReplyToCommentOwner.toString().equalsIgnoreCase(type)) {
			return "Y11";
		} else if (NotifyType.ReplyToPostOwner.toString().equalsIgnoreCase(type)) {
			return "Y12";
		} else if (NotifyType.StarOfWeek.toString().equalsIgnoreCase(type)) {
			return "Y13";
		}else {
			return "O0";
		}
	}	
	
	@Transient    
	public String getName(){
		if (getSenderName() == null || getSenderName().length() == 0) {
			if (NotifyType.getSenderGroupType().contains(getNotifyType())) {
				return "Your friend";
			} else if (NotifyType.getFriendType().contains(getNotifyType())) {
				return "";
			} else {
				return "Someone";
			}
		} else {
			return getSenderName();
		}
	}

	@Transient
	private String getNameFromAttribute() {
		try {
			JsonObject obj = new JsonObject(attribute);
			JsonArray array = obj.getJsonArray("nameList");
			return array.getString(0);
		} catch (Exception e) {
			return "Someone";
		}
	}
	
	@Transient
	public String getMessage() {
		if (NotifyType.CommentPost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " commented on your post.";
		} else if (NotifyType.FollowUser.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" +getName() + " started following you.";
		} else if (NotifyType.FollowCircle.toString().equalsIgnoreCase(getNotifyType())) {
			if (getCircleNames() != null && getCircleNames().size() > 0)
				return "\u200E" + getName() + " started following your circle, " + getCircleNames().get(0) + ".";
			else
				return "\u200E" + getName() + " started following your circle.";
		} else if (NotifyType.JoinBCFromFB.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + "Your Facebook friend " + getName() + " joined Beauty Circle.";
		} else if (NotifyType.JoinBCFromWeibo.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + "Your Weibo friend " + getName() + " joined Beauty Circle.";
		} else if (NotifyType.AddPost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " added a post.";
		} else if (NotifyType.CreateCircle.toString().equalsIgnoreCase(getNotifyType())) {
			if (getCircleNames() != null && getCircleNames().size() > 0)
				return "\u200E" + getName() + " created a new circle, " + getCircleNames().get(0) + ".";
			else
				return "\u200E" + getName() + " created a new circle.";
		} else if (NotifyType.LikePost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " likes your post.";
		} else if (NotifyType.CircleInPost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " added your post to a circle.";
		} else if (NotifyType.FriendLikePost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " like a post.";
		} else if (NotifyType.FriendFollowCircle.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " follow a circle.";
		} else if (NotifyType.FreeSample.toString().equalsIgnoreCase(getNotifyType())) {
			try {
				JsonArray array = new JsonArray(getRefContent());
				return LanguageCenter.getNotifyLang(getRegion()).getPushFreeSampleMessage(array.get(0).toString());
			} catch (Exception e) {
				
			}			
		} else if (NotifyType.Consultation.toString().equalsIgnoreCase(getNotifyType())) {
			try {
				JsonArray array = new JsonArray(getRefContent());
				return LanguageCenter.getNotifyLang(getRegion()).getPushConsultationMessage(array.get(0).toString());				
			} catch (Exception e) {
				
			}	
		} else if (NotifyType.ReplyToCommentOwner.toString().equalsIgnoreCase(getNotifyType())) {
			String tmp = "\u200E" + getName() + " replied your comment on " + getNameFromAttribute() + "'s post.";
			try {
				byte[] utf8Bytes = tmp.getBytes("UTF-8");
				if (utf8Bytes.length > 54) {
					return "\u200E" + getName().substring(0, 2) + "..." + " replied your comment on " + getNameFromAttribute().substring(0, 1) + "..." + "'s post.";
				}
			} catch (Exception e) {
			}
			return tmp;
		} else if (NotifyType.ReplyToPostOwner.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getName() + " replied a comment on your post.";
		} else if (NotifyType.StarOfWeek.toString().equalsIgnoreCase(getNotifyType())) {
			return getRefContent();
		}
		return "";
	}

	@Transient
	@JsonView(Views.Public.class)  
	public String getMsg() {
		if (NotifyType.CommentPost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " commented on your post.";
		} else if (NotifyType.FollowUser.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " started following you.";
		} else if (NotifyType.FollowCircle.toString().equalsIgnoreCase(getNotifyType())) {
			if (getCircleNames() != null && getCircleNames().size() > 0)
				return "\u200E" + getDisplayName() + " started following your circle, " + getCircleNames().get(0) + ".";
			else
				return "\u200E" + getDisplayName() + " started following your circle.";
		} else if (NotifyType.JoinBCFromFB.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " joined Beauty Circle.";
		} else if (NotifyType.JoinBCFromWeibo.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " joined Beauty Circle.";
		} else if (NotifyType.AddPost.toString().equalsIgnoreCase(getNotifyType())) {
			if (getGroupNum() > 1)
				return "\u200E" + getDisplayName() + " added " + getGroupNum().toString() + " posts.";
			else
				return "\u200E" + getDisplayName() + " added " + getGroupNum().toString() + " post.";
		} else if (NotifyType.CreateCircle.toString().equalsIgnoreCase(getNotifyType())) {
			if (getGroupNum() > 1)
				return "\u200E" + getDisplayName() + " created " + getGroupNum().toString() + " new circles.";
			else {
				return "\u200E" + getDisplayName() + " created " + getGroupNum().toString() + " new circle, " + getCircleNames().get(0)+".";
			}
		} else if (NotifyType.LikePost.toString().equalsIgnoreCase(getNotifyType())) {
			if (getGroupNum() > 1)
				return "\u200E" + getDisplayName() + " like your post.";
			else
				return "\u200E" + getDisplayName() + " likes your post.";
		} else if (NotifyType.CircleInPost.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " added your post to a circle.";
		} else if (NotifyType.FriendLikePost.toString().equalsIgnoreCase(getNotifyType())) {
			if (getGroupNum() > 1)
				return "\u200E" + getDisplayName() + " liked " + getGroupNum().toString() + " posts.";
			else 
				return "\u200E" + getDisplayName() + " liked " + getGroupNum().toString() + " post.";
		} else if (NotifyType.FriendFollowCircle.toString().equalsIgnoreCase(getNotifyType())) {
			if (getGroupNum() > 1)
				return "\u200E" + getDisplayName() + " followed " + getGroupNum().toString() + " circles.";
			else 
				return "\u200E" + getDisplayName() + " followed " + getGroupNum().toString() + " circle.";
		} else if (NotifyType.FreeSample.toString().equalsIgnoreCase(getNotifyType())) {
			try {
				JsonArray array = new JsonArray(getRefContent());
				return LanguageCenter.getNotifyLang(getRegion()).getCenterFreeSampleMsg(array.get(0).toString());
			} catch (Exception e) {
				
			}
		} else if (NotifyType.Consultation.toString().equalsIgnoreCase(getNotifyType())) {
			try {
				JsonArray array = new JsonArray(getRefContent());
				return LanguageCenter.getNotifyLang(getRegion()).getCenterConsultationMsg(array.get(0).toString());				
			} catch (Exception e) {
				
			}
		} else if (NotifyType.ReplyToCommentOwner.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " replied your comment on " + getNameFromAttribute() + "'s post.";
		} else if (NotifyType.ReplyToPostOwner.toString().equalsIgnoreCase(getNotifyType())) {
			return "\u200E" + getDisplayName() + " replied a comment on your post.";
		} else if (NotifyType.StarOfWeek.toString().equalsIgnoreCase(getNotifyType())) {
			return getRefContent();
		}
		return "";
	}

	@Transient
	public String getDeepLink() {
		if (NotifyType.getPostType().contains(getNotifyType())) {
			return "ybc://post/" + getRefId();
		} else if (NotifyType.getFollowType().contains(getNotifyType())) {
			return "ybc://me/" + getRefId();
		} else if (NotifyType.getCircleType().contains(getNotifyType())) {
			return "ybc://circle/" + getRefId();
		} else if (NotifyType.FreeSample.toString().equalsIgnoreCase(getNotifyType())) {
			return "ybc://free_sample/" + getRefId();
		} else if (NotifyType.Consultation.toString().equalsIgnoreCase(getNotifyType())) {
			return "ybc://event/" + getRefId() + "?action=result";
		} else if (NotifyType.getCommentType().contains(getNotifyType())) {
			return "ybc://comment/" + getRefId() + "?s=" + getCommentId();
		} else if (NotifyType.StarOfWeek.toString().equalsIgnoreCase(getNotifyType())) {
			return "ybc://weeklystar";
		}
		return "ybc://notifications";
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public Long getCommentId() {
		if (!NotifyType.getCommentType().contains(getNotifyType()) || getAttribute() == null)
			return null;
		try {
			JsonObject obj = new JsonObject(attribute);
			return obj.getLong("commentId");
		} catch (Exception e) {
			return null;
		}
	}

	@Transient
	public Long getSortValue() {
		return sortValue;
	}

	public void setSortValue(Long sortValue) {
		this.sortValue = sortValue;
	}

	@Override
	public int compareTo(Notify o) {
		if (this.getSortValue() > o.getSortValue()) {
			return 1;
		} else if (this.getSortValue() == o.getSortValue()) {
			return 0;
		} else {
			return -1;
		}		
	}	
}
