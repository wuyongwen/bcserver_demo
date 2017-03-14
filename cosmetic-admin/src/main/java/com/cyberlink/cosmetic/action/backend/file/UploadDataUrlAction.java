package com.cyberlink.cosmetic.action.backend.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.utils.colorthief.ColorThief;

@UrlBinding("/file/upload-dataurl.action")
public class UploadDataUrlAction extends AbstractAction {

    @SpringBean("file.fileService")
    private FileService fileService;
    
    private static final String loginMessage = "You need to login";
    private static final String wrongParamsMessage = "Invalid upload data";
    private static final String failedToUpload = "Failed to upload file";
    
    private String dataUrl = null;
    private String metadata = "";
    private FileType fileType = FileType.Photo;
    
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
    
    static {
		// Run once to load external ImageIO plugin.
        ImageIO.scanForPlugins();
    }
    
    @DefaultHandler
    public Resolution uploadByDataUrl() {

        if(getCurrentUserId() == null) {
        	String response = loginMessage;
        	return json(response);
        }
        
        if(dataUrl == null || metadata.equals("")) {
            return new ErrorResolution(400, wrongParamsMessage);
        }

        FileItem fileItem = null;
        try {
        	// get dominant color
        	String encodingPrefix = "base64,";
        	int contentStartIndex = dataUrl.indexOf(encodingPrefix) + encodingPrefix.length();
        	byte[] imageData = Base64.decodeBase64(dataUrl.substring(contentStartIndex));
        	BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imageData));
        	String rgbHexString;
        	int[] rgb = ColorThief.getColor(bufImg);
        	if(rgb != null)
        	{
        		rgbHexString = ColorThief.createRGBHexString(rgb);
        	}else{
        		rgbHexString = "#FFFFFF";
        	}
        	metadata = metadata.substring(0, metadata.length()-1) + String.format(",\"dominantedColor\":\"%s\"", rgbHexString.toUpperCase()) + "}";
        	
            fileItem = fileService.createImageFile(getCurrentUserId(), dataUrl, metadata, fileType);
            
        } catch (InvalidMetadataException | InvalidFileTypeException | IOException e) {
            logger.error("", e);
            return new StreamingResolution("text/html", failedToUpload);
        }
        
        if(fileItem == null)
            return new StreamingResolution("text/html", "");
        else {
            String response = String.format("{\"fileId\" : \"%d\", \"fileType\" : \"%s\", \"originalUrl\" : \"%s\", \"metadata\" :%s}", fileItem.getFile().getId(), fileType, fileItem.getOriginalUrl(), fileItem.getMetadata());
            return json(response);
        }
        
        
    }
}
