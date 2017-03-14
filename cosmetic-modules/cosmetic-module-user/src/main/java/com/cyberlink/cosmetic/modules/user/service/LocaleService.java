package com.cyberlink.cosmetic.modules.user.service;

import java.util.List;

public interface LocaleService {
    String getLocale(String input);

    String getLocale(List<String> inputs);
}
