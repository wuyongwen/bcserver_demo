package com.cyberlink.core.web.view.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1133260618632864498L;
    private List<T> results = Collections.emptyList();
    private Integer totalSize = 0;

    public PageResult() {
        super();
        this.results = new LinkedList<T>();
        this.totalSize = 0;
    }

    public PageResult(List<T> results, Integer totalSize) {
        super();
        this.results = results;
        this.totalSize = totalSize;
    }

    public void add(T t) {
        results.add(t);
    }

    @JsonView(Views.Basic.class)
    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    @JsonView(Views.Basic.class)
    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

}
