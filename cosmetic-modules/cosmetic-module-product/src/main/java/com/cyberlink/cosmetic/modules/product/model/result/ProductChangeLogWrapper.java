package com.cyberlink.cosmetic.modules.product.model.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttr;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.fasterxml.jackson.annotation.JsonView;

public class ProductChangeLogWrapper {
	
	private static BrandDao brandDao = 
			BeanLocator.getBean("product.BrandDao");
	private static ProductTypeDao productTypeDao = 
			BeanLocator.getBean("product.ProductTypeDao");
	private ProductChangeLog changeLog;
	private List<String> beforeValueList ;
	private List<String> afterValueList ; 

	public ProductChangeLogWrapper( ProductChangeLog logItem ){
		setChangeLog(logItem);
		setBeforeValueList( getBeforeValues() );
		setAfterValueList( getAfterValues() );
	}
	
	@JsonView(Views.Public.class)
	public Long getId(){
		return getChangeLog().getId() ;
	}
	
	@JsonView(Views.Public.class)
	public Date getTime(){
		return getChangeLog().getLastModified() ;
	}
	
	@JsonView(Views.Public.class)
	public Long getUserId(){
		return getChangeLog().getUser().getId() ;
	}
	
	@JsonView(Views.Public.class)
	public String getUserName(){
		return getChangeLog().getUser().getDisplayName() ;
	}
	
	@JsonView(Views.Public.class)
	public String getChangedItemName(){
		return getChangeLog().getRefType().toString() ;
	}
	
	@JsonView(Views.Public.class)
	public String getChangeItemId(){
		return getChangeLog().getRefId().toString();
		
	}
	
	@JsonView(Views.Public.class)
	public List<String> getBeforeValues(){
		List<String> oriValues = new ArrayList<String> ();
		for( ProductChangeLogAttr attr: getChangeLog().getAttrList() ){
			if( attr.getStatus().toString() == "BEFORE" ){
				String attrNameValue = "" ;
				switch( attr.getAttrName() ){
					case "Brand" :
						attrNameValue += "Brand: " ;
						//String brandName =  ;
						Brand brandItem = brandDao.findById(Long.valueOf(attr.getAttrValue())) ;
						attrNameValue += brandItem.getBrandName() ;
						break;
					case "ProdType" :
						attrNameValue += "Product Type: " ;
						ProductType typeItem = new ProductType();
						Long typeId = Long.valueOf(attr.getAttrValue()) ;
						typeItem = productTypeDao.findById( typeId );
						attrNameValue += typeItem.getTypeName();
						break; 
					case "OnShelf" :
						attrNameValue += "On Shelf: " ;
						if( Boolean.valueOf(attr.getAttrValue()) ){
							attrNameValue += "Yes" ;
						}
						else{
							attrNameValue += "No" ;
						}
						break; 
					case "ProdTitle" :
						attrNameValue += "Product Title: " + attr.getAttrValue() ;
						break;
					case "Priority" :
						attrNameValue += "Priority: " + attr.getAttrValue() ;
						break;
					case "BrandName" :
						attrNameValue += "Brand Name: " + attr.getAttrValue() ;
						break;
					case "BrandIndex":
						attrNameValue += "Brand Index: " + attr.getAttrValue() ;
						break;
					case "Locale":
						attrNameValue += "Locale: " + attr.getAttrValue() ;
						break;
					case "ProdTypeName":
						attrNameValue += "Category Name: " + attr.getAttrValue() ;
						break;
					default:
						break;
				}
				oriValues.add(attrNameValue);
			}
			
		}
		return oriValues;
	}
	
	@JsonView(Views.Public.class)
	public List<String> getAfterValues(){
		List<String> afterValues = new ArrayList<String> ();
		for( ProductChangeLogAttr attr: getChangeLog().getAttrList() ){
			if( attr.getStatus().toString() == "AFTER" ){
				String attrNameValue = "" ;
				switch( attr.getAttrName() ){
					case "Brand" :
						attrNameValue += "Brand: " ;
						//String brandName =  ;
						Brand brandItem = brandDao.findById(Long.valueOf(attr.getAttrValue())) ;
						attrNameValue += brandItem.getBrandName() ;
						break;
					case "ProdType" :
						attrNameValue += "Product Type: " ;
						ProductType typeItem = new ProductType();
						Long typeId = Long.valueOf(attr.getAttrValue()) ;
						typeItem = productTypeDao.findById( typeId );
						attrNameValue += typeItem.getTypeName();
						break; 
					case "OnShelf" :
						attrNameValue += "On Shelf: " ;
						if( Boolean.valueOf(attr.getAttrValue()) ){
							attrNameValue += "Yes" ;
						}
						else{
							attrNameValue += "No" ;
						}
						break; 
					case "ProdTitle" :
						attrNameValue += "Product Title: " + attr.getAttrValue() ;
						break;
					case "Priority" :
						attrNameValue += "Priority: " + attr.getAttrValue() ;
						break;
					case "BrandName" :
						attrNameValue += "Brand Name: " + attr.getAttrValue() ;
						break;
					case "BrandIndex":
						attrNameValue += "Brand Index: " + attr.getAttrValue() ;
						break;
					case "Locale":
						attrNameValue += "Locale: " + attr.getAttrValue() ;
						break;
					case "ProdTypeName":
						attrNameValue += "Category Name: " + attr.getAttrValue() ;
						break;
					default:
						break;
				}
				afterValues.add(attrNameValue);
			}
			
		}
		return afterValues;
	}
	
	public ProductChangeLog getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(ProductChangeLog changeLog) {
		this.changeLog = changeLog;
	}

	public List<String> getBeforeValueList() {
		return beforeValueList;
	}

	public void setBeforeValueList(List<String> beforeValueList) {
		this.beforeValueList = beforeValueList;
	}

	public List<String> getAfterValueList() {
		return afterValueList;
	}

	public void setAfterValueList(List<String> afterValueList) {
		this.afterValueList = afterValueList;
	}
	
}
