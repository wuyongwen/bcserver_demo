package com.cyberlink.cosmetic.action.api.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Attachments;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@UrlBinding("/api/v4.0/post/list-post-photo.action")
public class ListPostPhotoAction extends AbstractPostAction {
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("post.PostViewDao")
    private PostViewDao postViewDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    private List<String> locale = null;
    private Integer limit = 5;
    
    public void setLocale(List<String> locale) {
        this.locale = locale;
    }
    
    @DefaultHandler
    public Resolution route() {
        if(locale == null || locale.size() <= 0)
            return new ErrorResolution(ErrorDef.InvalidLocale);
        final Map<String, Object> results = new HashMap<String, Object>();
        
        List<Long> userIds = userDao.findIdByUserType(UserType.CL, locale);        
        BlockLimit blockLimit = new BlockLimit(0, limit);
        blockLimit.addOrderBy("createdTime", false);
        
        List<PostStatus> postStatues = new ArrayList<PostStatus>();
        postStatues.add(PostStatus.Published);
        List<Long> postIds = new ArrayList<Long>();
        if(postDao.findPostIdsByCLUsers(userIds, postStatues, postIds, blockLimit) <= 0) {
            results.put("results", new ArrayList<Map<String, Object>>());       
            results.put("totalSize", 0);
            return json(results);
        }
        
        Map<Long, String> postViewMap = postViewDao.getViewMapByCLPostIds(postIds);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Long postId : postViewMap.keySet()) {
            Map<String, Object> wrapper = new HashMap<String, Object>();
            String mainPostJson = postViewMap.get(postId);
            if(mainPostJson == null || mainPostJson.length() <= 0)
                continue;
            try {
                MainPostSimpleWrapper tmp = objectMapper.readValue(mainPostJson, MainPostSimpleWrapper.class);
                Attachments attch = tmp.getAttachments();
                if(attch.files.size() <= 0)
                    continue;
                com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File f = attch.files.get(0);
                ObjectNode actualObj = (ObjectNode)objectMapper.readTree(f.getMetadata());
                if(actualObj == null)
                    continue;

                JsonNode attrNode = actualObj.get("originalUrl");
                if(attrNode == null)
                    continue;

                wrapper.put("id", f.getFileId());
                wrapper.put("originalUrl", attrNode.asText());
                List<DPWCircle> circles = tmp.getCircles();
                if(circles == null || circles.size() <= 0)
                    continue;
                wrapper.put("postId", tmp.getPostId());
                DPWCircle firstCircles = circles.get(0);                
                wrapper.put("circleTypeId", firstCircles.getCircleTypeId());
                wrapper.put("circleDefaultType", firstCircles.getDefaultType());
            } catch (IOException e) {
                continue;
            }
            
            list.add(wrapper);
        }
        results.put("results", list);       
        results.put("totalSize", list.size());
        return json(results);

    }
}
