package com.cyberlink.cosmetic.modules.post.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

public class PostLogWrapper implements Serializable {
    public class RegionCount {
        @JsonView(Views.Public.class)
        public String region = "Unknown";
        
        @JsonView(Views.Public.class)
        public Long count = (long)0;
    }
    
    public class RegionStatistic {
        @JsonView(Views.Public.class)
        public List<RegionCount> likeCount = new ArrayList<RegionCount>();
        
        @JsonView(Views.Public.class)
        public List<RegionCount> commentCount = new ArrayList<RegionCount>();
    }
    
    private static final long serialVersionUID = -5526826966656223086L;

	private final Post post;
	protected RegionStatistic regionStatistic = new RegionStatistic();
	
	public PostLogWrapper(Post post) {
        this.post = post;
    }
	
	@JsonView(Views.Public.class)
    public Long getId() {
		return post.getId();
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone="GMT+00")
	@JsonView(Views.Public.class)
	public Date getCreatedTime() {
		return post.getCreatedTime();
	}
	
	@JsonView(Views.Public.class)
	public String getTitle(){
		return post.getTitle();
	} 

	@JsonView(Views.Public.class)
	public String getLocale(){
		return post.getLocale();
	} 

	@JsonView(Views.Simple.class)
    public RegionStatistic getRegionStatistic() {
        return regionStatistic;
    }
    public void setRegionLikeCount(Map<String, Long> map)
    {
        for(String key : map.keySet()) {
            RegionCount regCount = new RegionCount();
            regCount.region = key;
            regCount.count = map.get(key);
            this.regionStatistic.likeCount.add(regCount);
        }
    }
    
    public void setRegionCommentCount(Map<String, Long> map)
    {
        for(String key : map.keySet()) {
            RegionCount regCount = new RegionCount();
            regCount.region = key;
            regCount.count = map.get(key);
            this.regionStatistic.commentCount.add(regCount);
        }
    }
}
