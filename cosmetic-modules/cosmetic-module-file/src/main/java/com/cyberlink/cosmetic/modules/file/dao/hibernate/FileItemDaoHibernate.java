package com.cyberlink.cosmetic.modules.file.dao.hibernate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;

public class FileItemDaoHibernate extends AbstractDaoCosmetic<FileItem, Long> implements
        FileItemDao {   
    private final String regionOfExists = "com.cyberlink.cosmetic.modules.file.query.exists";
    private final String regionOfFindOriginal = "com.cyberlink.cosmetic.modules.file.model.FileItem.query.findOriginal";
    private final String regionOfFindOriginals = "com.cyberlink.cosmetic.modules.file.model.FileItem.query.findOriginals";
    private final String regionOfFindByFileIdAndThumbnailType = "com.cyberlink.cosmetic.modules.file.model.FileItem.query.findByFileIdAndThumbnailType";
    private final String regionOfFindByFileIdAndThumbnailTypeList = "com.cyberlink.cosmetic.modules.file.model.FileItem.query.findByFileIdAndThumbnailTypeList";
        
    public boolean exists(Long fileId, ThumbnailType thumbnailType) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("file", "aFile");
        dc.add(Restrictions.eq("aFile.id", fileId));
        dc.add(Restrictions.eq("thumbnailType", thumbnailType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.rowCount());
        final Long count = uniqueResult(dc, regionOfExists);
        return count != 0;
    }
    
    public FileItem findOriginal(Long fileId) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("file", "aFile");
        dc.add(Restrictions.eq("aFile.id", fileId));
        dc.add(Restrictions.eq("isOriginal", Boolean.TRUE));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc, regionOfFindOriginal);
    }
    
    public List<FileItem> findOriginals(Long... fileIds) {
        if (fileIds.length == 0)
            return Collections.emptyList();
        final DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("file", "aFile");
        dc.add(Restrictions.in("aFile.id", fileIds));
        dc.add(Restrictions.eq("isOriginal", Boolean.TRUE));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, regionOfFindOriginals);
    }
    
    public FileItem findByFileIdAndThumbnailType(Long fileId,
            ThumbnailType thumbnailType) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("file", "aFile");
        dc.add(Restrictions.eq("aFile.id", fileId));
        dc.add(Restrictions.eq("thumbnailType", thumbnailType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc, regionOfFindByFileIdAndThumbnailType);
    }

    public List<FileItem> findByFileIdAndThumbnailType(Long[] fileIds, 
            ThumbnailType thumbnailType) {
        if (fileIds.length == 0)
            return Collections.emptyList();
        final DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("file", "aFile");
        dc.add(Restrictions.in("aFile.id", fileIds));
        dc.add(Restrictions.eq("thumbnailType", thumbnailType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));       
        return findByCriteria(dc, regionOfFindByFileIdAndThumbnailTypeList);
    }
    
    public List<FileItem> findThumbnails(Long[] fileIds,
            ThumbnailType thumbnailType) {
        List<FileItem> result = findByFileIdAndThumbnailType(fileIds, thumbnailType);
        
        if (result.size() != fileIds.length) {
            Set<Long> lostFileIds = new HashSet<Long>(Arrays.asList(fileIds)); 
            for (FileItem item : result) {
                lostFileIds.remove(item.getFile().getId());
            }
            List<FileItem> resultQ65 = findByFileIdAndThumbnailType(lostFileIds.toArray(new Long[lostFileIds.size()]), ThumbnailType.Detail);
            result.addAll(resultQ65);
            
            if (resultQ65.size() != lostFileIds.size()) {
                for (FileItem item : resultQ65) {
                    lostFileIds.remove(item.getFile().getId());
                }
                result.addAll(findOriginals(lostFileIds.toArray(new Long[lostFileIds.size()])));        
            }
        }
        return result;
    }
    
    public List<FileItem> findByDateTime(Date startTime, Date endTime) {       
        final DetachedCriteria dc = createDetachedCriteria();
        dc.createAlias("file", "aFile");
        dc.add(Restrictions.ge("createdTime", startTime));
        dc.add(Restrictions.le("createdTime", endTime));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
    }

    public PageResult<FileItem> findAll(BlockLimit blockLimit) {     
        final DetachedCriteria dc = createDetachedCriteria();
        return blockQuery(dc, blockLimit);
    }

    public PageResult<FileItem> findByFileId(Long fileId, BlockLimit blockLimit) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("file.id", fileId));
        return blockQuery(dc, blockLimit);
    }
}
