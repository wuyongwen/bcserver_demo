package com.cyberlink.cosmetic.modules.post.dao.impl;

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.common.model.SolrSearchParam;
import com.cyberlink.cosmetic.modules.post.dao.SolrPostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;

public class SolrPostDaoImpl extends AbstractService implements
SolrPostDao {
//	@SuppressWarnings("static-access")
//	private static final Pattern PATTERN = Pattern.compile("[' ']+").compile(
//			"[.。,，;！？#@#￥$%&*()（）=《》<>‘、’；：\"\\?!:']");
//	private SolrServer server;
//	private SolrServer suggestServer;
//	private PostDao postDao;
//
//	public PageResult<Post> search(SolrSearchParam param) {
//		final SolrQuery query = new SolrQuery();
//		PageResult<Post> solrSearchResult = new PageResult<Post>();
//		query.setFields("id");
//		String keyword = escapeQueryChars(param.getKeyword());
//		if (hasCJKCharater(param.getKeyword())) {
//            keyword = addLikeSearchForCJK(keyword);
//        }
//        query.addFilterQuery("postTitle:" + param.getLocale());
//        query.setQuery(keyword);
//        query.setRows(param.getLimit());
//        query.setStart(param.getOffset());
//        try {
//            QueryResponse rsp = server.query(query);
//            final List<Post> results = new ArrayList<Post>();
//            for (SolrDocument s : rsp.getResults()) {
//                final Long id = Long.valueOf(s.getFieldValue("id").toString());
//                results.add(postDao.findById(id));
//            }
//            solrSearchResult.setTotalSize((int)rsp.getResults().getNumFound());
//            solrSearchResult.setResults(results);
//            return solrSearchResult;
//        } catch (SolrServerException e) {
//        	solrSearchResult.setResults(new ArrayList<Post>());
//        	solrSearchResult.setTotalSize(0);
//        	logger.error(e.getMessage(), e);
//        }
//		
//        return solrSearchResult;
//	}
//	
//	public PageResult<String> suggestion(SolrSearchParam param) {
//		ModifiableSolrParams params = new ModifiableSolrParams();
//		PageResult<String> solrSuggestResult = new PageResult<String>();
//		String keyword = escapeQueryChars(param.getKeyword());
//		if (hasCJKCharater(param.getKeyword())) {
//            keyword = addLikeSearchForCJK(keyword);
//        }
//		params.set("qt", "/suggest");
//		params.set("q", keyword);
//		params.set("suggest.dictionary", "mySuggester");
//		params.set("wt", "json");
//		//query.setRows(param.getLimit());
//        //query.setStart(param.getOffset());
//        try {
//            QueryResponse rsp = server.query(params);
//            SpellCheckResponse spResponse = rsp.getSpellCheckResponse() ;
//            //if there's no API for suggester
//            //need to send http request like 
//            //http://54.92.79.134:8983/solr/post/suggest?suggest=true&suggest.build=true&suggest.dictionary=mySuggester&wt=json&suggest.q=bea
//            //to get the solr suggestion
//            //be careful on the suggest.build=true means refresh suggester dictionary!!  
//            final List<String> results = new ArrayList<String>();
//            solrSuggestResult.setTotalSize((int)rsp.getResults().getNumFound());
//            solrSuggestResult.setResults(results);
//            return solrSuggestResult;
//        } catch (SolrServerException e) {
//        	solrSuggestResult.setResults(new ArrayList<String>());
//        	solrSuggestResult.setTotalSize(0);
//        	logger.error(e.getMessage(), e);
//        }
//		return solrSuggestResult;
//	}
//	
//	protected final Boolean hasCJKCharater(String keyword) {
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
//	public SolrServer getServer() {
//		return server;
//	}
//
//	public void setServer(SolrServer server) {
//		this.server = server;
//	}
//
//	public PostDao getPostDao() {
//		return postDao;
//	}
//
//	public void setPostDao(PostDao postDao) {
//		this.postDao = postDao;
//	}
//
//	public SolrServer getSuggestServer() {
//		return suggestServer;
//	}
//
//	public void setSuggestServer(SolrServer suggestServer) {
//		this.suggestServer = suggestServer;
//	}

}
