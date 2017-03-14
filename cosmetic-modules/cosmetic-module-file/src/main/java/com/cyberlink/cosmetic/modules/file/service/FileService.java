package com.cyberlink.cosmetic.modules.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.stripes.action.FileBean;

import org.jets3t.service.ServiceException;

import com.cyberlink.cosmetic.modules.file.exception.InvalidFileException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FileService {
    
    FileItem createImageFile(Long userId, String dataUrl, String metadata, FileType fileType) throws InvalidMetadataException, InvalidFileTypeException, IOException;

    FileItem createFile(Long userId, FileBean fileBean, String metadata, FileType fileType, boolean forceMd5Check) throws InvalidFileException, IOException;
    
    FileItem createThumbnail(FileItem originalItem, Integer width, Integer height) throws NoSuchAlgorithmException, IOException, ServiceException;
    
    FileItem createThumbnail(Long fileId, ThumbnailType thumbnailType, boolean deleteSourceFile) throws NoSuchAlgorithmException, IOException, ServiceException;
    
    FileItem uploadToS3(Long userId, FileBean fileBean, String metadata, FileType fileType, boolean forceMd5Check) throws InvalidFileException, IOException;
    
    String uploadRawToS3(Long userId, FileBean fileBean, FileType fileType) throws InvalidFileException, IOException;
    
    FileItem createBcFile(Long userId, FileType fileType, String metadata, String fileName, String filePath, Long fileSize, String contentType, String md5, Integer width, Integer height) throws InvalidFileException, IOException;
    
    FileItem updateRedirectUrl(Long fileId, String redirectUrl, Boolean isWidget) throws JsonProcessingException;
    
    /**
     * delete the file in temp-storage and also update IS_DELETED = true
     * @return delete details
     * */
    ArrayList<String> deleteFile(FileItem fileItem) throws ServiceException;
    
    String getFilePath(Long id, FileType fileType, String fileName);
    
    HashMap<String, Object> getMd5(InputStream is);
}
