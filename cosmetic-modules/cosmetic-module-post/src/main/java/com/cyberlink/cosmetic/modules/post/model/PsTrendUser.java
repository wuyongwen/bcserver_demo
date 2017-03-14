package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@DynamicUpdate
@Table(name = "BC_PS_TREND_USR")
public class PsTrendUser extends AbstractCoreEntity<Long> {
    
    private static final long serialVersionUID = -1705770227694924067L;
    
    private String uuid;
    private Long groups;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }    
    
    @Column(name = "UUID")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(name = "GROUPS")
    public Long getGroups() {
        return this.groups;
    }

    public void setGroups(Long groups) {
        this.groups = groups;
    }
    
}
