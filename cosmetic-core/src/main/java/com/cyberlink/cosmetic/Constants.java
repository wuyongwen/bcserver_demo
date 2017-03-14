package com.cyberlink.cosmetic;

import java.util.Calendar;
import java.util.Properties;

import com.cyberlink.core.BeanLocator;

public final class Constants {
    private static boolean is_initialized = Boolean.FALSE;
    private static Boolean is_redisLikeEnable = null;
    private static Boolean is_promotionalLikeEnable = null;
    
    private static final Integer[] INITIAL_VALUES = new Integer[] { 1 };
    public static final String PARAM_CURRENT_USER_ID = "__current_user_id";
    private static final Properties properties = BeanLocator
            .getBean("core.properties");

    private Constants() {

    }

    public static Integer[] getInitialValues() {
        return INITIAL_VALUES;
    }

    public static boolean isProductionMode() {
        return Boolean.FALSE;
    }

    public static final Long getShardId() {
        return (System.getProperty("shard.id") != null) ? Long.parseLong(System.getProperty("shard.id")) : 1l;
    }
    
    public static final String getContestDomain(){
    	 return properties.getProperty("website.contestDomain");
    }
    
    public static final String getCnContestDomain(){
        return properties.getProperty("website.cn.contestDomain");
    }
    
    public static final String getPftServiceDomain() {
        return properties.getProperty("website.pftServiceDomain");
    }
    
    public static final String getWebsiteDomain() {
        return properties.getProperty("website.domain");
    }

    public static final String getWebsiteSupportHttps() {
        return properties.getProperty("website.supporthttps");
    }
    
    public static final String getWebsiteIsWritable() {
        return properties.getProperty("website.is.writable");
    }
    
    public static final String getBackendPath() {
        return properties.getProperty("backend.path");
    }
    
    public static final String getCdnDomain() {
        return properties.getProperty("file.cdn.domain");
    }
    
    public static final String getBcCdnDomain() {
        return properties.getProperty("file.bc.cdn.domain");
    }
    
    public static final String getStorageLocalRoot() {
        return properties.getProperty("file.storage.local.root"); 
    }
    
    public static final String getFileBucket() {
        return properties.getProperty("file.bucket");
    }
    
    public static final String getOSSDomain() {
        return properties.getProperty("file.oss.domain");
    }
    
    public static final Boolean getIsCN() {
        return properties.getProperty("file.isCN").equalsIgnoreCase("true");
    }
    
    public static final String getPostRegion() {
        return properties.getProperty("post.region");
    }

    public static final String getPostRescueLogPath() {
        return properties.getProperty("post.rescuse.log.path");
    }
    
    public static final String getNotifyRegion() {
        return properties.getProperty("notify.region");
    }

    public static final String getNativeLibPath() {
        return properties.getProperty("native.lib.path");
    }
    
    public static final String getCert(String type) {
        if (type.equalsIgnoreCase("YMK"))
        	return properties.getProperty("apns.ymk.cert");
        else if (type.equalsIgnoreCase("YCN"))
        	return properties.getProperty("apns.ycn.cert");
        else if (type.equalsIgnoreCase("YBC"))
        	return properties.getProperty("apns.ybc.cert");
        else
        	return properties.getProperty("apns.ycp.cert");
    }

    public static final String getCertPass() {
    	return properties.getProperty("apns.pass");
    }

    public static final Boolean getIsApnsDevEnv() {
    	return properties.getProperty("apns.devEnv").equalsIgnoreCase("true");
    }

    public static final String getSyncHost() {
    	return properties.getProperty("sync.host");
    }

    public static final String getSyncPort() {
    	return properties.getProperty("sync.port");
    }

    public static final String getSyncPath() {
    	return properties.getProperty("sync.path");
    }

    public static final String getSyncUser() {
    	return properties.getProperty("db.user");
    }

    public static final String getSyncPassword() {
    	return properties.getProperty("db.pass");
    }

    public static final String getSyncCommand() {
    	return properties.getProperty("sync.command");
    }

    public static final int getNotifyOffsetUnit() {
    	String unit = properties.getProperty("notify.offset.unit");
    	if (unit != null && unit.equalsIgnoreCase("HOUR")){
    		return Calendar.HOUR;
    	} else {
    		return Calendar.MINUTE;
    	}
    }

    public static final int getNotifyOffset() {
    	String offset = properties.getProperty("notify.offset");
    	if (offset != null && offset.length() > 0)
    		return Integer.valueOf(properties.getProperty("notify.offset"));
    	else 
    		return 0;
    }

    public static final long getNotifyCheckTime() {
    	String offset = properties.getProperty("notify.check.time");
    	if (offset != null && offset.length() > 0)
    		return Long.valueOf(properties.getProperty("notify.check.time"));
    	else 
    		return 60000;
    }

    public static final String getCfgName() {
    	return properties.getProperty("db.cfg");
    }

    public static final Boolean getIsWriteOneDB() {
    	return properties.getProperty("db.writeone").equalsIgnoreCase("true");
    }

    public static final String getWebsiteWrite() {
    	return properties.getProperty("website.write");
    }

    public static final String getWebsiteReadToken() {
        return properties.getProperty("website.read.token");
    }

