package com.cyberlink.cosmetic.modules.gcm.model;

import java.util.ArrayList;
import java.util.List;

public class GCMPayload {
	private List<String> registration_ids = new ArrayList<String>();
	private Message data = new Message();
	private String collapse_key = "demo";
	private Long time_to_live = Long.valueOf(3);
	private Boolean delay_while_idle = true;

	public List<String> getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}

	public String getCollapse_key() {
		return collapse_key;
	}

	public void setCollapse_key(String collapse_key) {
		this.collapse_key = collapse_key;
	}

	public Long getTime_to_live() {
		return time_to_live;
	}

	public void setTime_to_live(Long time_to_live) {
		this.time_to_live = time_to_live;
	}

	public Boolean getDelay_while_idle() {
		return delay_while_idle;
	}

	public void setDelay_while_idle(Boolean delay_while_idle) {
		this.delay_while_idle = delay_while_idle;
	}

	public Message getData() {
		return data;
	}

	public void setData(Message data) {
		this.data = data;
	}
}
