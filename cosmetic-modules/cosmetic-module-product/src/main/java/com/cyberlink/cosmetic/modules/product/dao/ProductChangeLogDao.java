package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface ProductChangeLogDao extends GenericDao<ProductChangeLog, Long>{
	PageResult<ProductChangeLog> listProdChangeLog( Long userId,
			ProductChangeLogType itemType, Long itemId, Long offset, Long limit );
	List<User> findUserList();
}
