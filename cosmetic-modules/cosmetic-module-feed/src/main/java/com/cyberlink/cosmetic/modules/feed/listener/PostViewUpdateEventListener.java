package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.post.repository.PostViewRepository;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Creator;

public class PostViewUpdateEventListener extends
        AbstractEventListener<PostViewUpdateEvent> {
    
    private PostViewRepository postViewRepository;
    
    public void setPostViewRepository(PostViewRepository postViewRepository) {
        this.postViewRepository = postViewRepository;
    }
    
    @Override
    public void onEvent(PostViewUpdateEvent e) {
        try {
            switch(e.getUpdateType()) {
            case MainPost :
                postViewRepository.createOrUpdatePostView(e.getKeyId(), e.getPost());
                break;
            case PostAttr :
                postViewRepository.updatePostAttr(e.getKeyId(), e.getPostViewAttr());
                break;
            case Creator : {
                Creator c = e.getCreator();
                postViewRepository.createOrUpdatePostViewUser(e.getKeyId(), c.avatar, c.userType, c.cover, c.description, c.displayName);
                break;
            }
            case Circle :
                postViewRepository.createOrUpdatePostViewCircle(e.getKeyId(), e.getCircle().circleName, e.getCircle().getDisplay());
                break;
            case Like :
                postViewRepository.createOrUpdatePostLikes(e.getKeyId(), e.getUserId(), e.getLiked());
                break;
            default:
                break;
            }
        } catch (Exception e1) {
        }
    }

}
