package com.cyberlink.cosmetic.modules.product.model;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_BRAND")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Brand extends AbstractCoreEntity<Long> {
	private static final long serialVersionUID = -1723677025998408882L;

	private Long id;
	private String brandName;
	private String locale ;
	private BrandIndex brandIndex ;
	private Integer priority ;
	private List<BrandNameAlias> brandNameAliasList ;
	private List<ProductSearchKeyword> keywordList ;
	

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@JsonView(Views.Public.class)
    @Column(name = "BRAND_NAME")
	public String getBrandName() {
		return brandName;
	}
	
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	
	@Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDEX_ID")
	public BrandIndex getBrandIndex() {
		return brandIndex;
	}

	public void setBrandIndex(BrandIndex brandIndex) {
		this.brandIndex = brandIndex;
	}

	@Column(name = "PRIORITY", nullable=true)
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@JsonView(Views.Public.class)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "refId", cascade={CascadeType.ALL})
	@NotFound(action=NotFoundAction.IGNORE)
	@Where(clause="IS_DELETED = 0 and REF_TYPE ='Brand'")
	public List<ProductSearchKeyword> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<ProductSearchKeyword> keywordList) {
		this.keywordList = keywordList;
	}

	@JsonView(Views.Public.class)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "refBrand", cascade={CascadeType.ALL})
	@NotFound(action=NotFoundAction.IGNORE)
	@Where(clause="IS_DELETED = 0")
	public List<BrandNameAlias> getBrandNameAliasList() {
		return brandNameAliasList;
	}

	public void setBrandNameAliasList(List<BrandNameAlias> brandNameAliasList) {
		this.brandNameAliasList = brandNameAliasList;
	}
}
