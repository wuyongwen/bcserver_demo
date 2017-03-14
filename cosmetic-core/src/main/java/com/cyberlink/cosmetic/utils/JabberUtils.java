package com.cyberlink.cosmetic.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JabberUtils {
    private static final String DOMAIN = "u.cyberlink.com";
    private static final String SERVICE = "conference.u.cyberlink.com";
    private static final Logger logger = LoggerFactory
            .getLogger(JabberUtils.class);

    public static final String getRobotJid() {
        return "robot@" + DOMAIN;
    }

    public static final String getBareJid(Long userId) {
        return userId + "@" + DOMAIN;
    }

    public static final String getRoomJid(Long groupId) {
        return groupId + "@" + SERVICE;
    }

    public static final String getService() {
        return SERVICE;
    }

    public static final String getOccupantJid(Long groupId, Long userId) {
        return getRoomJid(groupId) + "/" + userId;
    }

    public static final Long getUserIdFromBareJid(String bareJid) {
        final String[] buff = StringUtils.split(bareJid, "@" + DOMAIN);
        if (buff != null && buff.length != 0) {
            return Long.valueOf(buff[0]);
        }
        return null;
    }

    public static final Long getUserIdFromOccupantJid(String occupantJid) {
        final String[] buff = StringUtils.split(occupantJid, "@" + SERVICE);
        if (buff != null && buff.length == 2) {
            return Long.valueOf(buff[1]);
        }
        return null;
    }

    public static final boolean isOccupantJid(String jid) {
        return StringUtils.contains("@" + SERVICE, jid);
    }

    public static final boolean isRoomJid(String roomJid) {
        return StringUtils.contains(roomJid, "@" + SERVICE);
    }

    public static final Long getGroupIdFromRoomJid(String roomJid) {
        try {
            final String[] buff = StringUtils.split(roomJid, "@" + SERVICE);
            return Long.valueOf(buff[0]);
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    public static final String getDomain() {
        return DOMAIN;
    }
}
