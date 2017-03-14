package com.cyberlink.cosmetic.modules.user.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface AccountDao extends GenericDao<Account, Long>{
    Account findBySourceAndReference(AccountSourceType source, String ... reference);
    List<Account> findByUserId(Long userId);
    Map<String, Long> findUserIdBySourceAndReference(AccountSourceType source, List<String> reference);
    List<Account> findByEmail(String email);
    Integer updateStatus(String email, AccountMailStatus status);
    Integer updateStatusByEmail(Collection<String> email, AccountMailStatus status);
    Account findByIdWithNotDelete(Long id);
    // backend use only
    PageResult<User> findUserByEmail(String email, Long offset, Long limit);

}
