package com.cyberlink.cosmetic.modules.product.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus;

public interface ProductCommentDao extends GenericDao<ProductComment, Long>{
	ProductComment findByProductIdAndUserId(Long userId, Long... productId);
	PageResult<ProductComment> findByProductId(Long productId, Long userId, Long offset, Long limit);
	PageResult<ProductComment> findByReportedComments(String locale, ReportedProdCommentStatus status, Long offset, Long limit);
	List<ProductComment> findProdByCommentTime(Product product, Date startTime, Date endTime);
	PageResult<ProductComment> findByCreatorId(Long creatorId, Long offset, Long limit);
	PageResult<ProductComment> findByReportedByCreatorId(Long creatorId, Long offset, Long limit);
	List<ProductComment> getAllProductCommentsByDate(String locale, Date startTime, Date endTime);
}
