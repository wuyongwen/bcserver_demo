package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.result.ProductCommentWrapper;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/ListComment.action")
public class ListProductCommentAction extends AbstractAction{
	@SpringBean("product.ProductCommentDao")
	private ProductCommentDao commentDao;

	@SpringBean("user.AttributeDao")
    private AttributeDao attributeDao;

	@SpringBean("product.ProductDao")
    private ProductDao productDao;
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
	
	private Long productId;
	private Long userId;
	private Long offset = Long.valueOf(0);
	private Long limit = Long.valueOf(10);
	private String apiVersion = "" ;
	
	@DefaultHandler
	public Resolution route() {
		if (!productDao.exists(productId)) {
			return new ErrorResolution(ErrorDef.InvalidProductId);
		}
		final Map<String, Object> results = new HashMap<String, Object>();
		if (apiVersion == null || apiVersion.length() == 0 || apiVersion.equals("1.0")) {
			PageResult<ProductComment> pageResult = commentDao.findByProductId(productId, userId, offset, limit);
			List<ProductCommentWrapper> commentList = new ArrayList<ProductCommentWrapper> ();
			for( ProductComment comment: pageResult.getResults() ){
				commentList.add(new ProductCommentWrapper(comment));
			}
			results.put("results", commentList);
			results.put("totalSize", pageResult.getTotalSize());
			return json(results);			
		} else {
			PageResult<ProductComment> pageResult = commentDao.findByProductId(productId, null, offset, limit);
			List<Long> creatorIdList = getCreatorIdList(pageResult.getResults());
			if(offset.longValue() == 0 && userId != null){
				List<ProductComment> userComment = commentDao.findByProductId(productId, userId, offset, limit).getResults();
				if( userComment.size() > 0 ){
					userComment.get(0).getUser().setCurUserId(userId);
					ProductCommentWrapper userCommentWrapper = new ProductCommentWrapper(userComment.get(0));
					results.put("userComment", userCommentWrapper);
				}
			}
			List<ProductCommentWrapper> commentList = new ArrayList<ProductCommentWrapper> ();
			for( ProductComment comment: pageResult.getResults() ){
				if( userId != null ){
					Set<Long> followedIdList = subscribeDao.findIdBySubscriberAndSubscribees(userId, SubscribeType.User, creatorIdList.toArray(new Long[creatorIdList.size()]) );
					comment.getUser().setCurUserId(userId);
					if( followedIdList.contains(comment.getUser().getId()) ){
						comment.getUser().setIsFollowed(Boolean.TRUE);
					}
				}
				commentList.add(new ProductCommentWrapper(comment));
			}
			results.put("results", commentList);
			results.put("totalSize", pageResult.getTotalSize());
			return json(results);
		}
	}
	
	public List<Long> getCreatorIdList( List<ProductComment> commentList ){
		List<Long> idList = new ArrayList<Long>();
		for( ProductComment c : commentList ){
			idList.add(c.getUser().getId());
		}
		return idList;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
}
