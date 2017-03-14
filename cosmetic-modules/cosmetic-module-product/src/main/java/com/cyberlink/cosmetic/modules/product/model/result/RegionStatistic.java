package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class RegionStatistic implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2171068599053307296L;
	private List<ProdRatingCounter> ratingCountList ;
	private List<ProdCommentCounter> commentCountList ;
	
	@JsonView(Views.Public.class)
	public List<ProdRatingCounter> getRatingCount() {
		return ratingCountList;
	}
	
	public void setRatingCount(List<ProdRatingCounter> ratingCount) {
		this.ratingCountList = ratingCount;
	}
	
	@JsonView(Views.Public.class)
	public List<ProdCommentCounter> getCommentCount() {
		return commentCountList;
	}
	
	public void setCommentCount(List<ProdCommentCounter> commentCount) {
		this.commentCountList = commentCount;
	}
	
}
