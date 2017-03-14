package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.google.common.collect.ImmutableList;

// list circle type
@UrlBinding("/api/circle/list-circletype.action")
public class ListCircleTypeAction extends AbstractAction {
    
	@SpringBean("circle.circleTypeDao")	
	private CircleTypeDao circleTypeDao;
	
	private int offset = 0;
    private int limit = 20;
    protected String locale ;
    protected Long curUserId;
    static private Long OTHER_TYPE_GROUP_ID = 8L;
    
    static private ImmutableList<Long> debugUIds = ImmutableList.of(
            6336005L,   //Victor
            2226001L,   //Frank
            3561936171L,//bc_debug@perfectcorp.com
            2262001L,   //Johnny
            186001L,    //Jau
            2562002L,   //Alice
            162001L     //Christine
            );
    
    protected PageResult<CircleType> getCircleType() {
        if(curUserId != null && debugUIds.contains(curUserId)) {
            PageResult<CircleType> results = getCircleType(null);
            return removeOther(results);
        }
        return getCircleType(true);
    }
    
    private PageResult<CircleType> getCircleType(Boolean isVisible) {
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        return circleTypeDao.listTypesByLocale(locale, isVisible, blockLimit);
    }
    
    private PageResult<CircleType> removeOther(PageResult<CircleType> in) {
        List<CircleType> list = new ArrayList<CircleType>();
        int removedCount = 0;
        for(CircleType ct : in.getResults()) {
            if(OTHER_TYPE_GROUP_ID.equals(ct.getCircleTypeGroupId())) {
                removedCount++;
                continue;
            }
            list.add(ct);
        }
        PageResult<CircleType> out = new PageResult<CircleType>();
        out.setResults(list);
        out.setTotalSize(in.getTotalSize() - removedCount);
        return out;
    }
    
	@DefaultHandler
	public Resolution route() {
	    PageResult<CircleType> pageResult = getCircleType();
	    List<CircleType> toRemove = new ArrayList<CircleType>();
        for(CircleType ct : pageResult.getResults()) {
            if(ct.getDefaultType().equals("HOW-TO"))
                toRemove.add(ct);
        }
        if(toRemove.size() > 0) {
            pageResult.getResults().removeAll(toRemove);
            Integer totalSize = pageResult.getTotalSize() - toRemove.size();
            pageResult.setTotalSize(totalSize);
        }
		return json(pageResult);
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
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
}
