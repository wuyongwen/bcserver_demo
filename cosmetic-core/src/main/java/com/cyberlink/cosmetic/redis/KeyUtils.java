package com.cyberlink.cosmetic.redis;

import java.util.UUID;

public abstract class KeyUtils {

    public static String userFollowing(Object userId) {
        return "u:" + userId + ":user.following";
    }

    public static String userExplicitFollower(Object userId) {
        return "u:" + userId + ":user.explicit.follower";
    }

    public static String circleFollowing(Object userId) {
        return "u:" + userId + ":circle.following";
    }

    public static String circleExplicitFollower(Object circleId) {
        return "u:" + circleId + ":circle.explicit.follower";
    }

    public static String userCirclePublic(Object userId) {
        return "u:" + userId + ":circle.public";
    }

    public static String pool(String postfix, Object userId) {
        return "p:" + userId + ":" + postfix;
    }

    public static String trendPool(String poolId) {
        return "t:" + poolId + ":" + "trend";
    }
    
    public static String perTrendPool(String locale, String circleType, String perType) {
        return "t:" + locale + "_" + circleType + ":" + perType;
    }
    
    public static String trendServiceHost() {
        return "t:hostname:trend.serice";
    }
    
    public static String trendPool(String locale, String circleKey, String perType, Long idx) {
        String oLocale = locale == null ? "null" : locale.toLowerCase();
        String oCircleKey = circleKey == null ? "null" : circleKey.toLowerCase();
        String oTlt = perType.toLowerCase();
        return "t:" + oLocale + "_" + oCircleKey + ":" + oTlt + "." + idx.toString();
    }
    
    public static String trendPoolInfo(String locale, String circleKey, String perType) {
        String oLocale = locale == null ? "null" : locale.toLowerCase();
        String oCircleType = circleKey == null ? "null" : circleKey.toLowerCase();
        String oTlt = perType.toLowerCase();
        return "t:" + oLocale + "_" + oCircleType + ":" + oTlt + ".info";
    }
    
    public static String trendPoolJoiner() {
        return "t:user:trend.pool.joiner";
    }
    
    public static String trendPoolUsers(Long shardId) {
    	return "t:" + shardId.toString() + ":trend.pool.users";
    }
    
    public static String trendPoolUserCategories(Long shardId, Long userId) {
    	return "t:" + shardId.toString() + ":trend.pool.user.categories" + "." + userId.toString();
    }
    
    public static String trendPoolUserGroup(Long shardId, Long userId) {
    	return "t:" + shardId.toString() + ":trend.pool.user.group" + "." + userId.toString();
    }
    
    public static String userFeed(Object userId) { 
        return "f:" + userId + ":";
    }

    public static String newFeedNotify(Long userId) {
        Long mod = userId % 1000;
        if(mod.equals(1L))
            mod = (userId / 1000) % 1000;
        String shard = String.valueOf(mod);
        return "u:" + shard + ":" + "new.feed.notify";
    }
    
    public static String randomFollowerKey(Long followeeId) {
        return "u:" + followeeId + ":follower." + UUID.randomUUID();
    }
    
    public static String postView(Long postId, String postfix) {
        return "u:" + postId.toString() + ":post." + postfix;
    }
    
    public static String userInterestPool() {
        return "u.user:interest";
    }
    
    public static String userInterestPoolLocale() {
        return "u.user.locale:interest";
    }
    
    public static String feedJoiner() {
        return "u:user:feed.joiner";
    }
    
    public static String targetLikers(String targetType, Long targetId) {
        return "u:" + targetId.toString() + ":" + targetType.toLowerCase() + ".likers";
    }
    
    public static String userLiked(String targetType, Long userId) {
        return "u:" + userId.toString() + ":" + targetType.toLowerCase()  + ".liked";
    }
    
    public static String promotionalLikeTarget(String locale, String date) {
        return "u:" + date + ":" + locale;
    }
    
    public static String promotionalLikeCount(String targetType, String targetId) {
        return "u:" + targetId + ":" + targetType;
    }
    
    public static String promotionalPostLikeCount(String locale) {
        return "u:" + locale + ":promo.base.count";
    }
    
    public static String promotionalLikeServiceHost() {
        return "u:hostname:promotional.like";
    }
    
    public static String userSession(String token) {
    	return token;
    }
    
    public static String activeInfo(Long roomId) {
    	return "room:" + roomId + ":usr.active";
    }
    
    public static String phoneRegistrationRequest(Object uuid) {
        return "u.phone.registration:request:" + uuid;
    }

    public static String phoneRegistration(Object uuid) {
        return "u.phone.registration:" + uuid;
    }
}
