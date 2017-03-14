package com.cyberlink.cosmetic.modules.common.dao;

import java.util.List;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.common.model.Locale;

public interface LocaleDao extends GenericDao<Locale, Long> {
    
    public enum LocaleType {
        INPUT_LOCALE("inputLocale", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getInputLocale();
            }
        }),
        USER_LOCALE("userLocales", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getUserLocales();
            }
        }),
        POST_LOCALE("postLocale", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getPostLocale();
            }
        }),
        PRODUCT_LOCALE("productLocale", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getProductLocale();
            }
        }),
        EVENT_LOCALE("eventLocale", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getEventLocale();
            }
        }),
        DISCOVER_TAB("discoverTabString", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getDiscoverTabString();
            }
        }),
        TRENDING_TAB("trendingTabString", new ValueHandler() {
            public String getValue(Locale locale) {
                return locale.getTrendingTabString();
            }
        });
        
        public interface ValueHandler {
            String getValue(Locale locale);
        }
        
        private ValueHandler valueHandler;
        private String variableName;
        
        private LocaleType(String variableName, ValueHandler valueHandler) {
            this.variableName = variableName;
            this.valueHandler = valueHandler;
        }

        public String getValue(Locale locale) {
            return valueHandler.getValue(locale);
        }
        
        public String getVariableName() {
            return variableName;
        }
        
        public static String getDefaultInputLocale() {
            return "en_US";
        }
        
        public static String getDefaultSourceLocale() {
            return "en_ROW";
        }
    }
    
    Locale getAvailableInputLocale(String inputLocale);
    Set<String> getLocaleByType(String inputLocale, LocaleType localeType);
    List<String> getAvailableInputLocaleByType(String value, LocaleType localeType);
    Set<String> getAvailableLocaleByType(LocaleType localeType);
    Locale getDefaultLocale();
	Locale findByLocale(String inputLocale);
    
}
