package com.cyberlink.cosmetic.modules.product.model;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_PRODUCT_COMMENT")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@DynamicUpdate
public class ProductComment extends AbstractEntity<Long>{
	private static final long serialVersionUID = -5574035742013401202L;

    private Product product;
    private String comment;
    private float rating;
    private User user;
    private ProductCommentStatus status;
    private List<ReportedProdComment> reportedTickets ;
    
    private Long creatorId;
    
    public class Creator {
    	public Long userId = Long.valueOf(1);
    	public String avatar = "http://vector.me/files/images/7/8/782527/cyberlink.png";
    	public String displayName = "CyberLink Beauty Circle";
    	public String cover = "http://vector.me/files/images/7/8/782527/cyberlink.png";
        public UserType userType = UserType.Normal;
        public String description = "";
        
    	public Creator(User creator)
        {
            userId = creator.getId();
            displayName = creator.getDisplayName();
            avatar = creator.getAvatarUrl();
            cover = creator.getCoverUrl();
            userType = creator.getUserType();
            description = creator.getDescription();
        }
        
    	@JsonView(Views.Public.class)
    	public Long getUserId(){
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
        public String getCover(){
        	return cover;
        }
        
        @JsonView(Views.Public.class)
        public UserType getUserType(){
        	return userType;
        }
        
        @JsonView(Views.Public.class)
        public String getDescription() {
			return description;
		}
        
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	@Column(name = "COMMENT_TEXT")
    @JsonView(Views.Public.class)    
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "RATING")
    @JsonView(Views.Public.class)    
	public float getRating() {
		return rating;
	}
	
	public void setRating(float rating) {
		this.rating = rating;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "STATUS", nullable=true)
	@Enumerated(EnumType.STRING)
	public ProductCommentStatus getStatus() {
		return status;
	}

	public void setStatus(ProductCommentStatus status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reportedComment", cascade={CascadeType.ALL})
	@Where(clause = "IS_DELETED = 0")
	public List<ReportedProdComment> getReportedTickets() {
		return reportedTickets;
	}

	public void setReportedTickets(List<ReportedProdComment> reportedTickets) {
		this.reportedTickets = reportedTickets;
	}

	@Transient
	public Long getCreatorId() {
		return creatorId;
	}

	@Transient
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
}
