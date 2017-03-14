package com.cyberlink.cosmetic.modules.user.model.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserSubType;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

public class UserInfoWrapper {
	public static class PersonalInfoView extends Views.Public {
    }
    
	private final User user;
	private Long postCount; // Backward compatible for howToCount
	private Long howToCount;
	private Long likedHowToCount;
    private Long likedTargetCount; // Backward compatible for likedYclCount
	private Long likedYclCount;
	private Long yclLookCount;
	private Long followerCount;
	private Long followingCount;
	private Long blockCount;
	private Long liveBrandCount;
	private List<EventImage> eventImageList = new ArrayList<EventImage>();
	
	public UserInfoWrapper(User user, Long likedPostCount, Long postCount, Long likedTargetCount, Long yclLookCount, Long followerCount, Long followingCount, Long blockCount, Long liveBrandCount, List<Object> imageObjList) {
        this.user = user;
        this.howToCount = postCount == null ? 0L : postCount;
        this.postCount = this.howToCount;
        this.likedHowToCount = likedPostCount == null ? 0L : likedPostCount;
        this.yclLookCount = yclLookCount == null ? 0L : yclLookCount;
		this.likedYclCount = likedTargetCount == null ? 0L : likedTargetCount;
		this.likedTargetCount = this.likedYclCount;
        this.followerCount = followerCount == null ? 0L : followerCount;
        this.followingCount = followingCount == null ? 0L : followingCount;
        this.blockCount = blockCount == null ? 0L : blockCount;
        this.liveBrandCount = liveBrandCount == null ? 0L : liveBrandCount;
        if (imageObjList != null) {
        	for (Object obj : imageObjList) {
        		EventImage eventImg = new EventImage();
        		Object[] row = (Object[]) obj;
        		eventImg.setId((long) row[0]);
        		eventImg.setImageUrl(String.valueOf(row[1]));
        		eventImg.setEventLink(String.valueOf(row[2]));
        		eventImageList.add(eventImg);
        	}
        }
    }
	
	@JsonView(Views.Public.class)
    public Long getId() {
		return user.getId();
	}
	
    @Temporal(TemporalType.TIMESTAMP)
	@JsonView(Views.Public.class)
	public Date getLastModified() {
		return user.getLastModified();
	}
	
	@JsonView(Views.Public.class)
	public String getDisplayName(){
		return user.getDisplayName();
	} 
	
	@JsonView(Views.Public.class)
	public String getAvatarUrl() {
		return user.getAvatarUrl();
	}
	
	@JsonView(Views.Public.class)
	public String getAvatarDetail() {
		return user.getAvatarDetail();
	}

	@JsonView(Views.Public.class)
	public String getCoverUrl() {
		return user.getCoverUrl();
	}
	
	@JsonView(Views.Public.class)
	public String getBgImageUrl() {
		return user.getBgImageUrl();
	}
	
	@JsonView(Views.Public.class)
	public String getWebsiteUrl() {
		return user.getWebsiteUrl();
	}
	
	@JsonView(Views.Public.class)
	public String getIconUrl() {
		return user.getIconUrl();
	}

	@JsonView(Views.Public.class)
	public GenderType getGender() {
		return user.getGender();
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT+00")
	@JsonView(Views.Public.class)
	public Date getBirthDay() {
		return user.getBirthDay();
	}

	@JsonView(Views.Public.class)
	public UserType getUserType() {
		return user.getUserType();
	}
	
	@JsonView(Views.Public.class)
    public UserSubType getUserSubType() {
		return user.getUserSubType();
	}

	@JsonView(Views.Public.class)
	public String getDescription() {
		return user.getDescription();
	}

	@JsonView(Views.Public.class)
	public String getRegion() {
		return user.getRegion();
	}

	@JsonView(Views.Public.class)
	public String getAttribute() {
		return user.getAttribute();
	}

	@JsonView(Views.Public.class)
	public Boolean getIsFollowed() {
		return user.getIsFollowed();
	}

	@JsonView(Views.Public.class)
	public Boolean getIsBlocked() {
		return user.getIsBlocked();
	}
	
	@JsonView(Views.Public.class)
	public Boolean getIsChatable() {
		return user.getIsChatable();
	}

	@JsonView(Views.Public.class)
	public Long getFollowerCount() {
		return followerCount;
	}

	@JsonView(Views.Public.class)
	public Long getFollowingCount() {
		return followingCount;
	}
	
	@JsonView(Views.Public.class)
	public Long getBlockCount() {
		return blockCount;
	}

	@JsonView(Views.Public.class)
	public Long getLiveBrandCount() {
		return liveBrandCount;
	}

	@JsonView(Views.Public.class)
	public List<String> getTab() {
		return user.getListInAttr("tab");
	}
	
	@JsonView(Views.Public.class)
    public Long getLikedTargetCount() {
        return likedTargetCount;
    }
	
	@JsonView(Views.Public.class)
    public Long getYclLookCount() {
        return yclLookCount;
    }
	
	@JsonView(Views.Public.class)
    public Long getPostCount() {
        return postCount;
    }
	
	@JsonView(Views.Public.class)
	public Long getHowToCount() {
        return howToCount;
    }

	@JsonView(Views.Public.class)
    public Long getLikedYclCount() {
        return likedYclCount;
    }

	@JsonView(Views.Public.class)
    public Long getLikedHowToCount() {
        return likedHowToCount;
    }
	    
	@JsonView(Views.Public.class)
    public List<EventImage> getEventImageList() {
        return eventImageList;
    }
	
	@JsonView(Views.Public.class)
	public String getEmail(){
		List<Account> list = user.getAccountList();
		if (list != null && list.size() > 0)
			return list.get(list.size()-1).getAccount();
		return null; 
	} 
	
	@JsonView(Views.Public.class)
	public String getUniqueId() {
		return user.getUniqueId();
	}
	
	@JsonView(Views.Public.class)
	public Long getLevel() {
		return user.getLevel();
	}
	
	@JsonView(Views.Public.class)
	public Boolean getCertify() {
		return user.getCertify();
	}
	
	@JsonView(Views.Public.class)
	public Boolean getStarOfWeek() {
		return user.getStarOfWeek();
	}
	
	// personal information
	@JsonView(PersonalInfoView.class)
	public String getName() {
		return user.getName();
	}
	
	@JsonView(PersonalInfoView.class)
	public String getPhone() {
		return user.getPhone();
	}
	
	@JsonView(PersonalInfoView.class)
	public String getAddress() {
		return user.getAddress();
	}
	
	@JsonView(PersonalInfoView.class)
	public String getReceiveEmail() {
		return user.getMail();
	}
	
	public class EventImage {
    	private Long id;
    	private String imageUrl;
    	private String eventLink;

    	@JsonView(Views.Public.class)
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@JsonView(Views.Public.class)
		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		@JsonView(Views.Public.class)
		public String getEventLink() {
			return eventLink;
		}

		public void setEventLink(String eventLink) {
			this.eventLink = eventLink;
		}
    	
    }
}
