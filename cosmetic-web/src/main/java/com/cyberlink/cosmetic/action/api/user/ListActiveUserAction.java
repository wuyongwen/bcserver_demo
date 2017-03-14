package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.repository.UserInfoRepository;

@UrlBinding("/api/user/list-active-user.action")
public class ListActiveUserAction extends AbstractAction{
	@SpringBean("user.userInfoRepository")
    private UserInfoRepository userInfoRepository;
	
	@Validate(required = true, on = "route")
	private Long roomId;
	private Long offset = 0l;
	
	@DefaultHandler
    public Resolution route() {
		final Map<String, Object> results = userInfoRepository.getActiveInfo(roomId, offset);
		return json(results);
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}
	
}