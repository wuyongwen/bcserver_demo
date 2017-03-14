package com.cyberlink.cosmetic.action.backend.v2.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.service.AutoFanPagePostService;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.user.dao.FanPageUserDao;
import com.cyberlink.cosmetic.modules.user.model.FanPageUser;
import com.restfb.json.JsonObject;

@UrlBinding("/v2/user/create-fan-page-user.action")
public class CreateFanPageUserAction extends AbstractAction {
	@SpringBean("user.FanPageUserDao")
	private FanPageUserDao fanPageUserDao;
	
	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
    
    @SpringBean("backend.AutoFanPagePostService")
    private AutoFanPagePostService autoFanPagePostService;
    
    private String tagCircleMap = "";
    private String fanPageLink = "";
    private Map<String, String> fanPageMap = new HashMap<String, String>();
    private String fanPageId;
    private String forwardData;
    PageResult<Circle> circles = new PageResult<Circle>();
    private Long userId;
    private String nextUrl;
    private String status = "false";
    private Boolean autoPost = Boolean.FALSE;
    private String sincetimepicker = "";
    private String untiltimepicker = "";
    
    private Map<String, String> myMap = new HashMap<String, String>();
    
    public Map<String, String> getMyMap(){
    	return myMap;
    }
    
    public String getTagCircleMap() {
        return this.tagCircleMap;
    }
    
    public void setTagCircleMap(String tagCircleMap) {
    	this.tagCircleMap = tagCircleMap;
    }
    
    public String getFanPageLink() {
        return this.fanPageLink;
    }

	public void setFanPageLink(String fanPageLink) {
    	this.fanPageLink = fanPageLink;
    }
    
    public Map<String, String> getFanPageMap() {
		return fanPageMap;
	}

	public void setFanPageMap(Map<String, String> fanPageMap) {
		this.fanPageMap = fanPageMap;
	}
	
	public String getFanPageId() {
		return fanPageId;
	}

	public void setFanPageId(String fanPageId) {
		this.fanPageId = fanPageId;
	}

	public String getForwardData() {
		return forwardData;
	}

	public void setForwardData(String forwardData) {
		this.forwardData = forwardData;
	}
    
    public PageResult<Circle> getCircles() {
        return this.circles;
    }
    
