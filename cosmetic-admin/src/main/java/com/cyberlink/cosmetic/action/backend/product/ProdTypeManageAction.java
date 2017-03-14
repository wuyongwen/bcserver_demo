package com.cyberlink.cosmetic.action.backend.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.IntegerTypeConverter;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogAttrDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttr;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttrStatus;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttrType;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.action.backend.AbstractAction;

@UrlBinding("/product/ProdTypeManage.action")
public class ProdTypeManageAction extends AbstractAction{

	static final String ProdTypeManageHome = "/product/ProdTypeManage.action" ;
	static final String editTypeInfo = "/product/editProdTypeInfo.jsp" ;
	static final String createNewType = "/product/createNewProdType.jsp" ;
	
	
	private PageResult<ProductType> typeList;
	private int offset = 0, limit = 20 ;
	private int pages ;
	private Long typeId ;
	private String locale = "zh_TW" ;
	private String typeName ;
	private Set<String> localeList ;
	private ProductType typeItem ;
	private Integer sortPriority ;

	@SpringBean("product.ProductTypeDao")
	protected ProductTypeDao productTypeDao;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;

	@SpringBean("product.ProductChangeLogDao")
	private ProductChangeLogDao productChangeLogDao ;
	
	@SpringBean("product.ProductChangeLogAttrDao")
	private ProductChangeLogAttrDao productChangeLogAttrDao ;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	@DefaultHandler
	public Resolution route() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		typeList = (productTypeDao.listAllProdTypeByLocale(locale, offset, limit));
		setPages( (typeList.getTotalSize() / limit)+ 1 ) ;
        return forward();
    }
	
	public Resolution changeLocale(){
		return new RedirectResolution(ProdTypeManageHome).addParameter("locale", locale);
		
	}
	
	public Resolution updateProdTypeRequest(){
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		typeItem = productTypeDao.findById(typeId);
		sortPriority = typeItem.getSortPriority() ;
		locale = typeItem.getLocale();
		return forward(editTypeInfo);
	}
	
	public Resolution submitProdTypeUpdates(){
		List<ProductChangeLogAttr> AttrLogList = new ArrayList<ProductChangeLogAttr> ();
		typeItem = productTypeDao.findById(typeId);
		if( !typeItem.getTypeName().equals(typeName) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.PRODTYPENAME);
			oldAttrLog.setAttrValue(typeItem.getTypeName());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.PRODTYPENAME);
			newAttrLog.setAttrValue(typeName);
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			typeItem.setTypeName(typeName);
		}
		if( !typeItem.getLocale().equals(locale) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.LOCALE);
			oldAttrLog.setAttrValue(typeItem.getLocale());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.LOCALE);
			newAttrLog.setAttrValue(locale);
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			typeItem.setLocale(locale);
		}
		if( ( typeItem.getSortPriority() == null && sortPriority != null)
				|| ( typeItem.getSortPriority() != null && sortPriority == null ) 
				|| ( typeItem.getSortPriority() != null && sortPriority != null 
				&& typeItem.getSortPriority().intValue() != sortPriority.intValue() ) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.PRIORITY);
			if( typeItem.getSortPriority() != null ){
				oldAttrLog.setAttrValue(typeItem.getSortPriority().toString());
			}
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.PRIORITY);
			if( sortPriority != null ){
				newAttrLog.setAttrValue(sortPriority.toString());
			}
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
			typeItem.setSortPriority(sortPriority);
		}
		if(AttrLogList.size() > 0){
			ProductChangeLog newLog = new ProductChangeLog () ;
			newLog.setUser(getCurrentUser());
			newLog.setRefType(ProductChangeLogType.Type);
			newLog.setRefId(typeItem.getId());
			newLog = productChangeLogDao.create(newLog) ;
			for( ProductChangeLogAttr LogAttr : AttrLogList ){
				LogAttr.setProdChangeLogId(newLog.getId());
				productChangeLogAttrDao.create(LogAttr);
			}
		}
		productTypeDao.update(typeItem);
		return new RedirectResolution(ProdTypeManageHome).addParameter("locale", locale);
	}

	public Resolution deleteProdType(){
		productTypeDao.delete(typeId);
		return backToReferer();
	}

	public Resolution createNewProdTypeRequest(){
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		return forward(createNewType);
	}
	
	public Resolution submitNewProdType(){
		ProductType newProdType = new ProductType();
		newProdType.setTypeName(typeName);
		newProdType.setLocale(locale);
		newProdType.setSortPriority(sortPriority);
		productTypeDao.create(newProdType);
		return new RedirectResolution(ProdTypeManageHome).addParameter("locale", locale);
	}
	
	@ValidationMethod(on="submitProdTypeUpdates")
	public void validateProdTypeUpdates(ValidationErrors errors) {
		if(typeName == null){
			typeItem = productTypeDao.findById(typeId);
			sortPriority = typeItem.getSortPriority() ;
			locale = typeItem.getLocale();
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			errors.add("typeName", new SimpleError("Category Name is required!"));
		}
		List<ProductType> typeList = productTypeDao.listProdTypeByTypeNameLocale(typeName, locale) ;
		Boolean otherTypeWithSameName = Boolean.FALSE ;
		for( ProductType typeItem : typeList ){
			if( typeItem.getId().longValue() != typeId.longValue() ){
				otherTypeWithSameName = Boolean.TRUE;
				break;
			}
		}
		if( typeList.size() > 0 && otherTypeWithSameName ){
			typeItem = productTypeDao.findById(typeId);
			sortPriority = typeItem.getSortPriority() ;
			locale = typeItem.getLocale();
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			errors.add("typeName", new SimpleError("Category Name is already existed!"));
		}
	}
	
	@ValidationMethod(on="submitNewProdType")
	public void validateNewProdTypeCreation(ValidationErrors errors) {
		if(typeName == null){
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			
			errors.add("typeName", new SimpleError("Category Name is required!"));
		}
		
		if( productTypeDao.listProdTypeByTypeNameLocale(typeName, locale).size() > 0 ){
			localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
			errors.add("typeName", new SimpleError("Category Name is already existed!"));
		}
		
	}

	public PageResult<ProductType> getTypeList() {
		return typeList;
	}

	public void setTypeList(PageResult<ProductType> typeList) {
		this.typeList = typeList;
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

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public ProductType getTypeItem() {
		return typeItem;
	}

	public void setTypeItem(ProductType typeItem) {
		this.typeItem = typeItem;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public Integer getSortPriority() {
		return sortPriority;
	}

	public void setSortPriority(Integer sortPriority) {
		this.sortPriority = sortPriority;
	}

	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}
}
