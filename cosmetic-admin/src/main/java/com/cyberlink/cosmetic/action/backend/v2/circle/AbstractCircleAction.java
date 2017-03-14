package com.cyberlink.cosmetic.action.backend.v2.circle;

import java.util.List;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;

public class AbstractCircleAction extends AbstractAction {
    
    @SpringBean("circle.circleService")
    protected CircleService circleService;
    
    @SpringBean("circle.circleDao")
    protected CircleDao circleDao;
    
    protected List<Circle> getBcDefaultCircle(String region) {
        return circleService.getBcDefaultCircle(region);
    }
    
    protected List<Circle> getUserDefaultCircle(Long userId, Boolean withDeleted) {
        return circleService.getUserDefaultCircle(userId, withDeleted);
    }
    
    protected PageResult<Circle> listUserCircle(Long userId, Boolean withSecret, String region, Boolean withDefault, BlockLimit blockLimit) {
        return circleService.listUserCircle(userId, withSecret, region, withDefault, blockLimit);
    }
    
    protected Circle getUserAccessibleCircle(Circle relatedCircle, Long userId, Boolean createIfNotExist) {
        return circleService.getUserAccessibleCircle(relatedCircle, userId, createIfNotExist);
    }
}
