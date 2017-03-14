package com.cyberlink.cosmetic.action.api.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendGroupDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendUserDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrendUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/post/update-user-trend-group.action")
public class UpdateUserTrendGroup extends AbstractAction {
    
    @SpringBean("post.psTrendUserDao")
    private PsTrendUserDao psTrendUserDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    @SpringBean("post.psTrendGroupDao")
    private PsTrendGroupDao psTrendGroupDao;
    
    @SpringBean("core.jdbcTemplate")
    private TransactionTemplate transactionTemplate;
    
    private String groups;
    private int BATCH_SIZE = 100;
    
    @Validate(required = true, on = "route")
    public void setGroups(String groups) {
        this.groups = groups;
    }
    
    @DefaultHandler
    public Resolution route() {
        try {
            Map<String, Long> groupIdMap = psTrendGroupDao.getAvailableId();
            Map<String, String> inGroupMap = objectMapper.readValue(groups, new TypeReference<Map<String, String>>(){});
            List<String> toAdd = new ArrayList<String>();
            for(final String uuid : inGroupMap.keySet()) {
                toAdd.add(uuid);
                if(toAdd.size() >= BATCH_SIZE) {
                    batchUpdate(groupIdMap, toAdd, inGroupMap);
                    toAdd.clear();
                }
            }
            if(toAdd.size() > 0) {
                batchUpdate(groupIdMap, toAdd, inGroupMap);
                toAdd.clear();
            }
        } catch (IOException e) {
            logger.error("", e);
            new ErrorResolution(400, e.getCause().getMessage());
        }
        
        return success();
    }
    
    private Boolean batchUpdate(final Map<String, Long> groupIdMap, final List<String> toAdd, final Map<String, String> inGroupMap) {
        Boolean result = true;
        try {
            result = transactionTemplate.execute(new TransactionCallback<Boolean>() {

                @Override
                public Boolean doInTransaction(
                        TransactionStatus status) {
                    List<PsTrendUser> pstus = psTrendUserDao.findGroupByUuids(toAdd);
                    for(PsTrendUser pstu : pstus) {
                        if(!groupIdMap.containsKey(inGroupMap.get(pstu.getUuid())))
                            continue;
                        pstu.setGroups(groupIdMap.get(inGroupMap.get(pstu.getUuid())));
                        psTrendUserDao.update(pstu);
                        toAdd.remove(pstu.getUuid());
                    }
                    if(toAdd.size() > 0) {
                        for(String uuid : toAdd) {
                            PsTrendUser newPstu = new PsTrendUser();
                            newPstu.setUuid(uuid);
                            newPstu.setGroups(groupIdMap.get(inGroupMap.get(uuid)));
                            psTrendUserDao.create(newPstu);
                        }
                    }
                    return true;
                }
                
            });
        }
        catch(Exception e) {
            logger.error("", e);
            result = false;
        }
        return result;
    }
}