    public String getNextUrl() {
		return nextUrl;
	}

	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}

	public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getAutoPost() {
        return this.autoPost;
    }
    
    public void setAutoPost(Boolean autoPost) {
        this.autoPost = autoPost;
    }
    
	public String getSincetimepicker() {
		return sincetimepicker;
	}

	public void setSincetimepicker(String sincetimepicker) {
		this.sincetimepicker = sincetimepicker;
	}

	public String getUntiltimepicker() {
		return untiltimepicker;
	}

	public void setUntiltimepicker(String untiltimepicker) {
		this.untiltimepicker = untiltimepicker;
	}

	private Map<String, String> getDefaultMap(String locale) {
		Map<String, String> map = new HashMap<String, String>();
		if (locale.equals("zh_TW")) {
			map.put("美妝", "妝容,腮紅,彩妝,底妝,粉底,修容,粉,BB霜,CC霜");
			map.put("眼妝", "眼,睫毛,眉,眼妝 ");
			map.put("唇妝", "唇,口紅,嘴");
			map.put("自拍", "自拍");
			map.put("美甲", "指,甲");
			map.put("穿搭", "穿搭,時尚,流行,裝,褲,裙,鞋,衫,衣");
			map.put("護膚保養", "保養,美白,美肌,膚,膜,抗老,拉提,粉刺,清潔,痘,斑,液,身體");
			map.put("美髮", "髮");
		} else if (locale.equals("zh_CN")) {
			map.put("美妆", "妆");
			map.put("眼妆", "眼");
			map.put("唇妆", "唇");
			map.put("自拍", "自拍");
			map.put("美甲", "甲");
			map.put("穿搭", "搭");
			map.put("护肤保养", "肤");
			map.put("美发", "发");
		} else {
			map.put("Makeup",
					"makeup,blush,cosmetics,foundation,powder,cream");
			map.put("Eye Makeup", "eye,mascara");
			map.put("Lips", "lip");
			map.put("Selfie", "selfie");
			map.put("Nails", "manicure,nail,finger,armor,pedicure");
			map.put("Outfits",
					"outfit,fashion,top,style,wear,pant,skirt,shirt,trouser,suit,coat,jacket");
			map.put("Skincare",
					"skin,whitening,lotion,clean,peels,masks,anti-aging,acne,body");
			map.put("Hair", "hair,perm");
		}
		return map;
	}
    
	private void loadAvailableCircles(FanPageUser fanPageUser) {
		if (userId == null)
			userId = getCurrentUserId();

		String locale = getCurrentUserLocale();
		Map<String, String> defaultMap = getDefaultMap(locale);

		int offset = 0;
		int limit = 100;
		List<Circle> totalResult = new ArrayList<Circle>();
		do {
			PageResult<Circle> result = circleService.listUserCircle(userId,
					true, locale, true, new BlockLimit(offset, limit));
			if (result.getResults().size() <= 0)
				break;

			totalResult.addAll(result.getResults());
			offset += limit;
			if (offset > circles.getTotalSize())
				break;

		} while (true);
		
		List<Circle> remove = new ArrayList<Circle>();
		for (Circle c : totalResult) {
			if ("HOW-TO".equalsIgnoreCase(c.getDefaultType()))
				remove.add(c);
		}
		totalResult.removeAll(remove);
		
		circles.setResults(totalResult);
		circles.setTotalSize(totalResult.size());

		// mapping
		JsonObject jsonObj = null;
		if (fanPageUser != null) {
			tagCircleMap = fanPageUser.getTagCircleMap();
			try {
				if (tagCircleMap != null || !tagCircleMap.isEmpty())
					jsonObj = new JsonObject(tagCircleMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
				jsonObj = null;
			}
		}

		if (jsonObj == null)
			jsonObj = new JsonObject();
		else {
			List<String> removeList = new ArrayList<String>();
			Iterator<?> keys = jsonObj.keys();
			while (keys.hasNext()) {
				Boolean bRemove = Boolean.TRUE;
				String key = (String) keys.next();
				for (Circle circle : circles.getResults()) {
					if (key.equals(circle.getId().toString())) {
						bRemove = Boolean.FALSE;
						break;
					}
				}
				if (bRemove)
					removeList.add(key);
			}
			
			for (String key : removeList)
				jsonObj.remove(key);
		}

		for (Circle circle : circles.getResults()) {
			String circleName = circle.getCircleName();
			String circleId = circle.getId().toString();
			if (!jsonObj.has(circleId)) {
				if (defaultMap.containsKey(circleName))
					jsonObj.put(circleId, defaultMap.get(circleName));
				else
					jsonObj.put(circleId, circleName);
			}
		}
		tagCircleMap = jsonObj.toString();
		if (fanPageUser != null) {
			fanPageUser.setTagCircleMap(tagCircleMap);
			fanPageUserDao.update(fanPageUser);
		}
			
	}
	
    @DefaultHandler
	public Resolution route() {
		if (getCurrentUser() == null) {
			return new StreamingResolution("text/html", "Need Login");
		}

		FanPageUser fanPageUser = fanPageUserDao
				.findFanPageUserByUserId(getCurrentUserId());
		if (fanPageUser == null) {
			return forward();
		}

		JsonObject fanPageObj = null;
		if (fanPageUser.getFanPage() != null
				&& !fanPageUser.getFanPage().isEmpty()) {
			try {
				fanPageObj = new JsonObject(fanPageUser.getFanPage());
			} catch (Exception e) {
				return json("Resource error");
			}

			if (fanPageObj != null) {
				Iterator<?> keys = fanPageObj.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					try {
						fanPageMap.put(key, fanPageObj.getJsonObject(key)
								.getString("name"));
					} catch (Exception e) {
					}
				}
			}
		}

		// handle old case
		if (fanPageObj != null && fanPageMap.isEmpty()) {
			JsonObject newfanPage = new JsonObject();
			Iterator<?> keys = fanPageObj.keys();
			while (keys.hasNext()) {
				String fanPageName = (String) keys.next();
				JsonObject subObj = null;
				try {
					subObj = autoFanPagePostService.getFanpageObject(
							fanPageName, fanPageObj.getString(fanPageName));
					Iterator<?> fanPageIds = subObj.keys();
					while (fanPageIds.hasNext()) {
						String fanPageId = (String) fanPageIds.next();
						newfanPage.put(fanPageId, subObj.getJsonObject(fanPageId));
						fanPageMap.put(
								fanPageId,
								subObj.getJsonObject(fanPageId).getString(
										"name"));
					}
				} catch (Exception e) {

				}
			}
			if (!fanPageMap.isEmpty()) {
				fanPageUser.setFanPage(newfanPage.toString());
				fanPageUserDao.update(fanPageUser);
			}
		}
		
		loadAvailableCircles(fanPageUser);

		autoPost = fanPageUser.getAutoPost();
    	
    	if(tagCircleMap != null && !tagCircleMap.isEmpty()){
			try {
				JsonObject jsonObj = new JsonObject(tagCircleMap);
				Iterator<?> keys = jsonObj.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					myMap.put(key, jsonObj.getString(key));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
        return forward();
    }
    
	public Resolution pullData() {
		if (getCurrentUser() == null) {
			return new StreamingResolution("text/html", "Need Login");
		}

		FanPageUser fanPageUser = fanPageUserDao
				.findFanPageUserByUserId(getCurrentUserId());
		JsonObject fanPageObj = null;
		if (fanPageUser.getFanPage() != null
				&& !fanPageUser.getFanPage().isEmpty()) {
			try {
				fanPageObj = new JsonObject(fanPageUser.getFanPage());
			} catch (Exception e) {
				return json("Resource error");
			}
		}

		if (status.equalsIgnoreCase("true") && fanPageId != null) {
			if (fanPageObj != null && fanPageObj.has(fanPageId)) {
				String lastPostTime = fanPageObj.getJsonObject(fanPageId)
						.getString("lastPostTime");
				Date sinceTime = null;
				Date untilTime = null;
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				if (nextUrl == null || nextUrl.isEmpty()) {
					try {
						sinceTime = sdf.parse(sincetimepicker);
					} catch (Exception e) {
						if (!lastPostTime.isEmpty()) {
							sinceTime = new Date(Long.valueOf(lastPostTime));
							sincetimepicker = sdf.format(sinceTime);
						}
					}
					try {
						untilTime = sdf.parse(untiltimepicker);
					} catch (Exception e) {
						untilTime = null;
					}
				}

				JsonObject returnObj = new JsonObject();
				returnObj = autoFanPagePostService.doQuery(fanPageId,
						fanPageUser, 20, sinceTime, untilTime, nextUrl, null);
				if (sincetimepicker != null && !sincetimepicker.isEmpty())
					returnObj.put("sinceTime", sincetimepicker);
				forwardData = returnObj.toString();
				return json(forwardData);
			}
		}
		return json(null);
	}
    
	public Resolution updateTagCircleMap() {
		if (getCurrentUser() == null) {
			return new StreamingResolution("text/html", "Need Login");
		}

		if (tagCircleMap == null || tagCircleMap.isEmpty())
			return json("tagCircleMap is empty.");
		else {
			try {
				JsonObject jsonObject = new JsonObject(tagCircleMap);
				
				// check default circle
				String defaultCircleId = null;
				Iterator<?> keys = jsonObject.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String keywordStr = jsonObject.getString(key);
					if (keywordStr == null || keywordStr.isEmpty())
						continue;

					String[] keywords = keywordStr.split("\\s*,\\s*");
					for (int idx = 0; idx < keywords.length; idx++) {
						if (AutoFanPagePostService.defaultKey.equals(keywords[idx])) {
							defaultCircleId = key;
							break;
						}
					}

					if (defaultCircleId != null) {
						Circle defaultCircle = circleDao.findById(
								Long.valueOf(defaultCircleId), false);
						if (!defaultCircle.getIsSecret())
							return json("The default circle is not a secret circle.");
						break;
					}
				}
			} catch (Exception e) {
				return json("The format of circle map is wrong.");
			}
		}

		Long userId = getCurrentUserId();
		FanPageUser fanPageUser = fanPageUserDao
				.findFanPageUserByUserId(userId);
		if (fanPageUser != null) {
			fanPageUser.setLocale(getCurrentUserLocale());
			fanPageUser.setTagCircleMap(tagCircleMap);
			fanPageUserDao.update(fanPageUser);
		} else {
			FanPageUser newFanPageUser = new FanPageUser();
			newFanPageUser.setUserId(userId);
			newFanPageUser.setLocale(getCurrentUserLocale());
			newFanPageUser.setTagCircleMap(tagCircleMap);
			newFanPageUser.setAutoPost(false);
			fanPageUserDao.create(newFanPageUser);
		}

		return json("done");
	}
    
	public Resolution newFanPage() {
		if (getCurrentUser() == null) {
			return new StreamingResolution("text/html", "Need Login");
		}
		
		JsonObject fbObj = autoFanPagePostService.getFanpageObject(fanPageLink);
		if (fbObj == null)
			return json("This fanPage is not a valid URL!");

		FanPageUser fanPageUser = fanPageUserDao
				.findFanPageUserByUserId(getCurrentUserId());
		JsonObject fanPageObj = null;
		if (fanPageUser != null) {
			if (fanPageUser.getFanPage() != null
					&& !fanPageUser.getFanPage().isEmpty()) {
				try {
					fanPageObj = new JsonObject(fanPageUser.getFanPage());
				} catch (Exception e) {
					return json("Resource error");
				}
			}
		}
		
		if (fanPageObj == null)
			fanPageObj = new JsonObject();
		Iterator<?> keys = fbObj.keys();
		while (keys.hasNext()) {
			String fanPageId = (String) keys.next();
			if (!fanPageObj.has(fanPageId)) {
				fanPageObj.put(fanPageId, fbObj.getJsonObject(fanPageId));
			}
		}

		if (fanPageUser != null) {
			fanPageUser.setFanPage(fanPageObj.toString());
			fanPageUserDao.update(fanPageUser);
		} else {
			loadAvailableCircles(null);
			FanPageUser newFanPageUser = new FanPageUser();
			newFanPageUser.setUserId(userId);
			newFanPageUser.setLocale(getCurrentUserLocale());
			newFanPageUser.setFanPage(fanPageObj.toString());
			newFanPageUser.setTagCircleMap(tagCircleMap);
			newFanPageUser.setAutoPost(false);
			fanPageUserDao.create(newFanPageUser);
		}
		return json("done");
	}
    
	public Resolution deleteFanPage() {
		if (getCurrentUser() == null) {
			return new StreamingResolution("text/html", "Need Login");
		}

		if (fanPageId == null)
			return json("done");

		FanPageUser fanPageUser = fanPageUserDao
				.findFanPageUserByUserId(getCurrentUserId());
		JsonObject fanPageObj = null;
		if (fanPageUser == null) {
			return json("done");
		} else {
			if (fanPageUser.getFanPage() != null
					&& !fanPageUser.getFanPage().isEmpty()) {
				try {
					fanPageObj = new JsonObject(fanPageUser.getFanPage());
				} catch (Exception e) {
					return json("Resource error");
				}
			} else
				return json("done");
		}

		if (fanPageObj.has(fanPageId)) {
			fanPageObj.remove(fanPageId);
			fanPageUser.setFanPage(fanPageObj.toString());
			fanPageUserDao.update(fanPageUser);
		}
		return json("done");
	}

	public Resolution postData() {
		if (getCurrentUser() == null) {
			return new StreamingResolution("text/html", "Need Login");
		}

		FanPageUser fanPageUser = fanPageUserDao
				.findFanPageUserByUserId(getCurrentUserId());
		JsonObject fanPageObj = null;
		if (fanPageUser == null) {
			return json("You don't have any fanpage.");
		} else {
			if (fanPageUser.getFanPage() != null
					&& !fanPageUser.getFanPage().isEmpty()) {
				try {
					fanPageObj = new JsonObject(fanPageUser.getFanPage());
				} catch (Exception e) {
					return json("Resource error");
				}
			} else
				fanPageObj = new JsonObject();
		}

		if (forwardData != null && fanPageId != null) {
			PostStatus postStatus;
			if (PostStatus.Drafted.toString().equalsIgnoreCase(status))
				postStatus = PostStatus.Drafted;
			else
				postStatus = PostStatus.Published;

			forwardData = forwardData.replaceAll("\r", "\\\\r").replaceAll(
					"\n", "\\\\n");
			String lastPostTime = null;
			if (fanPageObj.has(fanPageId))
				lastPostTime = fanPageObj.getJsonObject(fanPageId).getString(
						"lastPostTime");
			JsonObject result = autoFanPagePostService.createPost(forwardData,
					lastPostTime, postStatus);

			if (result.has("errorMsg"))
				return json(result.getString("errorMsg"));
			if (result.has("lastPostTime")) {
				lastPostTime = result.getString("lastPostTime");
				JsonObject subObj = fanPageObj.getJsonObject(fanPageId);
				fanPageObj.put(fanPageId,
						subObj.put("lastPostTime", lastPostTime));
				fanPageUser.setFanPage(fanPageObj.toString());
			}
		}

		fanPageUser.setAutoPost(autoPost);
		fanPageUserDao.update(fanPageUser);
		return json(null);
	}

	
	// Admin control function
	public Resolution forceStart() {
		if (!getCurrentUserAdmin())
        	return new StreamingResolution("text/html", "Need to login");
		
		autoFanPagePostService.exec();
		return json(autoFanPagePostService.getStatus());
	}
	
	public Resolution startBgjob() {
		if (!getCurrentUserAdmin())
        	return new StreamingResolution("text/html", "Need to login");
        	
		autoFanPagePostService.start();
		return json(autoFanPagePostService.getStatus());
	}
	
	public Resolution stopBgjob() {
		if (!getCurrentUserAdmin())
        	return new StreamingResolution("text/html", "Need to login");
        	
		autoFanPagePostService.stop();
		return json(autoFanPagePostService.getStatus());
	}
	
	public Resolution statusBgjob() {
		if (!getCurrentUserAdmin())
        	return new StreamingResolution("text/html", "Need to login");
        	
		return json(autoFanPagePostService.getStatus());
	}
}