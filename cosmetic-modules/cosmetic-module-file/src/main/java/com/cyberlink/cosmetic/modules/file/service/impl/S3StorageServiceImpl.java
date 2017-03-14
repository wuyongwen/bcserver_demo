package com.cyberlink.cosmetic.modules.file.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.service.StorageService;

public class S3StorageServiceImpl extends AbstractService implements
        StorageService {

    private RestS3Service s3Service;
    
    private String bucket;

    public void setS3Service(RestS3Service s3Service) {
        this.s3Service = s3Service;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void uploadFile(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException {
       S3Object object = new S3Object(fileItem.getFilePath());
       
       object.setDataInputFile(new File(fileItem.getLocalFilePath())); 
       object.setContentLength(fileItem.getFileSize());
       object.setContentType(fileItem.getContentType());   
       
       object.addMetadata("Cache-Control", "max-age=31536000");
       
       AccessControlList acl = s3Service.getBucketAcl(bucket);
       acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
       object.setAcl(acl);
       
       if (fileItem.getMd5Bytes() != null)
           object.setMd5Hash(fileItem.getMd5Bytes());
       
       s3Service.putObject(bucket, object); 
    }

    public String uploadFile(File ioFile, String path, String mimeType) throws NoSuchAlgorithmException, IOException, ServiceException {
        S3Object object = new S3Object(path + ioFile.getName());
        object.setDataInputFile(ioFile); 
        object.setContentLength(ioFile.length());
        object.setContentType(mimeType);   
        
        object.addMetadata("Cache-Control", "max-age=31536000");
        
        AccessControlList acl = s3Service.getBucketAcl(bucket);
        acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        object.setAcl(acl);
        s3Service.putObject(bucket, object); 
        String cdnDomain = Constants.getBcCdnDomain();
        if(cdnDomain == null || cdnDomain.length() <= 0)
            cdnDomain = Constants.getCdnDomain();
        return "http://" + cdnDomain + "/" + path + ioFile.getName(); 
     }
    
    public String uploadRawFile(File ioFile, String path, String mimeType) throws NoSuchAlgorithmException, IOException, ServiceException {
        S3Object object = new S3Object(path);
        object.setDataInputFile(ioFile); 
        object.setContentLength(ioFile.length());
        object.setContentType(mimeType);   
        
        object.addMetadata("Cache-Control", "max-age=31536000");
        
        AccessControlList acl = s3Service.getBucketAcl(bucket);
        acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        object.setAcl(acl);
        s3Service.putObject(bucket, object); 
        String cdnDomain = Constants.getBcCdnDomain();
        if(cdnDomain == null || cdnDomain.length() <= 0)
            cdnDomain = Constants.getCdnDomain();
        return "http://" + cdnDomain + "/" + path; 
     }
    
    public void downloadFile(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException {
        S3Object object = s3Service.getObject(bucket, fileItem.getFilePath());        
        InputStream is = object.getDataInputStream();

        File targetFile = new File(fileItem.getLocalFilePath());
        targetFile.getParentFile().mkdirs();
        FileUtils.copyInputStreamToFile(is, targetFile);
        if (is != null) is.close();
        
        is = new FileInputStream(targetFile); 
        if (!object.verifyData(is)) {
            if (is != null) is.close();
            targetFile.delete(); 
        }
       
        if (is != null) is.close();
        object.closeDataInputStream();
        object = null;
    }

    public void deleteFile(FileItem fileItem) throws ServiceException {
        s3Service.deleteObject(bucket, fileItem.getFilePath());
    }

    public void setAclPublic(FileItem fileItem) throws ServiceException {
        AccessControlList acl = s3Service.getBucketAcl(bucket);
        acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        s3Service.putObjectAcl(bucket, fileItem.getFilePath(), acl);
    }

    public void setAclPrivate(FileItem fileItem) throws ServiceException {
        AccessControlList acl = s3Service.getBucketAcl(bucket);
        acl.revokeAllPermissions(GroupGrantee.ALL_USERS);
        s3Service.putObjectAcl(bucket, fileItem.getFilePath(), acl);
    }

    public List<String> copyFiles(List<FileItem> fileItems, String sourceBucket,
            String targetBucket) throws ServiceException {      
        AccessControlList acl = s3Service.getBucketAcl(sourceBucket);
        acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        List<String> result = new ArrayList<String>();
        int index = 1;
        
        for (FileItem fileItem : fileItems) {
            String objectKey = fileItem.getFilePath();
            S3Object[] targetObjects = s3Service.listObjects(targetBucket, objectKey, null);
            
            if (targetObjects.length == 0) {         
                S3Object[] sourceObjects = s3Service.listObjects(sourceBucket, objectKey, null);    
                if (sourceObjects.length == 1) {
                    S3Object copyObject = new S3Object(sourceObjects[0].getKey());    
                    copyObject.setAcl(acl);
                    s3Service.copyObject(sourceBucket, sourceObjects[0].getKey(), targetBucket, copyObject, false);
                    
                    result.add(index + ". copy to target bucket: " + sourceObjects[0].getKey());
                } else {
                    result.add(index + ". not found in source bucket: FileItemId: " + fileItem.getId() + ", key: " + objectKey);
                }
                index++;
            }
        }
        return result;
    }
}
