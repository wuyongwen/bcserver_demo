package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@DynamicUpdate
@Table(name = "BC_PS_TREND")
public class PsTrend extends AbstractCoreEntity<PsTrend.PsTrendKey> {
    
    private static final long serialVersionUID = -1604722223893160784L;
    
    @Embeddable
    static public class PsTrendKey implements Serializable {

        private static final long serialVersionUID = 1353333251882342043L;
        
        private Long pid;
        private Long groups;
        private String locale;
        
        @Column(name = "ID")
        public Long getPid() {
            return pid;
        }

        public void setPid(Long pId) {
            this.pid = pId;
        }

        @Column(name = "GROUPS")
        public Long getGroups() {
            return groups;
        }

        public void setGroups(Long groups) {
            this.groups = groups;
        }
        
        @Column(name = "LOCALE")
        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder().
                append(pid).
                append(groups).
                append(locale).
                toHashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PsTrendKey))
                return false;
            
            PsTrendKey rhs = (PsTrendKey) obj;
            return new EqualsBuilder().
                append(pid, rhs.pid).
                append(groups, rhs.groups).
                append(locale, rhs.locale).
                isEquals();
        }
    }
    
    private Date displayTime;

    private Long promoteScore;
    
    @EmbeddedId
    public PsTrendKey getId() {
        return id;
    }     
    
    @Column(name = "DISPLAY_TIME")
    public Date getDisplayTime() {
        return displayTime;
    }
    
    public void setDisplayTime(Date displayTime) {
        this.displayTime = displayTime;
    }

    @Column(name = "PROMOTE_SCORE")
    public Long getPromoteScore() {
        return promoteScore;
    }
    
    public void setPromoteScore(Long promoteScore) {
        this.promoteScore = promoteScore;
    }
}
