package com.cyberlink.cosmetic.modules.search.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_KEYWORD")
public class PostKeyword extends AbstractCoreEntity<Long> {

	private static final long serialVersionUID = -5458646528927251495L;
	
	private String keyword;
	private Long freq;
	private String lang;
	
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
	
	@Column(name = "LANG")
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Column(name = "KEYWORD")
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Column(name = "FREQ")
	public Long getFreq() {
		return freq;
	}

	public void setFreq(Long freq) {
		this.freq = freq;
	}
}
