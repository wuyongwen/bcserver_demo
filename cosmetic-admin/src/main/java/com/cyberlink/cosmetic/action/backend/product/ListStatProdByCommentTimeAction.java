package com.cyberlink.cosmetic.action.backend.product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.result.StatProdByLocaleWrapper;
import com.cyberlink.cosmetic.modules.product.model.result.StatisticProductWrapper;

@UrlBinding("/product/ListStatProdByCommentTime.action")
public class ListStatProdByCommentTimeAction extends AbstractAction{
	
	@SpringBean("product.ProductDao")
	private ProductDao productDao;
	
	@SpringBean("product.ProductCommentDao")
	private ProductCommentDao productCommentDao;
	
	private String startTime, endTime ;
	private int offset = 0, limit = 100;
	
	@DefaultHandler
    public Resolution route() {
		//PageLimit pageLimit = new PageLimit(offset, limit);
		final Map<String, Object> results = new HashMap<String, Object>();
		SimpleDateFormat dateTimeParser = new SimpleDateFormat ();
		dateTimeParser.applyLocalizedPattern("yyyy-MM-dd HH:mm:ss");
		//dateTimeParser.setTimeZone(TimeZone.getTimeZone("Taipei"));
		Date startTimeParser = null, endTimeParser = null;
		if( startTime != null && endTime != null ){
			try {
				startTimeParser = dateTimeParser.parse(startTime);
				endTimeParser = dateTimeParser.parse(endTime);
			} catch (ParseException e) {
				//e.printStackTrace();
			}
		
		}
		
		//get ID list
		List<Long> idList = productDao.findProdByCommentTime(startTimeParser, endTimeParser);
		List<Product> prodList = new ArrayList<Product> ();
		if( idList.size() == 0 ){
			results.put("results", idList);
			results.put("totalSize", idList.size());
			return json(results);
		}
		
		int startingOffset ;
		if( offset <= 1 ){
			startingOffset = 0;
		}
		else{
			startingOffset = (offset-1) * limit ;
		}
		int endOffset = startingOffset + limit ;
		if( startingOffset >= idList.size() ){
			results.put("results", prodList);
			results.put("totalSize", prodList.size());
			return json(results);
		}
		if( endOffset > idList.size() ){
			endOffset = idList.size() ;
		}
		
		for( int i = startingOffset; i < endOffset; i++ ){
			Product pItem = productDao.findById(idList.get(i)) ;
			pItem.setProdCommentList(productCommentDao.findProdByCommentTime(pItem, startTimeParser, endTimeParser));
			prodList.add(pItem);
		}
		List<StatProdByLocaleWrapper> statisticProdList = new ArrayList<StatProdByLocaleWrapper>() ;
		for( Product pItem : prodList ){
			statisticProdList.add(new StatProdByLocaleWrapper(pItem)) ;
		}
		results.put("results", statisticProdList);
		results.put("totalSize", idList.size());
		return json(results);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
