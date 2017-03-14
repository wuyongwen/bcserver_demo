package com.cyberlink.cosmetic.action.backend.event;

import org.displaytag.tags.TableTagParameters;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/event/EventRedeemManager.action")
public class EventRedeemManagerAction extends AbstractAction{
	
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
  
    private String locale;
    private int defaultPageSize = 100;
    private PageResult<EventUser> pageResult = new PageResult<EventUser>();
    private Long brandEventId;

    @DefaultHandler
    public Resolution listRoute() {
        if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
        if (!brandEventDao.exists(brandEventId))
        	return new StreamingResolution("text/html", "Invalid brandEvent Id");
        
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
            PageResult<EventUser> eventUsers = eventUserDao.findRedeemedEventUsersByEventId(brandEventId, blockLimit);
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
}
