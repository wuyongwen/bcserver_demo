package com.cyberlink.cosmetic.action.api.product;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductCollectionDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.TargetType;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/user-collection.action")
public class UserCollectionAction extends AbstractAction {
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;

    @SpringBean("product.ProductCollectionDao")
    private ProductCollectionDao collectionDao;

    @SpringBean("product.ProductDao")
    private ProductDao productDao;
    
    private String token;
    private TargetType targetList;
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);;

    @DefaultHandler
    public Resolution route() {
        Session session = sessionDao.findByToken(token);
        if (session == null)
        	return new ErrorResolution(ErrorDef.InvalidToken);
        else if (session.getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
        
        if (targetList == null)
        	return new ErrorResolution(ErrorDef.InvalidTargetList);
        
        final Map<String, Object> results = new HashMap<String, Object>();
        
        PageResult<Long> result = collectionDao.findByUserIdAndType(session.getUserId(), targetList, offset, limit);
        results.put("result", result.getResults());
        results.put("totalSize", result.getTotalSize());
        return json(results);
    }
    
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public TargetType getTargetList() {
        return targetList;
    }
    public void setTargetList(TargetType targetList) {
        this.targetList = targetList;
    }
    public Long getOffset() {
        return offset;
    }
    public void setOffset(Long offset) {
        this.offset = offset;
    }
    public Long getLimit() {
        return limit;
    }
    public void setLimit(Long limit) {
        this.limit = limit;
    }
    
    
}
