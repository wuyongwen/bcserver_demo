package com.cyberlink.cosmetic.action.backend.misc;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/misc/FacebookAdManage.action")
public class FacebookAdManageAction extends AbstractAction {

	@SpringBean("web.objectMapper")
	private ObjectMapper objectMapper;

	@SpringBean("user.AttributeDao")
	private AttributeDao attributeDao;
	
	private Long id;
	private String fbAdName; // fbAd, fbAd_01
	private Integer offset;
	private Integer limit;
	List<Attribute> fbAdList;

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		fbAdList = attributeDao.findByRefType(AttributeType.FbAdControl);
		
		return new ForwardResolution("/misc/FacebookAdManage.jsp");
	}
	
	public Resolution modify() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }

		Attribute attr = attributeDao.findById(id);
		String attrName = attr.getAttrName();
		String attrValue = attr.getAttrValue();
		Map<String, Object> fbAd = new LinkedHashMap<String, Object>();
		try {
			fbAd = objectMapper.readValue(attrValue, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		fbAd.put("id", id.toString());
		fbAd.put("fbAd", attrName);

		return json(fbAd);
	}

	public Resolution update() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }

		Map<String, Integer> fbAdValue = new LinkedHashMap<String, Integer>();
		fbAdValue.put("adOffset", offset);
		fbAdValue.put("adLimit", limit);
		String jsonStr = "";
		try {
			jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fbAdValue);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		
		Attribute attr = attributeDao.findById(id);
		attr.setAttrValue(jsonStr);
		attributeDao.update(attr);

		return json("done");
	}
	
	public Resolution create() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		Map<String, Integer> fbAdValue = new LinkedHashMap<String, Integer>();
		fbAdValue.put("adOffset", offset);
		fbAdValue.put("adLimit", limit);
		String jsonStr = "";
		try {
			jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fbAdValue);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		
		Attribute attr = new Attribute();
		attr.setRefType(AttributeType.FbAdControl);
		attr.setAttrName(fbAdName);
		attr.setAttrValue(jsonStr);
		attributeDao.create(attr);

		return json("done");
	}

	public Long getId() {
		return id;
	}

	@Validate(required = true, on = { "modify", "update" })
	public void setId(Long id) {
		this.id = id;
	}

	public String getFbAdName() {
		return fbAdName;
	}

	@Validate(required = true, on = "create")
	public void setFbAdName(String fbAdName) {
		this.fbAdName = fbAdName;
	}

	public Integer getOffset() {
		return offset;
	}

	@Validate(required = true, on = { "create", "update" })
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	@Validate(required = true, on = { "create", "update" })
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List<Attribute> getFbAdList() {
		return fbAdList;
	}
	
}
