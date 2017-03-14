package com.cyberlink.cosmetic.action.backend.product;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.cyberlink.core.web.view.page.PageResult;
//import com.cyberlink.cosmetic.action.api.product.result.ProductWrapper;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogAttrDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.RelProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.SolrProductDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttr;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttrStatus;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogAttrType;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.product.model.ProductProductEffect;
import com.cyberlink.cosmetic.modules.product.model.ProductSearchParam;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;
import com.cyberlink.cosmetic.modules.product.service.SolrProductUpdater;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/product/ProductManage.action")
public class ProductManageAction extends AbstractAction{
	
	static final String ProductManageHome = "/product/ProductManage.action" ;
	static final String ProductManageHomePage = "/product/ProductManage-route.jsp" ;
	static final String editProdInfo = "/product/editProductInfo.jsp" ;
	static final String seeProdDetail = "/product/seeProdDetailInfo.jsp" ;
    //@SpringBean("product.solrProductDao")
    //private SolrProductDao solrDao;
	
	@SpringBean("product.ProductDao")
	private ProductDao productDao;
	
	@SpringBean("product.BrandDao")
	private BrandDao brandDao;
	
	@SpringBean("product.ProductTypeDao")
	private ProductTypeDao productTypeDao;
	
	@SpringBean("product.RelProductTypeDao")
	private RelProductTypeDao relProductTypeDao ;
	
	@SpringBean("product.StorePriceRangeDao")
    protected StorePriceRangeDao storePriceRangeDao;
	
	//@SpringBean("product.solrProductUpdater")
	//protected SolrProductUpdater solrProductUpdate;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("product.ProductChangeLogDao")
	private ProductChangeLogDao productChangeLogDao ;
	
	@SpringBean("product.ProductChangeLogAttrDao")
	private ProductChangeLogAttrDao productChangeLogAttrDao ;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	private PageResult<Product> productList;
	private List<Brand> brandList ;
	private List<ProductType> prodTypeList ;
	private Set<String> localeList ;
	private List<StorePriceRange> storePriceRangeList ;
	private List<StorePriceRange> selectedPriceRangeList ;
	private int offset = 0, limit = 100 ;
	private Float priceMin = null , priceMax = null ;
	private Long priceRangeId = null ;
	private Product productItem ;
	
	private long productId;
	private int pages ;

	private String locale="zh_TW";
	private Long typeGroupId;
	private String productName;
	private String productTitle;
	private String productDescription;
	private String img_thumbnail;
	private String img_original;
	private Long barCode;
	private String TrialOnYCMakeUp;
	private Long brandId;
	private Long brandIdForQuery;
	private Store store;
	private String productStoreLink ;
	private Float price;
	private Long newProdTypeId[];
	private Long typeId ;
	private List <ProductProductEffect> productProductEffect = new ArrayList<ProductProductEffect>(0);
	private Long extProdID;
	private Boolean onShelfForQuery = null ;
	private Boolean onShelf;
	private String searchKeyword;
	private int prodTypeSize = 0 ;
	
	@DefaultHandler
	public Resolution route() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
	    storePriceRangeList = storePriceRangeDao.listAllPriceRangeByLocale(locale);
	    selectedPriceRangeList = storePriceRangeDao.listPriceRangeByIdLocale(priceRangeId, locale);

