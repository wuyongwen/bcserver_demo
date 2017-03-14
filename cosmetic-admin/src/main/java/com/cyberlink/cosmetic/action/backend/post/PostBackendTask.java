package com.cyberlink.cosmetic.action.backend.post;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.service.BackendPostService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/post/backend-task.action")
public class PostBackendTask extends AbstractAction{

    @SpringBean("backend.BackendPostService")
    private BackendPostService backendPostService;
    
    @DefaultHandler
    public Resolution postServiceStatus() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Task Count", backendPostService.getTaskCount());
        result.put("Status", backendPostService.getStatus());
        return json(result);
    }

    public Resolution startPostService() {
        backendPostService.start();
        return postServiceStatus();
    }
    
    public Resolution stopPostService() {
        backendPostService.stop();
        return postServiceStatus();
    }
}
