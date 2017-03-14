package com.cyberlink.cosmetic.action.backend.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.IntegerTypeConverter;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.BrandIndexDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogAttrDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttr;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttrStatus;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttrType;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.action.backend.AbstractAction;

@UrlBinding("/product/BrandManage.action")
public class BrandManageAction extends AbstractAction{
	
	@SpringBean("product.BrandDao")
	protected BrandDao brandDao;
	
	@SpringBean("product.BrandIndexDao")
	protected BrandIndexDao brandIndexDao;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("product.ProductChangeLogDao")
	private ProductChangeLogDao productChangeLogDao ;
	
	@SpringBean("product.ProductChangeLogAttrDao")
	private ProductChangeLogAttrDao productChangeLogAttrDao ;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	static final String BrandManageHome = "/product/BrandManage.action" ;
	static final String createNewBrand = "/product/createNewBrand.jsp" ;
	static final String editBrandInfo = "/product/editBrandInfo.jsp" ; 
	static final String createNewBrandIndex = "/product/createNewBrandIndex.jsp" ;
	static final String ajaxBrandOption = "/product/ajaxOption/ajaxBrandIndexOption.jsp" ;
	
	private PageResult<Brand> brandList;
	private PageResult<BrandIndex> brandIndexList = new PageResult<BrandIndex>();
	private Set<String> localeList ;
	private int offset = 0, limit = 20 ;
	private Brand brandItem ;
	private long brandId;
	private Long brandIndexId ;
	private int pages ;
	
	private String locale = "zh_TW" ;
	private String brandName ;
	private Integer priority ;
	private String indexCharacter ;
	
