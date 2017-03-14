package com.cyberlink.cosmetic.modules.search.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.model.UserKeyword;

public interface UserKeywordDao extends GenericDao<UserKeyword, Long>{
	public UserKeyword findByUserIdAndType(long userId, TypeKeyword type);
	public void removeKeyword(long userId, TypeKeyword type);
}
