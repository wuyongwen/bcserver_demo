package com.cyberlink.cosmetic.modules.product.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;
import com.cyberlink.cosmetic.modules.product.service.SolrProductUpdater;

public class SolrProductUpdaterImpl extends AbstractService implements
        SolrProductUpdater {
//    private final static int BATCH_SIZE = 1000;
//    private SolrServer server;
//    private ProductDao productDao;
//    private SessionFactory sessionFactory;
//
//    public void setSessionFactory(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public void setProductDao(ProductDao productDao) {
//        this.productDao = productDao;
//    }
//
//    public void setServer(SolrServer server) {
//        this.server = server;
//    }
//
//    @Override
//    public void updateAll() {
//        final Long total = productDao.countUndeleted();
//        for (int i = 1; i <= (total / BATCH_SIZE) + 1; i++) {
//            final List<SolrInputDocument> sids = new ArrayList<SolrInputDocument>();
//            for (final Product p : productDao.findUndeleted(i, BATCH_SIZE)) {
//            	sids.add(generate(p));
//            }
//            update(sids);
//            clearHibernateSession();
//        }
//    }
//
//    private void update(List<SolrInputDocument> ds) {
//        try {
//            server.add(ds, 10000);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//
//    protected final void clearHibernateSession() {
//        sessionFactory.getCurrentSession().clear();
//    }
//
//    @Override
//    public void update(Long productId) {
//        try {
//            final Product p = productDao.findById(productId);
//            final SolrInputDocument sid = generate(p);
//            server.add(sid, 500);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//
//    private SolrInputDocument generate(Product p) {
//        final SolrInputDocument doc = new SolrInputDocument();
//        //System.out.println("dumping product ID " + p.getId() );
//        doc.addField("id", p.getId());
//        doc.addField("locale", p.getLocale());
//        doc.addField("brandId", p.getBrand().getId());
//        doc.addField("brandName", p.getBrand().getBrandName());
//        doc.addField("productTitle", p.getProductTitle());
//        doc.addField("priceRangeId", p.getPriceRange().getId());
//        for (final RelProductType pt : p.getRelProductType()) {
//            doc.addField("typeId", pt.getProductType().getId());
//            doc.addField("typeName", pt.getProductType().getTypeName());
//        }
//        doc.addField("productDescription", p.getProductDescription());
//        doc.addField("createTime", p.getCreatedTime());
//        doc.addField("img_thumbnail", p.getImg_thumbnail());
//        doc.addField("img_original", p.getImg_original());
//        return doc;
//    }
//
//    @Override
//    public void delete(Long productId) {
//        try {
//            server.deleteByQuery("id:" + productId);
//            server.commit();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void deleteAll() {
//        try {
//            server.deleteByQuery("*:*");
//            server.commit();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void optimize() {
//        try {
//            server.optimize();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }

}
