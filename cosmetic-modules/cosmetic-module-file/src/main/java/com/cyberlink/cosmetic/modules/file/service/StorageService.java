package com.cyberlink.cosmetic.modules.file.service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.jets3t.service.ServiceException;

import com.cyberlink.cosmetic.modules.file.model.FileItem;

public interface StorageService {
   
    void uploadFile(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException;
    
    String uploadFile(File ioFile, String path, String mimeType) throws NoSuchAlgorithmException, IOException, ServiceException;
    
    String uploadRawFile(File ioFile, String path, String mimeType) throws NoSuchAlgorithmException, IOException, ServiceException;
    
    void downloadFile(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException;
    
    void deleteFile(FileItem fileItem) throws ServiceException;
    
    void setAclPublic(FileItem fileItem) throws ServiceException;
    
    void setAclPrivate(FileItem fileItem) throws ServiceException;
    
    /**
     * copy files from source bucket to target bucket. keep the metadata of the source object and make it public.
     * @return copy details
     * */
    List<String> copyFiles(List<FileItem> fileItems, String sourceBucket, String targetBucket) throws ServiceException;
}
