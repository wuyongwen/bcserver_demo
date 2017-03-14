package com.cyberlink.cosmetic.action.backend.event;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.BrandEventStatus;
import com.cyberlink.cosmetic.modules.event.model.EventAttr;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

/**
 * @author Ben_Chen
 *
 */
@UrlBinding("/event/EventManager.action")
public class EventManagerAction extends AbstractAction{

    @SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    @SpringBean("notify.NotifyDao")
    private NotifyDao notifyDao;
  
    private ServiceType serviceType = ServiceType.FREE_SAMPLE;
    private String locale;
    private PageResult<BackendBrandEvent> pageResult = new PageResult<BackendBrandEvent>();
    private Set<String> eventLocales = new LinkedHashSet<String>();
    private Long brandEventId;
    private BackendBrandEvent brandEventDetail;
    private Integer priority;
    private Long postId;
	private List<String> companyEmailList;
    private List<String> pfEmailList;
    private String prodName;
    private String prodDescription;
    private String prodDetail;
    private String applyDesc;
    private String eventTypeDesc;
    private String receiveDesc;
    private String comment;
    private String storesInfo;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String companySendDate;
    private String drawTime;
    private String receiveBeginDate;
    private String receiveEndDate;

    @DefaultHandler
    public Resolution listRoute() {
        if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
        
        if(locale == null)
            locale = "en_US";
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        Date timeNow = cal.getTime();
        List<BrandEvent> beventList = brandEventDao.listBrandEvent(locale, serviceType);
        for(BrandEvent be : beventList) {
            pageResult.add(new BackendBrandEvent(be, timeNow));
            pageResult.setTotalSize(beventList.size());
        }
        
        eventLocales = localeDao.getAvailableLocaleByType(LocaleType.EVENT_LOCALE);
        return forward();
    }
    
    public Resolution detail() {
    	BrandEvent brandEvent = brandEventDao.findById(brandEventId);
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        Date timeNow = cal.getTime();
    	brandEventDetail = new BackendBrandEvent(brandEvent, timeNow);
    	String metaData = brandEventDetail.getEvent().getMetadata();
    	if (metaData != null && metaData.length() > 0) {
			try {
				JSONObject metaDataJson = new JSONObject(metaData);
				if (metaDataJson != null)
					postId = Long.parseLong(metaDataJson.get("postId").toString());
			} catch (JSONException e) {
				logger.error(e.getMessage());
			}
		}
    	return forward();
    }
    
    public Resolution detailUpdate(){
    	BrandEvent brandEvent = brandEventDao.findById(brandEventId);
    	String companyEmails = "";
    	String pfEmails = "";
    	brandEvent.setPriority(priority);
    	EventAttr eventAttr = brandEvent.getEventAttrJNode();
    	if(companyEmailList != null && companyEmailList.size() > 0){
    		for(String companyEmail : companyEmailList){
    			if(!StringUtils.isBlank(companyEmail))
    				companyEmails += (companyEmail + ", ");
    		}
    		if(companyEmails.length() > 2)
    			companyEmails = companyEmails.substring(0,companyEmails.length()-2);
    	}
    	if(pfEmailList != null && pfEmailList.size() > 0){
    		for(String pfEmail : pfEmailList){
    			if(!StringUtils.isBlank(pfEmail))
    				pfEmails += (pfEmail + ", ");
    		}
    		if(pfEmails.length() > 2)
    			pfEmails = pfEmails.substring(0,pfEmails.length()-2);
    	}
    	if(storesInfo != null && storesInfo != ""){
    		brandEvent.setStoresValue(storesInfo);
    	}
    	
    	Map<String, Object> mataDataMap = new HashMap<String, Object>();
		mataDataMap.put("postId", postId);
		JSONObject metaDataJson = new JSONObject(mataDataMap);
    	brandEvent.setMetadata(metaDataJson.toString());
    	
    	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    	eventAttr.setStartTime(covertStringToDate(sdFormat,startTime));
    	eventAttr.setEndTime(covertStringToDate(sdFormat,endTime));
    	eventAttr.setCompanySendDate(covertStringToDate(sdFormat,companySendDate));
    	eventAttr.setDrawTime(covertStringToDate(sdFormat,drawTime));
    	eventAttr.setReceiveBeginDate(covertStringToDate(sdFormat,receiveBeginDate));
    	eventAttr.setReceiveEndDate(covertStringToDate(sdFormat,receiveEndDate));
    	eventAttr.setCompanyEmail(companyEmails);
    	eventAttr.setPfEmail(pfEmails);
    	eventAttr.setApplyDesc(applyDesc);
    	eventAttr.setEventTypeDesc(eventTypeDesc);
    	eventAttr.setReceiveDesc(receiveDesc);
    	brandEvent.setTitle(title);
    	brandEvent.setDescription(description);
    	brandEvent.setEventAttrJNode(eventAttr);
    	brandEvent.setProdName(prodName);
    	brandEvent.setProdDescription(prodDescription);
    	brandEvent.setProdDetail(prodDetail);
    	brandEvent.setComment(comment);
    	brandEventDao.update(brandEvent);
    	return new RedirectResolution(EventManagerAction.class, "detail").addParameter("brandEventId", brandEventId);
    }
    
