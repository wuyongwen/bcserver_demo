package com.cyberlink.cosmetic.action.backend;

import java.util.List;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.User;

public class UserAccessControl {
    @SpringBean("user.AttributeDao")
    private AttributeDao attributeDao;

	User user = null;
	Long accessMap = Long.valueOf(0);
	
	public UserAccessControl(User user) {
		attributeDao = BeanLocator.getBean("user.AttributeDao");
		this.user = user;
		if (this.user != null) {
			List<Attribute> attr = attributeDao.findByRefIdAndNames(AttributeType.AccessControl, user.getId(), "AccessMap");
			if (attr.size() > 0) {
				accessMap = Long.parseLong((String) attr.get(0).getAttrValue(), 16);
			}
		}
	}
	public void saveAccessControl() {
		if (user == null)
			return;
        Attribute attr = attributeDao.findOneByRefIdAndName(AttributeType.AccessControl, user.getId(), "AccessMap");
        if (attr == null) {
        	attr = new Attribute();
        }
        attr.setAttrName("AccessMap");
        attr.setAttrValue(Long.toHexString(accessMap));
        attr.setRefId(user.getId());
        attr.setRefType(AttributeType.AccessControl);
        attributeDao.update(attr);		
	}
	public UserAccessControl setUser(User user) {
		if (user == null) {
			this.user = null;
			accessMap = Long.valueOf(0);
			return this;
		}
		if (this.user == null || !this.user.getId().equals(user.getId())) {
			this.user = user;
	    	List<Attribute> attr = attributeDao.findByRefIdAndNames(AttributeType.AccessControl, user.getId(), "AccessMap");
	        if (attr.size() > 0) {
	        	accessMap = Long.valueOf((String) attr.get(0).getAttrValue());
	        }        	
		}
        return this;
	}
	
	public Boolean getUserManagerAccess() {
		if ((accessMap.longValue() & AccessMap.USER_MANAGER_ACCESS.value()) != 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void setUserManagerAccess(Boolean flag) {
		if (flag)
			accessMap = accessMap.longValue() | AccessMap.USER_MANAGER_ACCESS.value();
		else
			accessMap = accessMap.longValue() & (~AccessMap.USER_MANAGER_ACCESS.value());
	}
	
	public Boolean getPostManagerAccess() {
		if ((accessMap.longValue() & AccessMap.POST_MANAGER_ACCESS.value()) != 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void setPostManagerAccess(Boolean flag) {
		if (flag)
			accessMap = accessMap.longValue() | AccessMap.POST_MANAGER_ACCESS.value();
		else
			accessMap = accessMap.longValue() & (~AccessMap.POST_MANAGER_ACCESS.value());
	}

	public Boolean getCircleManagerAccess() {
		if ((accessMap.longValue() & AccessMap.CIRCLE_MANAGER_ACCESS.value()) != 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void setCircleManagerAccess(Boolean flag) {
		if (flag)
			accessMap = accessMap.longValue() | AccessMap.CIRCLE_MANAGER_ACCESS.value();
		else
			accessMap = accessMap.longValue() & (~AccessMap.CIRCLE_MANAGER_ACCESS.value());
	}

	public Boolean getProductManagerAccess() {
		if ((accessMap.longValue() & AccessMap.PRODUCT_MANAGER_ACCESS.value()) != 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void setProductManagerAccess(Boolean flag) {
		if (flag)
			accessMap = accessMap.longValue() | AccessMap.PRODUCT_MANAGER_ACCESS.value();
		else
			accessMap = accessMap.longValue() & (~AccessMap.PRODUCT_MANAGER_ACCESS.value());
	}

	public Boolean getReportManagerAccess() {
		if ((accessMap.longValue() & AccessMap.REPORT_MANAGER_ACCESS.value()) != 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void setReportManagerAccess(Boolean flag) {
		if (flag)
			accessMap = accessMap.longValue() | AccessMap.REPORT_MANAGER_ACCESS.value();
		else
			accessMap = accessMap.longValue() & (~AccessMap.REPORT_MANAGER_ACCESS.value());
	}
	
	public Boolean getReportAuditorAccess() {
        if ((accessMap.longValue() & AccessMap.REPORT_AUDITOR_ACCESS.value()) != 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void setReportAuditorAccess(Boolean flag) {
        if (flag)
            accessMap = accessMap.longValue() | AccessMap.REPORT_AUDITOR_ACCESS.value();
        else
            accessMap = accessMap.longValue() & (~AccessMap.REPORT_AUDITOR_ACCESS.value());
    }
	
    public Boolean getEventManagerAccess() {
        if ((accessMap.longValue() & AccessMap.EVENT_MANAGER_ACCESS.value()) != 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void setEventManagerAccess(Boolean flag) {
        if (flag)
            accessMap = accessMap.longValue() | AccessMap.EVENT_MANAGER_ACCESS.value();
        else
            accessMap = accessMap.longValue() & (~AccessMap.EVENT_MANAGER_ACCESS.value());
    }
    
    public Boolean getApkManagerAccess() {
        if ((accessMap.longValue() & AccessMap.APK_MANAGER_ACCESS.value()) != 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void setApkManagerAccess(Boolean flag) {
        if (flag)
            accessMap = accessMap.longValue() | AccessMap.APK_MANAGER_ACCESS.value();
        else
            accessMap = accessMap.longValue() & (~AccessMap.APK_MANAGER_ACCESS.value());
    }
    
	public Long getAccessMap() {
		return accessMap;
	}

}
