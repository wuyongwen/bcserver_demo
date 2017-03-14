package com.cyberlink.cosmetic.action.backend.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.CommentsMailService;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;

public class CommentsMailServiceImpl extends AbstractService implements CommentsMailService {

	private MailInappropPostCommentService mailInappropPostCommentService;
	private CommentDao commentDao;
	private ProductCommentDao productCommentDao;
	
	static final String CRONEXPRESSION = "0 30 15 * * ? *";
	static private Boolean isRunning = Boolean.TRUE;
	
	@Override
	public void start() {
		isRunning = Boolean.TRUE;
	}

	@Override
	public void stop() {
		isRunning = Boolean.FALSE;
	}

	@Override
	public String getStatus() {
		if (!isRunning)
			return "CommentsMailService isn't running";
		else
			return "CommentsMailService is running";
	}
	
	@Override
	public void execNow(){
		exec();
	}

	@Override
	@BackgroundJob(cronExpression = CRONEXPRESSION)
	public void exec() {
		if (!isRunning) {
			logger.info("CommentsMailService isn't running");
			return;
		} else
			logger.info("CommentsMailService is running");
		Calendar cal = Calendar.getInstance();
        Date timeNow = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date timeBeforeday = cal.getTime();
		List<Comment> commentList = commentDao.getAllCommentsByDate("Post", "zh_CN", timeBeforeday, timeNow);
		List<Comment> subCommentList = commentDao.getAllSubCommentsByDate("Comment", "zh_CN", timeBeforeday, timeNow);
		List<ProductComment> productCommentList = productCommentDao.getAllProductCommentsByDate("zh_CN", timeBeforeday, timeNow);
		
		StringBuffer mailContent = new StringBuffer();
		if(!(commentList.isEmpty() && productCommentList.isEmpty()))
			mailContent.append("Total comments count:").append(commentList.size()+productCommentList.size()+subCommentList.size()).append("<br><br>");
		if(!commentList.isEmpty()){
			mailContent.append("Posts comment(user ID,comment text):").append("<br><br>");
			if(!commentList.isEmpty()){
				for(Comment comment : commentList){
					mailContent.append(comment.getCreatorId()).append("    ").append(comment.getCommentText()).append("<br>");
				}
			}
			if(!subCommentList.isEmpty()){
				for(Comment subComment : subCommentList){
					mailContent.append(subComment.getCreatorId()).append("    ").append(subComment.getCommentText()).append("<br>");
				}
			}
		}
		if(!productCommentList.isEmpty()){
			mailContent.append("<br><br>");
			mailContent.append("Products comment(user ID,comment text):").append("<br><br>");
			if(!productCommentList.isEmpty()){
				for(ProductComment productComment : productCommentList){
					mailContent.append(productComment.getCreatorId()).append("    ").append(productComment.getComment()).append("<br>");
				}
			}
		}
		if(mailContent.length() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("(yyyy/MM/dd)");
			mailInappropPostCommentService.directSend(new String[]{"Frank_Chuang@PerfectCorp.com","Ben_Chen@PerfectCorp.com"}, "Check comments(posts,products) " + Constants.getWebsiteDomain() + sdf.format(timeNow), mailContent.toString());
		}
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public void setProductCommentDao(ProductCommentDao productCommentDao) {
		this.productCommentDao = productCommentDao;
	}
	
    public void setMailInappropPostCommentService(MailInappropPostCommentService mailInappropPostCommentService) {
        this.mailInappropPostCommentService = mailInappropPostCommentService;
    }
}
