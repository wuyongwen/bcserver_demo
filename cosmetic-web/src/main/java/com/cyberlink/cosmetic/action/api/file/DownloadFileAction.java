package com.cyberlink.cosmetic.action.api.file;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HttpCache;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.jets3t.service.ServiceException;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.StorageService;

@UrlBinding("/api/file/download-file.action")
public class DownloadFileAction extends AbstractAction {
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    @SpringBean("file.fileService")
    private FileService fileService;
    
    @SpringBean("file.storageService")
    private StorageService storageService;
    
    private Long fileId;
    
    private Long fileItemId;
    
    private Integer width;
    
    private Integer height;
    
    private ThumbnailType thumbnailType;
    
    private Long originalFileItemId;
    
    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getFileItemId() {
        return fileItemId;
    }

    public void setFileItemId(Long fileItemId) {
        this.fileItemId = fileItemId;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setThumbnailType(ThumbnailType thumbnailType) {
        this.thumbnailType = thumbnailType;
    }
    
    public void setOriginalFileItemId(Long originalFileItemId) {
        this.originalFileItemId = originalFileItemId;
    }
    
    @HttpCache(expires=31536000)
    public Resolution getFile() {
        Long [] ids = new Long[1];
        ids[0] = fileId;
        List<FileItem> listResults = fileItemDao.findThumbnails(ids, thumbnailType);
        if(listResults.size() > 0)
            return new RedirectResolution(listResults.get(0).getOriginalUrl());
        return new net.sourceforge.stripes.action.ErrorResolution(404, "fileId or fileItemId Not Found");
    }
    
    public Resolution createThumbnail() {
        FileItem originalItem = fileItemDao.findById(originalFileItemId);
        if(originalItem == null)
            return new net.sourceforge.stripes.action.ErrorResolution(404, "File Not Found");
        FileItem fileItem = null;
        try {
            fileItem = fileService.createThumbnail(originalItem, width, height);
        } catch (NoSuchAlgorithmException | IOException | ServiceException e) {
            e.printStackTrace();
        }
        
        if(fileItem == null)
            return new net.sourceforge.stripes.action.ErrorResolution(404, "File Not Found");
        return new RedirectResolution(fileItem.getOriginalUrl());
    }
    
    @DefaultHandler
    @HttpCache(expires=31536000)
    public Resolution route() throws NoSuchAlgorithmException, IOException, ServiceException {
        if (fileId == null && fileItemId == null)
            return new net.sourceforge.stripes.action.ErrorResolution(404, "fileId or fileItemId Not Found");
            
        FileItem fileItem = null;
        
        if (fileItemId != null) {
            
            if (!fileItemDao.exists(fileItemId))
                return new ErrorResolution(ErrorDef.InvalidFileItemId);
            
            fileItem = fileItemDao.findById(fileItemId);
        
        } else if (fileId != null) {
            
            if (!fileDao.exists(fileId))
                return new ErrorResolution(ErrorDef.InvalidFileId);

            File fileEntity = fileDao.findById(fileId);
            FileItem originalItem = null;
                          
            if (width != null && height != null && fileEntity.getFileType().isSupportResize()) {
                for (FileItem item : fileEntity.getFileItems()) {
                    if (item.getWidth().equals(width) && item.getHeight().equals(height)) {
                        fileItem = item;
                        break;
                    }
                    if (item.getIsOriginal())
                        originalItem = item;
                }
                
                if (fileItem == null && originalItem != null) {
                    if(!Constants.getWebsiteIsWritable().equals("true")) {
                        return new RedirectResolution("http://" + Constants.getWebsiteWrite() + "/api/file/download-file.action")
                        .addParameter("createThumbnail", "")
                        .addParameter("originalFileItemId", originalItem.getId())
                        .addParameter("width", width)
                        .addParameter("height", height);
                    }
                    else {
                        fileItem = fileService.createThumbnail(originalItem, width, height);
                    }
                }
            } else {
                fileItem = fileItemDao.findOriginal(fileId);
            }
        }
        
        if (fileItem != null)
            return new RedirectResolution(fileItem.getOriginalUrl());

        return new net.sourceforge.stripes.action.ErrorResolution(404, "File Not Found");
    }
}
