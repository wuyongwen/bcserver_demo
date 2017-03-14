package com.cyberlink.cosmetic.modules.post.listener;

import java.util.Date;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.post.model.PostHeat;
import com.cyberlink.cosmetic.modules.post.repository.PostHeatRepository;
import com.cyberlink.cosmetic.modules.post.repository.PsTrendHeatRepository;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.event.UserBadgeEvent;
import com.cyberlink.cosmetic.modules.user.event.UserBadgeEvent.CommandType;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;

public class UserBadgeEventListener extends
        AbstractEventListener<UserBadgeEvent> {

    private PostHeatRepository postHeatRepository;
    private PsTrendHeatRepository psTrendHeatRepository;
    private UserAttrDao userAttrDao;
    
    public void setPostHeatRepository(PostHeatRepository postHeatRepository) {
        this.postHeatRepository = postHeatRepository;
    }
    
    public void setPsTrendHeatRepository(PsTrendHeatRepository psTrendHeatRepository) {
        this.psTrendHeatRepository = psTrendHeatRepository;
    }
    
    public void setUserAttrDao(UserAttrDao userAttrDao) {
        this.userAttrDao = userAttrDao;
    }
    
    @Override
    public void onEvent(UserBadgeEvent event) {
        if(event == null)
            return;
        
        CommandType cmd = event.getCmd();
        if(cmd == null)
            return;
        
        switch(cmd) {
            case cp: {
                Long creatorId = event.getUi();
                Long postId = event.getPi();
                String locale = event.getLo();
                Date created = event.getCd();
                if(creatorId == null || creatorId == null || locale == null || created == null)
                    break;
                UserAttr userAttr = userAttrDao.findByUserId(creatorId);
                if(userAttr == null || userAttr.getHowToCount() == null || userAttr.getHowToCount() < PostHeat.minPostCount)
                    break;
                
                PostHeat ph = new PostHeat();
                ph.setId(postId.toString());
                ph.setLoc(locale);
                ph.setCirIns(0);
                ph.setLikes(0);
                ph.setUid(creatorId.toString());
                ph.setDate(created);
                postHeatRepository.create(ph);
                break;
            }
            case lk: {
                Long postId = event.getPi();
                Integer diff = event.getDv();
                if(postId == null || diff == null)
                    break;
                postHeatRepository.updateLikeCount(postId.toString(), diff);
                psTrendHeatRepository.updateLikeCount(postId.toString(), diff);
                break;
            }
            case ci: {
                Long postId = event.getPi();
                if(postId == null)
                    break;
                postHeatRepository.updateCircleInCount(postId.toString());
                psTrendHeatRepository.updateCircleInCount(postId.toString());
                break;
            }
            case fl:
            case nu:
            default:
                break;
            }
        }

}
