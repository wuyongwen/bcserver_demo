package com.cyberlink.core.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<PK extends Serializable> implements
        IdEntity<PK>, Serializable {

    private static final long serialVersionUID = 8750196465247561362L;

}
