package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface ReportedProdCommentDao extends GenericDao<ReportedProdComment, Long>{
	ReportedProdComment findReportedProdCommentByReporterComment(User reporter, ProductComment reportedComment);
	List<ReportedProdComment> findReviewedReportByCommentId( Long commentId );
	List<ReportedProdComment> findReviewedReportByCommentCreatorId( Long creatorId );
	PageResult<ReportedProdComment> listReportedProdCommentByLocaleStatus(String locale, 
			ReportedProdCommentStatus status, Long offset, Long limit);
}
