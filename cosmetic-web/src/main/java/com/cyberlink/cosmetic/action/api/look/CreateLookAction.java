package com.cyberlink.cosmetic.action.api.look;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.look.dao.LookDao;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.Look;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/look/create-look.action")
public class CreateLookAction extends AbstractAction {

    @SpringBean("look.LookDao")
    private LookDao lookDao;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private Long typeId;
    private String featureRoomId;
	private String name;
	private String description;
	private String imageUrls;
	private String attachmentUrl;

	@Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
	
	public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
	
	public void setFeatureRoomId(String featureRoomId) {
        this.featureRoomId = featureRoomId;
    }

	public void setName(String name) {
        this.name = name;
    }
	
	public void setDescription(String description) {
        this.description = description;
    }
	
	public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }
	
	public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
	
	@DefaultHandler
	public Resolution route() {
	    RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
	    if(!authenticate())
            return new ErrorResolution(authError);
	    if(typeId == null || !lookTypeDao.exists(typeId))
	        return new ErrorResolution(ErrorDef.InvalidTypeId);
		
	    Look newLook = new Look();
	    newLook.setUserId(getCurrentUserId());
	    newLook.setTypeId(typeId);
	    newLook.setFeatureRoomId(featureRoomId);
	    newLook.setName(name);
	    newLook.setDescription(description);
	    newLook.setImageUrls(imageUrls);
	    newLook.setAttachmentUrl(attachmentUrl);
	    Look newCreatedLook = lookDao.create(newLook);
	    if(newCreatedLook == null)
	        return new ErrorResolution(ErrorDef.BadRequest);
	    final Map<String, Object> results = new HashMap<String, Object>();
	    results.put("lookId", newCreatedLook.getId());
		return json(results);
	}
}
