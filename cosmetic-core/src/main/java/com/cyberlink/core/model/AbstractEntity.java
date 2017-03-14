package com.cyberlink.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class AbstractEntity<PK extends Serializable> extends
        AbstractCoreEntity<PK> {

    private static final long serialVersionUID = -6211604178143619852L;

    @Id
    @GenericGenerator(name = "generalIdGenerator", strategy = "com.cyberlink.social.hibernate.id.GeneralIdGenerator")
    @GeneratedValue(generator = "generalIdGenerator")
    @Column(name = "ID")
    public PK getId() {
        return id;
    }

}
