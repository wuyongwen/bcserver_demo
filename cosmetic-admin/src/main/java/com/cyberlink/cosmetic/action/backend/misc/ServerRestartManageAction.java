package com.cyberlink.cosmetic.action.backend.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.service.ArticleData;
import com.cyberlink.cosmetic.modules.post.service.AutoPostService.PostTask;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Admin.UserEvent;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/misc/serverRestartManage.action")
public class ServerRestartManageAction extends AbstractAction{

	
    @SpringBean("user.AttributeDao")
    private AttributeDao attributeDao;
    
    public static Long restartServerTime = null;
    
	private String regionSel;
	private String datetimepicker;
    
	@DefaultHandler
	public Resolution route() {
	    if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		return forward();
    }
    
    
	public Resolution post() {
	    if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
	    
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date startTime = null;
		if (datetimepicker == null || datetimepicker.isEmpty())
			return new StreamingResolution("text/html","Please select server restart time");
		try {
			startTime = sdf.parse(datetimepicker);
			restartServerTime = startTime.getTime();
			if (startTime.compareTo(new Date()) < 0) {
				return new StreamingResolution("text/html","Start Time expired");
			}
		} catch (ParseException e) {
			return new StreamingResolution("text/html", "Invalid server restart time");
		}
		
		
		try {
			FileWriter file;
			String filePath = Constants.getRestartTimePath();
			File f = new File(filePath);
			if (!f.exists()) {
				f.mkdirs();
			}
			file = new FileWriter(filePath + "serverRestartTime.txt");
			file.write(datetimepicker);
			file.flush();
			file.close();
		} catch (Exception e) {
			return new StreamingResolution("text/html", "Set server restart time fail:"+e.getMessage());
		}
		return new StreamingResolution("text/html", "Set server restart time success");
	}
    
	public Resolution clear() {
		ServerRestartManageAction.restartServerTime = null;
		String filePath = Constants.getRestartTimePath();
		try{
		File file = new File(filePath+ "serverRestartTime.txt");
		if(!file.delete())
			return new StreamingResolution("text/html", "Delete server restart time operation is failed.Path:"+filePath+ "serverRestartTime.txt");
		}catch(Exception e){
			return new StreamingResolution("text/html", "Delete server restart time operation is failed.Path:"+filePath+ "serverRestartTime.txt " + e.getMessage());
		}
		return new StreamingResolution("text/html", "Clear server restart time success");
	}
	

	public String getRegionSel() {
		return regionSel;
	}
	public void setRegionSel(String regionSel) {
		this.regionSel = regionSel;
	}
	
	public String getDatetimepicker() {
		return datetimepicker;
	}
	public void setDatetimepicker(String datetimepicker) {
		this.datetimepicker = datetimepicker;
	}
	
	public String getServerTime(){
		return String.valueOf((new Date()).getTime());
	}

	public Date getRestartServerTimeSetting() {
		if(ServerRestartManageAction.restartServerTime != null)
			return new Date(ServerRestartManageAction.restartServerTime);
		else
			return null;
	}
}

