package com.cyberlink.cosmetic.action.api.product;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ReportedProdCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentReason;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

@UrlBinding("/api/prod/ReportInappropProdComment.action")
public class ReportInappropProdCommentAction extends AbstractAction{
	
	@SpringBean("product.ProductCommentDao")
	private ProductCommentDao commentDao;
	
	@SpringBean("product.ReportedProdCommentDao")
	private ReportedProdCommentDao reportedCommentDao;
	
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	private Long commentId;
	private String reason ;

	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
		if (!authenticate()) {
			return new ErrorResolution(authError);
		}
		if( !commentDao.exists(commentId) ) {
			return new ErrorResolution(ErrorDef.InvalidProdCommentId);
		}
		ReportedProdCommentReason reportedReason = null ;
		switch( reason ) {
			case "Inappropriate":
				reportedReason = ReportedProdCommentReason.Inappropriate;
				break;
			case "Copyright":
				reportedReason = ReportedProdCommentReason.Copyright;
				break;
			case "Other":
				reportedReason = ReportedProdCommentReason.Other;
				break;
			default:
				return new ErrorResolution(ErrorDef.InvalidReportReason);
		}
		ProductComment reportedComment = commentDao.findById(commentId);
		if( reportedComment.getUser().getId().longValue() == getCurrentUserId().longValue() ){
			return new ErrorResolution(ErrorDef.ReportSelfProductReview);
		}
		if( reportedComment.getUser().getUserType() != UserType.Normal ){
			return new ErrorResolution(ErrorDef.ReportCLAccount);
		}
		User reportedUser = userDao.findById(getCurrentUserId()) ;
		if( reportedCommentDao.
				findReportedProdCommentByReporterComment(reportedUser, reportedComment) != null ){
			//return new ErrorResolution(ErrorDef.DuplicatedReportProductReview);
			
			//sync with comment report behavior. return ok if user already reported this comment.
			return success();
		}
		
		ReportedProdComment newProdCommentReport = new ReportedProdComment();
		newProdCommentReport.setReporter(reportedUser);
		newProdCommentReport.setReportedComment(reportedComment);
		newProdCommentReport.setReportReason(reportedReason);
		newProdCommentReport.setReviewStatus(ReportedProdCommentStatus.NewReported);
		reportedCommentDao.create(newProdCommentReport);
		
		return success();
	}
	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
