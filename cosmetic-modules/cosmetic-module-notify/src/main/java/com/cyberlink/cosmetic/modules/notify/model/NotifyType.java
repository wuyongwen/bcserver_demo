package com.cyberlink.cosmetic.modules.notify.model;

import java.util.ArrayList;
import java.util.List;

public enum NotifyType {
	Message, CommentPost, ReplyComment, ReplyToCommentOwner, ReplyToPostOwner, FollowUser, FollowCircle, JoinBC, JoinBCFromFB, JoinBCFromWeibo, AddPost, CreateCircle, LikePost, CircleInPost, FriendLikePost, FriendFollowCircle, FreeSample, Consultation, StarOfWeek;
	public static List<String> getYouType() {
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.CommentPost.toString());
		list.add(NotifyType.ReplyToCommentOwner.toString());
		list.add(NotifyType.ReplyToPostOwner.toString());
		list.add(NotifyType.FollowUser.toString());
		list.add(NotifyType.FollowCircle.toString());
		list.add(NotifyType.LikePost.toString());
		list.add(NotifyType.CircleInPost.toString());
		list.add(NotifyType.FreeSample.toString());
		list.add(NotifyType.Consultation.toString());
		list.add(NotifyType.StarOfWeek.toString());
		return list;
	}
	public static List<String> getYouWithoutCommentType() {
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.ReplyToCommentOwner.toString());
		list.add(NotifyType.ReplyToPostOwner.toString());
		list.add(NotifyType.FollowUser.toString());
		list.add(NotifyType.FollowCircle.toString());
		list.add(NotifyType.LikePost.toString());
		list.add(NotifyType.CircleInPost.toString());
		return list;
	}
	public static List<String> getAllWithoutCommentType() {
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.ReplyToCommentOwner.toString());
		list.add(NotifyType.ReplyToPostOwner.toString());
		list.add(NotifyType.FollowUser.toString());
		list.add(NotifyType.FollowCircle.toString());
		list.add(NotifyType.LikePost.toString());
		list.add(NotifyType.CircleInPost.toString());
		list.add(NotifyType.JoinBC.toString());
		list.add(NotifyType.JoinBCFromFB.toString());
		list.add(NotifyType.JoinBCFromWeibo.toString());
		list.add(NotifyType.AddPost.toString());
		list.add(NotifyType.CreateCircle.toString());
		list.add(NotifyType.FriendLikePost.toString());
		list.add(NotifyType.FriendFollowCircle.toString());
		return list;
	}
	
	public static List<String> getFriendType() {		
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.JoinBC.toString());
		list.add(NotifyType.JoinBCFromFB.toString());
		list.add(NotifyType.JoinBCFromWeibo.toString());
		list.add(NotifyType.AddPost.toString());
		list.add(NotifyType.CreateCircle.toString());
		list.add(NotifyType.FriendLikePost.toString());
		list.add(NotifyType.FriendFollowCircle.toString());
		return list;
	}
	
	public static List<String> getCommentType() {
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.CommentPost.toString());
		list.add(NotifyType.ReplyComment.toString());
		list.add(NotifyType.ReplyToCommentOwner.toString());
		list.add(NotifyType.ReplyToPostOwner.toString());
		return list;
	}

	public static List<String> getPostType() {		
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.CommentPost.toString());
		list.add(NotifyType.AddPost.toString());
		list.add(NotifyType.LikePost.toString());
		list.add(NotifyType.CircleInPost.toString());
		list.add(NotifyType.FriendLikePost.toString());
		return list;
	}	

	public static List<String> getCircleType() {		
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.FollowCircle.toString());
		list.add(NotifyType.CreateCircle.toString());
		list.add(NotifyType.FriendFollowCircle.toString());
		return list;
	}	

	public static List<String> getFollowType() {		
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.FollowUser.toString());
		list.add(NotifyType.JoinBC.toString());
		list.add(NotifyType.JoinBCFromFB.toString());
		list.add(NotifyType.JoinBCFromWeibo.toString());
		return list;
	}	

	public static List<String> getSenderGroupType() {		
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.AddPost.toString());
		list.add(NotifyType.CreateCircle.toString());
		list.add(NotifyType.FriendLikePost.toString());
		list.add(NotifyType.FriendFollowCircle.toString());
		return list;
	}	
	public static List<String> getNonSenderGroupType() {		
		List<String> list = new ArrayList<String>();
		list.add(NotifyType.CommentPost.toString());
		list.add(NotifyType.FollowCircle.toString());
		list.add(NotifyType.LikePost.toString());
		list.add(NotifyType.CircleInPost.toString());
		return list;
	}	

}
