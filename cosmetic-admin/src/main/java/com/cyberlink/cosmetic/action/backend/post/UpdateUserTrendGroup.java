package com.cyberlink.cosmetic.action.backend.post;

import java.io.File;
import java.util.Calendar;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.service.PsTrendScheduleService;

@UrlBinding("/post/update-user-trend-group.action")
public class UpdateUserTrendGroup extends AbstractAction {
    
    @SpringBean("backend.psTrendScheduleServiceImpl")
    private PsTrendScheduleService psTrendScheduleService;
    
    private FileBean dataFile;
    
    @Validate(required = true, on = "route")
    public void setDataFile(FileBean dataFile) {
        this.dataFile = dataFile;
    }
    
    @DefaultHandler
    public Resolution route() {
        File updateData;
        try {
            updateData = createUpdateTrendGroupFile();
        } catch (Exception e) {
            return new ErrorResolution(400, e.getMessage());
        }
        psTrendScheduleService.updateUsrGroupFromFile(updateData);
        return json("Complete");
    }
    
    private File createUpdateTrendGroupFile() throws Exception {
        Calendar cal = Calendar.getInstance();
        String docDir = Constants.getPostUpdateTrendGroupPath();
        if(docDir == null)
            return null;
        docDir += "/" + String.format("%04d%02d%02d_%02d%02d", 
                cal.get(Calendar.YEAR), 
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DATE),
                cal.get(Calendar.HOUR_OF_DAY),
                (int)Math.floor(cal.get(Calendar.MINUTE)/30) * 30);
        File dDocDir = new File(docDir);

        if (dDocDir.exists()) {
            Exception e = new Exception("Update document already exist.");
            logger.error("", e );
            throw e;
        }
        
        if(!dDocDir.mkdirs()) {
            Exception e = new Exception("Failed to create directory :" + dDocDir); 
            logger.error("", e );
            throw e;
        }
        
        String filePath = docDir + "/" + String.valueOf("orginal.dat");
        File docFile = new File(filePath);        
        dataFile.save(docFile);
        return docFile;
    }
}
