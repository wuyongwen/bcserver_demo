package com.cyberlink.cosmetic.action.backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.event.ApplicationEventPublisherHolder;
import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageLimitFactory;
import com.cyberlink.cosmetic.action.backend.misc.ServerRestartManageAction;
import com.cyberlink.cosmetic.amqp.MessageProducer;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractAction extends
        com.cyberlink.core.web.stripes.AbstractAction {
    @SpringBean("core.pageLimitFactory")
    private PageLimitFactory pageLimitFactory;

    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;

    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;

    @SpringBean("user.AttributeDao")
    protected AttributeDao attributeDao;
    
    @SpringBean("common.localeDao")
	private LocaleDao localeDao;
    
    @SpringBean("core.applicationEventPublisherHolder")
    private ApplicationEventPublisherHolder applicationEventPublisherHolder;
    
    /*@SpringBean("core.amqp.messageProducer")
    private MessageProducer messageProducer;*/
    
    protected UserAccessControl userAccessControl = null;
    protected User currentUser = null;
    protected Boolean isAdmin = null;
    protected String ogTitle = "Beauty Circle";
    protected String ogImage = "../common/theme/backend/images/logo.png";
    protected String ogDescription = "Beauty Circle from Cyberlink";
    private List<String> userLocaleList;
    private Set<String> postLocaleList ;
    private Session session = null;
	protected ErrorDef authError = ErrorDef.InvalidToken;

    public Set<String> getPostLocaleList() {
		if (postLocaleList == null) {
			postLocaleList = localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE);
		}
    	return postLocaleList;
	}

	public List<String> getUserLocaleList() {
		if (userLocaleList == null) {
			userLocaleList = new ArrayList<String>(localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE));
			Collections.reverse(userLocaleList);
		}
		return userLocaleList;
	}

	protected final PageLimit getPageLimit() {
        return pageLimitFactory.factory(getServletRequest());
    }

    protected final PageLimit getPageLimit(String tableId) {
        return pageLimitFactory.factory(getServletRequest(), tableId);
    }

    protected final Resolution json(String key, Object object) {
        return json(key, object, Views.Public.class);
    }

    protected final Resolution json(String key, Object object,
            Class<?> serializationView) {
        final Map<String, Object> m = new HashMap<String, Object>();
        m.put(key, object);
        return json(m, serializationView);
    }

    public final Resolution json(Object object) {
        return json(object, Views.Public.class);
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public User getCurrentUser() {
        if(currentUser != null)
            return currentUser;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                Session loginSession = sessionDao.findByToken(token);                
                if (loginSession == null) {
                	return null;
                } else {	
                    currentUser = loginSession.getUser();
                	return currentUser;
                }
            } else {
                return null;            	
            }
        }          
        return null;
    }    

    public Long getCurrentUserId() {
    	User user = getCurrentUser();
    	if (user != null) {
            return user.getId();
    	}
        return null;
    }
    
    public String getCurrentUserName() {
    	User user = getCurrentUser();
    	if (user != null) {
        	String name = user.getDisplayName();
        	if (name == null || name.length() == 0) {
        		name = "UserId : " + String.valueOf(user.getId());
        	}
        	return name;
    	}
        return null;
    }
    
    public Boolean getCurrentUserAdmin() {
        if(isAdmin != null)
            return isAdmin;
        
    	User user = getCurrentUser();
    	if (user != null) {
        	List<Attribute> attr = attributeDao.findByNameAndRefIds(AttributeType.AccessControl, "Access", user.getId());
            if (attr.size() > 0 && attr.get(0).getAttrValue().equals("Admin")) {
                isAdmin = Boolean.TRUE;
                return isAdmin;
            }        	
            isAdmin = Boolean.FALSE;
            return isAdmin;
    	}
    	isAdmin = Boolean.FALSE;
    	return isAdmin;
    }
    
    /*public Boolean getCurrentUserAdmin(User user) {
    	if (user != null) {
        	List<Attribute> attr = attributeDao.findByNameAndRefIds(AttributeType.AccessControl, "Access", user.getId());
            if (attr.size() > 0 && attr.get(0).getAttrValue().equals("Admin")) {
            	return Boolean.TRUE;
            }        	
        	return Boolean.FALSE;    		
    	}
        return Boolean.FALSE;
    }*/
    
    public String getCurrentUserAvatarUrl() {
    	User user = getCurrentUser();
    	if (user != null) {
            return user.getAvatarUrl();
    	}
        return null;
    }
    
    public String getCurrentUserLocale() {
    	User user = getCurrentUser();
    	if (user != null) {
        	String locale = user.getRegion();
        	return locale;
    	}
        return null;
    }  

    public UserStatus getCurrentUserStatus()	{
    	User user = getCurrentUser();
    	if (user != null) {
        	return user.getUserStatus();
    	}
        return null;
    }
    
    public UserAccessControl getAccessControl() {
    	User user = getCurrentUser();
    	if (userAccessControl == null) {
        	userAccessControl = new UserAccessControl(user);
    	} else {
    		userAccessControl.setUser(user);
    	}
    	return userAccessControl;
    }

    /*public UserAccessControl getAccessControl(User user) {
    	if (userAccessControl == null) {
        	userAccessControl = new UserAccessControl(user);
    	} else {
    		userAccessControl.setUser(user);
    	}
    	return userAccessControl;
    }*/
    
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
    
    protected final Resolution json(Object object, Class<?> serializationView) {
        try {
            return new StreamingResolution("application/json", objectMapper
                    .writerWithView(serializationView).writeValueAsString(
                            object));
        } catch (Exception e) {
            logger.error("json serialize error:" + object);
        }
        return new ErrorResolution(500);
    }
    
    public String getOgTitle() {
        return ogTitle;
    }
    
    public String getOgImage() {
        return ogImage;
    }
    
    public String getOgDescription() {
        return ogDescription;
    }
    
    public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	protected final void publishEvent(Event e) {
        applicationEventPublisherHolder.getApplicationEventPublisher()
                .publishEvent(e);
    }
	
	protected final void publishDurableEvent(DurableEvent e) {
	    /*try {
	        messageProducer.convertAndSend(e);
	    }
	    catch (Exception ex) {
	        logger.error(ex.getMessage());
	    }*/
    }
	
    public String getRestartServerTime() {
    	if(ServerRestartManageAction.restartServerTime != null)
    		return String.valueOf(ServerRestartManageAction.restartServerTime - (new Date()).getTime());
    	else 
    		return null;
    }
}
