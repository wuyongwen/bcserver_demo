package com.cyberlink.cosmetic.action.backend.product;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.action.backend.AbstractAction;

@UrlBinding("/product/ProdCommentRatingManage.action")
public class ProdCommentRatingManageAction extends AbstractAction{
	
	@SpringBean("product.ProductCommentDao")
	private ProductCommentDao commentDao;

	@SpringBean("user.AttributeDao")
    private AttributeDao attributeDao;

	@SpringBean("product.ProductDao")
    private ProductDao productDao;
	
	private static final String ProdCommentRatingManage = "/product/ProdCommentRatingManage.action" ;
	private static final String ProdCommentRatingManagePage = "/product/ProdCommentManage-route.jsp" ;
	
	private PageResult<ProductComment> commentList ;
	private Long commentId;
	private Long productId ;
	private ProductWrapper targetProductItem ;
	private int offset = 0, limit = 20 ;
	private int pages ;
	
	@DefaultHandler
	public Resolution route() {
		targetProductItem = new ProductWrapper(productDao.findById(productId)) ;
		commentList = commentDao.findByProductId(productId, null, Long.valueOf(offset), Long.valueOf(limit)) ;
		setPages( (commentList.getTotalSize() / limit)+ 1 ) ;
		return forward(ProdCommentRatingManagePage);
	}

	public Resolution deleteComment() {
		commentDao.delete(commentId);
		
		return backToReferer();
	}
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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

	public static String getProdcommentratingmanage() {
		return ProdCommentRatingManage;
	}

	public static String getProdcommentratingmanagepage() {
		return ProdCommentRatingManagePage;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public PageResult<ProductComment> getCommentList() {
		return commentList;
	}

	public void setCommentList(PageResult<ProductComment> commentList) {
		this.commentList = commentList;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public ProductWrapper getTargetProductItem() {
		return targetProductItem;
	}

	public void setTargetProductItem(ProductWrapper targetProductItem) {
		this.targetProductItem = targetProductItem;
	}
}
