package com.cyberlink.cosmetic.action.backend.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.result.StatisticProductWrapper;

@UrlBinding("/product/ListStatisticProduct.action")
public class ListStatisticProductAction extends AbstractAction{
	@SpringBean("product.ProductDao")
	private ProductDao productDao;
	
	private int offset = 0, limit = 100;
	
	@DefaultHandler
    public Resolution route() {
		PageLimit pageLimit = new PageLimit(offset, limit);
		final Map<String, Object> results = new HashMap<String, Object>();
		PageResult<Product> prodList = productDao.findAllProduct(pageLimit);
		List<StatisticProductWrapper> statisticProdList = new ArrayList<StatisticProductWrapper>() ;
		for( Product pItem : prodList.getResults() ){
			statisticProdList.add(new StatisticProductWrapper(pItem)) ;
		}
		results.put("results", statisticProdList);
		results.put("totalSize", prodList.getTotalSize());
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
}
