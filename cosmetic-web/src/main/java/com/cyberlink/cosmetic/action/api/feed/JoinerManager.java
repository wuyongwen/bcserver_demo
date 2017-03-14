package com.cyberlink.cosmetic.action.api.feed;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.post.AbstractPostAction;
import com.cyberlink.cosmetic.modules.user.repository.FeedJoinerRepository;

@UrlBinding("/api/feed/joiner-manager.action")
public class JoinerManager extends AbstractPostAction {
    
    @SpringBean("user.feedJoinerRepository")
    private FeedJoinerRepository feedJoinerRepository;
    
    private Long userId;
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Resolution add() {
        return json(feedJoinerRepository.addUser(userId));
    }
    
    public Resolution isJoiner() {
        return json(feedJoinerRepository.isFeedJoiner(userId));
    }
    
    public Resolution removeUser() {
        return json(feedJoinerRepository.removeUser(userId));
    }
    
    public Resolution removeAll() {
        return json(feedJoinerRepository.removeAll());
    }
    
    @DefaultHandler
    public Resolution route() {
        return json(feedJoinerRepository.getUserList());
    }
}
