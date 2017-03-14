package com.cyberlink.cosmetic.modules.user.repository.elasticsearch;

import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.model.UserHeat;
import com.cyberlink.cosmetic.modules.user.repository.UserHeatRepository;
import com.cyberlink.cosmetic.core.repository.EsRepository.EsResult;
import com.cyberlink.cosmetic.core.repository.HttpClient.EsRepositoryHttpClient;

public class UserHeatRepositoryES extends EsRepositoryHttpClient<UserHeat>
    implements UserHeatRepository {
    
    @Override
    public EsResult<Boolean> updatePostCount(String id) {
        return updateField(id, "ctx._source.posts += 1");
    }

    @Override
    public EsResult<Boolean> updateLikeCount(String id, Integer updateBy) {
        return updateField(id, "ctx._source.likes += " + updateBy.toString());
    }

    @Override
    public EsResult<Boolean> updateCircleInCount(String id) {
        return updateField(id, "ctx._source.cirIns += 1");
    }
    
    @Override
    public EsResult<Boolean> updateFollowerCount(String id, Integer updateBy) {
        return updateField(id, "ctx._source.followers += 1");
    }
    
    @Override
    public EsResult<Boolean> updateBadge(String id, BadgeType badgeType) {
        return updateField(id, "ctx._source.badge = \"" + badgeType.toString() + "\"");
    }
    
    @Override
    protected String index() {
        return "cosmetic";
    }
    
    @Override
    protected String type() {
        return "user-heat";
    }

    @Override
    protected Class<?> getEntityClass() {
        return UserHeat.class;
    }

}