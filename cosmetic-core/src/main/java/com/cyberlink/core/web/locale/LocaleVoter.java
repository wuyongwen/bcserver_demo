package com.cyberlink.core.web.locale;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Alan She
 */
public interface LocaleVoter {
    Locale vote(HttpServletRequest request);
}
