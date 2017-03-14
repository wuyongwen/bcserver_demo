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
import com.cyberlink.cosmetic.modules.mail.service.MailInappropProdCommentService;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ReportedProdCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentStatus;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdComment;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentReason;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentResult;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/product/ReportedProdCommentManage.action")
public class ReportedProdCommentManageAction extends AbstractAction{
	
	private static final String reportedProdCommentManageHome = "/product/ReportedProdCommentManage.action" ;
	private static final String reportedProdCommentManagePage = "/product/ReportedProdCommentManage-route.jsp" ;
	
	@SpringBean("user.UserDao")
    private UserDao userDao;
	
	@SpringBean("user.AccountDao")
    private AccountDao accountDao;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("product.ProductCommentDao")
	private ProductCommentDao commentDao;
	
	@SpringBean("product.ReportedProdCommentDao")
	private ReportedProdCommentDao reportedCommentDao;
	
	@SpringBean("mail.mailInappropProdCommentService")
	protected MailInappropProdCommentService mailService;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	private String locale = "zh_TW";
	private ReportedProdCommentStatus reviewStatus = ReportedProdCommentStatus.NewReported ;
	
	private PageResult<ProductComment> dbCommentList = new PageResult<ProductComment>();
	private Set<String> localeList ;
	private int offset = 0;
	private int limit = 20;
	private int pages ;
	private Long userId ;
	private Boolean showOnlyReportedComments;
	private String userEmail ;
	private Long[] reportCommentId ;
	private ReportedProdCommentStatus[] ticketReviewStatus;
	private String[] ticketReviewResult;
	
	private List<ProductComment> reviewedBannedCommentList = new ArrayList<ProductComment> ();
	private List<ProductComment> reviewedPublishedCommentList = new ArrayList<ProductComment> ();
	private int sizeOfReviewedBannedCommentList ;
	private int sizeOfReviewedPublishedCommentList ;
	
	private List<User> creatorList = new ArrayList<User> ();
	private List<ProductComment> specificUserCommentList = new ArrayList<ProductComment> ();
	
	private Long[] reviewedBannedCommentIdList ;
	private Long[] reviewedPublishedCommentIdList ;
	
	@DefaultHandler
	public Resolution route() {
		localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		if(userId == null){
			setDbCommentList(commentDao.findByReportedComments(locale, reviewStatus, Long.valueOf(offset), Long.valueOf(limit)));
		}
		else if( showOnlyReportedComments != null && showOnlyReportedComments == Boolean.TRUE ){
			dbCommentList = commentDao.findByReportedByCreatorId(userId, Long.valueOf(offset), Long.valueOf(limit));
		}
		else{
			dbCommentList = commentDao.findByCreatorId(userId, Long.valueOf(offset), Long.valueOf(limit));
		}
		
		if( dbCommentList.getTotalSize() % limit == 0 ) {
			setPages( (dbCommentList.getTotalSize() / limit) ) ;
		}
		else {
			setPages( (dbCommentList.getTotalSize() / limit) + 1 ) ;
		}
		return forward(reportedProdCommentManagePage);
	}
	
	public Resolution changePageOffset(){
		return new RedirectResolution(reportedProdCommentManageHome)
		.addParameter("locale", locale).addParameter("reviewStatus", reviewStatus).addParameter("limit", limit);
	}
	
