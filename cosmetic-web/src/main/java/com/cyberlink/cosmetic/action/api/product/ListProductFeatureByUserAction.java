package com.cyberlink.cosmetic.action.api.product; 

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductFeatureDao;
import com.cyberlink.cosmetic.modules.product.model.ProductFeature;
import com.cyberlink.cosmetic.utils.AppVersion;

@UrlBinding("/api/product/ListProductFeatureByUser.action")
public class ListProductFeatureByUserAction extends AbstractAction{

    private Long userId;
    private int offset = 0;
    private int limit = 10;
    private String appVersion = "1.0.0";

    @SpringBean("product.ProductFeatureDao")
	protected ProductFeatureDao productFeatureDao;
	
    @DefaultHandler
    public Resolution list() {
        if(userId == null)
            return new ErrorResolution(ErrorDef.InvalidUserId);
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        Date startDate = cal.getTime();
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("typeIndex", true);
        blockLimit.addOrderBy("productIndex", true);
        blockLimit.addOrderBy("createdTime", false);
        Long appVer = AppVersion.getAppVersion(appVersion);
        PageResult<ProductFeature> results = productFeatureDao.getProductFeatureByUser(userId, startDate, startDate, appVer, blockLimit);
		return json(results);
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setOffset(int offset) {
		this.offset = offset;
	}
    
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
}
