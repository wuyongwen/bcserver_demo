package com.cyberlink.cosmetic.action.backend.post;

import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.post.dao.PostAutoArticleDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle.ArticleType;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.model.UserType;

@UrlBinding("/post/createAutoArticleAction.action")
public class CreateAutoArticleAction extends AbstractAction{
	@SpringBean("post.PostDao")
    private PostDao postDao;
	
	@SpringBean("post.PostService")
    private PostService postService;
	
	@SpringBean("post.PostAutoArticleDao")
    private PostAutoArticleDao postAutoArticleDao;
	
	
	private int limit = 100;
	private int offset = 0;
	private ArticleType articleType = ArticleType.Unkown;
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	@DefaultHandler
    public Resolution route() {
		if (!getCurrentUserAdmin()) {
            return new ErrorResolution(403, "Need to login");
        }
		
		PageResult<Post> pageResult = postDao.findPostByUsersType(UserType.Blogger, new BlockLimit(offset, limit));
		int totalSuccess = 0;
		int totalDelete = 0;		
		while (pageResult.getResults() != null && pageResult.getResults().size() != 0) {
			List<Post> postList = pageResult.getResults();
			logger.info(String.format("Record auto post in Auto Article Table - offset: %d, totalSize: %d" , offset, pageResult.getTotalSize()));			
			Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(postList, ThumbnailType.Detail);
			
			for (Post pt : postList) {
				if (pt.getIsDeleted())
					continue;
				for(Object obj : postFileItems.get(pt.getId())) {
					if(obj instanceof FileItem) {
	                    FileItem fileItem = (FileItem)obj;
	                    FileType fileType = fileItem.getFile().getFileType();
	                    if (fileType == FileType.Photo) {
	                    	String url = fileItem.getMetadataJson().get("redirectUrl").textValue();
	                    	if (url == null || url.isEmpty())
	                    		continue;
	                    	PostAutoArticle postAutoArticle =  postAutoArticleDao.findByLink(url);
	                    	if (postAutoArticle == null ) {
	                    		postAutoArticle = createAutoArticle(pt, articleType, null, url);
	                    		if (postAutoArticle == null)
	        						logger.error(String.format("Record auto post Fail, postID: %d, link: %s" , pt.getId(), url));
	        					else
	        						totalSuccess++;	
	                    	} else if (!pt.getId().equals(postAutoArticle.getPostId()) && pt.getPostStatus() == PostStatus.Hidden) {
	                    		PostApiResult <Boolean> result = postService.deletePost(pt.getCreatorId(), pt.getId());
	                    		if (result.success())
	                    			totalDelete++;
	                    		else
	                    			logger.error(String.format("Delete auto post Fail, postID: %d, link: %s" , pt.getId(), url));
	                    	}
	                    }
					}
				}
			}
			offset += limit;
			pageResult = postDao.findPostByUsersType(UserType.Blogger, new BlockLimit(offset, limit));
		}
		
		return json(String.format("Create Article Table Success, totalSuccess: %d, totalDelete: %d", totalSuccess, totalDelete));
	}
	
	public PostAutoArticle createAutoArticle(Post post, ArticleType articleType, String articleId, String link) {
    	PostAutoArticle postAutoArticle = new PostAutoArticle();
    	postAutoArticle.setShardId(post.getCreatorId());
    	postAutoArticle.setCreatorId(post.getCreatorId());
    	postAutoArticle.setCreator(post.getCreator());
    	postAutoArticle.setPostId(post.getId());
    	postAutoArticle.setLocale(post.getLocale());
    	postAutoArticle.setTitle(post.getTitle());
    	postAutoArticle.setContent(post.getContent());
    	postAutoArticle.setLink(link);
    	postAutoArticle.setPostStatus(post.getPostStatus());
    	postAutoArticle.setArticleType(articleType);
    	postAutoArticle.setArticleId(articleId);
    	return postAutoArticleDao.create(postAutoArticle);
    }
	
}