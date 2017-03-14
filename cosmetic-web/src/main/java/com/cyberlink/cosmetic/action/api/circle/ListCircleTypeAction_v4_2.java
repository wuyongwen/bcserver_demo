package com.cyberlink.cosmetic.action.api.circle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.core.service.CacheService;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Attachments;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

// list circle type
@UrlBinding("/api/v4.2/circle/list-circletype.action")
public class ListCircleTypeAction_v4_2 extends ListCircleTypeAction {

    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("post.PostViewDao")
    protected PostViewDao postViewDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    /* Currently no need this feature*/
    /*@SpringBean("cache.ehcacheService")
    private CacheService cacheMgr;

    private String CACHE_NAME = "com.cyberlink.cosmetic.modules.circle.model.CircleType.imgUrl";
    
    public String getImgUrl(Long circleTypeId) {
        List<PostStatus> postStatus = new ArrayList<PostStatus>();
        postStatus.add(PostStatus.Published);
        List<String> locales = new ArrayList<String>();
        locales.add(locale);
        BlockLimit blockLimit = new BlockLimit(0, 1);
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);    
        List<Long> resultList = new ArrayList<Long>();
        postService.listPostByCircle_v3_1(null, circleTypeId, locales, postStatus, "Date", resultList, blockLimit);
        if(resultList.size() <= 0) {
            return null;
        }
        
        PostView postView = postViewDao.findByPostId(resultList.get(0));
        if(postView == null)
            return null;
        
        String mainPostJson = postView.getMainPost();
        if(mainPostJson == null || mainPostJson.length() <= 0)
            return null;
        try {
            MainPostSimpleWrapper tmp = objectMapper.readValue(mainPostJson, MainPostSimpleWrapper.class);
            Attachments attch = tmp.getAttachments();
            if(attch.files.size() <= 0)
                return null;
            com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File f = attch.files.get(0);
            ObjectNode actualObj = (ObjectNode)objectMapper.readTree(f.getMetadata());
            if(actualObj == null)
                return null;
            JsonNode attrNode = actualObj.get("originalUrl");
            if(attrNode == null)
                return null;
            return attrNode.asText();
        } catch (IOException e) {
        }
        
        return null;
    }*/
    
	@DefaultHandler
	public Resolution route() {
		PageResult<CircleType> pageResult = getCircleType();		
        /*for(CircleType ct : pageResult.getResults()) 
        {
            String imgUrl = cacheMgr.get(CACHE_NAME, ct.getId());
            if(imgUrl == null) {
                imgUrl = getImgUrl(ct.getId());
                if(imgUrl == null)
                    imgUrl = "";
                cacheMgr.put(CACHE_NAME, ct.getId(), imgUrl);
            }
            ct.setImgUrl(imgUrl);
        }*/
        
		return json(pageResult);
	}

}
