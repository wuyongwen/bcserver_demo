package com.cyberlink.cosmetic.action.api.product;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductCollectionDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.ProductCollection;
import com.cyberlink.cosmetic.modules.product.model.TargetType;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/remove-collection.action")
public class RemoveCollectionAction extends AbstractAction{
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;

    @SpringBean("product.ProductCollectionDao")
    private ProductCollectionDao collectionDao;

    @SpringBean("product.ProductDao")
    private ProductDao productDao;
    
    private String token;
    private Long productId;
    private TargetType targetList;

    @DefaultHandler
    public Resolution route() {
    	RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        Session session = sessionDao.findByToken(token);
        if (session == null)
        	return new ErrorResolution(ErrorDef.InvalidToken);
        else if (session.getStatus() == SessionStatus.Invalied)
        	return new ErrorResolution(ErrorDef.AccountEmailDeleted);
        
        if (!productDao.exists(productId))
        	return new ErrorResolution(ErrorDef.InvalidProductId);
        if (targetList == null)
        	return new ErrorResolution(ErrorDef.InvalidTargetList);
        
        ProductCollection collection = collectionDao.findProductCollection(productId, session.getUserId(), targetList);
        if (collection != null) {
            collection.setIsDeleted(Boolean.TRUE);
            collectionDao.update(collection);
        }
        return success();
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

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
}
