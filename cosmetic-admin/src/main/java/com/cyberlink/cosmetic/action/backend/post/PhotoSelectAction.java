package com.cyberlink.cosmetic.action.backend.post;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.tuple.Pair;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.service.PhotoScoreService;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService.ImageViolationType;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/post/photo-selection.action")
public class PhotoSelectAction extends AbstractAction {
    
    public enum RunTimeUnit {
        Month(Calendar.MONTH), Hour(Calendar.HOUR), Minute(Calendar.MINUTE);
        
        private int value;
        RunTimeUnit(int value) {
            this.value = value;
        }
        
        public int GetValue() {
            return value;
        }
    }
    
    @SpringBean("file.photoProcessService")
    private PhotoProcessService photoProcessService;
    
    @SpringBean("backend.PhotoScoreService")
    private PhotoScoreService photoScoreService;
    
    @SpringBean("mail.mailInappropPostCommentService")
    private MailInappropPostCommentService mailInappropPostCommentService;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    private String extUrl = null;
    
    private String dataUrl = null;
    
    private RunTimeUnit runTimeUnit = RunTimeUnit.Month;

    private Integer runDuration = 3;
    
    private Long runMinScore = 100L;
    
    private Map<String, Object> postServiceStatus;
    
    private int handlerCount = 2;
    
    private int threadCount = 2;
    
    private Boolean exportFace = false;

    private String operMessage = "";
    
    public void setExtUrl(String extUrl) {
        this.extUrl = extUrl;
    }
    
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
    
    public void setRunTimeUnit(RunTimeUnit runTimeUnit) {
        this.runTimeUnit = runTimeUnit;
    }

    public void setRunDuration(Integer runDuration) {
        this.runDuration = runDuration;
    }
    
    public void setRunMinScore(Long runMinScore) {
        this.runMinScore = runMinScore;
    }
    

    public Map<String, Object> getPostServiceStatus() {
        return postServiceStatus;
    }
    
    public int getHandlerCount() {
        return handlerCount;
    }

    public void setHandlerCount(int handlerCount) {
        this.handlerCount = handlerCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public Boolean getExportFace() {
        return exportFace;
    }

    public void setExportFace(Boolean exportFace) {
        this.exportFace = exportFace;
    }
    
    public String getOperMessage() {
        return operMessage;
    }
    
    @DefaultHandler
    public Resolution route() {
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        postServiceStatus = photoScoreService.getStatus();
        postServiceStatus.put("OS Arch", osArch);
        postServiceStatus.put("OS Name", osName);
        return forward();
    }
    
    public Resolution getScore() {
        long startTime = System.currentTimeMillis();
        Float score = null;
        Pair<BufferedImage, Integer> pImgInfo = null;
        try {
            if(extUrl != null)
                pImgInfo = photoProcessService.getBufferAndLengthFromUrl(extUrl);
            else if(dataUrl != null)
                pImgInfo = photoProcessService.getBufferAndLengthFromDataUrl(dataUrl);
        }
        catch(Exception e) {
        }
        if(pImgInfo != null)
            score = photoProcessService.GetScore(pImgInfo);
        if(score == null)
            return new ErrorResolution(400, "Failed");
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Score", score);
        result.put("Process Time", String.valueOf(duration) + " ms");
        return json(result);
    }
    
    public Resolution getFace() {
        long startTime = System.currentTimeMillis();
        String rectJson = null;
        Pair<BufferedImage, Integer> pImgInfo = null;
        try {
            if(extUrl != null)
                pImgInfo = photoProcessService.getBufferAndLengthFromUrl(extUrl);
            else if(dataUrl != null)
                pImgInfo = photoProcessService.getBufferAndLengthFromDataUrl(dataUrl);
        }
        catch(Exception e){
        }
        if(pImgInfo != null) {
            rectJson = photoProcessService.DetectFace(pImgInfo, exportFace);
        }
        if(rectJson == null)
            return new ErrorResolution(400, "Failed");
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Face Rect", rectJson);
        result.put("Process Time", String.valueOf(duration) + " ms");
        return json(result);
    }
    
    public Resolution getViolate() {
        long startTime = System.currentTimeMillis();
        Map<ImageViolationType, Boolean> voilated = null;
        Pair<BufferedImage, Integer> pImgInfo = null;
        try {
            if(extUrl != null)
                pImgInfo = photoProcessService.getBufferAndLengthFromUrl(extUrl);
            else if(dataUrl != null)
                pImgInfo = photoProcessService.getBufferAndLengthFromDataUrl(dataUrl);
        }
        catch(Exception e){
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        if(pImgInfo != null) {
            List<ImageViolationType> detecType = new ArrayList<ImageViolationType>();
            detecType.add(ImageViolationType.Porn);
            detecType.add(ImageViolationType.Violence);
            voilated = photoProcessService.DetectImageViolation(pImgInfo.getLeft(), detecType, result);
        }
        if(voilated == null)
            return new ErrorResolution(400, "Failed");
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        result.put("Voilated", voilated);
        result.put("Process Time", String.valueOf(duration) + " ms");
        return json(result);
    }
    
    public Resolution startProcessService() {
        photoProcessService.Start(handlerCount, threadCount);
        startScoreService();
        operMessage = "Complete";
        return route();
    }
    
    public Resolution stopProcessService() {
        stopScoreService();
        photoProcessService.Stop();
        operMessage = "Complete";
        return route();
    }
    
    public Resolution startScoreService() {
        photoScoreService.start();
        return route();
    }
    
    public Resolution stopScoreService() {
        photoScoreService.stop();
        return route();
    }
    
    public Resolution CleanOldRecord() {
        photoScoreService.CleanOldRecord();
        return json("Complete");
    }
    
    public Resolution release() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for(PostNewPool.NewPoolGroup g : PostNewPool.NewPoolGroup.values()) {
            Map<String, Object> tmpR = new LinkedHashMap<String, Object>();
            photoScoreService.releaseFromPoolToNew(g, true, tmpR);
            result.put(g.toString(), tmpR);
        }
        return json(result);
    }
    
    public Resolution runInWindows() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        Date endDate = cal.getTime();
        cal.add(runTimeUnit.GetValue(), -runDuration);
        Date startDate = cal.getTime();    
        Map<String, Object> summary = new HashMap<String, Object>();
        Throwable error = photoScoreService.runFor(startDate, endDate, runMinScore, true, summary, null); 
        if(error == null)
            summary.put("Result", "Complete");
        else
            summary.put("Result", error.getMessage());
        
        String content;
        try {
            content = objectMapper.writerWithView(Views.Public.class).writeValueAsString(summary);
        } catch (JsonProcessingException e) {
            content = e.getMessage();
        }
        mailInappropPostCommentService.directSend("Victor_Chew@PerfectCorp.com", "runInWindows", content);
        return json(summary);
    }
    
    public Resolution processUnhandled() {
        photoScoreService.HandleUnhandledPostScore(null, null);
        return json("Complete");
    }
    
    public Resolution getJobs() {
        String result = "";
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (String jobName : scheduler.getJobNames(groupName)) {
                  Trigger[] triggers = scheduler.getTriggersOfJob(jobName,groupName);
                  Date nextFireTime = triggers[0].getNextFireTime();
                  result += "[jobName] : " + jobName + " [groupName] : " + groupName + " - " + nextFireTime + "\n";
        
                }
            }
        }
        catch(Exception e) {
            result += e.getMessage();
        }
        return json(result);
    }

}
