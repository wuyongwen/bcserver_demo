package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_AUTO_ARTICLE")
public class PostAutoArticle extends AbstractEntity<Long> {
	public enum ArticleType {
		Pinterest, Google, Unkown;
	}

	private static final long serialVersionUID = 2343591853447865985L;
	private User creator;
    private Long creatorId;
    private Long postId;
    private String locale;
    private String importFile;
    private String title;
    private String content;
    private String link;
    private PostStatus postStatus;
    private ArticleType articleType;
    private String articleId;
    
	public void setId(Long id) {
        this.id = id;
    }
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
	
	@Column(name = "USER_ID")
    public Long getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    @Column(name = "POST_ID")
    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
	
	@Column(name = "LOCALE", length = 8)
    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
	
    @Column(name = "TITLE", length = 100)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "CONTENT", length = 16777215)
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @Column(name = "LINK", length = 65535, columnDefinition="TEXT")
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }
	
    @Column(name = "POST_STATUS")
    @Enumerated(EnumType.STRING)
    public PostStatus getPostStatus() {
        return this.postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
    
    @Column(name = "ARTICLE_TYPE")
    @Enumerated(EnumType.STRING)
    public ArticleType getArticleType() {
        return this.articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
    }
    
    @Column(name = "ARTICLE_ID", length = 100)
    public String getArticleId() {
    	return this.articleId;
    }
    
    public void setArticleId(String articleId) {
    	this.articleId = articleId;
    }

    @Column(name = "IMPORT_FILE", length = 100)
	public String getImportFile() {
		return this.importFile;
	}

	public void setImportFile(String importFile) {
		this.importFile = importFile;
	}
	
}