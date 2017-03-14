package com.cyberlink.cosmetic.modules.circle.model;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;

@Entity
@DynamicUpdate
@Table(name = "BC_CIRCLE_ATTR")
public class CircleAttribute extends AbstractCoreEntity<Long> {

    public static enum CircleAttrType {
        PostCount(new ValueHandler() {
            public String handleValue(String oriValue, String newValue) {
                if(oriValue == null)
                    oriValue = "0";
                return String.valueOf(Long.valueOf(oriValue) + Long.valueOf(newValue));
            }
        }),
        FollowerCount(new ValueHandler() {
            public String handleValue(String oriValue, String newValue) {
                if(oriValue == null)
                    oriValue = "0";
                return String.valueOf(Long.valueOf(oriValue) + Long.valueOf(newValue));
            }
        }),
        Thumbnail(new ValueHandler() {
            public String handleValue(String oriValue, String newValue) {
                return newValue;
            }
        });
        
        public interface ValueHandler {
            String handleValue(String oriValue, String newValue);
        }

        private ValueHandler valueHandler;

        private CircleAttrType(ValueHandler valueHandler) {
            this.valueHandler = valueHandler;
        }
        
        public String getNewValue(String oriValue, String newValue) {
            return valueHandler.handleValue(oriValue, newValue);
        }
    }
    
    public static int maxCircleAttrSize = Circle.maxPostThumbnailSize - 1 + CircleAttrType.values().length;
    
    private static final long serialVersionUID = -4300673481339131745L;    
    
    private String region;
    private Circle circle;
    private CircleAttrType attrType;
    private String attrValue; 
    

	@Override
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @Column(name = "REGION")
    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    @Column(name = "ATTR_TYPE")
    @Enumerated(EnumType.STRING)
    public CircleAttrType getAttrType() {
        return this.attrType;
    }

    public void setAttrType(CircleAttrType attrType) {
        this.attrType = attrType;
    }
    
    @Column(name = "ATTR_VALUE")
    public String getAttrValue() {
        return this.attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CIRCLE_ID")
    @Where(clause="IS_DELETED=0")
    public Circle getCircle() {
    	return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}
    
}
