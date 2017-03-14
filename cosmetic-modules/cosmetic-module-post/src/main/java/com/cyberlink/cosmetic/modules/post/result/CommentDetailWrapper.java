package com.cyberlink.cosmetic.modules.post.result;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.CommentTag;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonView;

public class CommentDetailWrapper extends CommentBaseWrapper {

    public class Creator {
    	public User creator;
        public Long userId = (long)1;
        public String displayName = "";
        public String avatar = "";
        public String cover = "";
        public UserType userType = UserType.Normal;
        public String description = "";
        private Boolean isFollowed = false;
    	private Boolean starOfWeek = Boolean.FALSE;
		private String badge = "";
        
        public Creator(User creator)
        {
            if(creator == null)
                return;
            this.creator = creator;
            userId = creator.getId();
            displayName = creator.getDisplayName();
            avatar = creator.getAvatarUrl();
            cover = creator.getCoverUrl();
            userType = creator.getUserType();
            description = creator.getDescription();
            isFollowed = creator.getIsFollowed();
            starOfWeek = creator.getStarOfWeek();
            badge = creator.getBadge();
        }

        @JsonView(Views.Public.class)
        public Long getUserId()
        {
            return userId;
        }

        @JsonView(Views.Public.class)
        public String getDisplayName()
        {
            return displayName;
        }
        
        @JsonView(Views.Public.class)
        public String getAvatar()
        {
            return avatar;
        }

        @JsonView(Views.Public.class)
        public String getCover()
        {
            return cover;
        }
        
        @JsonView(Views.Public.class)
        public UserType getUserType() {
			return userType;
		}

        @JsonView(Views.Public.class)
        public String getDescription() {
			return description;
		}
        
        public void setIsFollowed(Boolean isFollowed)
        {
            this.isFollowed = isFollowed;
        }
        
        @JsonView(Views.Simple.class)
        public Boolean getIsFollowed() {
            return isFollowed;
        }
        
        @JsonView(Views.Simple.class)
		public Boolean getStarOfWeek() {
			return starOfWeek;
		}

		public void setStarOfWeek(Boolean starOfWeek) {
			this.starOfWeek = starOfWeek;
		}

		@JsonView(Views.Simple.class)
		public String getBadge() {
			return badge;
		}

		public void setBadge(String badge) {
			this.badge = badge;
		}
        
        public String getEmail() {
            if(creator == null)
                return "";
            List<Account> accs = creator.getAllEmailAccountList();
            if(accs == null || accs.size() <= 0)
                return "";
            return accs.get(0).getEmail();
        }
        
        public String getAccountSource() {
            if(creator == null)
                return "";
            List<Account> accs = creator.getAllEmailAccountList();
            if(accs == null || accs.size() <= 0)
                return "";
            return accs.get(0).getAccountSource().toString();
        }
    }

    public class ReceiverTag {
        public ReceiverTag(Long receiverId, String receiverName)
        {
            userId = receiverId;
            displayName = receiverName;
        }
        
        private Long userId = (long)0;
        private String displayName = "";
        
        @JsonView(Views.Public.class)
        public Long getUserId()
        {
            return userId;
        }
        
        @JsonView(Views.Public.class)
        public String getDisplayName()
        {
            return displayName;
        }
    }
    
    public class Tags {
        private List<ReceiverTag> receiverTags = new ArrayList<ReceiverTag>(0);
        
        public Tags()
        {
            
        }
        
        void setReceiverTags(List<ReceiverTag> receiverTags)
        {
            this.receiverTags = receiverTags;
        }
        
        @JsonView(Views.Public.class)
        public List<ReceiverTag> getReceiverTags() {
            return receiverTags;
        }
        
    }

    private Creator creator;
    private Tags tags;
    private Boolean isLiked = false;
    private Long likeCount = (long)0;
    private String refType;
    private Long refId;
    private Long subCommentCount = (long)0;
    private Long latestSubCommentId;
    private SubCommentDetailWrapper latestSubComment;
	
	public CommentDetailWrapper(Comment comment, List<CommentTag> receiversTag) {
        super(comment);
        creator = new Creator(comment.getCreator());
        refType = comment.getRefType();
        refId = comment.getRefId();
        subCommentCount = comment.getSubCommentCount();
        latestSubCommentId = comment.getLatestSubCommentId();
        tags = new Tags();
        if(receiversTag == null || receiversTag.size() <= 0)
            return;
        List<ReceiverTag> receiverTags = new ArrayList<ReceiverTag>(0);
        for(CommentTag ct : receiversTag) {
            Object target = ct.getTagTarget();
            if (!(target instanceof User))
                continue;
            User receiver = (User)target ;
            receiverTags.add(new ReceiverTag(receiver.getId(), receiver.getDisplayName()));
        }
        tags.setReceiverTags(receiverTags);
    }
	
	public CommentDetailWrapper(Comment comment) {
		super(comment);
		creator = new Creator(comment.getCreator());
		refType = comment.getRefType();
		refId = comment.getRefId();
	}

    @JsonView(Views.Simple.class)
    public Creator getCreator() {
        return creator;
    }
    
    @JsonView(Views.Simple.class)
    public String getComment() {
        return comment.getCommentText();
    }
    
    public void setIsLiked(Boolean isLiked)
    {
        this.isLiked = isLiked;
    }
    
    @JsonView(Views.Simple.class)
    public Boolean getIsLiked() {
        return isLiked;
    }
    
    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }
    
    @JsonView(Views.Simple.class)
    public Long getLikeCount() {
        return likeCount;
    }
    
    @JsonView(Views.Simple.class)
    public Tags getTags() {
        return tags;
    }
    
    public String getRefType() {
        return refType;
    }
    
    public Long getRefId() {
        return refId;
    }
    
    @JsonView(Views.Simple.class)
    public Long getSubCommentCount() {
		return subCommentCount;
	}

	public Long getLatestSubCommentId() {
		return latestSubCommentId;
	}

	public void setLatestSubComment(SubCommentDetailWrapper latestSubComment) {
		this.latestSubComment = latestSubComment;
	}
	
	@JsonView(Views.Simple.class)
	public SubCommentDetailWrapper getLatestSubComment() {
		return latestSubComment;
	}
}
