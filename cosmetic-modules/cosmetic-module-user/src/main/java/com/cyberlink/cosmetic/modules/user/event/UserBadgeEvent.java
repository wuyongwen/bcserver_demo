package com.cyberlink.cosmetic.modules.user.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cyberlink.core.event.DurableEvent;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBadgeEvent extends DurableEvent {

    private static final long serialVersionUID = -8675051473844569956L;

    static public UserBadgeEvent CreateAddPostEvent(Long postId, Long userId, String postLocale, Date created) {
        return new UserBadgeEvent(CommandType.cp, postId, userId, postLocale, created, null, null);
    }
    
    static public UserBadgeEvent CreateLikeEvent(Long postId, Long postCreatorId, Integer diff) {
        return new UserBadgeEvent(CommandType.lk, postId, postCreatorId, null, null, diff, null);
    }
    
    static public UserBadgeEvent CreateCircleInEvent(Long rootPostId, Long rootPostCreatorId) {
        return new UserBadgeEvent(CommandType.ci, rootPostId, rootPostCreatorId, null, null, null, null);
    }
    
    static public UserBadgeEvent createFollowEvent(Long userId, Integer diff) {
        List<Long> uis = new ArrayList<Long>();
        uis.add(userId);
        return new UserBadgeEvent(CommandType.fl, null, null, null, null, diff, uis);
    }
    
    static public UserBadgeEvent createFollowsEvent(List<Long> userIds, Integer diff) {
        return new UserBadgeEvent(CommandType.fl, null, null, null, null, diff, userIds);
    }
    
    public enum CommandType {
        cp, // create post
        lk, // like or unlike
        ci, // circleIn post
        fl, // follow or unfollow
        nu; // Unknown
    }

    private CommandType cmd;
    private Long pi;
    private Long ui;
    private String lo;
    private Date cd;
    private Integer dv;
    private List<Long> uis;

    public UserBadgeEvent(CommandType cmd, Long postId, Long userId, String locale, Date createdDate, Integer diffValue, List<Long> userIds) {
        super(new Object());
        this.cmd = cmd;
        this.pi = postId;
        this.ui = userId;
        this.lo = locale;
        this.cd = createdDate;
        this.dv = diffValue;
        this.uis = userIds;
    }

    public CommandType getCmd() {
        return cmd;
    }

    public void setCmd(CommandType cmd) {
        this.cmd = cmd;
    }

    public Long getPi() {
        return pi;
    }

    public void setPi(Long pi) {
        this.pi = pi;
    }

    public Long getUi() {
        return ui;
    }

    public void setUi(Long ui) {
        this.ui = ui;
    }

    public String getLo() {
        return lo;
    }

    public void setLo(String lo) {
        this.lo = lo;
    }

    public Date getCd() {
        return cd;
    }

    public void setCd(Date cd) {
        this.cd = cd;
    }

    public Integer getDv() {
        return dv;
    }

    public void setDv(Integer dv) {
        this.dv = dv;
    }
    
    public List<Long> getUis() {
        return uis;
    }

    public void setUis(List<Long> uis) {
        this.uis = uis;
    }

    public UserBadgeEvent() {
        super(new Object());
        this.cmd = CommandType.nu;
    }

    @Override
    public Boolean isGlobal() {
        return false;
    }
}
