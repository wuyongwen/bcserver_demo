package com.cyberlink.cosmetic.modules.file.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;

public interface FileItemDao extends GenericDao<FileItem, Long> {
    boolean exists(Long fileId, ThumbnailType thumbnailType);
    
    FileItem findOriginal(Long fileId);
    
    List<FileItem> findOriginals(Long... fileIds);
    
    FileItem findByFileIdAndThumbnailType(Long fileId, ThumbnailType thumbnailType);
    
    List<FileItem> findByFileIdAndThumbnailType(Long[] fileIds, ThumbnailType thumbnailType);
        
    /**
     * find thumbnails by fileIds and thumbnail type. if not found, use the Quality65 or original file item instead.
     * */
    List<FileItem> findThumbnails(Long[] fileIds, ThumbnailType thumbnailType);
    
    List<FileItem> findByDateTime(Date startTime, Date endTime);
    
    PageResult<FileItem> findAll(BlockLimit blockLimit);
    
    PageResult<FileItem> findByFileId(Long fileId, BlockLimit blockLimit);
}
