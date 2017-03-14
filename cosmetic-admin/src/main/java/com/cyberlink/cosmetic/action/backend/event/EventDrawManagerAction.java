package com.cyberlink.cosmetic.action.backend.event;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.displaytag.tags.TableTagParameters;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.event.model.ReceiveType;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventCouponService;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventHomeService;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventStoreService;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.json.JsonArray;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/event/EventDrawManager.action")
public class EventDrawManagerAction extends AbstractAction{

    @SpringBean("event.EventUserDao")
    private EventUserDao eventUserDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    @SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
    
    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("mail.mailJoinEventHomeService")
	private MailJoinEventHomeService mailJoinEventHomeService;
	
	@SpringBean("mail.mailJoinEventStoreService")
	private MailJoinEventStoreService mailJoinEventStoreService;
	
	@SpringBean("mail.mailJoinEventCouponService")
	private MailJoinEventCouponService mailJoinEventCouponService;
	
	@SpringBean("core.jdbcTemplate")
    private TransactionTemplate transactionTemplate;
  
    private int defaultPageSize = 100;
    private Long brandEventId;
    private PageResult<EventUser> selectedEventUsers = new PageResult<EventUser>();
    private String selectedEventUserIds;
    private Boolean isFinished = false;
    private Boolean isSend = false; 
    private ReceiveType receiveType;
    private Boolean isCoupon = Boolean.FALSE;
    private JsonArray couponCode = null;
    private int codeIndex = 0;
    private String invalidEventUserIdList;
    private Boolean isNotifyed = Boolean.FALSE;
    private List<Long> redrawEventUserIdList = null;
    private List<String> redrawCouponCodeList = null;

	@DefaultHandler
    public Resolution route() {
        if (!getCurrentUserAdmin() && !getAccessControl().getEventManagerAccess()) {
        	return new StreamingResolution("text/html", "Need to login");
        }
        if (!brandEventDao.exists(brandEventId))
        	return new StreamingResolution("text/html", "Invalid brandEvent Id");
        
        BrandEvent event = brandEventDao.findById(brandEventId);
        receiveType = event.getReceiveType();
        if(event.getNotifyTime() != null && !event.getNotifyTime().isEmpty()){
        	isNotifyed = Boolean.TRUE;
        }
        
        List<Long> randomList = eventUserDao.findIdsByEventId(brandEventId, Arrays.asList(EventUserStatus.Selected, EventUserStatus.Redeemed));
        if (randomList == null || randomList.isEmpty()) {
			randomList = parseSelectedList(selectedEventUserIds);
			if (randomList == null || randomList.isEmpty()) {
				randomList = eventUserDao.findRandomIdsByEventId(brandEventId, EventUserStatus.Joined, new BlockLimit(0, event.getQuantity().intValue()), !ReceiveType.Coupon.equals(receiveType));
		        selectedEventUserIds = convertToSelectedIds(randomList);
	        }
        } else {
        	selectedEventUserIds = convertToSelectedIds(randomList);
        	isFinished = true;
        }
        
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
            PageResult<EventUser> eventUsers = eventUserDao.findByIds(randomList, blockLimit);
            if(eventUsers.getResults().size() <= 0)
                break;
            selectedEventUsers.getResults().addAll(eventUsers.getResults());
            if(queryAll)
            	selectedEventUsers.setTotalSize(selectedEventUsers.getTotalSize() + eventUsers.getResults().size());
            else
            	selectedEventUsers.setTotalSize(eventUsers.getTotalSize());
            offset += limit;
            if(offset > eventUsers.getTotalSize())
                break;
        } while(false || queryAll);
        
        if(queryAll)
            defaultPageSize = selectedEventUsers.getTotalSize();  

