package com.cyberlink.cosmetic.action.backend.file;

import java.io.IOException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.validation.FileTypeConverter;
import com.cyberlink.cosmetic.action.backend.validation.JsonConverter;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileException;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.BOSService;
import com.cyberlink.cosmetic.modules.file.service.FileService;

@UrlBinding("/file/file-manage.action")
public class FileManageAction extends AbstractAction {
    @SpringBean("file.fileService")
    private FileService fileService;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    @SpringBean("file.bosService")
    private BOSService bosService;
    
    private static final String errorMessage = "You aren't an administrator";
    private static final String uploadFileInput = "/file/upload-file-input.jsp";
    private static final String uploadApkFile = "/file/upload-apk-file.jsp";
    
    private Long fileId;
    private FileType fileType; 
    private FileBean fileBean;
    private String metadata;
    private File fileEntity;
    private PageResult<FileItem> pageResult;
    private String originalUrl;
    private String apkType;

	public Long getFileId() {
        return fileId;
    }

    @Validate(required = true, on = "query")
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public FileType getFileType() {
        return fileType;
    }

    @Validate(required = true, converter = FileTypeConverter.class, on = "upload")
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public FileBean getFileBean() {
        return fileBean;
    }

    @Validate(required = true, on = {"upload", "uploadApk"})
    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    public String getMetadata() {
        return metadata;
    }

    @Validate(required = true, converter = JsonConverter.class, on = "upload")
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public File getFileEntity() {
        return fileEntity;
    }

    public void setFileEntity(File fileEntity) {
        this.fileEntity = fileEntity;
    }

    public PageResult<FileItem> getPageResult() {
        return pageResult;
    }
    
    public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
	public String getApkType() {
		return apkType;
	}

	@Validate(required = true, on = "uploadApk")
	public void setApkType(String apkType) {
		this.apkType = apkType;
	}

	@DefaultHandler
    public Resolution list() {
        if(!getCurrentUserAdmin())
            return new StreamingResolution("text/html", errorMessage);
 
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("createdTime", false);
        if (fileId != null)
            pageResult = fileItemDao.findByFileId(fileId, blockLimit);
        else
            pageResult = new PageResult<FileItem>();
        
        return forward();
    }
    
    public Resolution uploadInput() {
        if(!getCurrentUserAdmin())
            return new StreamingResolution("text/html", errorMessage);

        return forward(uploadFileInput);
    }
    
    public Resolution upload() throws IOException {
        if(!getCurrentUserAdmin())
            return new StreamingResolution("text/html", errorMessage);
        
        if (fileType == null) {
            deleteFile();
            return new StreamingResolution("text/html", ErrorDef.InvalidFileType.message());
        }
            
        if (metadata == null) {
            deleteFile();
            return new StreamingResolution("text/html", ErrorDef.InvalidMetadata.message());
        }
            
        FileItem fileItem = null;
        String originalUrl = null;
        try {
        	if (FileType.Raw.equals(fileType))
        		originalUrl = fileService.uploadRawToS3(getCurrentUserId(), fileBean, fileType);
        	else
        		fileItem = fileService.createFile(getCurrentUserId(), fileBean, metadata, fileType, false);
        } catch (InvalidFileException e) {
            return new StreamingResolution("text/html", ErrorDef.InvalidFile.message());
        }
        
        if (fileItem != null)
            return new RedirectResolution("/file/file-manage.action?query&fileId=" + fileItem.getFile().getId().toString());
        else if (originalUrl != null && !originalUrl.isEmpty())
        	return new RedirectResolution("/file/file-manage.action?query&fileId=0&originalUrl=" + originalUrl);
        else
            return new StreamingResolution("text/html", "Server gets errors, please contact with administrators");
    }
    
	public Resolution uploadInputApk() {
		if (!getCurrentUserAdmin() && !getAccessControl().getApkManagerAccess())
			return new StreamingResolution("text/html", errorMessage);
		return forward(uploadApkFile);
	}
	
	public Resolution uploadApk() {
		if (!getCurrentUserAdmin() && !getAccessControl().getApkManagerAccess())
			return new StreamingResolution("text/html", errorMessage);
		
		String bucket = "cosmetic-cn-01";
		String originalUrl = bosService.uploadFileByFileBean(fileBean, apkType, bucket);
		return new RedirectResolution("/file/file-manage.action?uploadInputApk&originalUrl=" + originalUrl);
	}
    
    public Resolution query() {
		if (!getCurrentUserAdmin())
			return new StreamingResolution("text/html", errorMessage);
		
		if (originalUrl != null) {
			return forward();
		} else {
			if (!fileDao.exists(fileId))
				return new StreamingResolution("text/html", "File Not Found");

			fileEntity = fileDao.findById(fileId);
		}
        return forward();
    }
    
    private void deleteFile() {
        try {
            fileBean.delete();
        } catch (IOException e) {
            logger.error("", e);
        }
    }
}
