package com.cyberlink.cosmetic.lang.model;

public class NotifyLang extends AbstractLang {

	public NotifyLang(String locale) {
		super(locale);
	}

	// displayName
	public String getDisplayNameAndOther(String name) {
		try {
			String str = resBundle.getString("notify.displayName.nameAndOther");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getDisplayNameAndOthers(String name, long num) {
		try {
			String str = resBundle
					.getString("notify.displayName.nameAndOthers");
			return String.format(str, name, num);
		} catch (Exception e) {
			return "";
		}
	}

	public String getDisplayNamesAndOther(String name1, String name2) {
		try {
			String str = resBundle
					.getString("notify.displayName.namesAndOther");
			return String.format(str, name1, name2);
		} catch (Exception e) {
			return "";
		}
	}

	public String getDisplayNamesAndOthers(String name1, String name2, long num) {
		try {
			String str = resBundle
					.getString("notify.displayName.namesAndOthers");
			return String.format(str, name1, name2, num);
		} catch (Exception e) {
			return "";
		}
	}

	public String getDisplayNameFriend() {
		try {
			return resBundle.getString("notify.displayName.friend");
		} catch (Exception e) {
			return "";
		}
	}

	public String getDisplayNameUnknown() {
		try {
			return resBundle.getString("notify.displayName.unknown");
		} catch (Exception e) {
			return "";
		}
	}

	// notify center
	public String getCenterCommentPost(String name) {
		try {
			String str = resBundle.getString("notify.center.commentPost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFollowUser(String name) {
		try {
			String str = resBundle.getString("notify.center.followUser");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFollowCircle(String name) {
		try {
			String str = resBundle.getString("notify.center.followCircle");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFollowCircleWithCircleName(String name, String circle) {
		try {
			String str = resBundle
					.getString("notify.center.followCircle.circleName");
			return String.format(str, name, circle);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterJoinBCFromFB(String name) {
		try {
			String str = resBundle.getString("notify.center.joinBCFromFB");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterJoinBCFromWeibo(String name) {
		try {
			String str = resBundle.getString("notify.center.joinBCFromWeibo");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterAddPost(String name) {
		try {
			String str = resBundle.getString("notify.center.addPost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterAddPosts(String name, int num) {
		try {
			String str = resBundle.getString("notify.center.addPosts");
			return String.format(str, name, num);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterCreateCircle(String name, String circle) {
		try {
			String str = resBundle.getString("notify.center.createCircle");
			return String.format(str, name, circle);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterCreateCircles(String name, int num) {
		try {
			String str = resBundle.getString("notify.center.createCircles");
			return String.format(str, name, num);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterLikePostWithName(String name) {
		try {
			String str = resBundle.getString("notify.center.likePost.name");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterLikePostWithOthers(String others) {
		try {
			String str = resBundle.getString("notify.center.likePost.others");
			return String.format(str, others);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterCircleInPost(String name) {
		try {
			String str = resBundle.getString("notify.center.circleInPost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFriendLikePost(String name) {
		try {
			String str = resBundle.getString("notify.center.friendLikePost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFriendLikePosts(String name, int num) {
		try {
			String str = resBundle.getString("notify.center.friendLikePosts");
			return String.format(str, name, num);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFriendFollowCircle(String name) {
		try {
			String str = resBundle
					.getString("notify.center.friendFollowCircle");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFriendFollowCircles(String name, int num) {
		try {
			String str = resBundle
					.getString("notify.center.friendFollowCircles");
			return String.format(str, name, num);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCenterFreeSampleMsg(String tittle) {
		try {
			String str = resBundle.getString("notify.center.freeSample.msg");
			return String.format(str, tittle);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getCenterConsultationMsg(String tittle) {
		try {
			String str = resBundle.getString("notify.center.consultation.msg ");
			return String.format(str, tittle);
		} catch (Exception e) {
			return "";
		}
	}

	// push notification
	public String getPushCommentPost(String name) {
		try {
			String str = resBundle.getString("notify.push.commentPost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushFollowUser(String name) {
		try {
			String str = resBundle.getString("notify.push.followUser");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushFollowCircle(String name) {
		try {
			String str = resBundle.getString("notify.push.followCircle");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushFollowCircleWithCircleName(String name, String circle) {
		try {
			String str = resBundle
					.getString("notify.push.followCircle.circleName");
			return String.format(str, name, circle);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushJoinBCFromFB(String name) {
		try {
			String str = resBundle.getString("notify.push.joinBCFromFB");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushJoinBCFromWeibo(String name) {
		try {
			String str = resBundle.getString("notify.push.joinBCFromWeibo");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushAddPost(String name) {
		try {
			String str = resBundle.getString("notify.push.addPost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushCreateCircle(String name) {
		try {
			String str = resBundle.getString("notify.push.createCircle");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushCreateCircleWithCircleName(String name, String circle) {
		try {
			String str = resBundle
					.getString("notify.push.createCircle.circleName");
			return String.format(str, name, circle);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushLikePostWithName(String name) {
		try {
			String str = resBundle.getString("notify.push.likePost.name");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushCircleInPost(String name) {
		try {
			String str = resBundle.getString("notify.push.circleInPost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushFriendLikePost(String name) {
		try {
			String str = resBundle.getString("notify.push.friendLikePost");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPushFriendFollowCircle(String name) {
		try {
			String str = resBundle.getString("notify.push.friendFollowCircle");
			return String.format(str, name);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getPushFreeSampleMessage(String tittle) {
		try {
			String str = resBundle.getString("notify.push.freeSample.message");
			return String.format(str, tittle);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getPushConsultationMessage(String tittle) {
		try {
			String str = resBundle.getString("notify.push.consultation.message");
			return String.format(str, tittle);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getStarOfWeekMessage() {
		try {
			return resBundle.getString("notify.starOfWeek.message");
		} catch (Exception e) {
			return "";
		}
	}
}