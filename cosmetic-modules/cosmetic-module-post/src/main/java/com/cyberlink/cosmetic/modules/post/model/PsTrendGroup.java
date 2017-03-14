package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
@Table(name = "BC_PS_TREND_GROUP")
public class PsTrendGroup extends AbstractCoreEntity<PsTrendGroup.PsTrendGroupKey> {

    private static final long serialVersionUID = -4183599081989828582L;
    
    public static final Date DEFAULT_POINTER = new Date(1451606400000L);
    
    @Embeddable
    static public class PsTrendGroupKey implements Serializable {
        
        private static final long serialVersionUID = 5927075763568297731L;

        private Long gId;
        private String locale;
        
        @Column(name = "ID")
        public Long getgId() {
            return gId;
        }

        public void setgId(Long gId) {
            this.gId = gId;
        }

        @Column(name = "LOCALE")
        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
    }
    
    private String groups;
    private String types;
    private String pointers;
    private Integer step;
    
    @EmbeddedId
    public PsTrendGroupKey getId() {
        return id;
    }
    
    @Column(name = "GROUPS")
    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
    
    @Column(name = "TYPES")
    public String getTypes() {
        return types;
    }
    
    public void setTypes(String types) {
        this.types = types;
    }
    
    @Column(name = "POINTERS")
    public String getPointers() {
        return pointers;
    }
    
    public void setPointers(String pointers) {
        this.pointers = pointers;
    }
    
    @Column(name = "STEP")
    public Integer getStep() {
        return step;
    }
    
    public void setStep(Integer step) {
        this.step = step;
    }
        
}
