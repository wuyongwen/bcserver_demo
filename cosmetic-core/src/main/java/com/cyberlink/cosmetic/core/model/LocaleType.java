package com.cyberlink.cosmetic.core.model;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public enum LocaleType {
    en_US(Locale.US), zh_TW(Locale.TAIWAN), zh_CN(Locale.SIMPLIFIED_CHINESE), ja_JP(
            Locale.JAPAN), fr_FR(Locale.FRANCE), ko_KR(Locale.KOREA), es_ES(
            new Locale("es", "ES")), de_DE(Locale.GERMANY), it_IT(Locale.ITALY);
    private Locale locale;

    private LocaleType(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public static LocaleType getByValue(String value) {
        for (final LocaleType lt : values()) {
            if (StringUtils.equalsIgnoreCase(lt.name(), value)) {
                return lt;
            }
        }

        return en_US;
    }

}
