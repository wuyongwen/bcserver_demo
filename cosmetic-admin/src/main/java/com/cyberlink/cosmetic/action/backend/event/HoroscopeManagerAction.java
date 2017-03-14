package com.cyberlink.cosmetic.action.backend.event;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.dao.HoroscopeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Horoscope;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/event/HoroscopeManager.action")
public class HoroscopeManagerAction extends AbstractAction{
    
	@SpringBean("post.HoroscopeDao")
	private HoroscopeDao horoscopeDao;
    
	@SpringBean("common.localeDao")
    private LocaleDao localeDao;
	
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    private String defaultLocale = "en_US";
    private String locale;
    private List<String> availableLocale = new ArrayList<String>();
	public List<Horoscope> horoscopeList;
	private Long horoscopeId;
	private Horoscope horoscope;
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public List<String> getAvailableLocale() {
		return availableLocale;
	}
	
	public List<Horoscope> getHoroscopeList() {
		return horoscopeList;
	}

	public void setHoroscopeList(List<Horoscope> horoscopeList) {
		this.horoscopeList = horoscopeList;
	}

	public Long getHoroscopeId() {
		return horoscopeId;
	}

	public void setHoroscopeId(Long horoscopeId) {
		this.horoscopeId = horoscopeId;
	}

	public Horoscope getHoroscope() {
		return horoscope;
	}

	public void setHoroscope(Horoscope horoscope) {
		this.horoscope = horoscope;
	}

	@DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		if(locale == null){
			locale = this.defaultLocale;
		}
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		horoscopeList = horoscopeDao.listByLocale(locale);
		return forward();
	}

    public Resolution modify() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		if(horoscopeId != null)
			horoscope = horoscopeDao.findById(horoscopeId);
		return forward();
	}
	
    public Resolution delete() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		if(horoscopeId != null){
			horoscopeDao.delete(horoscopeId);
		}
		return new RedirectResolution("/event/HoroscopeManager.action?locale="+locale);
	}
    
    public Resolution cancel() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		return new RedirectResolution("/event/HoroscopeManager.action?locale="+horoscope.getLocale());
	}
    
    public Resolution update() {
		if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
		
		if (!postDao.exists(horoscope.getPostId())) {
			return json("Invalid postId");
		}
		
		Horoscope newhoroscope = new Horoscope();
		if (horoscopeId != null)
			newhoroscope = horoscopeDao.findById(horoscopeId);
		newhoroscope.setLocale(horoscope.getLocale());
		newhoroscope.setPostId(horoscope.getPostId());
		newhoroscope.setTitle(horoscope.getTitle());
		newhoroscope.setImageUrl(horoscope.getImageUrl());
		if (horoscopeId != null)
			horoscopeDao.update(newhoroscope);
		else
			horoscopeDao.create(newhoroscope);
    	return new RedirectResolution("/event/HoroscopeManager.action?locale="+horoscope.getLocale());
    }
}
