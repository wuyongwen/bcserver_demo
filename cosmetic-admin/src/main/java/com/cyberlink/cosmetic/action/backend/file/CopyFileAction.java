package com.cyberlink.cosmetic.action.backend.file;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import org.jets3t.service.ServiceException;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.service.StorageService;

@UrlBinding("/file/copy-file.action")
public class CopyFileAction extends AbstractAction {
    private static final String errorMessage = "You aren't an administrator";
    private final String testBucket = "cosmetic-test-01";
    private final String prodBucket = "cosmetic-production-01";
    
    private Date startTime;
    private Date endTime;
    private List<String> result;
    
    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    @SpringBean("file.storageService")
    private StorageService storageService;
    
    public Date getStartTime() {
        return startTime;
    }

    @Validate(required = true, on = "copyfiles")
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    @Validate(required = true, on = "copyfiles")
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    @SuppressWarnings("unchecked")
    @DefaultHandler
    public Resolution route() {
        if(!getCurrentUserAdmin())
            return new StreamingResolution("text/html", errorMessage);
        
        Date reqStartTime = (Date) getServletRequest().getAttribute("reqStartTime");
        Date reqEndTime = (Date) getServletRequest().getAttribute("reqEndTime");
        result = (List<String>) getServletRequest().getAttribute("copyResult");

        Date today = removeTime(new Date());
 
        if (reqStartTime != null)
            startTime = reqStartTime;
        else
            startTime = today;
        
        if (reqEndTime != null)
            endTime = reqEndTime;
        else
            endTime = addHour(startTime, 8);
 
        return forward();
    }
    
    /**
     * copy files from production bucket to test bucket
     * */
    public Resolution copyfiles() {
        if(!getCurrentUserAdmin())
            return new StreamingResolution("text/html", errorMessage);              

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        result = new ArrayList<String>();
        result.add("Starting to copy " + prodBucket  + " to " + testBucket + " ...");
        result.add("File Item's createdTime is greater than or equal: " + sdf.format(startTime));
        result.add("File Item's createdTime is less than or equal: " + sdf.format(endTime));

        List<FileItem> fileItems = fileItemDao.findByDateTime(startTime, endTime);
        
        result.add("Total file items: " + fileItems.size());
        
        try {
            List<String> copyResult = storageService.copyFiles(fileItems, prodBucket, testBucket);
            result.add("Copy/handle file itmes: " + copyResult.size());
            if (copyResult.size() > 0) {
                result.add("== Detail as below ==");
                result.addAll(copyResult);
            } 
        } catch (ServiceException e) {
            result.add("Fail to copy files, please check logs");
            logger.error("Fail to copy files", e);
        }      
        result.add("Finish");
        
        FlashScope fs = FlashScope.getCurrent(getServletRequest(), true);
        if (fs.get("reqStartTime") == null)
            fs.put("reqStartTime", startTime);
        
        if (fs.get("reqEndTime") == null)
            fs.put("reqEndTime", endTime); 

        if (fs.get("copyResult") == null)
            fs.put("copyResult", result);
        
        return new RedirectResolution("/file/copy-file.action");
    }
    
    private Date removeTime(Date d) {
        Calendar c = Calendar.getInstance(); 
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    private Date addHour(Date d, int amount) {
        Calendar c = Calendar.getInstance(); 
        c.setTime(d);
        c.add(Calendar.HOUR_OF_DAY, amount);
        return c.getTime();
    }
}
