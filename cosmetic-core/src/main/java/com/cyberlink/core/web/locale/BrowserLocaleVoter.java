package com.cyberlink.core.web.locale;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Alan She
 */
public class BrowserLocaleVoter implements LocaleVoter {

    public Locale vote(HttpServletRequest request) {
        return request.getLocale();
    }
}