		if (searchKeyword != null && searchKeyword.length() > 0) {
	    	ProductSearchParam param = new ProductSearchParam();
	    	param.setKeyword(searchKeyword);
	    	param.setOffset(offset);
	    	param.setPageSize(limit);
	    	param.setLocale(locale);
			//productList = solrDao.searchProductWithFilter(param, brandIdForQuery, typeId, priceRangeId);
	    	productList = productDao.findProdByParameters(locale, brandIdForQuery, typeId, null, Long.valueOf(offset),
					Long.valueOf(limit), onShelfForQuery, selectedPriceRangeList );	    	
		}
		else {
			productList = productDao.findProdByParameters(locale, brandIdForQuery, typeId, null, Long.valueOf(offset),
						Long.valueOf(limit), onShelfForQuery, selectedPriceRangeList );
		}
		setBrandList(brandDao.listAllBrandByLocale(locale)) ;
		setProdTypeList(productTypeDao.listAllProdTypeByLocale(locale)) ;
        if( productList.getTotalSize() % limit == 0 ){
        	setPages( (productList.getTotalSize() / limit) ) ;
        }
        else{
        	setPages( (productList.getTotalSize() / limit) + 1 ) ;
        }
		return forward();
    }
	
	public Resolution searchByKeyword(){
		return new RedirectResolution(ProductManageHome).addParameter("locale", locale)
				.addParameter("searchKeyword", searchKeyword).addParameter("brandIdForQuery", brandIdForQuery)
				.addParameter("typeId", typeId).addParameter("priceRangeId", priceRangeId);
	}
	
	public Resolution updateProdRequest(){
		productItem = productDao.findById(productId);
		prodTypeSize = productItem.getRelProductType().size();
		onShelf = productItem.getOnShelf() ;
		setBrandList(brandDao.listAllBrandByLocale(locale)) ;
		setProdTypeList(productTypeDao.listAllProdTypeByLocale(locale)) ;
		return forward(editProdInfo);
	}
	
	public Resolution submitProductUpdates(){
		Product tempProductItem = new Product();
		tempProductItem = productDao.findById(productId);
		Long oldBrandId = tempProductItem.getBrand().getId();
		Boolean oldOnShelfFlag = tempProductItem.getOnShelf() ;
		String oldProdTitle = tempProductItem.getProductTitle() ;
		
		List<ProductChangeLogAttr> AttrLogList = new ArrayList<ProductChangeLogAttr> ();
		if( !oldProdTitle.equals(productTitle) ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.PRODTITLE);
			oldAttrLog.setAttrValue(oldProdTitle);
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.PRODTITLE);
			newAttrLog.setAttrValue(productTitle);
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
		}
		if( oldOnShelfFlag.booleanValue() != onShelf.booleanValue() ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.ONSHELF);
			oldAttrLog.setAttrValue(oldOnShelfFlag.toString());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.ONSHELF);
			newAttrLog.setAttrValue(onShelf.toString());
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
		}
		if( oldBrandId.longValue() != brandId.longValue() ){
			ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
			ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
			oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
			oldAttrLog.setAttrName(ProductChangeLogAttrType.BRAND);
			oldAttrLog.setAttrValue(oldBrandId.toString());
			newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
			newAttrLog.setAttrName(ProductChangeLogAttrType.BRAND);
			newAttrLog.setAttrValue(brandId.toString());
			AttrLogList.add(oldAttrLog);
			AttrLogList.add(newAttrLog);
		}
		tempProductItem.setBrand(brandDao.findById(brandId));
		tempProductItem.setOnShelf(onShelf);
		tempProductItem.setProductTitle(productTitle);
		productDao.update(tempProductItem);
		
		//get current configuration of type id list
		List<RelProductType> oldRelProdTypeList = relProductTypeDao.findByProdID(productId);
		//duplicate id list to have a copy for ATTR creating part
		List<Long> oldRelProdTypeIdList = new ArrayList<Long>();
		for( RelProductType type: oldRelProdTypeList ){
			oldRelProdTypeIdList.add(type.getProductType().getId());
		}
		//use set to get the input list
		Set<Long> tempNewTidSet = new HashSet<Long>();
		for( Long e : Arrays.asList(newProdTypeId) ){
			tempNewTidSet.add(e);
		}
		//get elements from set to reduce the duplcated values of Id list
		List<Long> newTypeIdList = new ArrayList<Long> ();
		for( Long e : tempNewTidSet ){
			newTypeIdList.add(e);
		}
		//duplicate a list of non-redundant new type Id list
		List<Long> NewRelProdTypeIdList = new ArrayList<Long>();
		for( Long e : newTypeIdList ){
			NewRelProdTypeIdList.add(e);
		}
		//start to update product-type relation, also remove old product types relations
		//then add new product-type relations
		for( int i=0; i<NewRelProdTypeIdList.size(); i++ ){
			for( int j=0; j<oldRelProdTypeList.size(); j++ ){
				if( NewRelProdTypeIdList.get(i).longValue() == oldRelProdTypeList.get(j).getProductType().getId().longValue() ){
					oldRelProdTypeList.remove(oldRelProdTypeList.get(j));
					break;
				}
			}
		}
		
		for( int i=0; i<oldRelProdTypeIdList.size(); i++ ){
			for( int j=0; j<newTypeIdList.size(); j++ ){
				if( oldRelProdTypeIdList.get(i).longValue() == newTypeIdList.get(j).longValue() ){
					newTypeIdList.remove(newTypeIdList.get(j));
					break;
				}
			}
		}
		//if there's difference. means Ids are updating. need to create log
		if( newTypeIdList.size() > 0 || oldRelProdTypeList.size() > 0){
			for( Long oldTID : oldRelProdTypeIdList ){
				ProductChangeLogAttr oldAttrLog = new ProductChangeLogAttr();
				oldAttrLog.setStatus(ProductChangeLogAttrStatus.BEFORE);
				oldAttrLog.setAttrName(ProductChangeLogAttrType.PRODTYPE);
				oldAttrLog.setAttrValue(oldTID.toString());
				AttrLogList.add(oldAttrLog);
			}
			for( Long newTID: NewRelProdTypeIdList ){
				ProductChangeLogAttr newAttrLog = new ProductChangeLogAttr();
				newAttrLog.setStatus(ProductChangeLogAttrStatus.AFTER);
				newAttrLog.setAttrName(ProductChangeLogAttrType.PRODTYPE);
				newAttrLog.setAttrValue(newTID.toString());
				AttrLogList.add(newAttrLog);
			}
		}
		//delete old type id relation
		for(RelProductType deleteRelProdType: oldRelProdTypeList){
			deleteRelProdType.setIsDeleted(true);
			relProductTypeDao.update(deleteRelProdType);
		}
		//create new type id relation
		for(Long newTypeID: newTypeIdList){
			if( relProductTypeDao.findByProdIDTypeID(productId, newTypeID) == null ){
				RelProductType newRelProductType = new RelProductType();
				newRelProductType.setProductId(productId);
				newRelProductType.setProductType(productTypeDao.findById(newTypeID));
				relProductTypeDao.create(newRelProductType);
			}
		}
		//create log if there's change
		if(AttrLogList.size() > 0){
			ProductChangeLog newLog = new ProductChangeLog () ;
			newLog.setUser(getCurrentUser());
			newLog.setRefType(ProductChangeLogType.Product);
			newLog.setRefId(productId);
			newLog = productChangeLogDao.create(newLog) ;
			for( ProductChangeLogAttr LogAttr : AttrLogList ){
				LogAttr.setProdChangeLogId(newLog.getId());
				productChangeLogAttrDao.create(LogAttr);
			}
		}
		productItem = tempProductItem ;
		//update search engine item
		//solrProductUpdate.update(productId);
		return forward(seeProdDetail);
	}
	
	public Resolution seeProductDetail(){
		productItem = productDao.findById(productId);
		return forward(seeProdDetail);
	}

	public Resolution deleteProduct(){
		Product deletingProduct = productDao.findById(productId);
		deletingProduct.setOnShelf(false);
		productDao.delete(deletingProduct);

		//solrProductUpdate.delete(productId);
		return backToReferer();
	}
	
	public Resolution addNewSelectionBox(){
		List<ProductType> typeList = productTypeDao.listAllProdTypeByLocale(locale) ;
	    return json(typeList); 
	}
	
	public PageResult<Product> getProductList() {
		return productList;
	}

	public void setProductList(PageResult<Product> productList) {
		this.productList = productList;
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

	public Product getProductItem() {
		return productItem;
	}

	public void setProductItem(Product productItem) {
		this.productItem = productItem;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getImg_thumbnail() {
		return img_thumbnail;
	}

	public void setImg_thumbnail(String img_thumbnail) {
		this.img_thumbnail = img_thumbnail;
	}

	public Long getBarCode() {
		return barCode;
	}

	public void setBarCode(Long barCode) {
		this.barCode = barCode;
	}

	public String getImg_original() {
		return img_original;
	}

	public void setImg_original(String img_original) {
		this.img_original = img_original;
	}

	public String getTrialOnYCMakeUp() {
		return TrialOnYCMakeUp;
	}

	public void setTrialOnYCMakeUp(String trialOnYCMakeUp) {
		TrialOnYCMakeUp = trialOnYCMakeUp;
	}

	public Boolean getOnShelf() {
		return onShelf;
	}

	public void setOnShelf(Boolean onShelf) {
		this.onShelf = onShelf;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public List<Brand> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<Brand> brandList) {
		this.brandList = brandList;
	}

	public List<ProductType> getProdTypeList() {
		return prodTypeList;
	}

	public void setProdTypeList(List<ProductType> prodTypeList) {
		this.prodTypeList = prodTypeList;
	}

	public Float getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(Float priceMin) {
		this.priceMin = priceMin;
	}

	public Float getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(Float priceMax) {
		this.priceMax = priceMax;
	}

	public Long[] getNewProdTypeId() {
		return newProdTypeId;
	}

	public void setNewProdTypeId(Long newProdTypeId[]) {
		this.newProdTypeId = newProdTypeId;
	}

	public List<StorePriceRange> getStorePriceRangeList() {
		return storePriceRangeList;
	}

	public void setStorePriceRangeList(List<StorePriceRange> storePriceRangeList) {
		this.storePriceRangeList = storePriceRangeList;
	}
	
	public Long getPriceRangeId() {
		return priceRangeId;
	}

	public void setPriceRangeId(Long priceRangeId) {
		this.priceRangeId = priceRangeId;
	}

	public List<StorePriceRange> getSelectedPriceRangeList() {
		return selectedPriceRangeList;
	}

	public void setSelectedPriceRangeList(List<StorePriceRange> selectedPriceRangeList) {
		this.selectedPriceRangeList = selectedPriceRangeList;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public int getProdTypeSize() {
		return prodTypeSize;
	}

	public void setProdTypeSize(int prodTypeSize) {
		this.prodTypeSize = prodTypeSize;
	}

	public Boolean getOnShelfForQuery() {
		return onShelfForQuery;
	}

	public void setOnShelfForQuery(Boolean onShelfForQuery) {
		this.onShelfForQuery = onShelfForQuery;
	}

	public Long getBrandIdForQuery() {
		return brandIdForQuery;
	}

	public void setBrandIdForQuery(Long brandIdForQuery) {
		this.brandIdForQuery = brandIdForQuery;
	}

	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}
	
}