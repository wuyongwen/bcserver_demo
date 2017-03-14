package com.cyberlink.cosmetic.modules.product.dao.impl;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.SolrProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductSearchParam;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;

public class SolrProductDaoImpl extends AbstractService implements
        SolrProductDao {
//    @SuppressWarnings("static-access")
//    private static final Pattern PATTERN = Pattern.compile("[' ']+").compile(
//            "[.。,，;！？#@#￥$%&*()（）=《》<>‘、’；：\"\\?!:']");
//    private SolrServer server;
//    private ProductDao productDao;
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
//    public PageResult<ProductWrapper> search(ProductSearchParam param) {        
//    	final SolrQuery query = new SolrQuery();
//    	PageResult<ProductWrapper> pageResult = new PageResult<ProductWrapper>();
//        query.setFields("id");
//
//        String keyword = escapeQueryChars(param.getKeyword());
//        if (hasCJKCharater(param.getKeyword())) {
//            keyword = addLikeSearchForCJK(keyword);
//        }
//        query.addFilterQuery("productTitle:" + param.getLocale());
//        query.setQuery(keyword);
//        query.setRows(param.getPageSize());
//        query.setStart(param.getStartFrom());
//
//        try {
//            QueryResponse rsp = server.query(query);
//            final List<ProductWrapper> results = new ArrayList<ProductWrapper>();
//            for (SolrDocument s : rsp.getResults()) {
//                final Long id = Long.valueOf(s.getFieldValue("id").toString());
//                results.add(new ProductWrapper(productDao.findById(id)));
//            }
//            pageResult.setTotalSize((int)rsp.getResults().getNumFound());
//            pageResult.setResults(results);
//            return pageResult;
//        } catch (SolrServerException e) {
//            pageResult.setResults(new ArrayList<ProductWrapper>());
//            pageResult.setTotalSize(0);
//        	logger.error(e.getMessage(), e);
//        }
//        return pageResult;
//    }
//
//    protected final Boolean hasCJKCharater(String keyword) {
//        Matcher m = PATTERN.matcher(keyword);
//        final String input = m.replaceAll(" ").replace(" ", "");
//        for (int i = 0; i < input.length(); i++) {
//            char c = input.charAt(i);
//            final UnicodeBlock ub = Character.UnicodeBlock.of(c);
//            if (((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
//                    || (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
//                    || (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
//                    || (ub == Character.UnicodeBlock.HANGUL_SYLLABLES)
//                    || (ub == Character.UnicodeBlock.HANGUL_JAMO)
//                    || (ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO)
//                    || (ub == Character.UnicodeBlock.HIRAGANA)
//                    || (ub == Character.UnicodeBlock.KATAKANA) || (ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS))) {
//                return Boolean.TRUE;
//            }
//        }
//        return Boolean.FALSE;
//    }
//
//    protected String escapeQueryChars(String s) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            // These characters are part of the query syntax and must be escaped
//            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '('
//                    || c == ')' || c == ':' || c == '^' || c == '[' || c == ']'
//                    || c == '\"' || c == '{' || c == '}' || c == '~'
//                    || c == '*' || c == '?' || c == '|' || c == '&' || c == ';') {
//                sb.append('\\');
//            }
//            sb.append(c);
//        }
//        return sb.toString();
//    }
//
//    protected final String addLikeSearchForCJK(String keyword) {
//        Matcher m = PATTERN.matcher(keyword);
//        if (!m.find()) {
//            String[] keywordSplit = StringUtils.split(keyword, " ");
//            List<String> starlist = new ArrayList<String>();
//            if (keywordSplit != null) {
//                for (final String s : keywordSplit) {
//                    starlist.add("*" + s + "*");
//                }
//            }
//            if (!starlist.isEmpty()) {
//                return StringUtils.join(starlist, " and ");
//            }
//        }
//        return keyword;
//    }
//
//	@Override
//	public PageResult<Product> searchProduct(ProductSearchParam param) {
//    	final SolrQuery query = new SolrQuery();
//    	PageResult<Product> pageResult = new PageResult<Product>();
//        query.setFields("id");
//
//        String keyword = escapeQueryChars(param.getKeyword());
//        if (hasCJKCharater(param.getKeyword())) {
//            keyword = addLikeSearchForCJK(keyword);
//        }
//        query.addFilterQuery("productTitle:" + param.getLocale());
//        query.setQuery(keyword);
//        query.setRows(param.getPageSize());
//        query.setStart(param.getStartFrom());
//
//        try {
//            QueryResponse rsp = server.query(query);
//            final List<Product> results = new ArrayList<Product>();
//            for (SolrDocument s : rsp.getResults()) {
//                final Long id = Long.valueOf(s.getFieldValue("id").toString());
//                results.add(productDao.findById(id));
//            }
//            pageResult.setTotalSize((int)rsp.getResults().getNumFound());
//            pageResult.setResults(results);
//            return pageResult;
//        } catch (SolrServerException e) {
//            pageResult.setResults(new ArrayList<Product>());
//            pageResult.setTotalSize(0);
//        	logger.error(e.getMessage(), e);
//        }
//        return pageResult;
//	}
//
//	public PageResult<Product> searchProductWithFilter(
//			ProductSearchParam param, Long brandId, Long typeId,
//			Long priceRangeId) {
//		final SolrQuery query = new SolrQuery();
//    	PageResult<Product> pageResult = new PageResult<Product>();
//    	query.setFields("id");
//
//        String keyword = escapeQueryChars(param.getKeyword());
//        if (hasCJKCharater(param.getKeyword())) {
//            keyword = addLikeSearchForCJK(keyword);
//        }
//        query.addFilterQuery("locale:" + param.getLocale());
//        if( brandId != null ){
//        	query.addFilterQuery("brandId:" + brandId);
//        }
//        if( typeId != null ){
//        	query.addFilterQuery("typeId:" + typeId);
//        }
//        if( priceRangeId != null ){
//        	query.addFilterQuery("priceRangeId:" + priceRangeId);
//        }
//        query.setQuery(keyword);
//        query.setRows(param.getPageSize());
//        query.setStart(param.getStartFrom());
//
//        try {
//            QueryResponse rsp = server.query(query);
//            final List<Product> results = new ArrayList<Product>();
//            for (SolrDocument s : rsp.getResults()) {
//                final Long id = Long.valueOf(s.getFieldValue("id").toString());
//                results.add(productDao.findById(id));
//            }
//            pageResult.setTotalSize((int)rsp.getResults().getNumFound());
//            pageResult.setResults(results);
//            return pageResult;
//        } catch (SolrServerException e) {
//            pageResult.setResults(new ArrayList<Product>());
//            pageResult.setTotalSize(0);
//        	logger.error(e.getMessage(), e);
//        }
//        return pageResult;
//	}

}
