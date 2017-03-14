package com.cyberlink.cosmetic.modules.file.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.sourceforge.stripes.action.FileBean;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jets3t.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.event.flie.FileBosUploadEvent;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.ImageService;
import com.cyberlink.cosmetic.modules.file.service.OSSService;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.file.utils.MimeTypeFileTypeMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FileServiceImpl extends AbstractService implements FileService {    

    private ImageService imageService;
    
    private StorageService storageService;
    
    private OSSService ossService;
    
    private FileDao fileDao;
    
    private FileItemDao fileItemDao;
    
    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void setOssService(OSSService ossService) {
		this.ossService = ossService;
	}

	public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    public void setFileItemDao(FileItemDao fileItemDao) {
        this.fileItemDao = fileItemDao;
    }

    public FileItem createThumbnail(FileItem originalItem, Integer width, Integer height) throws NoSuchAlgorithmException, IOException, ServiceException {       
        FileItem fileItem = null;
        File originalFile = new File(originalItem.getLocalFilePath());
        
        if (!originalFile.exists()) {
        	downloadFile(originalItem);
        }
        
        if (originalFile.exists()) {
            fileItem = new FileItem();
            fileItem.setShardId(originalItem.getFile().getUserId());
            fileItem.setFile(originalItem.getFile());
            fileItem.setFilePath(getFilePath(originalItem.getFile().getUserId(), originalItem.getFile().getFileType(), originalItem.getFileName()));
            fileItem.setFileName(FilenameUtils.getName(fileItem.getFilePath()));
            fileItem.setContentType(originalItem.getContentType());
            fileItem.setWidth(width);
            fileItem.setHeight(height);
            fileItem.setOrientation(originalItem.getOrientation());
            fileItem.setIsOriginal(false);
            
            File newFile = new File(fileItem.getLocalFilePath());
            newFile.getParentFile().mkdirs();  
            imageService.thumbnail(originalItem, width, height, fileItem);
            deleteFile(originalFile);
            
            if (newFile.exists() && !newFile.isDirectory()) {
                fileItem.setFileSize(newFile.length());
                
                HashMap<String, Object> md5Result = getMd5(new FileInputStream(newFile));
                if (md5Result.size() > 0) {
                    fileItem.setMd5Bytes((byte[]) md5Result.get("md5Bytes"));
                    fileItem.setMd5((String) md5Result.get("md5"));
                }
                
                ObjectNode objectNode = (ObjectNode) originalItem.getMetadataJson();
                objectNode.put("fileSize", fileItem.getFileSize());
                objectNode.put("md5", fileItem.getMd5());
                objectNode.put("width", fileItem.getWidth());
                objectNode.put("height", fileItem.getHeight());
                if (fileItem.getOrientation() != null)
                    objectNode.put("orientation", fileItem.getOrientation());
                
                ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
                fileItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(objectNode));
                
                try {
                	uploadFile(fileItem);
                } catch (Exception e) {
                    logger.error("", e);
                    fileItem = null;
                    deleteFile(newFile);
                }
                
                if (fileItem != null) {
                	// Upload to BOS
                	uploadToBOS(fileItem);
                	
                    fileItem = fileItemDao.create(fileItem);
                    deleteFile(newFile);
                }
            } else {
                fileItem = null;
                deleteFile(newFile.getParentFile());
            }
        }
        
        return fileItem;
    }
    
    public FileItem createThumbnail(Long fileId, ThumbnailType thumbnailType, boolean deleteSourceFile) throws NoSuchAlgorithmException, IOException, ServiceException {       
        if (!fileDao.exists(fileId))
            return null;

        if (fileItemDao.exists(fileId, thumbnailType))
            return fileItemDao.findByFileIdAndThumbnailType(fileId, thumbnailType);
        
        FileItem originalItem = fileItemDao.findOriginal(fileId);
        if (!originalItem.getFile().getFileType().isSupportResize())
            return null;
        
        if (originalItem.getWidth() == null && originalItem.getHeight() == null)
            return null;
        
        if (thumbnailType.width() != null && thumbnailType.height() != null && originalItem.getWidth() <= thumbnailType.width())
            return originalItem;
        
        File originalFile = new File(originalItem.getLocalFilePath());
        
        if (!originalFile.exists())
        	downloadFile(originalItem);
        
        if (!originalFile.exists())
            return null;
                
        FileItem newItem = new FileItem();
        newItem.setShardId(originalItem.getFile().getUserId());
        newItem.setFile(originalItem.getFile());
        newItem.setFilePath(getFilePath(originalItem.getFile().getUserId(), originalItem.getFile().getFileType(),".jpg"));
        newItem.setFileName(FilenameUtils.getName(newItem.getFilePath()));
        newItem.setContentType(MediaType.IMAGE_JPEG_VALUE);
        newItem.setOrientation(originalItem.getOrientation());
        newItem.setIsOriginal(false);
        newItem.setThumbnailType(thumbnailType);
        
        File newFile = new File(newItem.getLocalFilePath());
        newFile.getParentFile().mkdirs();  
        imageService.thumbnail(originalItem, thumbnailType, newItem);
        if (deleteSourceFile)
            deleteFile(originalFile);
        
        if (newFile.exists() && !newFile.isDirectory()) {
            newItem.setFileSize(newFile.length());
            
            HashMap<String, Integer> dimension = getImageDimension(new FileInputStream(newFile));
            if (dimension != null) {
                newItem.setWidth(dimension.get("width"));
                newItem.setHeight(dimension.get("height"));
            }
            
            HashMap<String, Object> md5Result = getMd5(new FileInputStream(newFile));
            if (md5Result.size() > 0) {
                newItem.setMd5Bytes((byte[]) md5Result.get("md5Bytes"));
                newItem.setMd5((String) md5Result.get("md5"));
            }
            
            ObjectNode objectNode = (ObjectNode) originalItem.getMetadataJson();
            objectNode.put("fileSize", newItem.getFileSize());
            objectNode.put("md5", newItem.getMd5());
            objectNode.put("width", newItem.getWidth());
            objectNode.put("height", newItem.getHeight());
            if (newItem.getOrientation() != null)
                objectNode.put("orientation", newItem.getOrientation());
            
            ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
            newItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(objectNode));
            
            try {
            	uploadFile(newItem);
            } catch (Exception e) {
                logger.error("", e);
                newItem = null;
                deleteFile(newFile);
            }
            
            if (newItem != null) {
            	// Upload to BOS
            	uploadToBOS(newItem);
            	
                newItem = fileItemDao.create(newItem);                
                deleteFile(newFile);
            }
        } else {
            newItem = null;
            deleteFile(newFile.getParentFile());
        }
        
        return newItem;
    }
    
    public String getFilePath(Long id, FileType fileType, String fileName) {
        final StringBuffer sb = new StringBuffer();
        final String extension = FilenameUtils.getExtension(fileName);
        if (fileType != null) {
        	if(fileType.equals(FileType.LookEffect))
        		sb.append("Look").append("/");
        	else if (fileType.equals(FileType.Raw))
        		sb.append("Raw").append("/");
        	else if (fileType.equals(FileType.Avatar)) {
        		sb.append("av").append("/").append(id).append("/").append(UUID.randomUUID()).append(".").append(extension);
            	return sb.toString();
        	}
        }
        sb.append(id % 1000).append("/").append(id).append("/").append(RandomUtils.nextInt(1000)).append("/")
                .append(UUID.randomUUID()).append(".").append(extension);
        return sb.toString();
    }

    public FileItem createImageFile(Long userId, String dataUrl, String metadata, FileType fileType) throws InvalidMetadataException, InvalidFileTypeException, IOException {
        final String base64Constant = ";base64,";
        final Integer index = dataUrl.indexOf(base64Constant);
        final String mimeType = dataUrl.substring(0, index).replace("data:", "");
        final String base64String = dataUrl.substring(index + base64Constant.length());
        final String fileExtension = MimeTypeFileTypeMap.getExtension(mimeType);
        Integer width = 0;
        Integer height = 0;
        
        if (fileExtension == null || metadata.trim().equals(""))
            throw new InvalidMetadataException();

        if (!fileType.isImage())
            throw new InvalidFileTypeException();
        
        ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
        JsonNode jsonNode = null;
        JsonNode tempNode;
        try {
            jsonNode = objectMapper.readValue(metadata, JsonNode.class);                              
            if (jsonNode != null) {
                tempNode = jsonNode.path("width");
                if (!tempNode.isMissingNode()) width = tempNode.asInt();
                
                tempNode = jsonNode.path("height");
                if (!tempNode.isMissingNode()) height = tempNode.asInt();
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new InvalidMetadataException();
        }

        if (width == 0 || height == 0)
            throw new InvalidMetadataException();
        
        
        final byte[] data = Base64.decodeBase64(base64String);
        
        com.cyberlink.cosmetic.modules.file.model.File fileEntity = new com.cyberlink.cosmetic.modules.file.model.File();    
        fileEntity.setUserId(userId);
        fileEntity.setFileType(fileType);
        
        FileItem fileItem = new FileItem();
        fileItem.setShardId(userId);
        fileItem.setFile(fileEntity);
        fileItem.setFilePath(getFilePath(userId, fileType, "." + fileExtension));
        fileItem.setFileName(FilenameUtils.getName(fileItem.getFilePath()));
        fileItem.setContentType(mimeType);
        fileItem.setWidth(width);
        fileItem.setHeight(height);
        fileItem.setOrientation(1);
        fileItem.setIsOriginal(true);
        
        fileEntity.getFileItems().add(fileItem);
        
        File file = new File(fileItem.getLocalFilePath());
        file.getParentFile().mkdirs();
        OutputStream stream = new FileOutputStream(file);
        stream.write(data);       
        if (stream != null) stream.close();
       
        if (file.exists() && !file.isDirectory()) {
            fileItem.setFileSize(file.length());
            
            HashMap<String, Object> md5Result = getMd5(new FileInputStream(file));
            if (md5Result.size() > 0) {
                fileItem.setMd5Bytes((byte[]) md5Result.get("md5Bytes"));
                fileItem.setMd5((String) md5Result.get("md5"));
            }
            
            if (fileType.isImage()) {
                HashMap<String, Integer> dimension = getImageDimension(new FileInputStream(file));
                if (dimension != null) {
                    fileItem.setWidth(dimension.get("width"));
                    fileItem.setHeight(dimension.get("height"));
                }            
            }
                       
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.put("fileSize", fileItem.getFileSize());
            objectNode.put("md5", fileItem.getMd5());
            if (fileItem.getWidth() != null) objectNode.put("width", fileItem.getWidth());
            if (fileItem.getHeight() != null) objectNode.put("height", fileItem.getHeight());
            objectNode.put("orientation", fileItem.getOrientation());
            
            fileItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(jsonNode));
            
            try {
            	uploadFile(fileItem);
            } catch (Exception e) {
                logger.error("", e);
                fileItem = null;
                deleteFile(file);
            }
            
            if (fileItem != null) {
            	// Upload to BOS
            	uploadToBOS(fileItem);
            	
                fileEntity = fileDao.create(fileEntity);
                
                // TODO put in the queue
                try {
                    createThumbnails(fileItem);
                } catch (NoSuchAlgorithmException | ServiceException e) {
                    logger.error("", e);
                }
                
                deleteFile(file);
            }
        } else {
            fileItem = null;
            deleteFile(file.getParentFile());
        }
       
        return fileItem;
    }
    
    public FileItem uploadToS3(Long userId, FileBean fileBean, String metadata, FileType fileType, boolean forceMd5Check) throws InvalidFileException, IOException {
        if (fileBean.getSize() == 0) {
            fileBean.delete();
            throw new InvalidFileException();
        }
        
        ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
        JsonNode jsonNode = null;
        JsonNode tempNode = null;
        String metadataMd5 = "";
        if (!StringUtils.isBlank(metadata)) {
            jsonNode = objectMapper.readValue(metadata, JsonNode.class);      
            if (jsonNode != null) {
                tempNode = jsonNode.path("md5");
                if (!tempNode.isMissingNode()) metadataMd5 = tempNode.asText();
            }
        }
        
        HashMap<String, Object> md5Result = getMd5(fileBean.getInputStream());
        
        if (forceMd5Check) { 
            if (metadataMd5.equals("") || !metadataMd5.equals((String) md5Result.get("md5"))) {
                fileBean.delete();
                throw new InvalidFileException();
            } 
        }
        
        String fileName = fileBean.getFileName();                        
        FileItem fileItem = new FileItem();
        fileItem.setShardId(userId);
        fileItem.setFilePath(getFilePath(userId, fileType, fileName));
        fileItem.setFileName(fileName);
        fileItem.setFileSize(fileBean.getSize());
        fileItem.setContentType(fileBean.getContentType());
        fileItem.setMd5Bytes((byte[]) md5Result.get("md5Bytes"));
        fileItem.setMd5((String) md5Result.get("md5"));
        fileItem.setIsOriginal(true);
        
        if (jsonNode != null) {
            tempNode = jsonNode.path("width");
            if (!tempNode.isMissingNode()) fileItem.setWidth(tempNode.asInt());
            
            tempNode = jsonNode.path("height");
            if (!tempNode.isMissingNode()) fileItem.setHeight(tempNode.asInt());
            
            tempNode = jsonNode.path("orientation");
            if (!tempNode.isMissingNode()) fileItem.setOrientation(tempNode.asInt());
        }
        
        if (fileType.isImage()) {
            HashMap<String, Integer> dimension = getImageDimension(fileBean.getInputStream());
            if (dimension != null) {
                fileItem.setWidth(dimension.get("width"));
                fileItem.setHeight(dimension.get("height"));
            }            
        }

        ObjectNode objectNode = (jsonNode != null ? (ObjectNode) jsonNode : objectMapper.createObjectNode());
        objectNode.put("fileSize", fileItem.getFileSize());
        objectNode.put("md5", fileItem.getMd5());     
        if (fileItem.getWidth() != null) objectNode.put("width", fileItem.getWidth());
        if (fileItem.getHeight() != null) objectNode.put("height", fileItem.getHeight());
        
        fileItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(objectNode));
        
        File file = new File(fileItem.getLocalFilePath());
        file.getParentFile().mkdirs();
        fileBean.save(file);
        
        try {
        	uploadFile(fileItem);
        } catch (Exception e) {
            logger.error("", e);
            fileItem = null;
            deleteFile(file);
        }
        
        if (fileItem != null) {
        	// Upload to BOS
        	uploadToBOS(fileItem);
        	
            deleteFile(file);
        }
        
        return fileItem;
    }
    
    public String uploadRawToS3(Long userId, FileBean fileBean, FileType fileType) throws InvalidFileException, IOException {
        /*if (fileBean.getSize() == 0) {
            fileBean.delete();
            throw new InvalidFileException();
        }
        
        String filePath = getFilePath(userId, fileType, fileBean.getFileName());
        String localFilePath = Constants.getStorageLocalRoot() + "/" + filePath;
        String mimeType = fileBean.getContentType();
        File file = new File(localFilePath);
        file.getParentFile().mkdirs();
        fileBean.save(file);
       
        try {
            return storageService.uploadRawFile(file, filePath, mimeType);
        } catch (IOException | NoSuchAlgorithmException | ServiceException e) {
            logger.error("", e);
        } finally {
        	deleteFile(file);
        }*/
        
        return "";
    }
    
    public FileItem createBcFile(Long userId, FileType fileType, String metadata, String fileName, String filePath, Long fileSize, String contentType, String md5, Integer width, Integer height) throws InvalidFileException, IOException {
        ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
        JsonNode jsonNode = null;
        JsonNode tempNode = null;
        if (!StringUtils.isBlank(metadata)) {
            jsonNode = objectMapper.readValue(metadata, JsonNode.class);      
        }         
        
        com.cyberlink.cosmetic.modules.file.model.File fileEntity = new com.cyberlink.cosmetic.modules.file.model.File();
        fileEntity.setUserId(userId);
        fileEntity.setFileType(fileType);
        
        FileItem fileItem = new FileItem();
        fileItem.setShardId(userId);
        fileItem.setFilePath(filePath);
        fileItem.setFileName(fileName);
        fileItem.setFileSize(fileSize);
        fileItem.setContentType(contentType);
        fileItem.setMd5Bytes(md5.getBytes());
        fileItem.setMd5(md5);
        fileItem.setIsOriginal(true);
        
        if (jsonNode != null) {
            tempNode = jsonNode.path("width");
            if (!tempNode.isMissingNode()) fileItem.setWidth(tempNode.asInt());
            
            tempNode = jsonNode.path("height");
            if (!tempNode.isMissingNode()) fileItem.setHeight(tempNode.asInt());
            
            tempNode = jsonNode.path("orientation");
            if (!tempNode.isMissingNode()) fileItem.setOrientation(tempNode.asInt());
        }
        
        if (fileType.isImage()) {
            if (width != null) {
                fileItem.setWidth(width);
            }          
            if (height != null) {
                fileItem.setHeight(height);
            }       
        }

        ObjectNode objectNode = (jsonNode != null ? (ObjectNode) jsonNode : objectMapper.createObjectNode());
        objectNode.put("fileSize", fileItem.getFileSize());
        objectNode.put("md5", fileItem.getMd5());     
        if (fileItem.getWidth() != null) objectNode.put("width", fileItem.getWidth());
        if (fileItem.getHeight() != null) objectNode.put("height", fileItem.getHeight());
        
        fileItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(objectNode));
        
        fileEntity.getFileItems().add(fileItem);
        fileItem.setFile(fileEntity);
        
        if (fileItem != null) {
            fileEntity = fileDao.create(fileEntity);
                       
            // TODO put in the queue
            try {
                createThumbnails(fileItem);
            } catch (NoSuchAlgorithmException | ServiceException e) {
                logger.error("", e);
            }
        }
        
        return fileItem;
    }
    
    public FileItem createFile(Long userId, FileBean fileBean, String metadata, FileType fileType, boolean forceMd5Check) throws InvalidFileException, IOException {
        if (fileBean.getSize() == 0) {
            fileBean.delete();
            throw new InvalidFileException();
        }
        
        ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
        JsonNode jsonNode = null;
        JsonNode tempNode = null;
        String metadataMd5 = "";
        if (!StringUtils.isBlank(metadata)) {
            jsonNode = objectMapper.readValue(metadata, JsonNode.class);      
            if (jsonNode != null) {
                tempNode = jsonNode.path("md5");
                if (!tempNode.isMissingNode()) metadataMd5 = tempNode.asText();
            }
        }
        
        HashMap<String, Object> md5Result = getMd5(fileBean.getInputStream());
        
        if (forceMd5Check) { 
            if (metadataMd5.equals("") || !metadataMd5.equals((String) md5Result.get("md5"))) {
                fileBean.delete();
                throw new InvalidFileException();
            } 
        }
        
        
        String fileName = fileBean.getFileName();                 
        
        com.cyberlink.cosmetic.modules.file.model.File fileEntity = new com.cyberlink.cosmetic.modules.file.model.File();
        fileEntity.setUserId(userId);
        fileEntity.setFileType(fileType);
        
        FileItem fileItem = new FileItem();
        fileItem.setShardId(userId);
        fileItem.setFilePath(getFilePath(userId, fileType, fileName));
        fileItem.setFileName(fileName);
        fileItem.setFileSize(fileBean.getSize());
        fileItem.setContentType(fileBean.getContentType());
        fileItem.setMd5Bytes((byte[]) md5Result.get("md5Bytes"));
        fileItem.setMd5((String) md5Result.get("md5"));
        fileItem.setIsOriginal(true);
        
        if (jsonNode != null) {
            tempNode = jsonNode.path("width");
            if (!tempNode.isMissingNode()) fileItem.setWidth(tempNode.asInt());
            
            tempNode = jsonNode.path("height");
            if (!tempNode.isMissingNode()) fileItem.setHeight(tempNode.asInt());
            
            tempNode = jsonNode.path("orientation");
            if (!tempNode.isMissingNode()) fileItem.setOrientation(tempNode.asInt());
        }
        
        if (fileType.isImage()) {
            HashMap<String, Integer> dimension = getImageDimension(fileBean.getInputStream());
            if (dimension != null) {
                fileItem.setWidth(dimension.get("width"));
                fileItem.setHeight(dimension.get("height"));
            }            
        }

        ObjectNode objectNode = (jsonNode != null ? (ObjectNode) jsonNode : objectMapper.createObjectNode());
        objectNode.put("fileSize", fileItem.getFileSize());
        objectNode.put("md5", fileItem.getMd5());     
        if (fileItem.getWidth() != null) objectNode.put("width", fileItem.getWidth());
        if (fileItem.getHeight() != null) objectNode.put("height", fileItem.getHeight());
        
        fileItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(objectNode));
        
        fileEntity.getFileItems().add(fileItem);
        fileItem.setFile(fileEntity);
        
        File file = new File(fileItem.getLocalFilePath());
        file.getParentFile().mkdirs();
        fileBean.save(file);
        
        try {
        	uploadFile(fileItem);
        } catch (Exception e) {
            logger.error("", e);
            fileItem = null;
            deleteFile(file);
        }
        
        if (fileItem != null) {
        	// Upload to BOS
        	uploadToBOS(fileItem);
        	
            fileEntity = fileDao.create(fileEntity);
                       
            // TODO put in the queue
            try {
                createThumbnails(fileItem);
            } catch (NoSuchAlgorithmException | ServiceException e) {
                logger.error("", e);
            }
            
            deleteFile(file);
        }
        
        return fileItem;
    }
    
    public FileItem updateRedirectUrl(Long fileId, String redirectUrl, Boolean isWidget) throws JsonProcessingException {
    	
        PageResult<FileItem> pageResult = fileItemDao.findByFileId(fileId, new BlockLimit(0, 100));
        List<FileItem> fileItems = pageResult.getResults();
        
        FileItem originalFileItem = fileItems.get(0);
        ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
        for (FileItem fileItem : fileItems) {
        	if (fileItem.getIsOriginal() == true)
        		originalFileItem = fileItem;
        	
        	ObjectNode objectNode = (ObjectNode) fileItem.getMetadataJson();
        	if (objectNode != null) {
        		objectNode.put("redirectUrl", redirectUrl);
        		objectNode.put("isWidget", isWidget);
        		fileItem.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(objectNode));
        		fileItemDao.update(fileItem);
        	}
        }
    	return originalFileItem;
    }
    
    public HashMap<String, Object> getMd5(InputStream is) {
        byte[] md5Bytes = null;
        String md5 = "";
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            md5Bytes = DigestUtils.md5(is);
            md5 = Hex.encodeHexString(md5Bytes);
            result.put("md5Bytes", md5Bytes);
            result.put("md5", md5);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
        }
        return result;
    }
       
    private static HashMap<String, Integer> getImageDimension(InputStream is) {
        final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
        HashMap<String, Integer> dimension = null;
        ImageInputStream imageIs = null;
        try {
            imageIs = ImageIO.createImageInputStream(is);            
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(imageIs);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(imageIs);
                    
                    dimension = new HashMap<String, Integer>();
                    dimension.put("width", reader.getWidth(0));
                    dimension.put("height", reader.getHeight(0));                    
                } catch (IOException e) {
                    logger.error("", e);
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if (imageIs != null)
                try {
                    imageIs.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
        }
        return dimension;
    }
    
    private void createThumbnails(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException {
        if (fileItem.getFile().getFileType().thumbnailTypes() != null) {
            Long fileId = fileItem.getFile().getId();
            for (ThumbnailType thumbnailType : fileItem.getFile().getFileType().thumbnailTypes()) {
                createThumbnail(fileId, thumbnailType, false);
            }            
        }
    }
    
    /**
     * delete the file or directory and its parent folders in temp-storage
     * */
    private void deleteFile(File file) {    
        File tempStorage = new File(Constants.getStorageLocalRoot());
        
        if (file.exists()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory()) {
                if (file.list().length == 0)
                    file.delete();
                else
                    return;
            }
            
            file = file.getParentFile();
            while (file != null && !file.equals(tempStorage)) {
                if (file.list().length == 0)
                    file.delete();
                else
                    break;
                file = file.getParentFile();
            }
        }        
    }
    
    private void uploadToBOS(FileItem fileItem) {
        /*String cdnDomain = Constants.getCdnDomain();
        if (cdnDomain != null && !cdnDomain.isEmpty()) {
        	// get s3 url
        	String s3Url = "http://" + cdnDomain + "/" + fileItem.getFilePath(); 
        	publishDurableEvent(new FileBosUploadEvent(s3Url, fileItem.getFilePath(), fileItem.getFileSize(), fileItem.getContentType()));
        }*/
    }

    public ArrayList<String> deleteFile(FileItem fileItem) throws ServiceException {
        ArrayList<String> result = new ArrayList<String>();
        
        // delete the file and folders in temp-storage
        File tempStorage = new File(Constants.getStorageLocalRoot());
        File file = new File(fileItem.getLocalFilePath());
        
        if (file.exists()) {
            result.add("temp-storage: " + file.getPath());
            file.delete();
            
            file = file.getParentFile();
            while (file != null && !file.equals(tempStorage)) {
                if (file.list().length == 0) {
                    result.add("temp-storage: " + file.getPath());
                    file.delete();
                } else
                    break;
                file = file.getParentFile();
            }
        }
        
        /**
         * Don't actually delete the file in Amazon S3. so, you can easily recover the file through updating the database (Update IS_DELETE = 0 in BC_FILE_ITEM and BC_FILE)
         * */
        // delete the file in Amazon S3
        //storageService.deleteFile(fileItem);
        //result.add("S3: " + Constants.getFileBucket() + ", " + fileItem.getFilePath());
        
        // update IS_DELETED = 1 for BC_FILE_ITEM and BC_FILE
        fileItem.setIsDeleted(true);
        fileItem = fileItemDao.update(fileItem);
        result.add("FileItemId: " + fileItem.getId().toString());
        
        int isDelCount = 0;
        com.cyberlink.cosmetic.modules.file.model.File fileEntity = fileItem.getFile();
        fileDao.refresh(fileEntity);
        for (FileItem item : fileEntity.getFileItems()) {
            if (item.getIsDeleted())
                isDelCount++;
        }
        if (isDelCount == fileEntity.getFileItems().size()) {
            fileEntity.setIsDeleted(true);
            fileEntity = fileDao.update(fileEntity);
            result.add("FileId: " + fileEntity.getId().toString());
        }

        return result;
    }
    
    private void downloadFile(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException {
    	if (Constants.getIsCN())
    		ossService.downloadFile(fileItem);
    	else
    		storageService.downloadFile(fileItem);
    }
    
    private void uploadFile(FileItem fileItem) throws NoSuchAlgorithmException, IOException, ServiceException {
    	if (Constants.getIsCN())
    		ossService.uploadFile(fileItem);
    	else
    		storageService.uploadFile(fileItem);
    }
}
