package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostAutoArticleDao;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle.ArticleType;

public class PostAutoArticleDaoHibernate extends AbstractDaoCosmetic<PostAutoArticle, Long>
	implements PostAutoArticleDao {

	@Override
	public PostAutoArticle findByPostId(Long postId) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("postId", postId));
		List<PostAutoArticle> results = findByCriteria(dc);
		if (results == null || results.size() <= 0)
			return null;
		return results.get(0);
	}

	@Override
	public PostAutoArticle findByLink(String link) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("link", link));
		List<PostAutoArticle> results = findByCriteria(dc);
		if (results == null || results.size() <= 0)
			return null;
		return results.get(0);
	}

	@Override
	public List<String> findLinkByLocaleAndLinks(String locale, List<String> links) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.in("link", links));
		dc.setProjection(Projections.property("link"));
		return findByCriteria(dc);
	}

	@Override
	public PostAutoArticle findByLocaleAndLink(String locale, String link) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("locale", locale));
		dc.add(Restrictions.eq("link", link));
		List<PostAutoArticle> results = findByCriteria(dc);
		if (results == null || results.size() <= 0)
			return null;
		return results.get(0);
	}
	
	@Override
	public Map<String, List<String>> findFileNameByPostIds(List<Long> postIds) {
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        if(postIds == null || postIds.size() <= 0)
            return resultMap;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("postId", postIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("postStatus", PostStatus.Hidden));
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("locale"))
                .add(Projections.groupProperty("importFile")));
        
        List<Object> objs = findByCriteria(dc);
        List<String> enusNames = new ArrayList<String>();
        List<String> dedeNames = new ArrayList<String>();
        List<String> frfrNames = new ArrayList<String>();
        List<String> kokrNames = new ArrayList<String>();
        List<String> jajpNames = new ArrayList<String>();
        List<String> zhcnNames = new ArrayList<String>();
        List<String> zhtwNames = new ArrayList<String>();
        List<String> ptbrNames = new ArrayList<String>();
        List<String> enrowNames = new ArrayList<String>();
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            if (row[0] == null)
            	continue;
            if (row[0].equals("en_US")) {
            	enusNames.add((String) row[1]);
			} else if (row[0].equals("de_DE")) {
				dedeNames.add((String) row[1]);
			} else if (row[0].equals("fr_FR")) {
				frfrNames.add((String) row[1]);
			} else if (row[0].equals("zh_TW")) {
				zhtwNames.add((String) row[1]);
			} else if (row[0].equals("zh_CN")) {
				zhcnNames.add((String) row[1]);
			} else if (row[0].equals("ja_JP")) {
				jajpNames.add((String) row[1]);
			} else if (row[0].equals("ko_KR")) {
				kokrNames.add((String) row[1]);
			} else if (row[0].equals("pt_BR")) {
				ptbrNames.add((String) row[1]);
			} else if (row[0].equals("en_ROW")) {	
				enrowNames.add((String) row[1]);
			}
        }
        if (!enusNames.isEmpty())
        	resultMap.put("en_US", enusNames);
        if (!dedeNames.isEmpty())
        	resultMap.put("de_DE", dedeNames);
        if (!frfrNames.isEmpty())
        	resultMap.put("fr_FR", frfrNames);
        if (!zhtwNames.isEmpty())
        	resultMap.put("zh_TW", zhtwNames);
        if (!zhcnNames.isEmpty())
        	resultMap.put("zh_CN", zhcnNames);
        if (!jajpNames.isEmpty())
        	resultMap.put("ja_JP", jajpNames);
        if (!kokrNames.isEmpty())
        	resultMap.put("ko_KR", kokrNames);
        if (!ptbrNames.isEmpty())
        	resultMap.put("pt_BR", ptbrNames);
        if (!enrowNames.isEmpty())
        	resultMap.put("en_ROW", enrowNames);
        
        return resultMap;
	}
}