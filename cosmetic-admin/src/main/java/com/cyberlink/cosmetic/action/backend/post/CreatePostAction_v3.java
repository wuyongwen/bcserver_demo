package com.cyberlink.cosmetic.action.backend.post;


import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.dao.PostAutoArticleDao;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle.ArticleType;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/v3.0/post/create-post.action")
public class CreatePostAction_v3 extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("post.PostAutoArticleDao")
    private PostAutoArticleDao postAutoArticleDao;
    
    private String title;
    private String content;
    private List<Long> circleIds;
    private String tags;
    private String attachments;
    private String locale;
    private PostStatus postStatus = PostStatus.Hidden;
    private String postSource;
    private AppName appName = AppName.BACKEND_V1;
    private Date createdTime;
    private Long userId;
    private ArticleType articleType;
    private String articleId;
    private String link;
    private String importFile;
       
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }
    
    public void setCircleIds(List<Long> circleIds) {
        this.circleIds = circleIds;
    }
    
    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }
    
    public void setCreatedTime(Date createdTime) {
    	this.createdTime = createdTime;
    }
    
    public void setUserId(Long userId) {
    	this.userId = userId;
    }
    
    public void setArticleType(ArticleType articleType) {
    	this.articleType = articleType;
    }
    
    public void setArticleId(String articleId) {
    	this.articleId = articleId;
    }
    
    public void setLink(String link) {
    	this.link = link;
    }
    
    public void setImportFile(String importFile) {
		this.importFile = importFile;
	}

	@DefaultHandler
    public Resolution route() {
    	//Check the auto article whether is exist.
    	PostAutoArticle postAutoArticle = postAutoArticleDao.findByLocaleAndLink(locale, link);
    	if (postAutoArticle != null)
    		return json( String.format("{\"ErrorCode\" : \"%s\"}", "Existed Article"));
        PostApiResult <Post> result = postService.createPost(userId, locale, null, title, content, circleIds, attachments, tags, postStatus, postSource, appName, null, null, null, null, createdTime, null);
        if(!result.success())
        	return json( String.format("{\"ErrorCode\" : \"%s\"}", result.getErrorDef()));
        if (result.getResult().getPostStatus() == PostStatus.Published)
        	notifyService.addFriendNotifyByType(NotifyType.AddPost.toString(), userId, result.getResult().getId(), null);
        PostAutoArticle newpostAutoArticle = createAutoArticle(result.getResult(), articleType, articleId, link, importFile);
        if (newpostAutoArticle == null)
        	return json( String.format("{\"Warning\" : \"%s\"}", "Record Article Fail"));
        MainPostBaseWrapper pw = new MainPostBaseWrapper(result.getResult());
        return json(pw);
    }
    
    public PostAutoArticle createAutoArticle(Post post, ArticleType articleType, String articleId, String link, String importFile) {
    	PostAutoArticle postAutoArticle = new PostAutoArticle();
    	postAutoArticle.setShardId(post.getCreatorId());
    	postAutoArticle.setCreatorId(post.getCreatorId());
    	postAutoArticle.setCreator(post.getCreator());
    	postAutoArticle.setPostId(post.getId());
    	postAutoArticle.setLocale(post.getLocale());
    	postAutoArticle.setTitle(post.getTitle());
    	postAutoArticle.setContent(post.getContent());
    	postAutoArticle.setLink(link);
    	postAutoArticle.setPostStatus(post.getPostStatus());
    	postAutoArticle.setArticleType(articleType);
    	postAutoArticle.setArticleId(articleId);
    	postAutoArticle.setImportFile(importFile);
    	return postAutoArticleDao.create(postAutoArticle);
    }

}
