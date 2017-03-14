package com.cyberlink.cosmetic.modules.user.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_USER_ACCOUNT")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Account extends AbstractCoreEntity<Long>{
    private static final long serialVersionUID = -6617585057551468411L;
    private Long id;
    private AccountSourceType accountSource;
    private String account;
    private String email;
	private Long userId;
    private User user;
    private List<Member> member;
    private AccountMailStatus mailStatus;
    private Boolean isVerified = Boolean.FALSE;

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

    @Column(name = "ACCOUNT_SOURCE")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)    
    public AccountSourceType getAccountSource() {
        return accountSource;
    }
    
    public void setAccountSource(AccountSourceType accountSource) {
        this.accountSource = accountSource;
    }

    @Column(name = "ACCOUNT_REFERENCE")
    @JsonView(Views.Public.class)    
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Column(name = "USER_ID")
    @JsonView(Views.Public.class)    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	@NotFound(action=NotFoundAction.IGNORE)
	@Where(clause = "IS_DELETED = 0")
	public List<Member> getMember() {
		return member;
	}

	public void setMember(List<Member> member) {
		this.member = member;
	}

	@Column(name = "EMAIL")
    @JsonView(Views.Public.class)    
	public String getEmail() {
		if (email == null && accountSource == AccountSourceType.Email) {
			return getAccount();
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "MAIL_STATUS")
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Public.class)
	public AccountMailStatus getMailStatus() {
		return mailStatus;
	}

	public void setMailStatus(AccountMailStatus mailStatus) {
		this.mailStatus = mailStatus;
	}

	@Column(name = "IS_VERIFIED")
	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}
	
}
