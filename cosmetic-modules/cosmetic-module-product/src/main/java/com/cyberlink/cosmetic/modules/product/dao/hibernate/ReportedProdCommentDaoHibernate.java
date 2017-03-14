package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ReportedProdCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus;
import com.cyberlink.cosmetic.modules.user.model.User;

public class ReportedProdCommentDaoHibernate extends AbstractDaoCosmetic<ReportedProdComment, Long> 
implements ReportedProdCommentDao{
	
	private static final String listReportedProdCommentByLocale = 
			"com.cyberlink.cosmetic.modules.product.dao.hibernate."
			+ "ReportedProdCommentDaoHibernate.listReportedProdCommentByLocale" ;
	
	private static final String findReviewedReportByCommentId = 
			"com.cyberlink.cosmetic.modules.product.dao.hibernate."
			+ "ReportedProdCommentDaoHibernate.findReviewedReportByCommentId" ;
	
	private static final String findReviewedReportByCommentCreatorId = 
			"com.cyberlink.cosmetic.modules.product.dao.hibernate."
			+ "ReportedProdCommentDaoHibernate.findReviewedReportByCommentCreatorId" ;

	public ReportedProdComment findReportedProdCommentByReporterComment( User reporter, 
			ProductComment reportedComment ) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "reporter" , reporter ));
		dc.add(Restrictions.eq( "reportedComment" , reportedComment ));
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return uniqueResult(dc);
	}

	public PageResult<ReportedProdComment> listReportedProdCommentByLocaleStatus(
			String locale, ReportedProdCommentStatus status, Long offset, Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.createAlias("reportedComment", "rComment");
		dc.createAlias("rComment.product", "rProduct");
		dc.add(Restrictions.eq( "rProduct.locale" , locale )) ;
		if( status != null ){
			dc.add(Restrictions.eq( "reviewStatus" , status )) ;
		}
		return findByCriteria(dc, offset, limit, listReportedProdCommentByLocale);
	}

	public List<ReportedProdComment> findReviewedReportByCommentId(
			Long commentId) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "reportedComment.id" , commentId ));
		dc.add(Restrictions.eq( "reviewStatus" , ReportedProdCommentStatus.Reviewed )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc, "findReviewedReportByCommentId");
	}

	public List<ReportedProdComment> findReviewedReportByCommentCreatorId(Long creatorId) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.createAlias("reportedComment", "rComment");
		dc.createAlias("rComment.user", "rCreator");
		dc.add(Restrictions.eq( "rCreator.id" , creatorId )) ;
		dc.add(Restrictions.eq( "reviewStatus" , ReportedProdCommentStatus.Reviewed )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc, "findReviewedReportByCommentCreatorId");
	}
}
