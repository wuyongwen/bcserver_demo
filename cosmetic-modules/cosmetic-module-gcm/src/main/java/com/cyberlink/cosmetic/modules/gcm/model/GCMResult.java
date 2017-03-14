package com.cyberlink.cosmetic.modules.gcm.model;

import java.util.ArrayList;
import java.util.List;

public class GCMResult {
	private Long multicast_id;
	private Long success;
	private Long failure;
	private Long canonical_ids;
	private List<GCMResultItem> results = new ArrayList<GCMResultItem>();

	public Long getMulticast_id() {
		return multicast_id;
	}

	public void setMulticast_id(Long multicast_id) {
		this.multicast_id = multicast_id;
	}

	public Long getSuccess() {
		return success;
	}

	public void setSuccess(Long success) {
		this.success = success;
	}

	public Long getFailure() {
		return failure;
	}

	public void setFailure(Long failure) {
		this.failure = failure;
	}

	public Long getCanonical_ids() {
		return canonical_ids;
	}

	public void setCanonical_ids(Long canonical_ids) {
		this.canonical_ids = canonical_ids;
	}

	public List<GCMResultItem> getResults() {
		return results;
	}

	public void setResults(List<GCMResultItem> results) {
		this.results = results;
	}

}
