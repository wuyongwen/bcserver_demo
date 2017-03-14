package com.cyberlink.cosmetic.utils;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.core.service.I18nService;

public final class I18nUtils {
    private static I18nService i18nService = BeanLocator
            .getBean("web.i18nService");

    private I18nUtils() {
    }

    public static String getText(String messageKey, Object[] values) {
        return i18nService.getMessage(messageKey, values);
    }

    public static String getText(String messageKey) {
        return getText(messageKey, null);
    }
}