	@DefaultHandler
	public Resolution route() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
		brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
		brandList = (brandDao.listBrandByLocale(brandIndexId, locale, offset, limit));
		setPages( (brandList.getTotalSize() / limit)+ 1 ) ;
        return forward();
    }
	
	public Resolution changeLocale(){
		return new RedirectResolution(BrandManageHome).addParameter("locale", locale);
		
	}
	
	
	public Resolution updateBrandRequest(){
		brandItem = brandDao.findById(brandId);
		locale = brandItem.getLocale() ;
		priority = brandItem.getPriority();
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
		brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
		return forward(editBrandInfo);
	}
	
	public Resolution updateBrandIndexList(){
		//ajax codes to update options
		brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
		brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
		final Map<String, Object> results = new HashMap<String, Object>();
		results.put("results", brandIndexDao.listAllIndexByLocale(locale)) ;
		return json(results);
	}
	
	public Resolution submitBrandUpdates(){
		List<ProductChangeLogAttr> AttrLogList = new ArrayList<ProductChangeLogAttr> ();
		brandItem = brandDao.findById(brandId);
		if( !brandItem.getBrandName().equals(brandName) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.BRANDNAME);
			oldAttrLog.setAttrValue(brandItem.getBrandName());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.BRANDNAME);
			newAttrLog.setAttrValue(brandName);
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			brandItem.setBrandName(brandName);
		}
		if( !brandItem.getLocale().equals(locale) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.LOCALE);
			oldAttrLog.setAttrValue(brandItem.getLocale());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.LOCALE);
			newAttrLog.setAttrValue(locale);
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			brandItem.setLocale(locale);
		}
		if( brandItem.getBrandIndex().getId().longValue() != brandIndexId.longValue() ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.BRANDINDEX);
			oldAttrLog.setAttrValue(brandItem.getBrandIndex().getId().toString());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.BRANDINDEX);
			newAttrLog.setAttrValue(brandIndexId.toString());
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			brandItem.setBrandIndex(brandIndexDao.findById(brandIndexId));
		}
		if( (brandItem.getPriority() == null && priority != null)
				|| (brandItem.getPriority() != null && priority == null)
				|| (brandItem.getPriority() != null && priority != null && brandItem.getPriority().longValue() != priority.longValue() ) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.PRIORITY);
			if( brandItem.getPriority() != null ){
				oldAttrLog.setAttrValue(brandItem.getPriority().toString());
			}
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.PRIORITY);
			if( priority != null ){
				newAttrLog.setAttrValue(priority.toString());
			}
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			brandItem.setPriority(priority);
		}
		if(AttrLogList.size() > 0){
			ProductChangeLog newLog = new ProductChangeLog () ;
			newLog.setUser(getCurrentUser());
			newLog.setRefType(ProductChangeLogType.Brand);
			newLog.setRefId(brandItem.getId());
			newLog = productChangeLogDao.create(newLog) ;
			for( ProductChangeLogAttr LogAttr : AttrLogList ){
				LogAttr.setProdChangeLogId(newLog.getId());
				productChangeLogAttrDao.create(LogAttr);
			}
		}
		brandDao.update(brandItem);
		return new RedirectResolution(BrandManageHome).addParameter("locale", locale);
	}

	public Resolution deleteBrand(){
		brandDao.delete(brandId);
		return backToReferer();
	}
	
	public Resolution createNewBrandRequest(){
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
		brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
		return forward(createNewBrand);
	}
	
	public Resolution createNewBrandIndexRequest(){
		return forward(createNewBrandIndex);
	}
	
	public Resolution submitNewBrand(){
		Brand newBrand = new Brand();
		newBrand.setBrandName(brandName);
		newBrand.setLocale(locale);
		newBrand.setBrandIndex(brandIndexDao.findById(brandIndexId));
		newBrand.setPriority(priority);
		brandDao.create(newBrand);
		return new RedirectResolution(BrandManageHome).addParameter("locale", locale);
	}
	
	public Resolution updateSelectionBox(){
		List<BrandIndex> brandIndexList = brandIndexDao.listAllIndexByLocale(locale) ;
	    return json(brandIndexList); 
	}
	
	@ValidationMethod(on="submitNewBrand")
	public void validateNewBrandCreation(ValidationErrors errors) {
		if( brandName == null ){
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
			brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
			errors.add("brandName", new SimpleError("Brand Name is required!"));
		}
		List<Brand> brandList = brandDao.findBrandByNameLocale(brandName, locale) ;
		if( brandList.size() > 0 ){
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
			brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
			errors.add("brandName", new SimpleError("{0} is already used by other brand!."));
		}
	}
	
	@ValidationMethod(on="submitBrandUpdates")
    public void validateBrandUpdates(ValidationErrors errors) {
		if( brandName == null ){
			brandItem = brandDao.findById(brandId);
			locale = brandItem.getLocale() ;
			priority = brandItem.getPriority();
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
			brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
			errors.add("brandName", new SimpleError("Brand Name is required!"));
		}
		List<Brand> brandList = brandDao.findBrandByNameLocale(brandName, locale) ;
		Boolean otherBrandWithSameName = Boolean.FALSE ;
		for( Brand brandItem : brandList ){
			if( brandId != brandItem.getId() ){
				otherBrandWithSameName = Boolean.TRUE ;
				break;
			}
		}
		if( brandList.size() > 0 && otherBrandWithSameName ){
			brandItem = brandDao.findById(brandId);
			locale = brandItem.getLocale() ;
			priority = brandItem.getPriority();
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			brandIndexList.setResults( brandIndexDao.listAllIndexByLocale(locale) ) ;
			brandIndexList.setTotalSize(brandIndexDao.listAllIndexByLocale(locale).size());
			errors.add("brandName", new SimpleError("{0} is already used by other brand!."));
		}
    }
	
	public PageResult<Brand> getBrandList() {
		return brandList;
	}

	public void setBrandList(PageResult<Brand> brandList) {
		this.brandList = brandList;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Brand getBrandItem() {
		return brandItem;
	}

	public void setBrandItem(Brand brandItem) {
		this.brandItem = brandItem;
	}

	public long getBrandId() {
		return brandId;
	}

	public void setBrandId(long brandId) {
		this.brandId = brandId;
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}

}