    private Date covertStringToDate(SimpleDateFormat sdFormat, String dateStr){
    	try{
    		return sdFormat.parse(dateStr);
    	} catch (Exception e) {
    		return null;
    	}
    }
    
    public Resolution delete() {
		BrandEvent brandEvent = brandEventDao.findById(brandEventId);
		brandEvent.setIsDeleted(Boolean.TRUE);
		brandEventDao.update(brandEvent);
		try {
			if (brandEvent.getIsDeleted())
				notifyDao.deleteFreeSampleNotify(brandEvent.getId());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new StreamingResolution("text/html",
					"Delete notification fail");
		}
    	
    	return new RedirectResolution(EventManagerAction.class, "listRoute").addParameter("locale", locale);
    }
    
    public class BackendBrandEvent {
        BrandEvent brandEvent;
        BrandEventStatus status;
        int remainDays = 0;
        int remainHours = 0;
    	int remainMinutes = 0;
        
        public BackendBrandEvent(BrandEvent brandEvent, Date curDate) {
            this.brandEvent = brandEvent;
            try {
				if (brandEvent.getIsDeleted()) {
					status = BrandEventStatus.Deleted;
				} else if (brandEvent.getStartTime() == null
						|| brandEvent.getEndTime() == null) {
					status = BrandEventStatus.Unknown;
				} else if (curDate.before(brandEvent.getStartTime())) {
					status = BrandEventStatus.Upcoming;
					DateTime dt1 = new DateTime(curDate);
					DateTime dt2 = new DateTime(brandEvent.getStartTime());
					remainDays = Days.daysBetween(dt1, dt2).getDays();
				} else if (curDate.before(brandEvent.getEndTime())) {
					status = BrandEventStatus.Ongoing;
					DateTime dt1 = new DateTime(curDate);
					DateTime dt2 = new DateTime(brandEvent.getEndTime());
					remainDays = Days.daysBetween(dt1, dt2).getDays();
					remainHours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
					remainMinutes = Minutes.minutesBetween(dt1, dt2)
							.getMinutes() % 60;
				} else if (curDate.before(brandEvent.getDrawTime())) {
					status = BrandEventStatus.Drawing;
				} else {
					status = BrandEventStatus.Expired;
				}
            } catch (Exception e) {
            	status = BrandEventStatus.Unknown;
            }
        }
        
		public String getStatus() {
			return status.toString();
		}
        
        public BrandEvent getEvent() {
            return brandEvent;
        }
        
        public int getRemainDays() {
        	return remainDays;
        }
        
        public int getRemainHours() {
        	return remainHours;
        }
        
        public int getRemainMinutes() {
        	return remainMinutes;
        }
        
        public String getStartTime() {
        	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	try {
        		sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        		return sdFormat.format(brandEvent.getStartTime()) + " CST";
        	} catch (Exception e) {	
        	}
        	return "";
        }
        
        public String getEndTime() {
        	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	try {
        		sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        		return sdFormat.format(brandEvent.getEndTime()) + " CST";
        	} catch (Exception e) {	
        	}
        	return "";
        }
        
        public String getDrawTime() {
        	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	try {
        		sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        		return sdFormat.format(brandEvent.getDrawTime()) + " CST";
        	} catch (Exception e) {	
        	}
        	return "";
        }
        
        public String getReceiveBeginDate() {
        	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	try {
        		sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        		return sdFormat.format(brandEvent.getEventAttrJNode().getReceiveBeginDate()) + " CST";
        	} catch (Exception e) {	
        	}
        	return "";
        }
        
        public String getReceiveEndDate() {
        	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	try {
        		sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        		return sdFormat.format(brandEvent.getEventAttrJNode().getReceiveEndDate()) + " CST";
        	} catch (Exception e) {	
        	}
        	return "";
        }
        
        public String getCompanySendDate() {
        	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	try {
        		sdFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        		return sdFormat.format(brandEvent.getEventAttrJNode().getCompanySendDate()) + " CST";
        	} catch (Exception e) {	
        	}
        	return "";
        }
        
        public List<String> getCompanyEmail() {
        	String companyEmail = brandEvent.getEventAttrJNode().getCompanyEmail();
        	if (companyEmail != null && companyEmail.length() > 0)
        		return Arrays.asList(companyEmail.split("\\s*,\\s*"));
        	return Collections.emptyList();
        }
        
