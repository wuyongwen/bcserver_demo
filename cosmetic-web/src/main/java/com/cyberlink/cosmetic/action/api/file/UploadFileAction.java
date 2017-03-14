package com.cyberlink.cosmetic.action.api.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.action.api.validation.FileTypeConverter;
import com.cyberlink.cosmetic.action.api.validation.JsonConverter;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;

@UrlBinding("/api/file/upload-file.action")
public class UploadFileAction extends AbstractAction { 
    @SpringBean("file.fileService")
    private FileService fileService;
          
    private FileType fileType;
    
    private FileBean fileBean;
    
    private String metadata;

    private String fileName;
    
    private String filePath;
    
    private Long fileSize;
    
    private String contentType;
    
    private String md5;
    
    private Integer width;
    
    private Integer height;
    
    
    @Override
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }

    public FileType getFileType() {
        return fileType;
    }

    @Validate(required = true, converter = FileTypeConverter.class, on = "route")
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public FileBean getFileBean() {
        return fileBean;
    }

    @Validate(required = true, on = "route")
    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    public String getMetadata() {
        return metadata;
    }

    @Validate(required = true, converter = JsonConverter.class, on = "route")
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Resolution twoStepUpload_2() throws IOException {
        if (!authenticateByRedis())
            return new ErrorResolution(authError);
        
        FileItem fileItem = fileService.createBcFile(getCurrentUserId(), fileType, metadata, fileName, filePath, fileSize, contentType, md5, width, height);
        if (fileItem != null)
            return json("fileId", fileItem.getFile().getId());
        else
            return error();
    }
    
    @SuppressWarnings("unchecked")
    public Resolution twoStepUpload_1() throws IOException {
        if (!authenticateByRedis())
            return new ErrorResolution(authError);
        
        if (fileType == null) {
            deleteFile();
            return new ErrorResolution(ErrorDef.InvalidFileType);
        }
            
        if (metadata == null) {
            deleteFile();
            return new ErrorResolution(ErrorDef.InvalidMetadata);
        }
            
        FileItem fileItem;
        try {
            fileItem = fileService.uploadToS3(getCurrentUserId(), fileBean, metadata, fileType, true);
        } catch (InvalidFileException e) {
            return new ErrorResolution(ErrorDef.InvalidFile);
        }
        
        if (fileItem != null) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.putAll(getContext().getRequest().getParameterMap());
            parameters.put("twoStepUpload_2", "");
            parameters.put("fileName", fileItem.getFileName());
            parameters.put("filePath", fileItem.getFilePath());
            parameters.put("fileSize", fileItem.getFileSize());
            parameters.put("contentType", fileItem.getContentType());
            parameters.put("md5", fileItem.getMd5());
            parameters.put("width", fileItem.getWidth());
            parameters.put("height", fileItem.getHeight());
            HttpServletRequest request = getContext().getRequest();
            String url = request.getScheme() + "://" + Constants.getWebsiteWrite() + request.getRequestURI(); 
            return new RedirectResolution(url).addParameters(parameters);
        }
        else
            return error();
    }
    
    public Resolution uploadRawFile() throws IOException {       
    	if (!authenticateByRedis())
            return new ErrorResolution(authError);
                
        if (!FileType.Raw.equals(fileType)) {
            deleteFile();
            return new ErrorResolution(ErrorDef.InvalidFileType);
        }
        
        String originalUrl = "";
        try {
        	originalUrl = fileService.uploadRawToS3(getCurrentUserId(), fileBean, fileType);
        } catch (InvalidFileException e) {
            return new ErrorResolution(ErrorDef.InvalidFile);
        }
        
        if (originalUrl != null && !originalUrl.isEmpty()) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("fileId", null);
            resultMap.put("originalUrl", originalUrl);
            return json(resultMap);
        }
        else
            return error();
    }
    
    @DefaultHandler
    public Resolution route() throws IOException {
    	if (FileType.Raw.equals(fileType)) {
    		return uploadRawFile();
    	}
    	
        if(!Constants.getWebsiteIsWritable().equals("true")) {
            return twoStepUpload_1();
        }
        
    	if (!authenticateByRedis())
            return new ErrorResolution(authError);
                
        if (fileType == null) {
            deleteFile();
            return new ErrorResolution(ErrorDef.InvalidFileType);
        }
            
        if (metadata == null) {
            deleteFile();
            return new ErrorResolution(ErrorDef.InvalidMetadata);
        }
            
        FileItem fileItem;
        try {
            fileItem = fileService.createFile(getCurrentUserId(), fileBean, metadata, fileType, true);
        } catch (InvalidFileException e) {
            return new ErrorResolution(ErrorDef.InvalidFile);
        }
        
		if (fileItem != null) {      	
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("fileId", fileItem.getFile().getId());
            resultMap.put("originalUrl", fileItem.getOriginalUrl());
            return json(resultMap);
        }
        else
            return error();
    }
          
    private void deleteFile() {
        try {
            fileBean.delete();
        } catch (IOException e) {
            logger.error("", e);
        }
    }
}