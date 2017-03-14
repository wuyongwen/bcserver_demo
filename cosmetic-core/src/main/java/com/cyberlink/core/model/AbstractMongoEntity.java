package com.cyberlink.core.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public abstract class AbstractMongoEntity<PK extends Serializable> extends BaseEntity<PK> 
implements IdEntity<PK>, SoftDeletableEntity,
VersionedEntity, LastModifiedEntity, Serializable {
    private static final long serialVersionUID = 6853240479212439494L;
    protected PK id;
    private Integer objVersion;
    private Boolean isDeleted = Boolean.FALSE;
    private Date createdTime = Calendar.getInstance().getTime();
    private Date lastModified = Calendar.getInstance().getTime();

    public void setId(PK id) {
        this.id = id;
    }

    @Field("isDeleted")
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Field("objVersion")    
    public Integer getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(Integer objVersion) {
        this.objVersion = objVersion;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Field("createdTime")    
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Field("lastModified")    
    @JsonView(Views.Public.class)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object object) {
        if (!(object instanceof AbstractCoreEntity)) {
            return false;
        }
        AbstractMongoEntity<PK> rhs = (AbstractMongoEntity<PK>) object;
        return new EqualsBuilder().appendSuper(super.equals(object))
                .append(this.objVersion, rhs.objVersion)
                .append(this.isDeleted, rhs.isDeleted)
                .append(getId(), rhs.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(-334886091, -588489103)
                .appendSuper(super.hashCode()).append(this.objVersion)
                .append(this.isDeleted).append(getId()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .appendSuper(super.toString()).append("id", this.getId())
                .append("objVersion", this.objVersion)
                .append("isDeleted", this.isDeleted).toString();
    }
}