        public List<String> getPfEmail() {
        	String pfEmail = brandEvent.getEventAttrJNode().getPfEmail();
        	if (pfEmail != null && pfEmail.length() > 0)
        		return Arrays.asList(pfEmail.split("\\s*,\\s*"));
        	return Collections.emptyList();
        }
        
        public Boolean getIsBcc() {
        	return brandEvent.getEventAttrJNode().getIsBcc();
        }
        
        public Boolean getIsSent() {
        	return brandEvent.getEventAttrJNode().getIsSent();
        }
        
		public Boolean getHasWinnerList() {
			try {
				File file = new File(Constants.getFreeSamplePath()
						+ String.format("/%s.xls", brandEvent.getId()
								.toString()));
				if (file.exists())
					return true;
			} catch (Exception e) {
			}
			return false;
		}
		
		public Boolean getHasNotifyTime() {
			String ntimeString = brandEvent.getNotifyTime();
			if (ntimeString == null || ntimeString.isEmpty())
				return false;
			return true;
		}

		public String getNotifyTime() {
			String ntimeDetail = "";
			String ntimeString = brandEvent.getNotifyTime();
			if (ntimeString != null && !ntimeString.isEmpty()) {
				try {
					String[] ntimeArray = ntimeString.split("\\s*,\\s*");
					for (int i = 0; i < ntimeArray.length; i++) {
						if (ntimeArray[i] == null || ntimeArray[i].isEmpty())
							continue;
						try {
							SimpleDateFormat sdFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date time = new Date(Long.parseLong(ntimeArray[i]));
							sdFormat.setTimeZone(TimeZone
									.getTimeZone("GMT+8:00"));
							if (i + 1 == ntimeArray.length)
								ntimeDetail += "<b>" + sdFormat.format(time)
										+ " CST" + "</b>";
							else
								ntimeDetail += sdFormat.format(time) + " CST"
										+ "</br>";
						} catch (Exception e) {
							continue;
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					return e.getMessage();
				}
			}
			return ntimeDetail;
		}
	}
    
    public Resolution detailEdit() {
    	BrandEvent brandEvent = brandEventDao.findById(brandEventId);
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        Date timeNow = cal.getTime();
    	brandEventDetail = new BackendBrandEvent(brandEvent, timeNow);
    	String metaData = brandEventDetail.getEvent().getMetadata();
    	if (metaData != null && metaData.length() > 0) {
			try {
				JSONObject metaDataJson = new JSONObject(metaData);
				if (metaDataJson != null)
					postId = Long.parseLong(metaDataJson.get("postId").toString());
			} catch (JSONException e) {
				logger.error(e.getMessage());
			}
		}
    	return forward();
    }

    public PageResult<BackendBrandEvent> getPageResult() {
        return pageResult;
    }
    
    public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Set<String> getEventLocales() {
        return eventLocales;
    }

    public void setEventLocales(Set<String> eventLocales) {
        this.eventLocales = eventLocales;
    }

	public Long getBrandEventId() {
		return brandEventId;
	}

	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}

	public BackendBrandEvent getBrandEventDetail() {
		return brandEventDetail;
	}

	public void setBrandEventDetail(BackendBrandEvent brandEvent) {
		this.brandEventDetail = brandEvent;
	}

	public void setCompanyEmailList(List<String> companyEmailList) {
		this.companyEmailList = companyEmailList;
	}

	public void setPfEmailList(List<String> pfEmailList) {
		this.pfEmailList = pfEmailList;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public void setProdDescription(String prodDescription) {
		this.prodDescription = prodDescription;
	}

	public void setProdDetail(String prodDetail) {
		this.prodDetail = prodDetail;
	}

	public void setApplyDesc(String applyDesc) {
		this.applyDesc = applyDesc;
	}

	public void setReceiveDesc(String receiveDesc) {
		this.receiveDesc = receiveDesc;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setEventTypeDesc(String eventTypeDesc) {
		this.eventTypeDesc = eventTypeDesc;
	}

	public String getStoresInfo() {
		return storesInfo;
	}

	public void setStoresInfo(String storesInfo) {
		this.storesInfo = storesInfo;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setCompanySendDate(String companySendDate) {
		this.companySendDate = companySendDate;
	}

	public void setDrawTime(String drawTime) {
		this.drawTime = drawTime;
	}

	public void setReceiveBeginDate(String receiveBeginDate) {
		this.receiveBeginDate = receiveBeginDate;
	}

	public void setReceiveEndDate(String receiveEndDate) {
		this.receiveEndDate = receiveEndDate;
	}
}
