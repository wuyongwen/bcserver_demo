package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.post.event.PostViewProcessEvent;
import com.cyberlink.cosmetic.modules.post.repository.PostViewRepository;

public class PostViewProcessEventListener extends
        AbstractEventListener<PostViewProcessEvent> {

    private PostViewRepository postViewRepository;
    
    public void setPostViewRepository(PostViewRepository postViewRepository) {
        this.postViewRepository = postViewRepository;
    }

    @Override
    public void onEvent(final PostViewProcessEvent event) {
        if(event.getToDeletePostIds() != null) {
            postViewRepository.batchDeleteByPostIds(event.getToDeletePostIds());
            return;
        }
        else if (event.getPostView() != null && event.getToCreatePostId() != null) {
            postViewRepository.createOrUpdatePostView(event.getToCreatePostId(), event.getPostView());
            return;
        }
        else if(event.getToLikePostId() != null && event.getIsLiked() != null) {
            postViewRepository.createOrUpdatePostLikes(event.getToLikePostId(), event.getUserId(), event.getIsLiked());
            return;
        }
    }

}
