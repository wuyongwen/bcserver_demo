package com.cyberlink.cosmetic.web.view.displaytag;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageLimitFactory;

public class DisplayTagPageLimitFactory implements PageLimitFactory {
    private static final int PAGE_INDEX = 1;
    private static final int PAGE_SIZE = 20;
    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String DEFAULT_TABLE_ID = "null";
    private static final String DEFAULT_ORDER_BY = "id";

    public PageLimit factory(HttpServletRequest request) {
        return factory(request, DEFAULT_TABLE_ID);
    }

    public PageLimit factory(HttpServletRequest request, String tableId) {
        final PageLimit limit = new PageLimit();
        final ParamEncoder encoder = getParamEncoder(tableId);
        limit.setPageSize(getPageSize(request));
        limit.setPageIndex(getPageIndex(request, encoder));
        limit.setOrderBy(getOrderBy(request, encoder));
        limit.setAsc(getAsc(request, encoder));
        return limit;
    }

    private ParamEncoder getParamEncoder(String tableId) {
        return new ParamEncoder(tableId);
    }

    private boolean getAsc(HttpServletRequest request, ParamEncoder encoder) {
        final String order = request.getParameter(encoder
                .encodeParameterName(TableTagParameters.PARAMETER_ORDER));
        return "1".equals(order);
    }

    private String getOrderBy(HttpServletRequest request, ParamEncoder encoder) {
        final String sort = request.getParameter(encoder
                .encodeParameterName(TableTagParameters.PARAMETER_SORT));
        if (StringUtils.isNotBlank(sort)) {
            return sort;
        }
        return DEFAULT_ORDER_BY;
    }

    private int getPageIndex(HttpServletRequest request, ParamEncoder encoder) {
        final String value = request.getParameter(encoder
                .encodeParameterName(TableTagParameters.PARAMETER_PAGE));
        if (StringUtils.isBlank(value)) {
            return PAGE_INDEX;
        }
        return Integer.valueOf(value);
    }

    private int getPageSize(HttpServletRequest request) {
        final String value = request.getParameter(PARAM_PAGE_SIZE);
        if (StringUtils.isBlank(value)) {
            return PAGE_SIZE;
        }
        return Integer.valueOf(value);
    }

}
