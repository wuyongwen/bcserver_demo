package com.cyberlink.cosmetic.action.api.post;

import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.post.dao.PostDefaultTagDao;
import com.cyberlink.cosmetic.modules.post.model.PostDefaultTag;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/v4.4/post/list-post-default-tag.action")
public class ListPostDefaultTagAction extends AbstractAction {

    @SpringBean("post.PostDefaultTagDao")
    private PostDefaultTagDao postDefaultTagDao;
    
    private String locale;
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
	@DefaultHandler
	public Resolution route() {
	    List<PostDefaultTag> results = postDefaultTagDao.listByLocale(locale);
	    PageResult<PostDefaultTag> pageResult = new PageResult<PostDefaultTag>();
	    pageResult.setResults(results);
	    pageResult.setTotalSize(results.size());
		return json(pageResult);
	}
}
