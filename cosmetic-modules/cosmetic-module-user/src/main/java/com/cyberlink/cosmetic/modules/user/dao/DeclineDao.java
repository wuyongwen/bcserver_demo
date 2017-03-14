package com.cyberlink.cosmetic.modules.user.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DeclineDao extends GenericDao<Decline, Long>{
    Decline findByTypeAndDeclineid(String type, String declineid);
}
