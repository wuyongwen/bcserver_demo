package com.cyberlink.cosmetic.action.backend.circle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;

@UrlBinding("/circle/circle-manage.action")
public class CircleManageAction extends AbstractAction{
	@SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleTagGroupDao")
	private CircleTagGroupDao groupTagDao;

	@SpringBean("circle.circleTagDao")
	private CircleTagDao circleTagDao;
	
	private List<CircleType> circleTypeList ;
	private List<Circle> circleList ;
	private List<CircleTagGroup> circleTagGroupList;
	private List<CircleTag> circleTagList;
	
	public class SelectString {
		private String label;
		private Long id;
		SelectString(String label, Long id) {
			this.label = label;
			this.id = id;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
	}

	private List<SelectString> circleTypeDefaultList = new ArrayList<SelectString>();
	private List<SelectString> circleDefaultList = new ArrayList<SelectString>();
	private List<SelectString> circleTagGroupDefaultList = new ArrayList<SelectString>();
	private List<SelectString> circleTagDefaultList = new ArrayList<SelectString>();
	


	private String locale = "zh_TW";	
	private Long circleType;
	private Long circle;
	private Long circleTagGroup;
	private Long circleTag;
	
	private String name;
	private int offset = 0, limit = 20 ;
	private int pages ;
	
	@DefaultHandler
	public Resolution route() {
		if (locale != null) {
			PageResult<CircleType> pageResult = circleTypeDao.listTypesByLocale(locale, null, new BlockLimit(offset, limit));
			circleTypeList = pageResult.getResults();
			circleTypeDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
			if (circleType == null || circleType == -1) {
				circleType = Long.valueOf(-1);
				//circleTypeDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
				return forward();
			} 
			
			circleList = circleDao.findByTypeId(circleType);
			circleDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
			if (circle == null || circle == -1) {
				circle = Long.valueOf(-1);
				//circleDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
				return forward();
			}

			circleTagGroupList = groupTagDao.findByCircleId(circle);
			circleTagGroupDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
			if (circleTagGroup == null || circleTagGroup == -1) {
				circleTagGroup = Long.valueOf(-1);
				//circleTagGroupDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
				return forward();
			}
			
			circleTagList = circleTagDao.findByGroupId(circleTagGroup);
			circleTagDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
			if (circleTag == null || circleTag == -1) {
				circleTag = Long.valueOf(-1);
				//circleTagDefaultList.add(new SelectString("New Create", Long.valueOf(-1)));
				return forward();
			} 

		}
        
		return forward();
	}
	public Resolution create() {
		if (name == null || name.length() == 0) {
			return new RedirectResolution(CircleManageAction.class, "route")
			.addParameter("locale", locale)
			.addParameter("circleType", circleType)
			.addParameter("circle", circle)
			.addParameter("circleTagGroup", circleTagGroup)
			.addParameter("circleTag", circleTag);			
		}
		if (circleType == -1) {
			CircleType t = new CircleType();
			t.setLocale(locale);;
			t.setCircleTypeName(name);
			t = circleTypeDao.update(t);
			circleType = t.getId();
		} else if (circle == -1 && circleTypeDao.exists(circleType)) {
			Circle c = new Circle();
			c.setCircleName(name);
			c.setCricleTypeId(circleType);
			c = circleDao.update(c);
			circle = c.getId();
		} else if (circleTagGroup == -1 && circleDao.exists(circle)) {
			CircleTagGroup tg = new CircleTagGroup();
			tg.setCircleTagGroupName(name);
			tg.setCircleId(circle);
			tg = groupTagDao.update(tg);
			circleTagGroup = tg.getId();
		} else if (circleTag == -1 && groupTagDao.exists(circleTagGroup)) {
			CircleTag ct = new CircleTag();
			ct.setCircleTagName(name);
			ct.setCircleTagGroupId(circleTagGroup);
			ct = circleTagDao.update(ct);
			circleTag = ct.getId();
		}
		
		return new RedirectResolution(CircleManageAction.class, "route")
					.addParameter("locale", locale)
					.addParameter("circleType", circleType)
					.addParameter("circle", circle)
					.addParameter("circleTagGroup", circleTagGroup)
					.addParameter("circleTag", circleTag);

	}
	public List<CircleType> getCircleTypeList() {
		return circleTypeList;
	}

	public void setCircleTypeList(List<CircleType> circleTypeList) {
		this.circleTypeList = circleTypeList;
	}

	public List<Circle> getCircleList() {
		return circleList;
	}

	public void setCircleList(List<Circle> circleList) {
		this.circleList = circleList;
	}

	public List<CircleTagGroup> getCircleTagGroupList() {
		return circleTagGroupList;
	}

	public void setCircleTagGroupList(List<CircleTagGroup> circleTagGroupList) {
		this.circleTagGroupList = circleTagGroupList;
	}

	public List<CircleTag> getCircleTagList() {
		return circleTagList;
	}

	public void setCircleTagList(List<CircleTag> circleTagList) {
		this.circleTagList = circleTagList;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
	

	public Long getCircle() {
		return circle;
	}

	public void setCircle(Long circle) {
		this.circle = circle;
	}

	public Long getCircleTagGroup() {
		return circleTagGroup;
	}

	public void setCircleTagGroup(Long circleTagGroup) {
		this.circleTagGroup = circleTagGroup;
	}

	public Long getCircleTag() {
		return circleTag;
	}

	public void setCircleTag(Long circleTag) {
		this.circleTag = circleTag;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}	
	public Long getCircleType() {
		return circleType;
	}

	public void setCircleType(Long circleType) {
		this.circleType = circleType;
	}
	public List<SelectString> getCircleTypeDefaultList() {
		return circleTypeDefaultList;
	}

	public void setCircleTypeDefaultList(List<SelectString> circleTypeDefaultList) {
		this.circleTypeDefaultList = circleTypeDefaultList;
	}

	public List<SelectString> getCircleDefaultList() {
		return circleDefaultList;
	}

	public void setCircleDefaultList(List<SelectString> circleDefaultList) {
		this.circleDefaultList = circleDefaultList;
	}

	public List<SelectString> getCircleTagGroupDefaultList() {
		return circleTagGroupDefaultList;
	}

	public void setCircleTagGroupDefaultList(
			List<SelectString> circleTagGroupDefaultList) {
		this.circleTagGroupDefaultList = circleTagGroupDefaultList;
	}

	public List<SelectString> getCircleTagDefaultList() {
		return circleTagDefaultList;
	}

	public void setCircleTagDefaultList(List<SelectString> circleTagDefaultList) {
		this.circleTagDefaultList = circleTagDefaultList;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