    public static final Boolean getIsPostCacheView() {
    	return properties.getProperty("post.cacheView").equalsIgnoreCase("true");
    }
    
    public static final String getLoggingPath() {
    	return properties.getProperty("logback.logging.file.path");
    }
    
    public static final String getFreeSamplePath() {
    	return properties.getProperty("freesample.file.path");
    }
    
    public static final Boolean getCustomerMailEnable() {
    	return properties.getProperty("freesample.customer.mail.enabled").equalsIgnoreCase("true");
    }
    
    public static final String getMsrPreviewUrl() {
        return properties.getProperty("msr.preview.url"); 
    }

    public static final void setInitialized(boolean value) {
        Constants.is_initialized = value;
    }
    
    public static final Boolean isInitialized() {
        return Constants.is_initialized;
    }
    
    public static boolean getRedisLikeEnable() {
        if(Constants.is_redisLikeEnable == null)
            setRedisLikeEnable(properties.getProperty("redis.like.enable").equalsIgnoreCase("true"));
        
        return Constants.is_redisLikeEnable;
    }

    public static void setRedisLikeEnable(boolean is_redisLikeEnable) {
        Constants.is_redisLikeEnable = is_redisLikeEnable;
    }

    public static boolean getPromotionalLikeEnable() {
        if(Constants.is_promotionalLikeEnable == null)
            setPromotionalLikeEnable(properties.getProperty("redis.promotional.like.enable").equalsIgnoreCase("true"));
        
        return Constants.is_promotionalLikeEnable;
    }

    public static void setPromotionalLikeEnable(boolean is_promotionalLikeEnable) {
        Constants.is_promotionalLikeEnable = is_promotionalLikeEnable;
    }
    
    public static final Boolean getIsRedisFeedEnable() {
        return properties.getProperty("redis.feed.enable").equalsIgnoreCase("true");
    }
    
    public static final Boolean getIsRabbitMqEnable() {
        return properties.getProperty("rabbitmq.enable").equalsIgnoreCase("true");
    }
    
    public static boolean getPersonalTrendEnable() {
        String value = properties.getProperty("redis.personal.trend.enable");
        if(value == null)
            return false;
        return value.equalsIgnoreCase("true");
    }
    
    public static final String getRabbitMqUserName() {
        return properties.getProperty("rabbitmq.connection.username");
    }
    
    public static final String getRabbitMqPassword() {
        return properties.getProperty("rabbitmq.connection.password");
    }

    public static final String[] getRabbitMqSlaves() {
        String hosts = properties.getProperty("rabbitmq.slaves");
        if(hosts == null || hosts.length() <= 0)
            return null;
        return hosts.split(";");
    }
    
    public static final String getRabbitMqMaster() {
        String host = properties.getProperty("rabbitmq.master");
        if(host == null || host.length() <= 0)
            return null;
        if(!host.contains(":"))
            return null;
        return host;
    }
    
    public static final String getStatSDHost() {
        return properties.getProperty("statsd.host");
    }
    
    public static final Boolean getIsStatsdEnable() {
        return properties.getProperty("statsd.enable").equalsIgnoreCase("true");
    }
    
    public static final String getRestartTimePath() {
        return properties.getProperty("misc.restartTime.file.path");
    }
    
    public static final String getUploadJsonStringPath() {
        return properties.getProperty("product.uploadJsonString.file.path");
    }

	public static final String getSolrRelatedPostAPIDomain() {
        return properties.getProperty("solr.related.post.api.domain");
    }
	
	public static final String getSolrRelatedPostWriteAPIDomain() {
        return properties.getProperty("solr.related.post.write.api.domain");
    }
	
	public static final String getSolrSearchAPIDomain() {
        return properties.getProperty("solr.search.api.domain");
    }

	public static final String getBcmInitUrl() {
	    return properties.getProperty("bcm.init.url");
	}
	
	public static final String getBCWebsiteDomain() {
	    return properties.getProperty("bcwebsite.domain");
	}
	
	public static final String getGeoipPath() {
	    return properties.getProperty("maxmind.geoip.path");
	}
	
	public static final String getPostScoreLogPath() {
	    return properties.getProperty("post.score.disk.path");
	}
	
	public static final String getPostUpdateTrendGroupPath() {
	    return properties.getProperty("post.update.trend.group.path");
	}
	
	public static final Boolean enablePostPhotoProcess() {
	    String value = properties.getProperty("post.photo.process");
	    if(value == null)
	        return false;
	    return "true".equalsIgnoreCase(value);
	}
	
	public static final Boolean getIsHandleBounceMail() {
		return properties.getProperty("mail.handleBounceMail.auto.enable").equalsIgnoreCase("true");
	}
	
	public static final String getHostSesmail() {
		return properties.getProperty("mail.host.sesmail");
	}
	
	public static final String getHandleBounceMailPath() {
		return properties.getProperty("mail.handleBounceMail.path");
	}

	public static final Boolean enableNewMainPostJsonView() {
		return true;
	}
	
	public static final Boolean enableReplyCommentNotify() {
		String value = properties.getProperty("replycomment.notify.enable");
		if(value == null)
	        return false;
		return "true".equalsIgnoreCase(value);
	}
	
	public static Boolean ignoreSmsError() {
        return Boolean.valueOf(properties.getProperty("sms.error.ignored"));
    }
}
