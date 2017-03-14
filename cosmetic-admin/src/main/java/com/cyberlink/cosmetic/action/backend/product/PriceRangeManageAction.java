package com.cyberlink.cosmetic.action.backend.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

@UrlBinding("/product/PriceRangeManage.action")
public class PriceRangeManageAction extends AbstractAction{
	
	public static final String editPriceRange = "/product/editPriceRange.jsp" ;
	public static final String updatedPriceRange = "/product/updatedPriceRange.jsp" ;
	public static final String createNewPriceRange = "/product/createNewPriceRange.jsp" ;
	public static final String priceRangeManage = "/product/PriceRangeManage.action" ;
	
	@SpringBean("product.StorePriceRangeDao")
	private StorePriceRangeDao storePriceRangeDao;
	
	@SpringBean("product.ProductDao")
	private ProductDao productDao;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;

	private PageResult<StorePriceRange> priceRangeList = new PageResult<StorePriceRange>() ;
	private List<StorePriceRange> priceList = new ArrayList<StorePriceRange>();
	private Set<String> localeList ;
	private String locale = "zh_TW";
	private int offset = 0, limit = 20 ;
	private int pages ;
	private Float priceMin[], priceMax[];
	private String rangeName[];
	
	
	@DefaultHandler
	public Resolution route() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		priceList = storePriceRangeDao.listAllPriceRangeByLocale(locale) ;
		priceRangeList = storePriceRangeDao.listPriceRangeByLocale(locale, Long.valueOf(offset), Long.valueOf(limit) ) ;
		setPages( (priceRangeList.getTotalSize() / limit)+ 1 ) ;
		return forward();
	}
	
	public Resolution updatePriceRangeRequest() {
		priceList = storePriceRangeDao.listAllPriceRangeByLocale(locale) ;
		priceRangeList.setResults(priceList);
		priceRangeList.setTotalSize(priceList.size());
		return forward(editPriceRange);
	}
	
	public Resolution createNewPriceRangeRequest() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		return forward(createNewPriceRange);
	}
	
	public Resolution submitPriceRangeUpdates(){
		List<StorePriceRange> newRangeList = new ArrayList<StorePriceRange>();
		List<StorePriceRange> newRangeIterList = new ArrayList<StorePriceRange>();
		List<StorePriceRange> oldRangeList = storePriceRangeDao.listAllPriceRangeByLocale(locale);
		int i = 0;
		for(Float pMin: priceMin){
			StorePriceRange newRangeItem = new StorePriceRange();
			newRangeItem.setPriceMin(pMin);
			newRangeItem.setPriceMax(priceMax[i]);
			newRangeItem.setRangeName(rangeName[i]);
			newRangeItem.setLocale(locale);
			newRangeList.add(newRangeItem);
			newRangeIterList.add(newRangeItem);
			i++;
		}
		//check if same price range exist on old range
		for( StorePriceRange oldRangeItem:storePriceRangeDao.listAllPriceRangeByLocale(locale) ){
			for( StorePriceRange tempRangeItem:newRangeIterList ){
				if( oldRangeItem.getPriceMax() == tempRangeItem.getPriceMax()
						&& oldRangeItem.getPriceMin() == tempRangeItem.getPriceMin()){
					if( !oldRangeItem.getRangeName().equals(tempRangeItem.getRangeName()) ){
						oldRangeItem.setRangeName(tempRangeItem.getRangeName());
						storePriceRangeDao.update(oldRangeItem);
					}
					oldRangeList.remove(oldRangeItem);
					newRangeList.remove(tempRangeItem);
					break;
				}
			}
		}
		//creating new price ranges
		for( StorePriceRange newRangeItem:newRangeList ){
			storePriceRangeDao.create(newRangeItem);
		}
		//deleting old price ranges
		for( StorePriceRange deletingRange:oldRangeList ){
			storePriceRangeDao.delete(deletingRange);
		}
		newRangeList = storePriceRangeDao.listAllPriceRangeByLocale(locale);
		//update online products with old price ranges......
		for( StorePriceRange deletedRange:oldRangeList ){
			List<Product> updatingProductList = productDao.listProdByLocalePriceRangeId(locale, deletedRange.getId());
			for(Product productItem: updatingProductList){
				for( StorePriceRange range:newRangeList ){
					if( productItem.getPrice() >= range.getPriceMin() 
							&& productItem.getPrice() < range.getPriceMax() ){
						productItem.setPriceRange(range);
						productDao.update(productItem);
						break;
					}
				}
			}
		}

		priceRangeList.setResults(newRangeList);
		priceRangeList.setTotalSize(newRangeList.size());
		return new RedirectResolution(priceRangeManage).addParameter("locale", locale);
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public PageResult<StorePriceRange> getPriceRangeList() {
		return priceRangeList;
	}

	public void setPriceRangeList(PageResult<StorePriceRange> priceRangeList) {
		this.priceRangeList = priceRangeList;
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

	public Float[] getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(Float priceMin[]) {
		this.priceMin = priceMin;
	}

	public Float[] getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(Float priceMax[]) {
		this.priceMax = priceMax;
	}

	public List<StorePriceRange> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<StorePriceRange> priceList) {
		this.priceList = priceList;
	}

	public String[] getRangeName() {
		return rangeName;
	}

	public void setRangeName(String rangeName[]) {
		this.rangeName = rangeName;
	}

	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}

}
