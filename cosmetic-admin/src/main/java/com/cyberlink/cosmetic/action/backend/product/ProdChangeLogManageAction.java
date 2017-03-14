package com.cyberlink.cosmetic.action.backend.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogAttrDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogDao;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.product.model.result.ProductChangeLogWrapper;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/product/ProdChangeLog.action")
public class ProdChangeLogManageAction extends AbstractAction{

	static final String ProdChangeLogManageHome = "/product/ProdChangeLog.action" ;
	static final String ProdChangeLogManageHomePage = "/product/ProdChangeLog-route.jsp" ;
	
	@SpringBean("product.ProductChangeLogDao")
	private ProductChangeLogDao productChangeLogDao ;
	
	@SpringBean("product.ProductChangeLogAttrDao")
	private ProductChangeLogAttrDao productChangeLogAttrDao ;
	
	@SpringBean("user.UserDao")
	private UserDao userdao;
	
	@SpringBean("user.AttributeDao")
	private AttributeDao attributeDao;
	
	private PageResult<ProductChangeLog> productChangeLogList ;
	private PageResult<ProductChangeLogWrapper> wrappedChangeLogList = new PageResult<ProductChangeLogWrapper> ();
	private List<ProductChangeLogType> logTypeList ;
	private List<User> backendUserList = new ArrayList<User>() ;
	
	private Long userId ;
	private ProductChangeLogType itemType = null;
	private Long itemId;
	private Long offset = Long.valueOf(0) ;
	private Long limit = Long.valueOf(20) ;
	private int pages ;
	private Date date ;
	
	@DefaultHandler
	public Resolution route() {
		productChangeLogList = productChangeLogDao.listProdChangeLog(userId, itemType, itemId, offset, limit);
		List<ProductChangeLogWrapper> LogList = new ArrayList<ProductChangeLogWrapper>();
		for(ProductChangeLog log: productChangeLogList.getResults()){
			LogList.add( new ProductChangeLogWrapper(log) );
		}
		wrappedChangeLogList.setResults(LogList);
		wrappedChangeLogList.setTotalSize(productChangeLogList.getTotalSize());
		backendUserList = productChangeLogDao.findUserList();
		updatePageNumbers( wrappedChangeLogList.getTotalSize(), limit.intValue());
		initLogTypeList();
		return forward(ProdChangeLogManageHomePage);
	}
	
	public List<Long> getIdList( List<Attribute> attrList ){
		List<Long> curIdList = new ArrayList<Long> ();
		for(Attribute a : attrList){
			curIdList.add(a.getRefId());
		}
		
		return curIdList;
	}

	public PageResult<ProductChangeLog> getProductChangeLogList() {
		return productChangeLogList;
	}

	public void setProductChangeLogList(PageResult<ProductChangeLog> productChangeLogList) {
		this.productChangeLogList = productChangeLogList;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public PageResult<ProductChangeLogWrapper> getWrappedChangeLogList() {
		return wrappedChangeLogList;
	}

	public void setWrappedChangeLogList(PageResult<ProductChangeLogWrapper> wrappedChangeLogList) {
		this.wrappedChangeLogList = wrappedChangeLogList;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public ProductChangeLogType getItemType() {
		return itemType;
	}

	public void setItemType(ProductChangeLogType itemType) {
		this.itemType = itemType;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public List<ProductChangeLogType> getLogTypeList() {
		return logTypeList;
	}

	public void setLogTypeList(List<ProductChangeLogType> logTypeList) {
		this.logTypeList = logTypeList;
	}
	
	public void initLogTypeList(){
		List<ProductChangeLogType> initLogTypeList = new ArrayList<ProductChangeLogType>();
		initLogTypeList.add(ProductChangeLogType.Product);
		initLogTypeList.add(ProductChangeLogType.Brand);
		initLogTypeList.add(ProductChangeLogType.BrandIndex);
		initLogTypeList.add(ProductChangeLogType.PriceRange);
		initLogTypeList.add(ProductChangeLogType.Type);
		setLogTypeList(initLogTypeList);
		
	}
	
	public void updatePageNumbers(int totalResultSize, int currentLimit){
		if( totalResultSize % currentLimit == 0 ){
        	setPages( (totalResultSize / currentLimit) ) ;
        }
        else{
        	setPages( (totalResultSize / currentLimit) + 1 ) ;
        }
	}
	
	public void checkOffsetLimit( Long offset, Long limit ){
		
	}

	public List<User> getBackendUserList() {
		return backendUserList;
	}

	public void setBackendUserList(List<User> backendUserList) {
		this.backendUserList = backendUserList;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
