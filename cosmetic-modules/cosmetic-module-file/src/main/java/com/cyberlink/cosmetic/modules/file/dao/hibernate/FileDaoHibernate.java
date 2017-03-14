package com.cyberlink.cosmetic.modules.file.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileType;

public class FileDaoHibernate extends AbstractDaoCosmetic<File, Long>
    implements FileDao {
	private String regionOfFindByFileType = "com.cyberlink.cosmetic.modules.file.model.File.query.findByFileType";
	
	@Override
	public PageResult<File> findByFileType(FileType fileType, Long offset,
			Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
	    dc.add(Restrictions.eq("fileType", fileType));
	    dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
	    return findByCriteria(dc, offset, limit, regionOfFindByFileType);
	}

}