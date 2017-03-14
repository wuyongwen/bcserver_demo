package com.cyberlink.cosmetic.modules.post.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.model.AbstractCoreEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_NEW_POOL")
public class PostNewPool extends AbstractCoreEntity<Long> {
    
    private static final long serialVersionUID = 8001741846414550850L;
    
    private Long postId;
    private Long bonus;
    private String locale;
    private NewPoolGroup group;
    private Long circleTypeId;
    private String circleTypeIds;
    private List<Long> circleTypeIdList;
    private Long lookTypeId;
    private Boolean forceHideInAll;

    public enum NewPoolGroup {
        Normal,  Normal_Cat,  
        Publication,  Publication_Cat,  
        Beautyist,  Beautyist_Cat,  
        Brand,  Brand_Cat,
        Scraped, Scraped_Cat;
    }
    
    @Override
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @Column(name = "POST_ID")
    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    @Column(name = "LOCALE")
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    @Column(name = "POOL_GROUP")
    @Enumerated(EnumType.STRING)
    public NewPoolGroup getGroup() {
        return group;
    }
    
    public void setGroup(NewPoolGroup group){
        this.group = group;
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
    
    @Column(name = "CIRCLE_TYPE_IDS")
    public String getCircleTypeIds() {
        return this.circleTypeIds;
    }

    public void setCircleTypeIds(String circleTypeIds) {
        this.circleTypeIds = circleTypeIds;
    }
    
    @Column(name = "LOOK_TYPE_ID")
    public Long getLookTypeId() {
        return this.lookTypeId;
    }

    public void setLookTypeId(Long lookTypeId) {
        this.lookTypeId = lookTypeId;
    }
    
    @Column(name = "FORCE_HIDE_IN_ALL")
    public Boolean getForceHideInAll() {
        return this.forceHideInAll;
    }

    public void setForceHideInAll(Boolean forceHideInAll) {
        this.forceHideInAll = forceHideInAll;
    }
    
    @Transient
    public List<Long> getCircleTypeIdsList() {
        if(circleTypeIdList != null)
            return circleTypeIdList;
        circleTypeIdList = new ArrayList<Long>();
        if(circleTypeIds == null || circleTypeIds.length() <= 0) {
            circleTypeIdList.add(circleTypeId);
        }
        else {
            try {
                ObjectMapper m = BeanLocator.getBean("web.objectMapper");
                circleTypeIdList = m.readValue(circleTypeIds, new TypeReference<ArrayList<Long>>(){});
            } catch (Exception e) {
            }
        }
        return circleTypeIdList;
    }
    
}
