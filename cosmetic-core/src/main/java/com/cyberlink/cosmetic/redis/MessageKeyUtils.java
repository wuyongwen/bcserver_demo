package com.cyberlink.cosmetic.redis;

public abstract class MessageKeyUtils {

    public static String stickerPublisherName(Object id) {
        return "entity.sticker.publisher.name." + id;
    }

    public static String stickerPackName(Object id) {
        return "entity.sticker.pack.name." + id;
    }

    public static String stickerPublisherTitleOfUrl() {
        return "entity.sticker.publisher.title.of.url";
    }

    public static String xmppUnsupportedActivityMessage() {
        return "xmpp.unsupported.activity.message";
    }

}
