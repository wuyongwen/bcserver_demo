package com.cyberlink.cosmetic.core.service;

public interface I18nService {
    String getMessage(String code);

    String getMessage(String code, Object[] values);

}
