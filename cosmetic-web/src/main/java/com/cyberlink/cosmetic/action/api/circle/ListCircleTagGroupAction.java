package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;

// list circle tag group by circle ID
@UrlBinding("/api/circle/list-circletagroup.action")
public class ListCircleTagGroupAction extends AbstractAction {
	@SpringBean("circle.circleTagGroupDao")
	private CircleTagGroupDao circleTagGroupDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleTagDao")
	private CircleTagDao circleTagDao;
	
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	private Long circleId;

	
	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	@Validate(required = true, on = "route")
	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

	@DefaultHandler
	public Resolution route() {
		if(!circleDao.exists(circleId))
	        return new ErrorResolution(ErrorDef.InvalidCircleId);
		
		final Map<String, Object> results = new HashMap<String, Object>();
		List<CircleTagGroup> circleTagGroupList = new ArrayList<CircleTagGroup>();
		PageResult<CircleTagGroup> pageResult = circleTagGroupDao.findByCircleId(circleId, offset, limit);
		circleTagGroupList = pageResult.getResults();
		for (int nIdx=0; nIdx < circleTagGroupList.size(); nIdx++) {
			CircleTagGroup circleTagGroup = circleTagGroupList.get(nIdx);
			List<CircleTag> circleTagList = circleTagDao.findByGroupId(circleTagGroup.getId());
			circleTagGroup.setCircleTag(circleTagList);
			circleTagGroupList.set(nIdx, circleTagGroup);
		}
		results.put("results", circleTagGroupList);
		results.put("totalSize", pageResult.getTotalSize());
		return json(results);
	}
}
