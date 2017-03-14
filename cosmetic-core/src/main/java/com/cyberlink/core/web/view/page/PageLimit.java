package com.cyberlink.core.web.view.page;


public class PageLimit {
    public static final PageLimit ONE = new PageLimit(1, 1);
    public static final PageLimit TWO = new PageLimit(1, 2);
    public static final PageLimit THREE = new PageLimit(1, 3);
    public static final PageLimit FOUR = new PageLimit(1, 4);
    public static final PageLimit EIGHT = new PageLimit(1, 8);
    public static final PageLimit TWELVE = new PageLimit(1, 12);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private int pageIndex = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String orderBy = "";
    private boolean asc = true;
    private String exportType = "csv";

    public PageLimit() {
    }

    public PageLimit(final int pageIndex, final int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public PageLimit(final int pageIndex, final int pageSize,
            final String orderBy, final boolean asc) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.orderBy = orderBy;
        this.asc = asc;
    }

    public String getExportType() {
        return exportType;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getStartIndex() {
        return (pageIndex - 1) * pageSize;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(final boolean asc) {
        this.asc = asc;
    }

    public void setExportType(final String exportType) {
        this.exportType = exportType;
    }

    public void setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
    }

    public void setPageIndex(final int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
}
