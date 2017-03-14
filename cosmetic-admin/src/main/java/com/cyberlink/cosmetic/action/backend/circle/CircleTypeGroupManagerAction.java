package com.cyberlink.cosmetic.action.backend.circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.UserAccessControl;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

@UrlBinding("/circle/circle-type-group-manager.action")
public class CircleTypeGroupManagerAction extends AbstractAction{
    @SpringBean("circle.circleTypeGroupDao")
    private CircleTypeGroupDao circleTypeGroupDao;
    
    @SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleTagGroupDao")
	private CircleTagGroupDao groupTagDao;

	@SpringBean("circle.circleTagDao")
	private CircleTagDao circleTagDao;
	
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	// route
	private List<CircleTypeGroup> circleTypeGroups = new ArrayList<CircleTypeGroup>();
	private PageResult<CircleTypeGroup> pageResult = new PageResult<>();
	public List<CircleTypeGroup> getCircleTypeGroups() {
	   return circleTypeGroups;
	}
	
	public PageResult<CircleTypeGroup> getPageResult() {
       return pageResult;
    }
	
	// newGroup	
	private List<String> availableLocale = new ArrayList<String>();
	
	public List<String> getAvailableLocale() {
	    return availableLocale;
	}
	
	// create
	private String typeGroupName;
	private Integer typeGroupOrder;
    private String typeNameMap;
	private Boolean isVisible = false;
	private Long iconId;
	private String iconUrl;
	private String imgUrl;
    private Map<String, Long> exTypeId = new HashMap<String, Long>();
	private Map<String, String> exTypeName = new HashMap<String, String>();
	private Map<String, Boolean> exTypeVisible = new HashMap<String, Boolean>();
	
    // publishGroup
	// deleteGroup
	private Long circleTypeGroupId;
	private Long circleTypeId;

	public void setTypeGroupName(String typeGroupName) {
	    this.typeGroupName = typeGroupName;
	}
	
	public String getTypeGroupName() {
        return typeGroupName;
    }
	
    public Integer getTypeGroupOrder() {
        return typeGroupOrder;
    }

    public void setTypeGroupOrder(Integer typeGroupOrder) {
        this.typeGroupOrder = typeGroupOrder;
    }
    
	public void setTypeNameMap(String typeNameMap) {
        this.typeNameMap = typeNameMap;
    }
	
	public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
	
	public Boolean getIsVisible() {
        return isVisible;
    }
	
	public void setIconId(Long iconId) {
        this.iconId = iconId;
    }
	
	public Long getIconId() {
        return iconId;
    }
	
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
	    
	public void setCircleTypeGroupId(Long circleTypeGroupId) {
	    this.circleTypeGroupId = circleTypeGroupId;
	}
	
	public Long getCircleTypeGroupId() {
        return circleTypeGroupId;
    }
	
	public void setCircleTypeId(Long circleTypeId) {
		this.circleTypeId = circleTypeId;
	}
	
	public Long getCircleTypeId() {
		return circleTypeId;
	}
	
	public Map<String, Long> getExTypeId() {
		return exTypeId;
	}
	
	public Map<String, String> getExTypeName() {
	    return exTypeName;
	}
	
	public Map<String, Boolean> getExTypeVisible() {
		return exTypeVisible;
	}
	
	public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

	public ErrorResolution authorized() {
	    User curUser = getCurrentUser();
        if(curUser == null)
            return new ErrorResolution(403, "You need to login");
        
        UserAccessControl accCtrl = getAccessControl();
        if(!getCurrentUserAdmin() && !accCtrl.getCircleManagerAccess()) {
            return new ErrorResolution(403, "You need to login");
        }
        
        return null;
	}
	
	@DefaultHandler
	public Resolution route() {
	    ErrorResolution err = authorized();
	    if(err != null)
	        return err;
        
        circleTypeGroups = circleTypeGroupDao.findAll();
        pageResult.getResults().addAll(circleTypeGroups);
        pageResult.setTotalSize(circleTypeGroups.size());
        return forward();
	}
	
	public Resolution newGroup() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
        
