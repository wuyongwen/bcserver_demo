package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_NEW")
public class PostNew extends AbstractCoreEntity<Long> {

    private static final long serialVersionUID = -7682667713632635390L;
    
    private Post post;
    //private PostView postView;
    private String locale;
    private Long bonus;
    private Long circleTypeId;
    private Boolean mainType;
    private Long lookTypeId;
    private Boolean showInAll;
    private Boolean forceHideInAll;

    @Override
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", referencedColumnName = "ID")
    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID", insertable=false, updatable=false)
    public PostView getPostView() {
        return this.postView;
    }

    public void setPostView(PostView postView) {
        this.postView = postView;
    }*/
    
    @Column(name = "LOCALE")
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    @Column(name = "BONUS")
    public Long getBonus() {
        return this.bonus;
    }

    public void setBonus(Long bonus) {
        this.bonus = bonus;
    }

    @Column(name = "CIRCLE_TYPE_ID")
    public Long getCircleTypeId() {
        return this.circleTypeId;
    }

    public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
    }
    
    @Column(name = "LOOK_TYPE_ID")
    public Long getLookTypeId() {
        return this.lookTypeId;
    }

    public void setLookTypeId(Long lookTypeId) {
        this.lookTypeId = lookTypeId;
    }
    
    @Column(name = "SHOW_IN_ALL")
    public Boolean getShowInAll() {
        return this.showInAll;
    }

    public void setShowInAll(Boolean showInAll) {
        this.showInAll = showInAll;
    }

    @Column(name = "MAIN_TYPE")
    public Boolean getMainType() {
        return this.mainType;
    }

    public void setMainType(Boolean mainType) {
        this.mainType = mainType;
    }
    
    @Column(name = "FORCE_HIDE_IN_ALL")
    public Boolean getForceHideInAll() {
        return this.forceHideInAll;
    }

    public void setForceHideInAll(Boolean forceHideInAll) {
        this.forceHideInAll = forceHideInAll;
    }
    
}
