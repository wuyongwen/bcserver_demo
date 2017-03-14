package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.Calendar;
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
@Table(name = "BC_PS_TREND_POOL")
public class PsTrendPool extends AbstractCoreEntity<PsTrendPool.PsTrendPoolKey> {

    private static final long serialVersionUID = -6764152548774814608L;
    
    @Embeddable
    static public class PsTrendPoolKey implements Serializable {

        private static final long serialVersionUID = -5608540908190948324L;
        
        private Long pId;
        private Integer bucket;
        private Long circleTypeId;
        
        @Column(name = "ID")
        public Long getpId() {
            return pId;
        }
        
        public void setpId(Long pId) {
            this.pId = pId;
        }
        
        @Column(name = "BUCKET")
        public Integer getBucket() {
            return bucket;
        }
        
        public void setBucket(Integer bucket) {
            this.bucket = bucket;
        }

        @Column(name = "CIRCLE_TYPE_ID")
        public Long getCircleTypeId() {
            return circleTypeId;
        }
        
        public void setCircleTypeId(Long circleTypeId) {
            this.circleTypeId = circleTypeId;
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder().
                append(pId).
                append(bucket).
                append(circleTypeId).
                toHashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PsTrendPoolKey))
                return false;
            
            PsTrendPoolKey rhs = (PsTrendPoolKey) obj;
            return new EqualsBuilder().
                append(pId, rhs.pId).
                append(bucket, rhs.bucket).
                append(circleTypeId, rhs.circleTypeId).
                isEquals();
        }
    }
    
    private Date displayTime;
    
    @EmbeddedId
    public PsTrendPoolKey getId() {
        return id;
    }    
    
    @Column(name = "DISPLAY_TIME")
    public Date getDisplayTime() {
        return displayTime;
    }
    
    public void setDisplayTime(Date displayTime) {
        this.displayTime = displayTime;
    }
    
    static public Integer getBucketId(Date currentDate) {
        if(currentDate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int dateToSub = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if(dateToSub < 0)
            dateToSub = 6;
        cal.add(Calendar.DATE, -dateToSub);
        return cal.get(Calendar.WEEK_OF_YEAR) % 5;
    }
}
