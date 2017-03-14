package com.cyberlink.core.web.utl;

import java.util.Map;

public interface URLContentReader {
    String get(String url);

    String post(String url, Map<String, String> params);
}
