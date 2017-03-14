package com.cyberlink.cosmetic.modules.search.model;

public class Suggest {
	private String term;
	private Long weight;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}
}