	public Resolution updateProdCommentStatus(){
		for( int i = 0 ; i < reportCommentId.length; i++ ){
			if( ticketReviewResult[i].equals(ReportedProdCommentResult.Banned.toString()) ){
				// 1. set this comment being banned.
				ProductComment bannedComment = commentDao.findById(reportCommentId[i]);
				bannedComment.setStatus(ProductCommentStatus.Banned);
				bannedComment = commentDao.update(bannedComment) ;
				// 2. send mail to this user to notice them their comment being banned.
				mailService.send(reportCommentId[i]);
				// 3. set all related tickets status from new created to reviewed.
				if( bannedComment.getReportedTickets().size() > 0 ){
					List<ReportedProdComment> closingTickets = bannedComment.getReportedTickets() ;
					for( ReportedProdComment ticket : closingTickets ){
						ticket.setReviewStatus(ReportedProdCommentStatus.Reviewed);
						ticket.setReviewResult(ReportedProdCommentResult.Banned);
						ticket.setReviewer(userDao.findById(getCurrentUserId()));
						reportedCommentDao.update(ticket);
					}
				}
				else{
					ReportedProdComment adminReportedTicket = new ReportedProdComment();
					adminReportedTicket.setReportedComment(bannedComment);
					adminReportedTicket.setReporter(userDao.findById(getCurrentUserId()));
					adminReportedTicket.setReviewer(userDao.findById(getCurrentUserId()));
					adminReportedTicket.setReviewStatus(ReportedProdCommentStatus.Reviewed);
					adminReportedTicket.setReportReason(ReportedProdCommentReason.Other);
					adminReportedTicket.setReviewResult(ReportedProdCommentResult.Banned);
					reportedCommentDao.update(adminReportedTicket);
				}
				
			}
			else if( ticketReviewResult[i].equals(ReportedProdCommentResult.Published.toString()) ){
				// 1. close all tickets related to this comment.
				ProductComment reviewedOkComment = commentDao.findById(reportCommentId[i]);
				if(reviewedOkComment.getReportedTickets().size() > 0){
					List<ReportedProdComment> closingTickets = reviewedOkComment.getReportedTickets() ;
					for( ReportedProdComment ticket : closingTickets ){
						ticket.setReviewStatus(ReportedProdCommentStatus.Reviewed);
						ticket.setReviewResult(ReportedProdCommentResult.Published);
						ticket.setReviewer(userDao.findById(getCurrentUserId()));
						reportedCommentDao.update(ticket);
					}
				}
				else{
					ReportedProdComment adminReportedTicket = new ReportedProdComment();
					adminReportedTicket.setReportedComment(reviewedOkComment);
					adminReportedTicket.setReporter(userDao.findById(getCurrentUserId()));
					adminReportedTicket.setReviewer(userDao.findById(getCurrentUserId()));
					adminReportedTicket.setReviewStatus(ReportedProdCommentStatus.Reviewed);
					adminReportedTicket.setReportReason(ReportedProdCommentReason.Other);
					adminReportedTicket.setReviewResult(ReportedProdCommentResult.Published);
					reportedCommentDao.update(adminReportedTicket);
				}
				
			}
		}
		return new RedirectResolution(reportedProdCommentManageHome).addParameter("locale", locale);
	}
	
