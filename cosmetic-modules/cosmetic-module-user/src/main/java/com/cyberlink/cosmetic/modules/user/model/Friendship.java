package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;

@Entity
@Table(name = "BC_FRIENDSHIP")
@DynamicUpdate
public class Friendship extends AbstractEntity<Long> implements Comparable<Friendship>{
	private static final long serialVersionUID = 7948516977015558002L;

    private String displayName;

    private Long userId; 
    private Long friendId; 
    
    private String accountSource;
    private String sourceId; 
    

	@Column(name = "USER_ID")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "FRIEND_ID")
	public Long getFriendId() {
		return friendId;
	}

	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}

	@Column(name = "DISPLAY_NAME")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    @Column(name = "ACCOUNT_SOURCE")
	public String getAccountSource() {
		return accountSource;
	}

	public void setAccountSource(String accountSource) {
		this.accountSource = accountSource;
	}
	
	@Column(name = "SOURCE_ID")
    public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId= sourceId;
	}

	@Transient
    private Long sortValue = Long.valueOf(0);

    public void setId(Long id) {
        this.id = id;
    }
	
	@Transient
	public Long getSortValue() {
		return sortValue;
	}

	public void setSortValue(Long sortValue) {
		this.sortValue = sortValue;
	}

	@Override
	public int compareTo(Friendship o) {
		if (this.getSortValue() > o.getSortValue()) {
			return 1;
		} else if (this.getSortValue() == o.getSortValue()) {
			return 0;
		} else {
			return -1;
		}		
	}	
}
