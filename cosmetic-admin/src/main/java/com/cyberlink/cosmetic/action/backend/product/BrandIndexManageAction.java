package com.cyberlink.cosmetic.action.backend.product;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.BrandIndexDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.modules.product.service.BrandService;
import com.cyberlink.cosmetic.action.backend.AbstractAction;

@UrlBinding("/product/BrandIndexManage.action")
public class BrandIndexManageAction extends AbstractAction{
	
	@SpringBean("product.BrandIndexDao")
	protected BrandIndexDao brandIndexDao;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	static final String BrandIndexManageHome = "/product/BrandIndexManage.action" ;
	static final String createNewBrandIndex = "/product/createNewBrandIndex.jsp" ;
	
	
	private PageResult<BrandIndex> brandIndexList = new PageResult<BrandIndex>();
	private int offset = 0, limit = 20 ;
	private Set<String> localeList ;
	private BrandIndex brandIndex ;
	private Long brandIndexId ;
	private int pages ;
	private String locale = "zh_TW" ;
	private String indexCharacter ;
	
	@DefaultHandler
	public Resolution route() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		brandIndexList = brandIndexDao.listIndexByLocale( locale, Long.valueOf(offset), Long.valueOf(limit) ) ;
		setPages( (brandIndexList.getTotalSize() / limit)+ 1 ) ;
        return forward();
    }
	
	public Resolution changeLocale(){
		return new RedirectResolution(BrandIndexManageHome).addParameter("locale", locale);
		
	}
	
	public Resolution createNewBrandIndexRequest(){
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		return forward(createNewBrandIndex);
	}
	
	public Resolution submitNewBrandIndex(){
		BrandIndex newBrandIndex = new BrandIndex();
		newBrandIndex.setIndex(indexCharacter);
		newBrandIndex.setLocale(locale);
		brandIndexDao.create(newBrandIndex);
		return new RedirectResolution(BrandIndexManageHome).addParameter("locale", locale);
	}
	
	@ValidationMethod(on="submitNewBrandIndex")
	public void validateNewBrandCreation(ValidationErrors errors) {
		if( indexCharacter == null ){
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			errors.add("indexCharacter", new SimpleError("Brand index character is required!"));
		}
		
		if(brandIndexDao.findIndexByNameLocale(indexCharacter, locale) != null){
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			errors.add("indexCharacter", new SimpleError("Brand index is already used!"));
		}
		
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Long getBrandIndexId() {
		return brandIndexId;
	}

	public void setBrandIndexId(Long brandIndexId) {
		this.brandIndexId = brandIndexId;
	}

	public PageResult<BrandIndex> getBrandIndexList() {
		return brandIndexList;
	}

	public void setBrandIndexList(PageResult<BrandIndex> brandIndexList) {
		this.brandIndexList = brandIndexList;
	}

	public String getIndexCharacter() {
		return indexCharacter;
	}

	public void setIndexCharacter(String indexCharacter) {
		this.indexCharacter = indexCharacter;
	}


	public BrandIndex getBrandIndex() {
		return brandIndex;
	}


	public void setBrandIndex(BrandIndex brandIndex) {
		this.brandIndex = brandIndex;
	}

	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}

}
