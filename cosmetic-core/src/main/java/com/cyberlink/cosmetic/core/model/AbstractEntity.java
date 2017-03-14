package com.cyberlink.cosmetic.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@MappedSuperclass
public abstract class AbstractEntity<PK extends Serializable> extends
        AbstractCoreEntity<PK> {

    private static final long serialVersionUID = -6211604178143619852L;

    private Long shardId;
    
    @Transient
    public Long getShardId() {
        return shardId;
    }

    @Transient
    public void setShardId(Long shardId) {
        this.shardId = shardId;
    }

    @Id
    @GenericGenerator(name = "generalIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.GeneralIdGenerator")
    @GeneratedValue(generator = "generalIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public PK getId() {
        return id;
    }

}
