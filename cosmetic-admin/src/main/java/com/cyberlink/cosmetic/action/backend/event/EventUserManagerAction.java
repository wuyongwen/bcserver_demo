package com.cyberlink.cosmetic.action.backend.event;

import org.displaytag.tags.TableTagParameters;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventHomeService;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventStoreService;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/event/EventUserManager.action")
public class EventUserManagerAction extends AbstractAction{
	
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;

    @SpringBean("event.EventUserDao")
    private EventUserDao eventUserDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("mail.mailJoinEventHomeService")
	private MailJoinEventHomeService mailJoinEventHomeService;
	
	@SpringBean("mail.mailJoinEventStoreService")
	private MailJoinEventStoreService mailJoinEventStoreService;
  
    private String locale;
    private int defaultPageSize = 100;
    private PageResult<EventUser> pageResult = new PageResult<EventUser>();
    private Long brandEventId;
    private ReceiveType receiveType;

    @DefaultHandler
    public Resolution listRoute() {
        if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
        if (!brandEventDao.exists(brandEventId))
        	return new StreamingResolution("text/html", "Invalid brandEvent Id");
        
        getContext().getRequest().getSession().setAttribute("brandEventId", brandEventId);
        
        Boolean queryAll = getContext().getRequest().getParameter(TableTagParameters.PARAMETER_EXPORTING) != null;       
        int offset;
        int limit;
        if(!queryAll) {
            PageLimit pageLimit = getPageLimit("row");
            offset = pageLimit.getStartIndex();
            limit = defaultPageSize;
        }
        else {
            offset = 0;
            limit = 100;
        }
        
        do {
            BlockLimit blockLimit = new BlockLimit(offset, limit);
            PageResult<EventUser> eventUsers = eventUserDao.findEventUserByEventId(brandEventId, blockLimit);
            if(eventUsers.getResults().size() <= 0)
                break;
            pageResult.getResults().addAll(eventUsers.getResults());
            if(queryAll)
            	pageResult.setTotalSize(pageResult.getTotalSize() + eventUsers.getResults().size());
            else
            	pageResult.setTotalSize(eventUsers.getTotalSize());
            offset += limit;
            if(offset > eventUsers.getTotalSize())
                break;
        } while(false || queryAll);
        
        if(queryAll)
            defaultPageSize = pageResult.getTotalSize();
        return forward();
    }
    
    // testing code
    public Resolution sendNotify() {
    	brandEventId = (Long) getContext().getRequest().getSession().getAttribute("brandEventId");
    	int offset = 0;
    	int limit = 50;
    	BrandEvent brandEvent = brandEventDao.findByBrandEventId(brandEventId);
    	if (brandEvent == null) {
    		getContext().getRequest().getSession().removeAttribute("brandEventId");
    		return new StreamingResolution("text/html", "event doesn't exist");
    	}
        do {
        	PageResult<EventUser> eventUsers = eventUserDao.findEventUserByEventId(brandEventId, new BlockLimit(offset, limit));
        	if (eventUsers.getResults().size() <= 0)
        		break;
        	notifyService.sendEventNotify(eventUsers.getResults(), brandEvent);
        	
        	for (EventUser eventUser : eventUsers.getResults()) {
        		try {
	        		if (brandEvent.getReceiveType().equals(ReceiveType.Home))
						mailJoinEventHomeService.send(eventUser.getId(), brandEvent.getId());
					else if (brandEvent.getReceiveType().equals(ReceiveType.Store))
						mailJoinEventStoreService.send(eventUser.getId(), brandEvent.getId());
        		} catch (Exception e) {
        			logger.error(String.format("Send event mail fail, brandEventId=%d, userId=%d, mail=%s", brandEventId, eventUser.getUserId(), eventUser.getMail()));
        			logger.error(e.getMessage());
        		}
        	}
        	
        	offset += limit;
        	if (offset > eventUsers.getTotalSize())
        		break;
        	
        } while(true);
        
        getContext().getRequest().getSession().removeAttribute("brandEventId");
        return new StreamingResolution("text/html", "send notidy success");
    }

    public PageResult<EventUser> getPageResult() {
        return pageResult;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
    
	public Long getBrandEventId() {
		return brandEventId;
	}

	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}

	public ReceiveType getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(ReceiveType receiveType) {
		this.receiveType = receiveType;
	}

}
