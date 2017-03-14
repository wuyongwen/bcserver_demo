package com.cyberlink.cosmetic.core.model;

import java.util.Date;

import com.cyberlink.core.web.jackson.Views.Public;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractESEntity {
    
    private String id;
    
    private Date date;
    
    @JsonView(Public.class)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone="GMT")
    @JsonView(Public.class)
    public Date getDate() {
        if(date == null)
            return new Date();
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
