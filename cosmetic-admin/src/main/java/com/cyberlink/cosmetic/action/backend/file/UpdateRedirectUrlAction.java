package com.cyberlink.cosmetic.action.backend.file;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;

@UrlBinding("/file/update-redirect-url.action")
public class UpdateRedirectUrlAction extends AbstractAction {

    @SpringBean("file.fileService")
    private FileService fileService;
    
    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    private static final String errorMessage = "You aren't an administrator";
    private static final String wrongParamsMessage = "Invalid update data";
    private static final String failedToUpdate = "Failed to update file";
    
    private Long fileId;
    private String redirectUrl = null;
    private FileType fileType = FileType.Photo;
    private Boolean isWidget = Boolean.FALSE;
    
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
    
    public void setRedirectUrl(String redirectUrl) {
    	if (redirectUrl == null)
    		this.redirectUrl = "";
    	else
    		this.redirectUrl = redirectUrl;
    }
    
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
    
    public void setIsWidget(Boolean isWidget) {
		this.isWidget = isWidget;
	}

	@DefaultHandler
    public Resolution route() {
        
        if(getCurrentUserId() == null) {
            return new StreamingResolution("text/html", errorMessage);
        }
        
        if(fileId == null) {
            return new ErrorResolution(400, wrongParamsMessage);
        }

        FileItem fileItem = null;
        
        if(redirectUrl == null) {
        	fileItem = fileItemDao.findOriginal(fileId);
        }
        else {
            try {
                fileItem = fileService.updateRedirectUrl(fileId, redirectUrl, isWidget);
                
            } catch (JsonProcessingException e) {
                logger.error("", e);
                return new StreamingResolution("text/html", failedToUpdate);
            }
        }

        if(fileItem == null)
            return new StreamingResolution("text/html", "");
        else {
            String response = String.format("{\"fileId\" : \"%d\", \"fileType\" : \"%s\", \"metadata\" :%s}", fileItem.getFile().getId(), fileType, fileItem.getMetadata());
            return json(response);
        }
    }
}
