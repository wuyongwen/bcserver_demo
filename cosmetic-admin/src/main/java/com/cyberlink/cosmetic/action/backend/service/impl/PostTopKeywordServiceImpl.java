package com.cyberlink.cosmetic.action.backend.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.PostTopKeywordService;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.service.RelatedPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostTopKeywordServiceImpl extends AbstractService 
    implements PostTopKeywordService {
    
    private RelatedPostService relatedPostService;
    private MailInappropPostCommentService mailInappropPostCommentService;
    private ObjectMapper objectMapper;
    static private Boolean running = false;
    static private Boolean enable = true;
    static private Boolean pause = false;

    private String [] logReceivers = new String[] {"Fish_Hsu@PerfectCorp.com", "Victor_Chew@PerfectCorp.com", "Frank_Chuang@PerfectCorp.com"};

	@Override
    public void start() {
        setPause(false);
    }
    
    @Override
    public void stop() {
        setPause(true);
    }

    @Override
    public Map<String, Object> getStatus() {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Running", getRunning());
        results.put("Enable", getEnable());
        results.put("Pausing", getPause());
        return results;
    }

    @Override
    //@BackgroundJob(cronExpression = "0 0 8 ? * SUN *")
    public void exec() {
        if(!getEnable() || getRunning())
            return;
        if(getPause())
            return;
        
        setRunning(true);
    	try{
    		Date curTime = Calendar.getInstance().getTime();
            long markTime;
            long lapTime;
            Map<String, Object> summary = new HashMap<String, Object>();
            markTime = System.currentTimeMillis();
            relatedPostService.generatePostIdsByKeyword(curTime);
            lapTime = System.currentTimeMillis();
            summary.put("generatePostIdsByKeyword", lapTime - markTime);
            SendMail("Run PostTopKeyword scheduler 'generatePostIdsByKeyword' End", summary);
	    } catch (Exception e) {
			SendMail("Run PostTopKeyword scheduler 'generatePostIdsByKeyword' Error", e);
		} finally {
			setRunning(false);
		}
    }
    
    @Override
    //@BackgroundJob(cronExpression = "0 0 1 ? * THU *")
    public void exec2() {
        if(!getEnable() || getRunning())
            return;
        if(getPause())
            return;
        
        setRunning(true);
    	try{
    		Calendar cal = Calendar.getInstance();
    		cal.add(Calendar.DATE, -7);
    		Date dayInLastWeek = cal.getTime(); //get a day of last week
    		
            long markTime;
            long lapTime;
            Map<String, Object> summary = new HashMap<String, Object>();
            markTime = System.currentTimeMillis();
            relatedPostService.deleteOldRecord(dayInLastWeek);
            lapTime = System.currentTimeMillis();
            summary.put("deleteOldRecord", lapTime - markTime);
            SendMail("Run PostTopKeyword scheduler 'deleteOldRecord' End", summary);
	    } catch (Exception e) {
			SendMail("Run PostTopKeyword scheduler 'deleteOldRecord' Error", e);
		} finally {
			setRunning(false);
		}
    }
    
	public void setRelatedPostService(RelatedPostService relatedPostService) {
		this.relatedPostService = relatedPostService;
	}
	
    public void setMailInappropPostCommentService(MailInappropPostCommentService mailInappropPostCommentService) {
        this.mailInappropPostCommentService = mailInappropPostCommentService;
    }
    
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    private Boolean getRunning() {
        Boolean r;
        synchronized(running) {
            r = running;
        }
        return r;
    }

    private void setRunning(Boolean running) {
        synchronized(running) {
            PostTopKeywordServiceImpl.running = running;
        }
    }

    public Boolean getEnable() {
        Boolean e;
        synchronized(enable) {
            e = enable;
        }
        return e;
    }

    public void setEnable(Boolean enable) {
        synchronized(running) {
            PostTopKeywordServiceImpl.enable = enable;
        }
    }

    public Boolean getPause() {
        Boolean p;
        synchronized(pause) {
            p = pause;
        }
        return p;
    }

    public void setPause(Boolean pause) {
        synchronized(pause) {
            PostTopKeywordServiceImpl.pause = pause;
        }
    }
    
    private void SendMail(String subject, Object object) {
        subject += " - " + Constants.getWebsiteDomain();
        String content;
        try {
            content = objectMapper.writerWithView(Views.Public.class).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            content = e.getMessage();
        }
        
        mailInappropPostCommentService.directSend(logReceivers, subject, content);
    }
}
