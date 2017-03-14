package com.cyberlink.cosmetic.modules.product.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_PROD_CHANGE_LOG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class ProductChangeLog extends AbstractCoreEntity<Long>{
    private static final long serialVersionUID = -7185054561878216977L;

    private Long id;
    private User user;
    private ProductChangeLogType refType;
    private Long refId;
    private List<ProductChangeLogAttr> attrList = new ArrayList<ProductChangeLogAttr> ();
    
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "REF_TYPE")
    @Enumerated(EnumType.STRING)
    public ProductChangeLogType getRefType() {
        return refType;
    }
    
    public void setRefType(ProductChangeLogType refType) {
        this.refType = refType;
    }
    
    @Column(name = "REF_ID")
    public Long getRefId() {
        return refId;
    }
    
    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy="prodChangeLogId", cascade={CascadeType.ALL})
	@Where(clause = "IS_DELETED = 0")
	public List<ProductChangeLogAttr> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<ProductChangeLogAttr> attrList) {
		this.attrList = attrList;
	}
}