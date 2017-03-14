package com.cyberlink.cosmetic.action.backend.file.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jets3t.service.ServiceException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.service.StorageService;

public class CopyFileJob extends QuartzJobBean {
    final Logger logger = LoggerFactory.getLogger(getClass()); 
    final String testBucket = "cosmetic-test-01";
    final String prodBucket = "cosmetic-production-01";
    
    private FileItemDao fileItemDao;
    private StorageService storageService;

    public void setFileItemDao(FileItemDao fileItemDao) {
        this.fileItemDao = fileItemDao;
    }
    
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        Date today = removeTime(new Date());
        Date startDate = addDay(today, -2);
        Date endDate = addDay(today, 1);
      
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        logger.info("Starting to copy " + prodBucket  + " to " + testBucket + " ...");
        logger.info("File Item's createdTime is greater than or equal: " + sdf.format(startDate));
        logger.info("File Item's createdTime is less than or equal: " + sdf.format(endDate));

        List<FileItem> fileItems = fileItemDao.findByDateTime(startDate, endDate);
        
        logger.info("Total file items: " + fileItems.size());
        
        try {
            List<String> result = storageService.copyFiles(fileItems, prodBucket, testBucket);
            logger.info("Copy/handle file itmes: " + result.size());
            //for (String tmpString : result)
            //    logger.info(tmpString);  
        } catch (ServiceException e) {
            logger.error("Fail to copy files", e);
        }
               
        logger.info("Finish");
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
    
    private Date addDay(Date d, int amount) {
        Calendar c = Calendar.getInstance(); 
        c.setTime(d);
        c.add(Calendar.DATE, amount);
        return c.getTime();
    }
}