	public Resolution sendNotification(){
		//handling banned comment list
		if(reviewedBannedCommentIdList != null && reviewedBannedCommentIdList.length > 0){
			for( Long curCommentId : reviewedBannedCommentIdList ){
				// 1. set this comment being banned.
				ProductComment bannedComment = commentDao.findById(curCommentId);
				bannedComment.setStatus(ProductCommentStatus.Banned);
				bannedComment = commentDao.update(bannedComment) ;
				// 2. send mail to this user to notice them their comment being banned.
				mailService.send(curCommentId);
				// 3. set all related tickets status from new created to reviewed.
				List<ReportedProdComment> closingTickets = bannedComment.getReportedTickets() ;
				for( ReportedProdComment ticket : closingTickets ){
					ticket.setReviewStatus(ReportedProdCommentStatus.Reviewed);
					ticket.setReviewResult(ReportedProdCommentResult.Banned);
					ticket.setReviewer(userDao.findById(getCurrentUserId()));
					reportedCommentDao.update(ticket);
				}
			}
		}
		
		//handling reviewed OK comments
		if(reviewedPublishedCommentIdList != null && reviewedPublishedCommentIdList.length > 0){
			for( Long curCommentId : reviewedPublishedCommentIdList ){
				// 1. close all tickets related to this comment.
				ProductComment reviewedOkComment = commentDao.findById(curCommentId);
				List<ReportedProdComment> closingTickets = reviewedOkComment.getReportedTickets() ;
				for( ReportedProdComment ticket : closingTickets ){
					ticket.setReviewStatus(ReportedProdCommentStatus.Reviewed);
					ticket.setReviewResult(ReportedProdCommentResult.Published);
					reportedCommentDao.update(ticket);
				}
			}			
		}
		return route();
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
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

	public ReportedProdCommentStatus getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(ReportedProdCommentStatus reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public ReportedProdCommentStatus[] getTicketReviewStatus() {
		return ticketReviewStatus;
	}

	public void setTicketReviewStatus(ReportedProdCommentStatus[] ticketReviewStatus) {
		this.ticketReviewStatus = ticketReviewStatus;
	}

	public String[] getTicketReviewResult() {
		return ticketReviewResult;
	}

	public void setTicketReviewResult(String[] ticketReviewResult) {
		this.ticketReviewResult = ticketReviewResult;
	}

	public Long[] getReportCommentId() {
		return reportCommentId;
	}

	public void setReportCommentId(Long[] reportCommentId) {
		this.reportCommentId = reportCommentId;
	}

	public PageResult<ProductComment> getDbCommentList() {
		return dbCommentList;
	}

	public void setDbCommentList(PageResult<ProductComment> dbCommentList) {
		this.dbCommentList = dbCommentList;
	}

	public Long[] getReviewedBannedCommentIdList() {
		return reviewedBannedCommentIdList;
	}

	public void setReviewedBannedCommentIdList(
			Long[] reviewedBannedCommentIdList) {
		this.reviewedBannedCommentIdList = reviewedBannedCommentIdList;
	}

	public Long[] getReviewedPublishedCommentIdList() {
		return reviewedPublishedCommentIdList;
	}

	public void setReviewedPublishedCommentIdList(
			Long[] reviewedPublishedCommentIdList) {
		this.reviewedPublishedCommentIdList = reviewedPublishedCommentIdList;
	}

	public List<ProductComment> getReviewedBannedCommentList() {
		return reviewedBannedCommentList;
	}

	public void setReviewedBannedCommentList(
			List<ProductComment> reviewedBannedCommentList) {
		this.reviewedBannedCommentList = reviewedBannedCommentList;
	}

	public List<ProductComment> getReviewedPublishedCommentList() {
		return reviewedPublishedCommentList;
	}

	public void setReviewedPublishedCommentList(
			List<ProductComment> reviewedPublishedCommentList) {
		this.reviewedPublishedCommentList = reviewedPublishedCommentList;
	}

	public int getSizeOfReviewedPublishedCommentList() {
		return sizeOfReviewedPublishedCommentList;
	}

	public void setSizeOfReviewedPublishedCommentList(
			int sizeOfReviewedPublishedCommentList) {
		this.sizeOfReviewedPublishedCommentList = sizeOfReviewedPublishedCommentList;
	}

	public int getSizeOfReviewedBannedCommentList() {
		return sizeOfReviewedBannedCommentList;
	}

	public void setSizeOfReviewedBannedCommentList(
			int sizeOfReviewedBannedCommentList) {
		this.sizeOfReviewedBannedCommentList = sizeOfReviewedBannedCommentList;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public List<User> getCreatorList() {
		return creatorList;
	}

	public void setCreatorList(List<User> creatorList) {
		this.creatorList = creatorList;
	}

	public List<ProductComment> getSpecificUserCommentList() {
		return specificUserCommentList;
	}

	public void setSpecificUserCommentList(List<ProductComment> specificUserCommentList) {
		this.specificUserCommentList = specificUserCommentList;
	}

	public Boolean getShowOnlyReportedComments() {
		return showOnlyReportedComments;
	}

	public void setShowOnlyReportedComments(Boolean showOnlyReportedComments) {
		this.showOnlyReportedComments = showOnlyReportedComments;
	}

	public Set<String> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(Set<String> localeList) {
		this.localeList = localeList;
	}

}
