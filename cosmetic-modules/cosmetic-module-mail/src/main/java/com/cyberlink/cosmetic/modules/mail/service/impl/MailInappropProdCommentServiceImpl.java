package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisherAware;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropProdCommentService;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.user.model.Account;

public class MailInappropProdCommentServiceImpl extends AbstractMailService
implements MailInappropProdCommentService, ApplicationEventPublisherAware{

	private ProductCommentDao commentDao;
	
	protected MailInappropProdCommentServiceImpl() {
		super(MailType.REPORTBAD_NOTIFY_CREATOR);
	}

	@Override
	public void send(Long bannedCommentId) {
		ProductComment bannedComment = commentDao.findById(bannedCommentId);
		String email = "" ;
		List<Account> emailList = bannedComment.getUser().getAccountList() ;
		if( emailList.size() > 0 ){
			email = emailList.get(0).getAccount() ;
		}
		else{ //no email account
			return ;
		}
		String comment = bannedComment.getComment() ;
		//String copyright = "© 2015 CyberLink Corp. All Rights Reserved.";
		Locale locale = new Locale(bannedComment.getUser().getRegion().substring(0,2), 
				bannedComment.getUser().getRegion().substring(3,5)) ;
		String contentType = getLocalizedProductReview(locale) ;
		String subject = getLocalizedMailSubject(locale, contentType) ;
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("comment", comment) ;
		data.put("contentType", contentType);
		data.put("creator", bannedComment.getUser().getDisplayName() );
		data.put("copyRight", getCopyRight(locale));
        data.put("mailWidth", 700);
        
        final String content = getContent(locale, data);
		sendMail(subject, content, email);
		
	}
	
	private String getLocalizedProductReview(Locale locale){
		if (locale.getLanguage().equalsIgnoreCase("en")) {
			return "Product Review";        	
		} else if (locale.getLanguage().equalsIgnoreCase("de")){
			return "Produktbewertung";        	        	
		} else if (locale.getLanguage().equalsIgnoreCase("es")){
			return "reseña de producto";        	        	
		} else if (locale.getLanguage().equalsIgnoreCase("fr")){
			return "avis sur un produit";        	        	
		} else if (locale.getLanguage().equalsIgnoreCase("it")){
			return "articolo su un prodotto";        	        	
		} else if (locale.getLanguage().equalsIgnoreCase("ja")){
			return "製品レビュー";        	        	
		} else if (locale.getLanguage().equalsIgnoreCase("ko")){
			return "제품 리뷰";        	        	
		} else if (locale.getLanguage().equalsIgnoreCase("ru")){
			return "отзыв о продукте";        	        	
		} else if (locale.getCountry().equalsIgnoreCase("CN")){
			return "产品评论";        	        	
		} else if (locale.getCountry().equalsIgnoreCase("TW")){
			return "產品評論";        	        	
		} else {
			return "Product Review";   
		}
	}
	
	private String getLocalizedMailSubject(Locale locale, String contentTypeString){
		if (locale.getLanguage().equalsIgnoreCase("en")) {
			return "[Beauty Circle] your " + contentTypeString + " has been reported!";
		} else if (locale.getLanguage().equalsIgnoreCase("de")){
			return "[Beauty Circle] Ihr " + contentTypeString + " wurde als unangemessen gemeldet!";
		} else if (locale.getLanguage().equalsIgnoreCase("es")){
			return "¡[Beauty Circle] tu " + contentTypeString + " ha sido reportada(o)!";
		} else if (locale.getLanguage().equalsIgnoreCase("fr")){
			return "[Sphère Beauté] votre " + contentTypeString + " a été signalé!";
		} else if (locale.getLanguage().equalsIgnoreCase("it")){
			return "[Beauty Circle] il tuo " + contentTypeString + " è stato segnalato!";
		} else if (locale.getLanguage().equalsIgnoreCase("ja")){
			return "[Beauty サークル] 記載された " + contentTypeString + " が不適切との通報がありました。";
		} else if (locale.getLanguage().equalsIgnoreCase("ko")){
			return "[Beauty Circle] 고객님의 콘텐츠 " + contentTypeString + " 이(가) 신고 접수되었습니다.";
		} else if (locale.getLanguage().equalsIgnoreCase("ru")){
			return "[Beauty Circle] Ваш " + contentTypeString + " считается неуместным!";
		} else if (locale.getCountry().equalsIgnoreCase("CN")){
			return "[玩美圈] 您的" + contentTypeString + "已被玩美圈社群标记为不适宜内容";
		} else if (locale.getCountry().equalsIgnoreCase("TW")){
			return "[玩美圈] 您的" + contentTypeString + "已被玩美圈社群標記為不適宜內容";
		} else {
			return "[Beauty Circle] your " + contentTypeString + " has been reported!";
		}
	}

	public ProductCommentDao getCommentDao() {
		return commentDao;
	}

	public void setCommentDao(ProductCommentDao commentDao) {
		this.commentDao = commentDao;
	}

}
