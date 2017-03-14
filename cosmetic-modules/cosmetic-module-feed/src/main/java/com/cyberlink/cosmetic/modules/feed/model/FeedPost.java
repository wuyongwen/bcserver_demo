package com.cyberlink.cosmetic.modules.feed.model;

import java.io.Serializable;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class FeedPost implements Serializable {
    private static final long TS_OF_2014 = 1388534400000l;
    private static final long serialVersionUID = -9146504556350335682L;
    private final Long postId;
    private final PoolType poolType;
    private double score;
    private boolean ignore = Boolean.FALSE;

    public FeedPost(final PoolType poolType, final Long postId,
            final Boolean ignore) {
        this.poolType = poolType;
        this.postId = postId;
        this.ignore = ignore;
    }

    public FeedPost(TypedTuple<String> tt) {
        this.postId = Long.valueOf(tt.getValue());
        this.score = tt.getScore();
        this.poolType = extractPoolType(tt.getScore());
        this.ignore = extractIgnore(tt.getScore());
    }

    private boolean extractIgnore(Double score) {
        final long l = score.longValue();
        return (l & 32) != 0;
    }

    private PoolType extractPoolType(Double score) {
        final long l = score.longValue();
        return PoolType.getByIndex((int) (l % 32));
    }

    public Long getPostId() {
        return postId;
    }

    public PoolType getPoolType() {
        return poolType;
    }

    public double getScore() {
        return score;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void calculate() {
        final long ts = System.currentTimeMillis() - TS_OF_2014;
        long score = (ts << 12) + poolType.getIndex();
        if (ignore) {
            score += 32;
        }
        this.score = score;
    }

}
