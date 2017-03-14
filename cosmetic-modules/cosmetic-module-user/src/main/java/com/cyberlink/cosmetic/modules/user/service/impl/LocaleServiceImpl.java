package com.cyberlink.cosmetic.modules.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.user.service.LocaleService;

public class LocaleServiceImpl extends AbstractService implements
        LocaleService, InitializingBean {
    private static final String DEFAULT_LOCALE = "en_row";
    private Map<String, String> localeMap = new HashMap<String, String>();

    private LocaleDao localeDao;

    public void setLocaleDao(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }

    @Override
    public String getLocale(String input) {
        if (StringUtils.isBlank(input)) {
            return DEFAULT_LOCALE;
        }

        final String l = localeMap.get(StringUtils.lowerCase(input));
        if (StringUtils.isNotBlank(l)) {
            return l;
        }
        return DEFAULT_LOCALE;
    }

    public String getLocale(List<String> inputLocales) {
        return getLocale(getInputLocale(inputLocales));

    }

    private String getInputLocale(List<String> inputLocales) {
        if (inputLocales.isEmpty()) {
            return null;
        }
        return inputLocales.get(0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (final Locale l : localeDao.findAll()) {
            if (l.getIsDeleted()) {
                continue;
            }
            localeMap.put(l.getInputLocale().toLowerCase(), l.getPostLocale()
                    .toLowerCase());
        }
    }

}
