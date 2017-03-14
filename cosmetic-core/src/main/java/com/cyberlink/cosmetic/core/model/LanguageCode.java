package com.cyberlink.cosmetic.core.model;

import java.util.Locale;

public enum LanguageCode {
    CHS(Locale.SIMPLIFIED_CHINESE), CHT(Locale.TRADITIONAL_CHINESE), DEU(
            Locale.GERMANY), ENU(Locale.US), ESP(new Locale("es", "ES")), FRA(
            Locale.FRANCE), ITA(Locale.ITALY), JPN(Locale.JAPAN), KOR(
            Locale.KOREA);

    private Locale locale;

    private LanguageCode(final Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isEnu() {
        return ENU == this;
    }

    public boolean isJPN() {
        return JPN == this;
    }

    public boolean isIta() {
        return ITA == this;
    }

    public boolean isDeu() {
        return DEU == this;
    }

    public boolean isFra() {
        return FRA == this;
    }

    public boolean isKor() {
        return KOR == this;
    }

    public boolean isCht() {
        return CHT == this;
    }

    public boolean isChs() {
        return CHS == this;
    }

    public boolean isEsp() {
        return ESP == this;
    }

}
