package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentStatus;
import com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus;

public class ProductCommentDaoHibernate extends AbstractDaoCosmetic<ProductComment, Long>
implements ProductCommentDao{
	private String regionOfFindByProductId = "com.cyberlink.cosmetic.modules.product.model.ProductComment.query.findByProductId";
	private String findByReportedComments = "com.cyberlink.cosmetic.modules.product.model.ProductComment.query.findByReportedComments";
	private String findProdByCommentTime = "com.cyberlink.cosmetic.modules.product.model.ProductComment.query.findProdByCommentTime";
	
	public ProductComment findByProductIdAndUserId(Long userId, Long... productId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("product.id", productId));
        dc.add(Restrictions.eq("user.id", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("status", ProductCommentStatus.Published));
        return uniqueResult(dc);
	}

	public PageResult<ProductComment> findByProductId(Long productId, Long userId, Long offset, Long limit) {
		BlockLimit blockLimit = new BlockLimit(offset.intValue(), limit.intValue());
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("product.id", productId));
        if( userId != null ){
        	dc.add(Restrictions.eq("user.id", userId));
        }
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("status", ProductCommentStatus.Published));
        //return findByCriteria(dc, offset, limit, regionOfFindByProductId);
        return blockQuery(dc, blockLimit);
	}

	public PageResult<ProductComment> findByReportedComments(String locale, 
			ReportedProdCommentStatus status, Long offset, Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.createAlias("product", "p");
		dc.add(Restrictions.eq("p.locale", locale));
		dc.createAlias("reportedTickets", "rTicket");
		if( status != null ){
			dc.add(Restrictions.eq("rTicket.reviewStatus", status));
		}
		//dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return findByCriteria( dc, offset, limit, findByReportedComments );
	}

	public List<ProductComment> findProdByCommentTime(Product product, Date startTime, Date endTime) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("product", product));
		if( startTime != null && endTime != null ){
			dc.add(Restrictions.between("createdTime", startTime, endTime));
		}
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("status", ProductCommentStatus.Published));
		return findByCriteria(dc);
	}

	public PageResult<ProductComment> findByCreatorId(Long creatorId, Long offset, Long limit) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("user.id", creatorId));
		return findByCriteria(dc, offset, limit, null);
	}

	public PageResult<ProductComment> findByReportedByCreatorId(Long creatorId,
			Long offset, Long limit) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("user.id", creatorId));
		dc.createAlias("reportedTickets", "rTicket");
		dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return findByCriteria(dc, offset, limit, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProductComment> getAllProductCommentsByDate(String locale, Date startTime, Date endTime) {
		List<ProductComment> commentsList = new LinkedList<ProductComment>();
		int offset = 0;
		int limit = 100;
		do{
	        String finalSql = "SELECT BPC.COMMENT_TEXT, BPC.USER_ID FROM BC_PRODUCT_COMMENT BPC JOIN BC_PRODUCT BP ON BPC.PRODUCT_ID = BP.ID "
	        		+ "WHERE BP.IS_DELETED = FALSE AND BP.LOCALE = :locale AND BPC.IS_DELETED = FALSE AND BPC.STATUS = 'Published' "
	        		+ "AND  BPC.CREATED_TIME <= :endTime AND BPC.CREATED_TIME >= :startTime LIMIT :offset, :limit";
	        SQLQuery sqlCommentsQuery = getSession().createSQLQuery(finalSql);
	        sqlCommentsQuery.setParameter("locale", locale);
	        sqlCommentsQuery.setParameter("startTime", startTime);
	        sqlCommentsQuery.setParameter("endTime", endTime);
	        sqlCommentsQuery.setParameter("offset", offset);
	        sqlCommentsQuery.setParameter("limit", limit);
	        List<Object> objectsList = sqlCommentsQuery.list();
	        if(!objectsList.isEmpty()){
		    	for(Object obj : objectsList){
		    		ProductComment productComment = new ProductComment();
		        	Object[] row = (Object[]) obj;
		        	productComment.setComment((row[0]==null)?null:String.valueOf(row[0].toString()));
		        	productComment.setCreatorId((row[1]==null)?null:Long.valueOf(row[1].toString()));
		        	commentsList.add(productComment);
		    	}
		    	offset += limit;
	        }
	    	if(objectsList.size() < limit)
	    		break;
		}while(true);
		return commentsList;
	}
}
