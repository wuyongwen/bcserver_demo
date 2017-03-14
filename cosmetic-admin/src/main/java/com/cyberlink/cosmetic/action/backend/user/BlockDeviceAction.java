package com.cyberlink.cosmetic.action.backend.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.BlockDeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.event.UserBlockEvent;
import com.cyberlink.cosmetic.modules.user.model.BlockDevice;
import com.cyberlink.cosmetic.modules.user.model.Device;
import com.cyberlink.cosmetic.modules.user.service.UserService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/blockDevice.action")
public class BlockDeviceAction extends AbstractAction{
	@SpringBean("user.DeviceDao")
    private DeviceDao deviceDao;

	@SpringBean("user.BlockDeviceDao")
    protected BlockDeviceDao blockDao;
	
	@SpringBean("user.userService")
    protected UserService userService;
    
    static final String editUserInfo = "/user/blockDevice-list.jsp" ;
    
    private PageResult<Device> pageResult = new PageResult<Device>();

	// search 
	private Long searchId;
	private String uuid;
    

	@DefaultHandler
    public Resolution list() {
		if (searchId == null) {
			pageResult = new PageResult<Device>();
			pageResult.setResults(new ArrayList<Device>());
			pageResult.setTotalSize(Integer.valueOf(0));
			return forward();
		}
		PageLimit page = getPageLimit("row");
		PageResult<String> uuidPage = deviceDao.findDeviceUuidByUserId(searchId, Long.valueOf(page.getStartIndex()), Long.valueOf(page.getPageSize()));				
		pageResult.setTotalSize(uuidPage.getTotalSize());

		Set<String> uuidSet = new HashSet<String>(uuidPage.getResults());
		uuidSet = blockDao.checkIsBlocked(uuidSet);
		List<Device> list = new ArrayList<Device>();
		for (String u: uuidPage.getResults()) {
			Device d = new Device();
			d.setUuid(u);
			d.setUserId(searchId);
			d.setIsBlocked(uuidSet.contains(u));
			list.add(d);
		}
		pageResult.setResults(list);
		
        return forward();
    }

	public Resolution block() {
		if (uuid != null && uuid.length() > 0 && searchId != null) {
			List<BlockDevice> b = blockDao.findByUuid(uuid);
			if (b.size() > 0) {
				b.get(0).setIsDeleted(Boolean.FALSE);
				blockDao.update(b.get(0));
			} else {
				BlockDevice d = new BlockDevice();
				d.setShardId(searchId);
				d.setUuid(uuid);
				blockDao.create(d);
			}
			userService.deleteSessionByUser(searchId);
			publishDurableEvent(new UserBlockEvent(searchId));
		}
        return new RedirectResolution(BlockDeviceAction.class, "list").addParameter("searchId", searchId);
    }

	public Resolution unblock() {
		if (uuid != null && uuid.length() > 0 && searchId != null) {
			List<BlockDevice> list = blockDao.findByUuid(uuid);
			for (BlockDevice b: list) {
				b.setIsDeleted(Boolean.TRUE);
				blockDao.update(b);				
			}
		}
        return new RedirectResolution(BlockDeviceAction.class, "list").addParameter("searchId", searchId);
    }
	
	public Long getSearchId() {
		return searchId;
	}

	public void setSearchId(Long searchId) {
		this.searchId = searchId;
	}

	public PageResult<Device> getPageResult() {
		return pageResult;
	}

	public void setPageResult(PageResult<Device> pageResult) {
		this.pageResult = pageResult;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
