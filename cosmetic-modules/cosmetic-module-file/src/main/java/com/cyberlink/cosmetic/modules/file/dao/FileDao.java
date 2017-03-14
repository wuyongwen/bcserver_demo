package com.cyberlink.cosmetic.modules.file.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileType;

public interface FileDao extends GenericDao<File, Long> {
	PageResult<File> findByFileType(FileType fileType, Long offset, Long limit);
}