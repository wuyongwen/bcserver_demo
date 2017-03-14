package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.service.SolrPostUpdater;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;

public class SolrPostUpdaterImpl extends AbstractService implements
	SolrPostUpdater{
	
//	private final static int BATCH_SIZE = 1000;
//	private SolrServer server;
//	private PostDao postDao;
//	private SessionFactory sessionFactory;
//
//	@Override
//	public void updateAll() {
//		final Long totalPostCount = postDao.countUndeleted();
//		for (int i = 1; i <= (totalPostCount / BATCH_SIZE) + 1; i++) {
//			final List<SolrInputDocument> sids = new ArrayList<SolrInputDocument>();
//			for (final Post p : postDao.findUndeleted(i, BATCH_SIZE)) {
//            	sids.add(generate(p));
//            }
//			update(sids);
//            clearHibernateSession();
//		}
//		
//	}
//
//	public void update(Long postId) {
//		try {
//            final Post p = postDao.findById(postId);
//            final SolrInputDocument sid = generate(p);
//            server.add(sid, 500);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//		
//	}
//
//	public void delete(Long postId) {
//		try {
//            server.deleteByQuery("id:" + postId);
//            server.commit();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//		
//	}
//
//	public void optimize() {
//		try {
//			server.optimize();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}
//
//	public void deleteAll() {
//		try {
//			server.deleteByQuery("*:*");
//			server.commit();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}
//	
//	private void update(List<SolrInputDocument> ds) {
//        try {
//            server.add(ds, 10000);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//	
//	private SolrInputDocument generate(Post p) {
//        final SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id", p.getId());
//        doc.addField("postTitle", p.getTitle());
//        doc.addField("postTitle", p.getLocale());
//        doc.addField("postContext", p.getContent());
//        doc.addField("createTime", p.getCreatedTime());
//        return doc;
//    }
//	
//	protected final void clearHibernateSession() {
//        sessionFactory.getCurrentSession().clear();
//    }
//
//	public SolrServer getServer() {
//		return server;
//	}
//
//	public void setServer(SolrServer server) {
//		this.server = server;
//	}
//
//	public SessionFactory getSessionFactory() {
//		return sessionFactory;
//	}
//
//	public void setSessionFactory(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//	}
//
//	public PostDao getPostDao() {
//		return postDao;
//	}
//
//	public void setPostDao(PostDao postDao) {
//		this.postDao = postDao;
//	}

}
