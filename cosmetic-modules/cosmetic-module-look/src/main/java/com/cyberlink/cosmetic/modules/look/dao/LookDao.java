package com.cyberlink.cosmetic.modules.look.dao;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.look.model.Look;

public interface LookDao extends GenericDao<Look, Long>{
	PageResult<Look> findByUserId(Long userId, BlockLimit blockLimit);
}
