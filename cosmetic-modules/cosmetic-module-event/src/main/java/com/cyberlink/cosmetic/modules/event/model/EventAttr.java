package com.cyberlink.cosmetic.modules.event.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class EventAttr {
    
    private Date startTime;
    private Date endTime;
    private Date drawTime;
    private Date receiveBeginDate;
    private Date receiveEndDate;
    private Date companySendDate;
    private String companyEmail; 
    private String pfEmail;
    private String applyDesc;
    private String receiveDesc;
    private String eventTypeDesc;
    private String organizerName;
    private String organizerLogo;
    private Boolean isBcc;
    private Boolean isSent;
    
    public EventAttr() {
    }
    
    @JsonView(Views.Public.class)
	public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    @JsonView(Views.Public.class)
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    @JsonView(Views.Public.class)
    public Date getDrawTime() {
        return drawTime;
    }
    public void setDrawTime(Date drawTime) {
        this.drawTime = drawTime;
    }
    @JsonView(Views.Public.class)
    public Date getReceiveBeginDate() {
        return receiveBeginDate;
    }
    public void setReceiveBeginDate(Date receiveBeginDate) {
        this.receiveBeginDate = receiveBeginDate;
    }
    @JsonView(Views.Public.class)
    public Date getReceiveEndDate() {
        return receiveEndDate;
    }
    public void setReceiveEndDate(Date receiveEndDate) {
        this.receiveEndDate = receiveEndDate;
    }
    @JsonView(Views.Public.class)
    public Date getCompanySendDate() {
        return companySendDate;
    }
    public void setCompanySendDate(Date companySendDate) {
        this.companySendDate = companySendDate;
    }
    @JsonView(Views.Public.class)
    public String getCompanyEmail() {
        return companyEmail;
    }
    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
    @JsonView(Views.Public.class)
    public String getPfEmail() {
        return pfEmail;
    }
    public void setPfEmail(String pfEmail) {
        this.pfEmail = pfEmail;
    }
    @JsonView(Views.Public.class)
    public String getApplyDesc() {
        return StringEscapeUtils.unescapeJava(applyDesc);
    }
    public void setApplyDesc(String applyDesc) {
        this.applyDesc = StringEscapeUtils.escapeJava(applyDesc);
    }
    @JsonView(Views.Public.class)
    public String getReceiveDesc() {
        return StringEscapeUtils.unescapeJava(receiveDesc);
    }
    public void setReceiveDesc(String receiveDesc) {
        this.receiveDesc = StringEscapeUtils.escapeJava(receiveDesc);
    }
    @JsonView(Views.Public.class)
    public String getEventTypeDesc() {
        return StringEscapeUtils.unescapeJava(eventTypeDesc);
    }
    public void setEventTypeDesc(String eventTypeDesc) {
        this.eventTypeDesc = StringEscapeUtils.escapeJava(eventTypeDesc);
    }
	@JsonView(Views.Public.class)
    public String getOrganizerName() {
        return organizerName;
    }
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
    @JsonView(Views.Public.class)
    public String getOrganizerLogo() {
        return organizerLogo;
    }
    public void setOrganizerLogo(String organizerLogo) {
        this.organizerLogo = organizerLogo;
    }
    @JsonView(Views.Public.class)
	public Boolean getIsBcc() {
    	if (isBcc == null)
    		return Boolean.FALSE;
		return isBcc;
	}
	public void setIsBcc(Boolean isBcc) {
		this.isBcc = isBcc;
	}
	@JsonView(Views.Public.class)
	public Boolean getIsSent() {
    	if (isSent == null)
    		return Boolean.FALSE;
		return isSent;
	}
	public void setIsSent(Boolean isSent) {
		this.isSent = isSent;
	}
}
