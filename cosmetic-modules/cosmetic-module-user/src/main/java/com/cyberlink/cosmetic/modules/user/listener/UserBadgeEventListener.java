package com.cyberlink.cosmetic.modules.user.listener;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.core.repository.EsRepository.EsResult;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserBadgeEvent;
import com.cyberlink.cosmetic.modules.user.event.UserBadgeEvent.CommandType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.model.UserHeat;
import com.cyberlink.cosmetic.modules.user.repository.UserHeatRepository;
import com.cyberlink.cosmetic.modules.user.service.UserService;

public class UserBadgeEventListener extends
        AbstractEventListener<UserBadgeEvent> {

    private UserHeatRepository userHeatRepository;
    private UserDao userDao;
    private LocaleDao localeDao;
    private TransactionTemplate transactionTemplate;
    private UserService userService;
    private Boolean isBadProgramReady = false;
    
    public void setUserHeatRepository(UserHeatRepository userHeatRepository) {
        this.userHeatRepository = userHeatRepository;
    }
    
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setLocaleDao(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    private void handleFollowCase(Integer diff, List<Long> useIds) {
        for(Long userId : useIds) {
            EsResult<UserHeat> result = userHeatRepository.findById(userId.toString());
            if(result.error != null)
                continue;
            
            UserHeat uh = result.result;
            if(uh == null) {
                uh = createNewHeat(userId);
                if(uh == null)
                    continue;
            }
            userHeatRepository.updateFollowerCount(userId.toString(), diff);
            BadgeType oriBadge = uh.getValidBadge();
            uh.setFollowers(uh.getValidFollowers() + diff);
            if(diff > 0)
                upgradeBadge(oriBadge, uh);
        }
    }
    
    private void upgradeBadge(BadgeType oriBadge, final UserHeat uh) {
        final BadgeType finalBadge = uh.calculateNewBadge();
        if(finalBadge.equals(oriBadge))
            return;
        
        Boolean needUpdateBadge = false;
        switch(finalBadge) {
        case Platinum: {
            if(BadgeType.Gold.equals(oriBadge) ||
               BadgeType.Silver.equals(oriBadge) ||
               BadgeType.Normal.equals(oriBadge))
                needUpdateBadge = true;
        }
        case Gold: {
            if(BadgeType.Silver.equals(oriBadge) ||
               BadgeType.Normal.equals(oriBadge))
                 needUpdateBadge = true;
        }
        case Silver: {
            if(BadgeType.Normal.equals(oriBadge))
                needUpdateBadge = true;
        }
        case Normal:
        case Diamond:
        case StarOfWeek:
            default:
            break;
        }
        
        if(!needUpdateBadge)
            return;
        
        transactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                Long userId = Long.valueOf(uh.getId());
                User usr = userDao.findById(userId);
                if(usr == null)
                    return false;
                Locale locale = localeDao.getAvailableInputLocale(usr.getRegion());
                if(locale == null)
                    return false;
                
                Set<String> locs = locale.getUserLocaleList();
                if(locs == null || locs.size() <= 0)
                    return false;
                
                userService.updateUserBadge(locs.iterator().next(), userId, finalBadge);
                return true;
            }
            
        });
        
        userHeatRepository.updateBadge(uh.getId(), finalBadge);
        
    }
    
    private UserHeat createNewHeat(final Long userId) {
        if(!userDao.exists(userId))
            return null;
        
        try {
            UserHeat uh = transactionTemplate.execute(new TransactionCallback<UserHeat>() {

                @Override
                public UserHeat doInTransaction(TransactionStatus status) {
                    User user = userDao.findById(userId);
                    if(user == null)
                        return null;
                    
                    UserHeat uh = new UserHeat();
                    uh.setId(userId.toString());
                    uh.setLoc(user.getRegion());
                    uh.setCirIns(0);
                    uh.setFollowers(0);
                    uh.setLikes(0);
                    uh.setPosts(0);
                    uh.setDate(user.getCreatedTime());
                    return uh;
                }
                
            });
            if(uh == null)
                return null;
            
            EsResult<Boolean> result = userHeatRepository.create(uh);
            if(result.error != null || !result.result)
                return null;
            return uh;
        }catch(Exception e) {
            return null;
        }
    }
    
    @Override
    public void onEvent(UserBadgeEvent event) {
        if(!isBadProgramReady)
            return;
        
        if(event == null)
            return;
        
        CommandType cmd = event.getCmd();
        if(cmd == null)
            return;
        
        Long userId = event.getUi();
        if(userId == null) {
            if(event.getUi() != null && event.getDv() != null)
                handleFollowCase(event.getDv(), event.getUis());
            return;
        }

        EsResult<UserHeat> result = userHeatRepository.findById(userId.toString());
        if(result.error != null)
            return;
        
        UserHeat uh = result.result;
        if(uh == null) {
            uh = createNewHeat(userId);
            if(uh == null)
                return;
        }
        
        BadgeType oriBadge = uh.getValidBadge();
        switch(cmd) {
            case cp: {
                userHeatRepository.updatePostCount(userId.toString());
                uh.setPosts(uh.getValidPosts() + 1);
                upgradeBadge(oriBadge, uh);
                break;
            }
            case lk: {
                Integer diff = event.getDv();
                if(diff == null)
                    break;
                userHeatRepository.updateLikeCount(userId.toString(), diff);
                uh.setLikes(uh.getValidLikes() + diff);
                if(diff > 0)
                    upgradeBadge(oriBadge, uh);
                break;
            }
            case ci: {
                userHeatRepository.updateCircleInCount(userId.toString());
                uh.setCirIns(uh.getValidCirIns() + 1);
                upgradeBadge(oriBadge, uh);
                break;
            }
            case nu:
            default:
                break;
        }
    }
}
