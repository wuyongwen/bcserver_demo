package com.cyberlink.cosmetic.modules.user.model;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "BC_DECLINE")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Decline extends AbstractCoreEntity<Long>{
    private Long id;
    private String declineid;
    private String type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "TYPE")
    @JsonView(Views.Public.class)
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "DECLINEID")
    @JsonView(Views.Public.class)    
    public String getDeclineid() {
        return declineid;
    }

    public void setDeclineid(String declineid) {
        this.declineid = declineid;
    }
	
}
