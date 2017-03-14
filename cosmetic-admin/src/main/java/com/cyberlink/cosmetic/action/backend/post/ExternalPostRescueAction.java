package com.cyberlink.cosmetic.action.backend.post;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle.ArticleType;
import com.cyberlink.cosmetic.modules.post.service.ArticleData;
import com.cyberlink.cosmetic.modules.post.service.AutoPostService;
import com.cyberlink.cosmetic.modules.post.service.AutoPostService.PostTask;
import com.cyberlink.cosmetic.modules.user.dao.AdminDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Admin;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Admin.UserEvent;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

@UrlBinding("/post/externalPostRescue.action")
public class ExternalPostRescueAction extends AbstractAction{
	@SpringBean("user.SessionDao")
    private SessionDao sessionDao;
	
	@SpringBean("post.AutoPostService")
    private AutoPostService autoPostService;
	
	@SpringBean("user.AdminDao")
	private AdminDao adminDao;
	
	private FileBean jsonFile = null;
	
	public FileBean getJsonFile() {
		return jsonFile;
	}

	public void setJsonFile(FileBean jsonFile) {
		this.jsonFile = jsonFile;
	}

	@DefaultHandler
	public Resolution route() {
		boolean isLogin = false;
        Long userId = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
                //locale = curUser.getRegion();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
	
		return forward();
	}
	
	public Resolution post() {
		
		PostTask task = taskParser();
		
		if (task == null)
			return new StreamingResolution("text/html", "Rescue Post Fail");
		Admin admin = adminDao.findbyRefInfo(UserEvent.ExternalPost, task.getArticleList().get(0).getImportFile() + "_" + task.getPostRegion());
		if (admin != null && adminDao.exists(admin.getId())) {
			// start auto post
			autoPostService.setRequestHeader(this.getServletRequest().getHeader("User-Agent"));
			autoPostService.pushTask(task);
			autoPostService.startAutoPostThread();
		} else
			return new StreamingResolution("text/html", "admin record doesn't exist");
		
		return new StreamingResolution("text/html", "Rescue Post Success");
	}
	
	public PostTask taskParser() {
		if (jsonFile != null) {
			try {
				StringWriter writer = new StringWriter();
				IOUtils.copy(jsonFile.getInputStream(), writer, "UTF-8");
				String jsonString = writer.toString();
				JsonObject jsoObj = new JsonObject(jsonString);
				
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date startTime = dateFormatGmt.parse(jsoObj.getString("startTime"));
				Date requestTime = dateFormatGmt.parse(jsoObj.getString("requestTime"));
				String postRegion = jsoObj.getString("postRegion");
				int postDuration = jsoObj.getInt("postDuration");
				int postNumber = jsoObj.getInt("postNumber");
				List<Long> postCircles = new ArrayList<Long>(0);
				postCircles.add(jsoObj.getLong("postCircles"));	
				int articleSelNumber = jsoObj.getInt("articleSelNumber");
				
				JsonArray userArray = jsoObj.getJsonArray("userList");
				List<Long> userList = new ArrayList<Long>();
				for (int i=0 ; i<userArray.length() ; i++) {
					userList.add(userArray.getLong(i));
				}
				
				JsonArray articleArray = jsoObj.getJsonArray("articleList");
				List<ArticleData> articleList = new ArrayList<ArticleData>();
				for (int i=0 ; i<articleArray.length() ; i++) {
					JsonObject subObj = articleArray.getJsonObject(i);
					ArticleData art = new ArticleData();
					art.setTitle(subObj.getString("title"));
					art.setUrl(subObj.getString("link"));
					art.setContent(subObj.getString("content"));
					art.addImage(subObj.getString("image"));
					if (subObj.getString("articleType").equalsIgnoreCase(ArticleType.Google.toString()))
						art.setArticleType(ArticleType.Google);
					else if (subObj.getString("articleType").equalsIgnoreCase(ArticleType.Pinterest.toString()))
						art.setArticleType(ArticleType.Pinterest);
					else
						art.setArticleType(ArticleType.Unkown);
					art.setArticleId(subObj.getString("articleId"));
					art.setImportFile(subObj.getString("importFile"));
					art.setChecked(subObj.getBoolean("checked"));
					art.setbRemoveLink(subObj.getBoolean("bRemoveLink"));
					art.setIndex(subObj.getInt("index"));
					art.setOrder(subObj.getInt("order"));
					art.setCropped(subObj.getBoolean("bCropped"));
					art.setCroppedZone(subObj.getString("croppedZone"));
					articleList.add(art);
				}
				
				PostTask task = new PostTask(postRegion, userList, articleList, startTime, postCircles, postNumber, postDuration, articleSelNumber);
				task.setRequestTime(requestTime);
				return task;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return null;
	}
}