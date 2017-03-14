package com.cyberlink.cosmetic.modules.search.helper;

import java.util.Comparator;

import com.cyberlink.cosmetic.modules.search.model.Suggest;

public class SuggestComparator implements Comparator<Suggest>{

	@Override
	public int compare(Suggest s1, Suggest s2) {	
		if(s1.getWeight() > s2.getWeight())
			return -1;
		
		return 1;
	}

}