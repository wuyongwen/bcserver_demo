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
@Table(name = "BC_PRODUCT_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class ProductType extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = -2663937377895621000L;

	private Long id;
	private String typeName;
	private String locale;
	private Integer sortPriority ;
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

    @Column(name = "TYPE_NAME")
    @JsonView(Views.Public.class)    	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Column(name = "LOCALE")
    @JsonView(Views.Public.class)
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Column(name = "PRIORITY",nullable=true)
	public Integer getSortPriority() {
		return sortPriority;
	}

	public void setSortPriority(Integer sortPriority) {
		this.sortPriority = sortPriority;
	}
	
	@JsonView(Views.Public.class)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "refId", cascade={CascadeType.ALL})
	@NotFound(action=NotFoundAction.IGNORE)
	@Where(clause="IS_DELETED = 0 and REF_TYPE = 'Type'")
	public List<ProductSearchKeyword> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<ProductSearchKeyword> keywordList) {
		this.keywordList = keywordList;
	}
	
}
