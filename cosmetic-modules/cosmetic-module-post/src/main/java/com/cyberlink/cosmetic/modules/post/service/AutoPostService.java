package com.cyberlink.cosmetic.modules.post.service;

import java.util.Date;
import java.util.List;


public interface AutoPostService {
	void startAutoPostThread();
	void stopAutoPostThread();
	void pushTask(PostTask task);
	void setRequestHeader(String header);
	
	String getStatus();
	
	public class PostTask {
		
		private List<Long> userList;
		private List<ArticleData> articleList;
		private Date requestTime = new Date();
		private Date startTime;
		private List<Long> postCircles;
		private int postNumber;
		private int postDuration;
		private String postRegion;
		private int articleSelNumber;
		
				
		public PostTask(String postRegion, List<Long> userList, List<ArticleData> articleList, Date startTime, List<Long> postCircles, int postNumber, int postDuration, int articleSelNumber) {
			this.postRegion = postRegion;
			this.userList = userList;
			this.articleList = articleList;
			this.startTime = startTime;
			this.postCircles = postCircles;
			this.postNumber = postNumber;
			this.postDuration = postDuration;
			this.articleSelNumber = articleSelNumber;
		}
		
		public List<Long> getUserList() {
			return userList;
		}
		
		public List<ArticleData> getArticleList() {
			return articleList;
		}
		
		public Date getRequestTime() {
			return requestTime;
		}
		
		public void setRequestTime(Date requestTime) {
			this.requestTime = requestTime;
		}
		
		public Date getStartTime() {
			return startTime;
		}
		
		public List<Long> getPostCircles() {
			return postCircles;
		}
		
		public int getPostNumber() {
			return postNumber;
		}
		
		public int getPostDuration() {
			return postDuration;
		}
		
		public String getPostRegion() {
			return postRegion;
		}
		
		public int getArticleSelNumber() {
			return articleSelNumber;
		}
	}
}