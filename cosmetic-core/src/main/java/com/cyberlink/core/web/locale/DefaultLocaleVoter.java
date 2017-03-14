package com.cyberlink.core.web.locale;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Alan She
 */
public class DefaultLocaleVoter implements LocaleVoter {

    private Locale defaultLocale;

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Locale vote(HttpServletRequest request) {
        return defaultLocale;
    }
}
