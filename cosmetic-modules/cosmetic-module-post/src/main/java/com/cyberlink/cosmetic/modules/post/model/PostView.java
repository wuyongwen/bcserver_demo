package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_VIEW")
public class PostView extends AbstractEntity<Long> {

    public static class DetailView extends Views.Public {
    }
    
    private static final long serialVersionUID = 5052941795977992586L;
    
    private Long postId;
    private String mainPost;
    private String subPosts;
    private String attributeValue;
    private PostViewAttr attribute;
    
    @Column(name = "POST_ID")
    @JsonView(Views.Public.class)
    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @Column(name = "MAIN_POST")
    @JsonView(Views.Public.class)
    public String getMainPost() {
        return this.mainPost;
    }

    public void setMainPost(String mainPost) {
        this.mainPost = mainPost;
    }
    
    @Column(name = "SUB_POSTS")
    @JsonView(DetailView.class)
    public String getSubPosts() {
        return this.subPosts;
    }

    public void setSubPosts(String subPosts) {
        this.subPosts = subPosts;
    }

    @Column(name = "ATTRIBUTE")
    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
    
    @Transient
    @JsonView(Views.Public.class)   
    public PostViewAttr getAttribute() {
        if(attribute != null)
            return attribute;
        if (StringUtils.isBlank(attributeValue))
            return new PostViewAttr();
      
        try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            return m.readValue(attributeValue, new TypeReference<PostViewAttr>() {});
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return new PostViewAttr();
    }
    
    @Transient
    public void setAttribute(PostViewAttr attribute) {
        this.attribute = attribute;
        ObjectMapper m = BeanLocator.getBean("web.objectMapper");
        try {
            attributeValue = m.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
    }
}
