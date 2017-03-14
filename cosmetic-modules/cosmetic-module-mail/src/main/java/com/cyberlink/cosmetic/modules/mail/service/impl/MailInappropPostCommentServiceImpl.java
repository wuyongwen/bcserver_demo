package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.stripes.integration.spring.SpringBean;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropProdCommentService;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.User;

public class MailInappropPostCommentServiceImpl extends AbstractMailService
implements MailInappropPostCommentService, ApplicationEventPublisherAware{

    private PostDao postDao;
	private CommentDao commentDao;
	
	public CommentDao getCommentDao() {
        return commentDao;
    }
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    public PostDao getPostDao() {
        return postDao;
    }
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
	protected MailInappropPostCommentServiceImpl() {
		super(MailType.REPORTBAD_NOTIFY_CREATOR);
	}

	@BackgroundJob
	public void send(String bannedTargetType, Long bannedTargetId, String reason) {
	    User relCreator = null;
	    String bannedContent = null;
	    switch(bannedTargetType) {
            case "Post":
            {
                Post bannedPost = postDao.findById(bannedTargetId);
                relCreator = bannedPost.getCreator();
                bannedContent = bannedPost.getTitle();
                break;
            }
            case "Comment":
            {
                Comment bannedComment = commentDao.findById(bannedTargetId);
                relCreator = bannedComment.getCreator();
                bannedContent = bannedComment.getCommentText();
                break;
            }
            default:
                break;
        }
	    
	    if(relCreator == null)
	        return;
	    
	    Locale locale = new Locale(relCreator.getRegion().substring(0,2), relCreator.getRegion().substring(3,5));
	    List<Account> relAccounts = accountDao.findByUserId(relCreator.getId());
	    if(relAccounts == null || relAccounts.size() <= 0)
	        return;
	    
	    String email = null;
	    for(Account acc : relAccounts) {
	        if(acc.getAccountSource() != AccountSourceType.Email)
	            continue;
	        email = acc.getAccount();
	    }

	    if(email == null || email.length() <= 0)
	        return;
	    
	    final Map<String, Object> data = new HashMap<String, Object>();
        data.put("comment", bannedContent) ;
        data.put("contentType", getLocalizedContentType(bannedTargetType, locale));
        data.put("creator", relCreator.getDisplayName() );
        data.put("copyRight", getCopyRight(locale));
        data.put("mailWidth", 700);
        
        final String content = getContent(locale, data);
        sendMail(getLocalizedMailSubject(bannedTargetType, locale), content, email);
		
	}


	   private String getLocalizedContentType(String contentType, Locale locale){
	       String smallLetterLocale = locale.getLanguage().toLowerCase();
	       if(contentType.equals("Post")) {
	           switch (smallLetterLocale)
	           {
    	           case "de":
    	           {
    	               return "Artikel";
    	           }
    	           case "fr":
                   {
                       return "article";
                   }
    	           case "ja":
                   {
                       return "投稿";
                   }
    	           case "tw":
                   {
                       return "貼文";
                   }
    	           case "cn":
                   {
                       return "贴文";
                   }
    	           case "it":
                   {
                       return "post";   
                   }
    	           case "es":
                   {
                       return "publicación";
                   }
    	           case "ko":
                   {
                       return "포스팅";
                   }
    	           case "ru":
                   {
                       return "пост";
                   }           
    	           case "en":
                   default:
                   {
                       return "post";
                   }
	           }
	       }
	       else if(contentType.equals("Comment")) {
	           switch (smallLetterLocale)
               {
                   case "de":
                   {
                       return "Kommentar";
                   }
                   case "fr":
                   {
                       return "commentaire";
                   }
                   case "ja":
                   {
                       return "コメント";
                   }
                   case "tw":
                   {
                       return "留言";
                   }
                   case "cn":
                   {
                       return "留言";
                   }
                   case "it":
                   {
                       return "commento";   
                   }
                   case "es":
                   {
                       return "comentario";
                   }
                   case "ko":
                   {
                       return "댓글";
                   }
                   case "ru":
                   {
                       return "комментарий";
                   }           
                   case "en":
                   default:
                   {
                       return "comment";
                   }
               }
	       }
	       return null;
	    }
	    
       private String getLocalizedMailSubject(String contentType, Locale locale){
           String smallLetterLocale = locale.getLanguage().toLowerCase();
           String localizedContentType = getLocalizedContentType(contentType, locale);
           String toFormatString;
           switch (smallLetterLocale)
           {
               case "de":
               {
                   toFormatString = "[Beauty Circle] Ihr %s wurde als unangemessen gemeldet!";
                   break;
               }
               case "fr":
               {
                   toFormatString = "[Sphère Beauté] votre %s a été signalé!";
                   break;
               }
               case "ja":
               {
                   toFormatString = "[Beauty サークル] 記載された %sが不適切との通報がありました。";
                   break;
               }
               case "tw":
               {
                   toFormatString = "[玩美圈] 您的%s已被玩美圈社群標記為不適宜內容";
                   break;
               }
               case "cn":
               {
                   toFormatString = "[玩美圈] 您的%s已被玩美圈社群标记为不适宜内容";
                   break;
               }
               case "it":
               {
                   toFormatString = "[Beauty Circle] il tuo %s è stato segnalato!";
                   break;
               }
               case "es":
               {
                   toFormatString = "¡[Beauty Circle] tu %s ha sido reportada(o)!";
                   break;
               }
               case "ko":
               {
                   toFormatString = "[Beauty Circle] 고객님의 콘텐츠 %s 이(가) 신고 접수되었습니다.";
                   break;
               }
               case "ru":
               {
                   toFormatString = "[Beauty Circle] Ваш %s считается неуместным!";
                   break;
               }           
               case "en":
               default:
               {
                   toFormatString = "[Beauty Circle] your %s has been reported!";
                   break;
               }
           }
           
           return String.format(toFormatString, localizedContentType);
        }
       
    @Override
    @BackgroundJob
    public void directSend(String address, String subject, String content) {
        sendMail(subject, content, address);
    }
    
    @Override
    @BackgroundJob
    public void directSend(String[] address, String subject, String content) {
        sendMail(subject, content, address);
    }
}
