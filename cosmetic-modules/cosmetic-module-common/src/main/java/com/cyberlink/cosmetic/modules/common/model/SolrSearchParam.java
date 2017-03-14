package com.cyberlink.cosmetic.modules.common.model;

import java.io.Serializable;

public class SolrSearchParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6954323607284770391L;
	private String keyword;
    private Integer offset = 0;
    private Integer limit = 20;
    private String locale = "en_US";
    
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
    
}
