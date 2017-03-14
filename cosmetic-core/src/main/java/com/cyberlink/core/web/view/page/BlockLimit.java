package com.cyberlink.core.web.view.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BlockLimit {
    public static final BlockLimit ONE = new BlockLimit(1, 1);
    public static final BlockLimit TWO = new BlockLimit(1, 2);
    public static final BlockLimit THREE = new BlockLimit(1, 3);
    public static final BlockLimit FOUR = new BlockLimit(1, 4);
    public static final BlockLimit EIGHT = new BlockLimit(1, 8);
    public static final BlockLimit TWELVE = new BlockLimit(1, 12);

    private static final int DEFAULT_BLOCK_SIZE = 10;

    private int offset = 0;
    private int size = DEFAULT_BLOCK_SIZE;
    private Map<String, Boolean> orderBy = new LinkedHashMap<String, Boolean>();
    private String exportType = "csv";

    public BlockLimit() {
    }

    public BlockLimit(final int offset, final int size) {
        this.offset = offset;
        this.size = size;
    }

    public String getExportType() {
        return exportType;
    }

    public Map<String, Boolean> getOrderBy() {
        return orderBy;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public boolean isAsc(final String orderBy) {
        return this.orderBy.get(orderBy);
    }

    public void setExportType(final String exportType) {
        this.exportType = exportType;
    }

    public void addOrderBy(final String orderBy, final boolean asc) {
        this.orderBy.put(orderBy, asc);
    }

    public void setOrderBy(LinkedHashMap<String, Boolean> orderBy) {
        this.orderBy = orderBy;
    }
    
    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public void setSize(final int size) {
        this.size = size;
    }
}
