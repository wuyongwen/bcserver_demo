package com.cyberlink.core.web.view.page;

import javax.servlet.http.HttpServletRequest;

public interface PageLimitFactory {
    PageLimit factory(HttpServletRequest request);

    PageLimit factory(HttpServletRequest request, String tableId);
}