	    availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
	    if(circleTypeGroupId != null) {
	        CircleTypeGroup newCreatedCircleTypeGroup = circleTypeGroupDao.findById(circleTypeGroupId);
	        typeGroupName = newCreatedCircleTypeGroup.getGroupName();
	        typeGroupOrder = newCreatedCircleTypeGroup.getSortOrder();
	        imgUrl = newCreatedCircleTypeGroup.getImgUrl();
	        List<CircleType> exCirTypes = newCreatedCircleTypeGroup.getCircleTypes();
	        if(exCirTypes != null && exCirTypes.size() > 0) {
	            iconId = exCirTypes.get(0).getFileId();
	            iconUrl = exCirTypes.get(0).getIconUrl();
	            for(CircleType cT : exCirTypes) {
	                exTypeName.put(cT.getLocale(), cT.getCircleTypeName());
	                exTypeVisible.put(cT.getLocale(), cT.getIsVisible());
	                exTypeId.put(cT.getLocale(), cT.getId());
	            }
	        }
	        
	    }
		return forward();
	}
	
	private Map<String, User> GetLocaleCLUser() {
	    List<UserType> toGetUserTypes = new ArrayList<UserType>();
        toGetUserTypes.add(UserType.CL);
        Map<String, User> userLocaleMap = new HashMap<String, User>();
        Long offset = (long)0;
        Long limit = (long)100;
        do {
            PageResult<User> clUserIds = userDao.findByUserType(toGetUserTypes, null, offset, limit);
            if(clUserIds.getResults().size() <= 0)
                break;
            
            for(User usr : clUserIds.getResults()) {
                userLocaleMap.put(usr.getRegion(), usr);
            }
            offset += limit;
            if(offset > clUserIds.getTotalSize())
                break;
        } while(true);
        return userLocaleMap;
	}
	
	public Resolution create() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
	    
        CircleTypeGroup newCreatedCircleTypeGroup = null;
        if(circleTypeGroupId != null) { //modify CircleTypeGroup
            newCreatedCircleTypeGroup = circleTypeGroupDao.findById(circleTypeGroupId);
            newCreatedCircleTypeGroup.setGroupName(typeGroupName);
            newCreatedCircleTypeGroup.setSortOrder(typeGroupOrder);
            newCreatedCircleTypeGroup.setImgUrl(imgUrl);
        }
        else { //create CircleTypeGroup
            if(typeGroupName == null || typeGroupName.length() <= 0)
                return new ErrorResolution(400, "Bad typeGroupName");
            
            /*if(iconId == null)
                return new ErrorResolution(400, "Bad iconId");*/
            CircleTypeGroup circleTypeGroup = new CircleTypeGroup();
            circleTypeGroup.setGroupName(typeGroupName);
            circleTypeGroup.setSortOrder(typeGroupOrder);
            circleTypeGroup.setImgUrl(imgUrl);
            newCreatedCircleTypeGroup = circleTypeGroupDao.create(circleTypeGroup);
        }
	    
	    if(newCreatedCircleTypeGroup == null)
	        return new ErrorResolution(400, "Unknown error");
	    
	    availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE)); //get all available post type locale
	    JsonNode typeLocaleObj = null;
        if(typeNameMap != null && typeNameMap.length() > 0) {
            try {
                final ObjectMapper m = BeanLocator.getBean("web.objectMapper");
                typeLocaleObj = m.readValue(typeNameMap, JsonNode.class); //get all type names from jsp page
            } catch (Exception e) {
            }
        }
        
        Map<String, User> userLocaleMap = GetLocaleCLUser();        
        String defaultType = newCreatedCircleTypeGroup.getDefaultTypeName();
        Map<Long, String> exTypeIdNameMap =  new HashMap<Long, String>(); //for modifying the circle type name
        List<CircleType> exCirTypes = circleTypeDao.listTypesByTypeGroup(newCreatedCircleTypeGroup.getId(), null);
        for(CircleType cT : exCirTypes) {
            if(!typeLocaleObj.has(cT.getLocale()) || !availableLocale.contains(cT.getLocale()))
                continue;
        	String typeName = typeLocaleObj.get(cT.getLocale()).textValue();
        	exTypeIdNameMap.put(cT.getId(), typeName);
            availableLocale.remove(cT.getLocale()); //remove circle types which already had value (for creating new CircleType & new Circle)
        }
        
	    if(circleTypeGroupId != null) { //modify CircleType & related Circles
	    	List<Long> circleTypeIds = new ArrayList<Long>();
	    	List<Long> clUserIds = new ArrayList<Long>();
	    	
        	for(CircleType cT : exCirTypes){

        		circleTypeIds.add(cT.getId());
        		if(userLocaleMap.containsKey(cT.getLocale())) {
        		    User clUser = userLocaleMap.get(cT.getLocale());
        		    clUserIds.add(clUser.getId());
        		}
        		
        		//modify Circle types
	        	CircleType circleType = circleTypeDao.findById(cT.getId());
	        	circleType.setFileId(iconId);
	        	circleType.setImgUrl(imgUrl);
	        	circleType.setCircleTypeName(exTypeIdNameMap.get(cT.getId()));
	        	CircleType newCreatedCircleType = circleTypeDao.update(circleType); 
	        	if(newCreatedCircleType == null)
		            return new ErrorResolution(400, "Unknown error");
        	
	        	//modify the fake default circle for users
	        	PageResult<Circle> bcDefaultCircle = circleDao.findBcDefaultCircleByCircleTypeIds(ImmutableList.of(cT.getId()), (long)0, (long)1);
	        	if(bcDefaultCircle.getResults().size() > 0) {
    	        	Circle defaultCircle = bcDefaultCircle.getResults().get(0);
    	        	defaultCircle.setIconId(iconId);
    	        	defaultCircle.setCircleName(exTypeIdNameMap.get(cT.getId()));
    	        	circleDao.update(defaultCircle);
	        	}
        	}
        	
        	//modify default circle for CL account
        	int ctOffset = 0;
        	int ctLimit = 100;
        	do {
		        BlockLimit blockLimit = new BlockLimit(ctOffset, ctLimit);
		        PageResult<Circle> clCircles = circleDao.findByCLUserIds(clUserIds, circleTypeIds, true, blockLimit);
		        if(clCircles.getResults().size() <= 0)
		            break;
		        
		        for(Circle clCir : clCircles.getResults()) {
		            clCir.setIconId(iconId);
		            clCir.setCircleName(exTypeIdNameMap.get(clCir.getCircleTypeId()));
	                circleDao.update(clCir);
		        }
		        ctOffset += ctLimit;
		        if(ctOffset > clCircles.getTotalSize())
		            break;
        	} while(true);
        }
        
        //create new CircleType & Circles
	    for(String locale : availableLocale) {
	        String typeName = typeLocaleObj.get(locale).textValue();
            if(typeName == null || typeName.length() <= 0)
                continue;
            
	        CircleType circleType = new CircleType();
	        circleType.setLocale(locale);
	        circleType.setFileId(iconId);
	        circleType.setImgUrl(imgUrl);
	        circleType.setIsVisible(false);
	        circleType.setCircleTypeGroupId(newCreatedCircleTypeGroup.getId());
	        circleType.setCircleTypeName(typeName);
	        CircleType newCreatedCircleType = circleTypeDao.create(circleType); //create CircleType
	        if(newCreatedCircleType == null)
	            return new ErrorResolution(400, "Unknown error");
	        
	        Circle defaultCircle = new Circle();
	        defaultCircle.setIconId(iconId);
	        defaultCircle.setDefaultType(defaultType);
	        defaultCircle.setCircleName(typeName);
	        defaultCircle.setCricleTypeId(newCreatedCircleType.getId());
	        circleDao.create(defaultCircle); //create a fake default circle for users
	        
	        User clUser = userLocaleMap.get(locale);
	        if(clUser == null)
	            continue;
	            
	        Circle clCircle = new Circle();
	        clCircle.setIconId(iconId);
	        clCircle.setCreatorId(clUser.getId());
	        clCircle.setDefaultType(defaultType);
	        clCircle.setCircleName(typeName);
	        clCircle.setCricleTypeId(newCreatedCircleType.getId());
	        clCircle.setIsSecret(true);
            circleDao.create(clCircle); //also create a default circle for CL account
	    }
	    
		return json("Done");
	}
	
	public Resolution publishType() {
	    if(isVisible == null)
	        return json("Done");
	    
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
        
        List<Long> circleTypeIds = new ArrayList<Long>();
        circleTypeIds.add(circleTypeId);
	    
        List<UserType> toGetUserTypes = new ArrayList<UserType>();
        List<Long> userIds = new ArrayList<Long>();
        toGetUserTypes.add(UserType.CL);
        Long offset = (long)0;
        Long limit = (long)100;
        do {
            PageResult<User> clUserIds = userDao.findByUserType(toGetUserTypes, null, offset, limit);
            if(clUserIds.getResults().size() <= 0)
                break;
            
            for(User usr : clUserIds.getResults()) {
                userIds.add(usr.getId());
            }
            offset += limit;
            if(offset > clUserIds.getTotalSize())
                break;
        } while(true);
	    
	    int ctOffset  = 0;
	    int ctLimit = 100;	    
	    do {
	        BlockLimit blockLimit = new BlockLimit(ctOffset, ctLimit);
	        PageResult<Circle> circles = circleDao.findByCLUserIds(userIds, circleTypeIds, true, blockLimit);
	        if(circles.getResults().size() <= 0)
	            break;
	        
	        for(Circle cir : circles.getResults()) {
	            cir.setIsSecret(!isVisible);
                circleDao.update(cir);
	        }

	        ctOffset += ctLimit;
	        if(ctOffset > circles.getTotalSize())
	            break;
	    } while(true);
	    
        CircleType circleType = circleTypeDao.findById(circleTypeId);
        circleType.setIsVisible(isVisible);
        circleTypeDao.update(circleType);

	    return json("Done");
	}
	
	public Resolution publishGroup() { /* deprecated */
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
	    List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(circleTypeGroupId, null);
	    List<Long> circleTypeIds = new ArrayList<Long>();
	    for(CircleType cT : circleTypes) {
	        circleTypeIds.add(cT.getId());
	    }
	    
        List<UserType> toGetUserTypes = new ArrayList<UserType>();
        List<Long> userIds = new ArrayList<Long>();
        toGetUserTypes.add(UserType.CL);
        Long offset = (long)0;
        Long limit = (long)100;
        do {
            PageResult<User> clUserIds = userDao.findByUserType(toGetUserTypes, null, offset, limit);
            if(clUserIds.getResults().size() <= 0)
                break;
            
            for(User usr : clUserIds.getResults()) {
                userIds.add(usr.getId());
            }
            offset += limit;
            if(offset > clUserIds.getTotalSize())
                break;
        } while(true);
	    
	    int ctOffset  = 0;
	    int ctLimit = 100;	    
	    do {
	        BlockLimit blockLimit = new BlockLimit(ctOffset, ctLimit);
	        PageResult<Circle> circles = circleDao.findByCLUserIds(userIds, circleTypeIds, true, blockLimit);
	        if(circles.getResults().size() <= 0)
	            break;
	        
	        for(Circle cir : circles.getResults()) {
	            if(!cir.getIsSecret())
	                continue;
	            
                cir.setIsSecret(false);
                circleDao.update(cir);
	        }

	        ctOffset += ctLimit;
	        if(ctOffset > circles.getTotalSize())
	            break;
	    } while(true);
	    
	    List<CircleType> exCirTypes = circleTypeDao.listTypesByTypeGroup(circleTypeGroupId, null);
	    for(CircleType ct : exCirTypes) {
	        if(!ct.getIsVisible()) {
	            ct.setIsVisible(true);
	            circleTypeDao.update(ct);
	        }
	    }
	    
	    return json("Done");
	}
	
	public Resolution deleteGroup() {
	    ErrorResolution err = authorized();
        if(err != null)
            return err;
	    
        List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(circleTypeGroupId, null);
        List<Long> circleTypeIds = new ArrayList<Long>();
        for(CircleType cT : circleTypes) {
            cT.setIsDeleted(true);
            circleTypeDao.update(cT);
            circleTypeIds.add(cT.getId());
        }
        
        List<UserType> toGetUserTypes = new ArrayList<UserType>();
        List<Long> userIds = new ArrayList<Long>();
        toGetUserTypes.add(UserType.CL);
        Long offset = (long)0;
        Long limit = (long)100;
        do {
            PageResult<User> clUserIds = userDao.findByUserType(toGetUserTypes, null, offset, limit);
            if(clUserIds.getResults().size() <= 0)
                break;
            
            for(User usr : clUserIds.getResults()) {
                userIds.add(usr.getId());
            }
            offset += limit;
            if(offset > clUserIds.getTotalSize())
                break;
        } while(true);
        
        int ctOffset  = 0;
        int ctLimit = 100;      
        do {
            BlockLimit blockLimit = new BlockLimit(ctOffset, ctLimit);
            PageResult<Circle> circles = circleDao.findByCLUserIds(userIds, circleTypeIds, true, blockLimit);
            if(circles.getResults().size() <= 0)
                break;
            
            for(Circle cir : circles.getResults()) {
                cir.setDefaultType(null);
                circleDao.update(cir);
            }

            ctOffset += ctLimit;
            if(ctOffset > circles.getTotalSize())
                break;
        } while(true);
        
        Long dbcctOffset = (long)0;
        Long dbcctLimit = (long)100;
        do {
            PageResult<Circle> circles = circleDao.findBcDefaultCircleByCircleTypeIds(circleTypeIds, dbcctOffset, dbcctLimit);
            if(circles.getResults().size() <= 0)
                break;
            
            for(Circle cir : circles.getResults()) {
                cir.setIsDeleted(true);
                circleDao.update(cir);
            }

            dbcctOffset += dbcctLimit;
            if(dbcctOffset > circles.getTotalSize())
                break;
        } while(true);
        
        CircleTypeGroup circleTypeGroup = circleTypeGroupDao.findById(circleTypeGroupId);
        circleTypeGroup.setIsDeleted(true);
        circleTypeGroupDao.update(circleTypeGroup);
        
        return json("Done");
	}
}
