package com.cyberlink.cosmetic.modules.product.model;

import java.io.Serializable;

public class ProductSearchParam implements Serializable {

    private static final long serialVersionUID = 5031345290414830304L;
    private String keyword;
    private Integer offset = 0;
    private Integer limit = 16;
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

    public Integer getPageSize() {
        return limit;
    }

    public void setPageSize(Integer pageSize) {
        this.limit = pageSize;
    }

    public Integer getStartFrom() {
        return offset;
    }

    public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

}