        return forward();
    }
    
	public Resolution confirmRoute() {
		if (!getCurrentUserAdmin()
				&& !getAccessControl().getEventManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		
		List<Long> randomList = parseSelectedList(selectedEventUserIds);
		final List<String> selectedMailList = new ArrayList<String>();
		final List<String> selectedPhoneList = new ArrayList<String>();

		if (brandEventId != null && randomList != null && randomList.size() > 0) {
			// Coupon
			try {
				BrandEvent brandEvent = brandEventDao.findByBrandEventId(brandEventId);
				if (ReceiveType.Coupon.equals(receiveType)) {
					couponCode = new JsonArray(brandEvent.getCouponCode());
					isCoupon = Boolean.TRUE;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				return new StreamingResolution("text/html", "get coupon code fail");
			}
			
			int offset = 0;
			int limit = 100;

			WritableWorkbook workbook;
			File file = new File(Constants.getFreeSamplePath()
					+ String.format("/%s.xls", brandEventId.toString()));
			try {
				workbook = Workbook.createWorkbook(file);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return new StreamingResolution("text/html", "create file fail");
			}
			try {
				// optput winner list.
				WritableSheet sheet = workbook.createSheet("Winner List", 0);
				int labelIdx = 0;
				sheet.addCell(new Label(labelIdx++, 0, "Id"));
				sheet.addCell(new Label(labelIdx++, 0, "Display Name"));
				sheet.addCell(new Label(labelIdx++, 0, "Real Name"));
				sheet.addCell(new Label(labelIdx++, 0, "Birthday"));
				if(!ReceiveType.Coupon.equals(receiveType))
					sheet.addCell(new Label(labelIdx++, 0, "Phone"));
				sheet.addCell(new Label(labelIdx++, 0, "Email"));
				if (ReceiveType.Home.equals(receiveType)) {
					sheet.addCell(new Label(labelIdx++, 0, "User Address"));
				} else if (ReceiveType.Store.equals(receiveType)) {
					sheet.addCell(new Label(labelIdx++, 0, "Store Location"));
					sheet.addCell(new Label(labelIdx++, 0, "Store Name"));
					sheet.addCell(new Label(labelIdx++, 0, "Store Address"));
				} else if (ReceiveType.Coupon.equals(receiveType)) {
					sheet.addCell(new Label(labelIdx++, 0, "eCoupon"));
				}
				do {
					List<Long> selectedList = randomList.subList(
							offset,
							Math.min(offset + limit,
									randomList.size()));
					final List<EventUser> eventUsers = eventUserDao
							.findByIds(selectedList
									.toArray(new Long[selectedList.size()]));

					// update user's status to be Selected.
					Boolean response = transactionTemplate
							.execute(new TransactionCallback<Boolean>() {
								@Override
								public Boolean doInTransaction(
										TransactionStatus status) {
									try {
										for (EventUser eventUser : eventUsers) {
											if(redrawEventUserIdList != null && !redrawEventUserIdList.contains(eventUser.getId()))
												continue;
											String mail = eventUser.getMail();
											String phone = eventUser.getPhone();
											if (mail != null && !mail.isEmpty())
												selectedMailList.add(eventUser.getMail());
											if (phone != null && !phone.isEmpty())
												selectedPhoneList.add(eventUser.getPhone());
											eventUser.setUserStatus(EventUserStatus.Selected);
											if (isCoupon) {
												if (redrawCouponCodeList != null && !redrawCouponCodeList.isEmpty())
													eventUser.setCode(redrawCouponCodeList.get(codeIndex));
												else
													eventUser.setCode(couponCode.getString(codeIndex));
												codeIndex++;
											}
											eventUserDao.update(eventUser);
										}
										return true;
									} catch (Exception e) {
										return false;
									}
								}
							});

					if (response) {
						eventUserDao.UpdateEventUserInvailidByMailAndPhone(selectedMailList, selectedPhoneList, brandEventId, Boolean.FALSE, !isCoupon);
						int num = offset;
						for (EventUser eventUser : eventUsers) {
							num++;
							Long id = eventUser.getId();
							String displayName = eventUser.getDisplayName();
							String realName = eventUser.getName();
							String birthday = eventUser.getBirthDayString();
							String phone = eventUser.getPhone();
							String mail = eventUser.getMail();
							int idx = 0;
							if (id != null)
								sheet.addCell(new Label(idx, num, id.toString()));
							idx++;
							if (displayName != null)
								sheet.addCell(new Label(idx, num, displayName));
							idx++;
							if (realName != null)
								sheet.addCell(new Label(idx, num, realName));
							idx++;
							if (birthday != null)
								sheet.addCell(new Label(idx, num, birthday));
							idx++;
							if (!ReceiveType.Coupon.equals(receiveType)) {
								if (phone != null)
									sheet.addCell(new Label(idx, num, phone));
								idx++;
							}
							if (mail != null)
								sheet.addCell(new Label(idx, num, mail));
							idx++;
							if (ReceiveType.Home.equals(receiveType)) {
								String userAddress = eventUser.getUserAddress();
								if (userAddress != null)
									sheet.addCell(new Label(idx, num, userAddress));
								idx++;
							} else if (ReceiveType.Store.equals(receiveType)) {
								String storeLocation = eventUser.getStoreLocation();
								if (storeLocation != null)
									sheet.addCell(new Label(idx, num, storeLocation));
								idx++;
								String storeName = eventUser.getStoreName();
								if (storeName != null)
									sheet.addCell(new Label(idx, num, storeName));
								idx++;
								String storeAddress = eventUser.getStoreAddress();
								if (storeAddress != null)
									sheet.addCell(new Label(idx, num, storeAddress));
								idx++;
							} else if (ReceiveType.Coupon.equals(receiveType)) {
								String coupon = eventUser.getCode();
								if (coupon != null)
									sheet.addCell(new Label(idx, num, coupon));
								idx++;
							}
						}
					} else {
						workbook.close();
						if (updateFailHandling(file, brandEventId)) {
							return new StreamingResolution("text/html",
									"confirm list fail");
						} else
							return new StreamingResolution("text/html",
									"rollback user status fail");
					}
					offset += limit;
					if (offset > randomList.size())
						break;

				} while (true);
				workbook.write();
				workbook.close();

			} catch (Exception e) {
				logger.error(e.getMessage());
				try {
					workbook.close();
				} catch (Exception e1) {
					logger.error(e1.getMessage());
				}
				if (updateFailHandling(file, brandEventId))
					return new StreamingResolution("text/html",
							"confirm list fail");
				else
					return new StreamingResolution("text/html",
							"rollback user status fail");
			}

		}
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("brandEventId", brandEventId.toString());
		parameters.put("isSend",isSend.toString());
		return new RedirectResolution(EventDrawManagerAction.class, "route").addParameters(parameters);
	}
    
    public Resolution sendNotify() {
    	if (!getCurrentUserAdmin()
				&& !getAccessControl().getEventManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}
    	
    	int offset = 0;
    	int limit = 50;
    	BrandEvent brandEvent = brandEventDao.findByBrandEventId(brandEventId);
    	if (brandEvent == null) {
    		return new StreamingResolution("text/html", "event doesn't exist");
    	}
        do {
        	PageResult<EventUser> eventUsers = eventUserDao.findSelectedEventUsersByEventId(brandEventId, new BlockLimit(offset, limit));
        	if (eventUsers.getResults().size() <= 0)
        		break;
        	notifyService.sendEventNotify(eventUsers.getResults(), brandEvent);
        	
        	for (EventUser eventUser : eventUsers.getResults()) {
        		try {
	        		if (brandEvent.getReceiveType().equals(ReceiveType.Home))
						mailJoinEventHomeService.send(eventUser.getId(), brandEvent.getId());
					else if (brandEvent.getReceiveType().equals(ReceiveType.Store))
						mailJoinEventStoreService.send(eventUser.getId(), brandEvent.getId());
					else if (brandEvent.getReceiveType().equals(ReceiveType.Coupon))
						mailJoinEventCouponService.send(eventUser.getId(), brandEvent.getId());
        		} catch (Exception e) {
        			logger.error(String.format("Send event mail fail, brandEventId=%d, userId=%d, mail=%s", brandEventId, eventUser.getUserId(), eventUser.getMail()));
        			logger.error(e.getMessage());
        		}
        	}
        	
        	offset += limit;
        	if (offset > eventUsers.getTotalSize())
        		break;
        	
        } while(true);
        
		String ntimeString = brandEvent.getNotifyTime();
		Date currentTime = new Date();
		if (ntimeString == null || ntimeString.isEmpty()) {
			ntimeString = String.valueOf(currentTime.getTime());
		} else {
			try {
				// check the ntimeString length (256 is the length of
				// NOTIFY_TIME in BC_BRAND_EVENT table)
				ntimeString += "," + String.valueOf(currentTime.getTime());
				byte[] ntimeBytes = ntimeString.getBytes("UTF-8");
				while (ntimeBytes.length > 256) {
					int idx = ntimeString.indexOf(",");
					ntimeString = ntimeString.substring(idx + 1);
					ntimeBytes = ntimeString.getBytes("UTF-8");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				getContext().getRequest().getSession()
						.removeAttribute("brandEventId");
				return new StreamingResolution("text/html",
						"send notidy success, but record notify time fail.");
			}
		}
        
        brandEvent.setNotifyTime(ntimeString);
        brandEventDao.update(brandEvent);
        return new StreamingResolution("text/html", "send notidy success");
    }
    
	public Resolution redrawVacancy(){
    	if (!getCurrentUserAdmin()
				&& !getAccessControl().getEventManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}
    	
		List<Long> selectedList = parseSelectedList(selectedEventUserIds);		
		List<Long> redrawEventUserIdList = new ArrayList<Long>();
		if(invalidEventUserIdList != null && invalidEventUserIdList.length() >= 1){
    		String[] invalidStr = invalidEventUserIdList.split(",");
    		Long[] invalidEventUserIds = new Long[invalidStr.length];
    		int idx = 0;
    		for (String idStr : invalidStr) {
    			if (idStr != null && !idStr.isEmpty())
    				invalidEventUserIds[idx] = Long.parseLong(idStr);
    			idx++;
    		}
    		
    		List<EventUser> invalidList = eventUserDao.findByIds(invalidEventUserIds);
    		List<String> inValidMailList = new ArrayList<String>();
    		List<String> inValidPhoneList = new ArrayList<String>();
    		List<String> couponCodeList = new ArrayList<String>();	
    		for (EventUser eu: invalidList) {
    			String code = eu.getCode();
    			String mail = eu.getMail();
    			String phone = eu.getPhone();
    			if (code != null && !code.isEmpty())
    				couponCodeList.add(code);
    			if (mail != null && !mail.isEmpty())
    				inValidMailList.add(mail);
    			if (phone != null && !phone.isEmpty())
    				inValidPhoneList.add(phone);
    			selectedList.remove(eu.getId());
    		}

    		int redrawNum = invalidEventUserIds.length;
    		if (!couponCodeList.isEmpty())
    			redrawNum = couponCodeList.size();
    		
    		eventUserDao.UpdateEventUserInvailidByMailAndPhone(inValidMailList, inValidPhoneList, brandEventId, Boolean.TRUE, !ReceiveType.Coupon.equals(receiveType));
			List<Long> randomList = eventUserDao.findRandomIdsByEventId(brandEventId, EventUserStatus.Joined, new BlockLimit(0, redrawNum), !ReceiveType.Coupon.equals(receiveType));
			selectedList.addAll(randomList);
			redrawEventUserIdList.addAll(randomList);

			getContext().getRequest().getSession().setAttribute("randomList", selectedEventUserIds);
			return new RedirectResolution(EventDrawManagerAction.class, "confirmRoute")
					.addParameter("brandEventId", brandEventId)
					.addParameter("receiveType", receiveType)
					.addParameter("isSend", isSend)
					.addParameter("selectedEventUserIds", convertToSelectedIds(selectedList))
					.addParameter("redrawEventUserIdList", redrawEventUserIdList)
					.addParameter("redrawCouponCodeList", couponCodeList);
    	}
		return new RedirectResolution(EventDrawManagerAction.class, "route").addParameter("brandEventId", brandEventId).addParameter("isSend", isSend);
    }
    
    public Resolution reDraw() {
        return new RedirectResolution(EventDrawManagerAction.class, "route")
        		.addParameter("brandEventId", brandEventId).addParameter("isSend", isSend);
	}

	private Boolean updateFailHandling(File file, Long eventId) {
		try {
			if (file.exists())
				file.delete();
			return eventUserDao.batchRollbackStatus(eventId);

		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
	private List<Long> parseSelectedList(String selectedEventUserIds) {
		List<Long> randomList = new ArrayList<Long>();
		if (selectedEventUserIds == null || selectedEventUserIds.isEmpty())
			return randomList;
		
		try {
			String[] selectedStr = selectedEventUserIds.split(",");
			for (String idStr : selectedStr) {
				randomList.add(Long.parseLong(idStr));
			}
		} catch (Exception e) {
			logger.error("parsse selected ids fail");
			logger.error(e.getMessage());
		}
		return randomList;
	}
	
	private String convertToSelectedIds(List<Long> randomList) {
		if (randomList == null || randomList.isEmpty())
			return null;
		
		String selectedEventUserIds = "";
		int idx = 0;
		for(Long id : randomList) {
			if (idx <= 0)
				selectedEventUserIds = id.toString();
			else
				selectedEventUserIds += "," + id.toString();
			idx++;
		}
		return selectedEventUserIds;
	}
	
	// testing
	public Resolution rollback() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		File file = new File(Constants.getFreeSamplePath()
				+ String.format("/%s.xls", brandEventId.toString()));

		if (updateFailHandling(file, brandEventId))
			return new StreamingResolution("text/html", String.format(
					"rollback %s success", brandEventId.toString()));
		else
			return new StreamingResolution("text/html",
					"rollback user status fail");
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

	public PageResult<EventUser> getSelectedEventUsers() {
		return selectedEventUsers;
	}

	public String getSelectedEventUserIds() {
		return selectedEventUserIds;
	}

	public void setSelectedEventUserIds(String selectedEventUserIds) {
		this.selectedEventUserIds = selectedEventUserIds;
	}

	public Boolean getIsFinished() {
		return isFinished;
	}

	public Boolean getIsSend() {
		return isSend;
	}

	public void setIsSend(Boolean isSend) {
		this.isSend = isSend;
	}

	public ReceiveType getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(ReceiveType receiveType) {
		this.receiveType = receiveType;
	}

	public String getInvalidEventUserIdList() {
		return invalidEventUserIdList;
	}

	public void setInvalidEventUserIdList(String invalidEventUserIdList) {
		this.invalidEventUserIdList = invalidEventUserIdList;
	}

	public Boolean getIsNotifyed() {
		return isNotifyed;
	}

	public List<Long> getRedrawEventUserIdList() {
		return redrawEventUserIdList;
	}

	public void setRedrawEventUserIdList(List<Long> redrawEventUserIdList) {
		this.redrawEventUserIdList = redrawEventUserIdList;
	}

	public void setRedrawCouponCodeList(List<String> redrawCouponCodeList) {
		this.redrawCouponCodeList = redrawCouponCodeList;
	}
}
