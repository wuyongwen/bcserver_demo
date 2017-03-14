package com.cyberlink.cosmetic.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Locale {
	public static final Set<String> EN_LANGUAGE_SET = 
			new HashSet<String>(Arrays.asList(new String[] { "en_US", "en_GB"}));
	
    static final String [] availableLocale = new String [] {
        "en_US",
        "zh_TW",
        "zh_CN",
        "ja_JP",
        "fr_FR",
        "de_DE",
        "en_GB"
    };
    
    static final String [] availableRegion = new String [] {
        "en_US",
        "en_CA",
        "en_GB",
        "ja_JP",
        "de_DE",
        "fr_FR",
        "zh_TW",
        "zh_CN"
    };
    
    static final Map<String, String> localMap = new HashMap<String, String>();
    static
    {
        localMap.put("en", "en_US");
        localMap.put("zh", "zh_TW");
        localMap.put("ja", "ja_JP");
        localMap.put("fr", "fr_FR");
        localMap.put("de", "de_DE");
    }
    
    public static String getAvailableLocale(String inputLocale) {
        if(Arrays.asList(availableLocale).contains(inputLocale))
            return inputLocale;
        
        String language = inputLocale.split("_")[0];
        if(localMap.containsKey(language))
            return localMap.get(language);
        
        return "en_US";
    }

    public static Boolean isAvailableRegion(String inputLocale) {
    	if(Arrays.asList(availableRegion).contains(inputLocale))
            return Boolean.TRUE;
        else 
        	return Boolean.FALSE;
    }
    
    public static List<String> mapLocaleByLanguage(String inputLocale) {
    	if(Arrays.asList(availableLocale).contains(inputLocale)) {
        	if (EN_LANGUAGE_SET.contains(inputLocale))
        		return Arrays.asList(EN_LANGUAGE_SET.toArray(new String[EN_LANGUAGE_SET.size()]));
        	else
        		return Arrays.asList(inputLocale);
        } else {
        	List<String> list = new ArrayList<String>();
        	list.addAll(EN_LANGUAGE_SET);
        	if (inputLocale != null)
        		list.add(inputLocale);
        	return list;
        }
    }
    public static List<String> mapLocaleByLanguage(String inputLocale, Set<String> avalableSet) {
    	if(avalableSet.contains(inputLocale)) {
        	if (EN_LANGUAGE_SET.contains(inputLocale))
        		return Arrays.asList(EN_LANGUAGE_SET.toArray(new String[EN_LANGUAGE_SET.size()]));
        	else
        		return Arrays.asList(inputLocale);
        } else {
        	List<String> list = new ArrayList<String>();
        	list.add("en_ROW");
        	return list;
        }
    }    
    public static String getPostAvailableLocale(String requestLocale) {
        if( requestLocale == null ){
            return "en_US" ;
        }
        String dbLocale ;
        switch( requestLocale ){
            case "en_US" :
            case "en-US" :
            case "US" :
                dbLocale = "en_US";
                break;
            case "en_CA" :
            case "en-CA" :
            case "CA" :
            case "fr_CA" :
            case "fr-CA" :
                dbLocale = "en_CA";
                break;
            case "en_GB" :
            case "en-GB" :
            case "GB" :
                dbLocale = "en_GB";
                break;
            case "ja_JP" :
            case "ja-JP" :
            case "JP" :
                dbLocale = "ja_JP";
                break;
            case "de_DE" :
            case "de-DE" :
            case "DE" :
                dbLocale = "de_DE";
                break;
            case "fr_FR" :
            case "fr-FR" :
            case "FR" :
                dbLocale = "fr_FR";
                break;
            case "zh_TW" :
            case "zh-TW" :
            case "TW" :
                dbLocale = "zh_TW";
                break;
            case "zh_CN" :
            case "zh-CN" :
            case "CN" :
            case "zh_HK" :
            case "zh-HK" :
            case "HK" :
                dbLocale = "zh_CN";
                break;
            default:
                dbLocale = "en_US" ;
                break;
        }
        return dbLocale;
    }
}
