package com.cyberlink.cosmetic.action.backend.post;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.post.Parser.PinterestParser;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.post.dao.PostAutoArticleDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.service.ArticleData;
import com.cyberlink.cosmetic.modules.post.service.AutoPostService;
import com.cyberlink.cosmetic.modules.post.service.AutoPostService.PostTask;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.AdminDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Admin;
import com.cyberlink.cosmetic.modules.user.model.Admin.UserEvent;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonException;
import com.restfb.json.JsonObject;

@UrlBinding("/post/externalPost.action")
public class ExternalPostAction extends AbstractAction {
	@SpringBean("post.PostDao")
	private PostDao postDao;

	@SpringBean("post.PostService")
	private PostService postService;

	@SpringBean("user.SessionDao")
	private SessionDao sessionDao;

	@SpringBean("file.fileService")
	private FileService fileService;

	@SpringBean("common.localeDao")
	private LocaleDao localeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;

	@SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleService")
	private CircleService circleService;

	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("post.AutoPostService")
	private AutoPostService autoPostService;

	@SpringBean("post.PostAutoArticleDao")
	private PostAutoArticleDao postAutoArticleDao;

	@SpringBean("user.AdminDao")
	private AdminDao adminDao;

	// Post
	private String locale = "en_US";
	private Circle circle = null;
	private List<Long> selCircles = new ArrayList<Long>(0);

	// List
	private Integer pageSize = 100;
	private String pageIdx = "";
	private List<Integer> availablePageSize = new ArrayList<Integer>(
			Arrays.asList(10, 20, 50, 100));
	private String croppedImg;
	private int croppedIdx = -1;
	private String croppedZone;
	private String title;
	private String content;

	// config
	private List<String> availableRegion = new ArrayList<String>(0);
	private String regionSel;
	private List<Circle> circles = new ArrayList<Circle>(0);
	private Long circleSel = null;
	private int userNumber = 0;
	private String datetimepicker;
	private int postNumberSel = 1;
	private int durationSel;
	private String indexs = "";
	private String linkIndexs = "";
	private boolean isNext = false;

	// import
	private FileBean jsonFile = null;
	private String refInfo = "";

	// analysis
	private List<AnalysisData> analysisDataList = new ArrayList<AnalysisData>(0);

	private static final String externalPostPageRoute = "/post/externalPost-route.jsp";
	private static final String externalPostPageList = "/post/externalPost-list.jsp";
	private static final String externalPostPageConfig = "/post/externalPost-config.jsp";
	private Set<Integer> checkIndexes = Collections.<Integer> emptySet();
	private int selectSize = 0;
	private PageResult<ArticleData> pageResult = new PageResult<ArticleData>();

	public PageResult<ArticleData> getPageResult() {
		return pageResult;
	}

	public void setCheckIndexes(Set<Integer> checkIndexes) {
		this.checkIndexes = checkIndexes;
	}

	public Set<Integer> getCheckIndexes() {
		return this.checkIndexes;
	}

	public void setSelectSize(int selectSize) {
		this.selectSize = selectSize;
	}

	public int getSelectSize() {
		return selectSize;
	}

	public List<Circle> getCircles() {
		return circles;
	}

	public void setSelCircles(List<Long> selCircles) {
		this.selCircles = selCircles;
	}

	public void setJsonFile(FileBean jsonFile) {
		this.jsonFile = jsonFile;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public List<Integer> getAvailablePageSize() {
		return availablePageSize;
	}

	public List<String> getAvailableRegion() {
		return availableRegion;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLocale() {
		return locale;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	public Circle getCircle() {
		return circle;
	}

	public void setRegionSel(String regionSel) {
		this.regionSel = regionSel;
	}

	public void setCircleSel(Long circleSel) {
		this.circleSel = circleSel;
	}

	public int getUserNumber() {
		return userNumber;
	}

	public String getDatetimepicker() {
		return datetimepicker;
	}

	public void setDatetimepicker(String datetimepicker) {
		this.datetimepicker = datetimepicker;
	}
	
	public int getPostNumberSel() {
		return postNumberSel;
	}

	public int getDurationSel() {
		return durationSel;
	}

	public void setDurationSel(int durationSel) {
		this.durationSel = durationSel;
	}

	public void setIndexs(String indexs) {
		this.indexs = indexs;
	}

	public void setLinkIndexs(String linkIndexs) {
		this.linkIndexs = linkIndexs;
	}

	public void setIsNext(boolean isNext) {
		this.isNext = isNext;
	}

	public List<AnalysisData> getAnalysisDataList() {
		return analysisDataList;
	}

	public void setAnalysisDataList(List<AnalysisData> analysisDataList) {
		this.analysisDataList = analysisDataList;
	}

	public void setRefInfo(String refInfo) {
		this.refInfo = refInfo;
	}

	public String getCroppedImg() {
		return croppedImg;
	}

	public void setCroppedImg(String croppedImg) {
		if (croppedImg == null)
			this.croppedImg = "";
		else
			this.croppedImg = croppedImg;
	}

	public int getCroppedIdx() {
		return croppedIdx;
	}

	public void setCroppedIdx(int croppedIdx) {
		this.croppedIdx = croppedIdx;
	}

	public String getCroppedZone() {
		return croppedZone;
	}

	public void setCroppedZone(String croppedZone) {
		if (croppedZone == null)
			this.croppedZone = "";
		else
			this.croppedZone = croppedZone;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (content == null)
			this.content = "";
		else
			this.content = content;
	}

	@DefaultHandler
	public Resolution route() {
		clearAttribute();

		boolean isLogin = false;
		Long userId = null;
		HttpSession session = getContext().getRequest().getSession();
		if (session != null) {
			String token = (String) getContext().getRequest().getSession()
					.getAttribute("token");
			if (token != null && token.length() > 0) {
				isLogin = true;
				Session loginSession = sessionDao.findByToken(token);
				User curUser = loginSession.getUser();
				userId = curUser.getId();
				// locale = curUser.getRegion();
			}
		}

		if (!isLogin || userId == null) {
			return new StreamingResolution("text/html", "Need to login");
		}

		availableRegion.clear();
		availableRegion.addAll(localeDao
				.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		List<Long> userList = userDao.findIdByUserType(UserType.Blogger,
				Arrays.asList(locale));
		getContext().getRequest().getSession()
				.setAttribute("userList", userList);
		getContext().getRequest().getSession().setAttribute("locale", locale);
		userNumber = userList.size();

		return forward();
	}

	public Resolution getList() {
		boolean isLogin = false;
		Long userId = null;
		HttpSession session = getContext().getRequest().getSession();
		if (session != null) {
			String token = (String) getContext().getRequest().getSession()
					.getAttribute("token");
			if (token != null && token.length() > 0) {
				isLogin = true;
				Session loginSession = sessionDao.findByToken(token);
				User curUser = loginSession.getUser();
				userId = curUser.getId();
				// locale = curUser.getRegion();
			}
		}

		if (!isLogin || userId == null) {
			return new StreamingResolution("text/html", "Need to login");
		}

		try {
			if (!checkJsonResult(false)) {
				return forward();
			}

		} catch (JsonException e) {
			e.printStackTrace();
			return new ErrorResolution(400, "Invalid File");
		}

		ParamEncoder encoder = new ParamEncoder("row");
		String pageParaName = encoder
				.encodeParameterName(TableTagParameters.PARAMETER_PAGE);
		pageIdx = getContext().getRequest().getParameter(pageParaName);
		if (pageIdx == null || pageIdx.isEmpty())
			pageIdx = "1";

		getContext().getRequest().getSession().setAttribute("pageIdx", pageIdx);
		getContext().getRequest().getSession()
				.setAttribute("pageSize", pageSize);
		return forward();
	}

	public Resolution list() {
		if (!getCurrentUserAdmin()
				&& !getAccessControl().getReportManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		checkJsonResult(false);
		return forward();
	}

	@SuppressWarnings("unchecked")
	public Resolution saveEdit() {
		pageResult = (PageResult<ArticleData>) getContext().getRequest()
				.getSession().getAttribute("articlesResult");
		pageIdx = (String) getContext().getRequest().getSession()
				.getAttribute("pageIdx");
		pageSize = (Integer) getContext().getRequest().getSession()
				.getAttribute("pageSize");

		if (pageResult == null || pageResult.getTotalSize() == 0)
			return forward(externalPostPageRoute);

		if (pageIdx == null || pageIdx.isEmpty())
			pageIdx = "1";

		List<ArticleData> articles = pageResult.getResults();
		int total = articles.size();
		int startIdx = (Integer.parseInt(pageIdx) - 1) * pageSize;

		for (int idx = startIdx; idx < startIdx + pageSize; idx++) {
			if (idx == total)
				break;
			ArticleData art = articles.get(idx);
			art.setChecked(false);
			articles.set(idx, art);
		}

		for (Integer idx : checkIndexes) {
			ArticleData art = articles.get(idx);
			art.setChecked(true);
			articles.set(idx, art);
		}
		// pageResult need set Attribute ?
		return new RedirectResolution(ExternalPostAction.class, "list")
				.addParameter(
						new ParamEncoder("row")
								.encodeParameterName(TableTagParameters.PARAMETER_PAGE),
						pageIdx).addParameter("pageSize", pageSize);
	}

	@SuppressWarnings("unchecked")
	public Resolution config() {
		pageResult = (PageResult<ArticleData>) getContext().getRequest()
				.getSession().getAttribute("articlesResult");
		locale = (String) getContext().getRequest().getSession()
				.getAttribute("locale");
		// checkIndexes = (Set<Integer>)
		// getContext().getRequest().getSession().getAttribute("checkIndexes");
		List<ArticleData> articles = pageResult.getResults();

		if (isNext) {
			selectSize = 0;
			List<Integer> linkIndexList = new ArrayList<Integer>();
			if (linkIndexs != null && !linkIndexs.isEmpty()) {
				String[] linkIndexsArray = linkIndexs.split(",");
				for (String index : linkIndexsArray) {
					linkIndexList.add(Integer.valueOf(index));
				}
			}
			if (indexs != null && !indexs.isEmpty()) {
				String[] indexsList = indexs.split(",");
				List<Integer> indexList = new ArrayList<Integer>();
				for (String index : indexsList) {
					indexList.add(Integer.valueOf(index));
				}
				if (indexList != null && !indexList.isEmpty()) {
					for (ArticleData art : articles) {
						art.setChecked(false);
						art.setbRemoveLink(false);
						if (indexList.contains(art.getIndex())) {
							art.setChecked(true);
							selectSize++;
						}

						if (linkIndexList.contains(art.getIndex())) {
							art.setbRemoveLink(true);
						}
					}
				}
			}
			getContext().getRequest().getSession()
					.setAttribute("selectSize", selectSize);
		} else {
			selectSize = (int) getContext().getRequest().getSession()
					.getAttribute("selectSize");
		}

		// availableRegion.clear();
		// availableRegion.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		circles.clear();
		circles.addAll(circleService.getBcDefaultCircle(locale, null));
		List<Long> userList = userDao.findIdByUserType(UserType.Blogger,
				Arrays.asList(locale));
		getContext().getRequest().getSession()
				.setAttribute("userList", userList);
		getContext().getRequest().getSession().setAttribute("circles", circles);
		userNumber = userList.size();

		return forward(externalPostPageConfig);
	}

	@SuppressWarnings("unchecked")
	public Resolution post() {
		boolean isLogin = false;
		Long userId = null;
		String userName = "";
		HttpSession session = getContext().getRequest().getSession();
		if (session != null) {
			String token = (String) getContext().getRequest().getSession()
					.getAttribute("token");
			if (token != null && token.length() > 0) {
				isLogin = true;
				Session loginSession = sessionDao.findByToken(token);
				User curUser = loginSession.getUser();
				userId = curUser.getId();
				userName = curUser.getDisplayName();
				// locale = curUser.getRegion();
			}
		}

		if (!isLogin || userId == null) {
			return new StreamingResolution("text/html", "Need to login");
		}

		selectSize = (int) getContext().getRequest().getSession()
				.getAttribute("selectSize");
		pageResult = (PageResult<ArticleData>) getContext().getRequest()
				.getSession().getAttribute("articlesResult");
		locale = (String) getContext().getRequest().getSession()
				.getAttribute("locale");
		List<Long> userList = (List<Long>) getContext().getRequest()
				.getSession().getAttribute("userList");
		if (userList == null || userList.isEmpty())
			return new StreamingResolution("text/html", "UserList is Empty");

		List<ArticleData> articles = pageResult.getResults();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date startTime = null;
		if (datetimepicker == null || datetimepicker.isEmpty())
			return new StreamingResolution("text/html",
					"Please select Start Time");
		try {
			startTime = sdf.parse(datetimepicker);
			System.out.println(startTime);
			if (startTime.compareTo(new Date()) < 0) {
				return new StreamingResolution("text/html",
						"Start Time expired");
			}

		} catch (ParseException e) {
			return new StreamingResolution("text/html", "Invalid Start Time");
		}
		selCircles.clear();
		selCircles.add(circleSel);

		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		// user action record
		JsonObject jsonObj = new JsonObject();
		String importFile = (String) getContext().getRequest().getSession()
				.getAttribute("importFile");
		jsonObj.put("Import File", importFile);
		jsonObj.put("Import File Time", dateFormatGmt.format(new Date()));
		circles = (List<Circle>) getContext().getRequest().getSession()
				.getAttribute("circles");
		String circleName = "";
		for (Circle c : circles) {
			if (c.getId().equals(circleSel)) {
				circleName = c.getCircleName();
				break;
			}
		}
		jsonObj.put("FirstPostTime", "Not Yet");
		jsonObj.put("Circle", circleName);
		jsonObj.put("Per user post", postNumberSel);
		if (durationSel < 60)
			jsonObj.put("Duration", String.valueOf(durationSel) + " min");
		else
			jsonObj.put("Duration", String.valueOf(durationSel / 60.0) + " hr");
		jsonObj.put("User Name", userName);
		jsonObj.put("TotalSuccessNum", 0);
		createAdmin(userId, UserEvent.ExternalPost, importFile + "_" + locale,
				jsonObj.toString());

		// output task
		PostTask task = new PostTask(locale, userList, articles, startTime,
				selCircles, postNumberSel, durationSel, selectSize);
		JsonObject jsonTask = new JsonObject();
		jsonTask.put("startTime", dateFormatGmt.format(task.getStartTime()));
		jsonTask.put("requestTime", dateFormatGmt.format(task.getRequestTime()));
		jsonTask.put("postRegion", task.getPostRegion());
		jsonTask.put("postDuration", task.getPostDuration());
		jsonTask.put("postNumber", task.getPostNumber());
		jsonTask.put("postCircles", task.getPostCircles().get(0));
		jsonTask.put("articleSelNumber", task.getArticleSelNumber());

		JsonArray userArray = new JsonArray();
		for (Long l : task.getUserList()) {
			userArray.put(l);
		}
		jsonTask.put("userList", userArray);

		JsonArray articleArray = new JsonArray();
		for (ArticleData art : task.getArticleList()) {
			JsonObject subObj = new JsonObject();
			subObj.put("title", art.getTitle());
			subObj.put("link", art.getUrl());
			subObj.put("content", art.getContent());
			subObj.put("image", art.getImage());
			subObj.put("articleType", art.getArticleType().toString());
			subObj.put("articleId", art.getArticleId());
			subObj.put("importFile", art.getImportFile());
			subObj.put("checked", art.getChecked());
			subObj.put("bRemoveLink", art.getbRemoveLink());
			subObj.put("index", art.getIndex());
			subObj.put("order", art.getOrder());
			subObj.put("bCropped", art.getCropped());
			subObj.put("croppedZone", art.getCroppedZone());

			articleArray.put(subObj);
		}
		jsonTask.put("articleList", articleArray);

		try {
			FileWriter file;
			SimpleDateFormat outPutFormat = new SimpleDateFormat(
					"yyyy-MM-dd HHmmss");
			file = new FileWriter(Constants.getLoggingPath()
					+ String.format("/AutoPostTask(%s).json",
							outPutFormat.format(task.getRequestTime())));
			file.write(jsonTask.toString());
			file.flush();
			file.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		// start auto post
		autoPostService.setRequestHeader(this.getServletRequest().getHeader(
				"User-Agent"));
		autoPostService.pushTask(task);
		autoPostService.startAutoPostThread();

		clearAttribute();
		return new StreamingResolution("text/html", "Auto Post Success");
	}

	public Resolution status() {
		return json(autoPostService.getStatus());
	}

	public Resolution analysis() {
		boolean isLogin = false;
		Long userId = null;
		HttpSession session = getContext().getRequest().getSession();
		if (session != null) {
			String token = (String) getContext().getRequest().getSession()
					.getAttribute("token");
			if (token != null && token.length() > 0) {
				isLogin = true;
				Session loginSession = sessionDao.findByToken(token);
				User curUser = loginSession.getUser();
				userId = curUser.getId();
			}
		}

		if (!isLogin || userId == null) {
			return new StreamingResolution("text/html", "Need to login");
		}

		try {
			// List<String> locales = new ArrayList<String>(0);
			// locales.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date currentTime = new Date();
			List<Object> countObjs = postDao.countPostByCreateTimeAndLocale(
					currentTime, null, PostStatus.Hidden, null);

			Object[] firstRow = (Object[]) countObjs.get(0);
			Object[] lastRow = (Object[]) countObjs.get(countObjs.size() - 1);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse((String) firstRow[1]));
			Date endTime = dateFormat.parse((String) lastRow[1]);
			List<Date> dateList = new ArrayList<Date>(0);
			List<Integer> countList = new ArrayList<Integer>(0);
			List<String> localeDateStringList = new ArrayList<String>(0);

			while (cal.getTime().compareTo(endTime) <= 0) {
				countList.add(0);
				dateList.add(cal.getTime());
				localeDateStringList.add(String.format("%d/%d", cal.getTime()
						.getMonth() + 1, cal.getTime().getDate()));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			AnalysisData enusData = new AnalysisData("en_US", countList,
					localeDateStringList);
			AnalysisData dedeData = new AnalysisData("de_DE", countList,
					localeDateStringList);
			AnalysisData frfrData = new AnalysisData("fr_FR", countList,
					localeDateStringList);
			AnalysisData zhtwData = new AnalysisData("zh_TW", countList,
					localeDateStringList);
			AnalysisData zhchData = new AnalysisData("zh_CN", countList,
					localeDateStringList);
			AnalysisData jajpData = new AnalysisData("ja_JP", countList,
					localeDateStringList);
			AnalysisData kokrData = new AnalysisData("ko_KR", countList,
					localeDateStringList);
			AnalysisData ptbrData = new AnalysisData("pt_BR", countList,
					localeDateStringList);
			AnalysisData enrowData = new AnalysisData("en_ROW", countList,
					localeDateStringList);

			for (Object obj : countObjs) {
				Object[] row = (Object[]) obj;
				if (row[0] == null)
					continue;
				if (row[0].equals("en_US")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						enusData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("de_DE")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						dedeData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("fr_FR")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						frfrData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("zh_TW")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						zhtwData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("zh_CN")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						zhchData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("ja_JP")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						jajpData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("ko_KR")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						kokrData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("pt_BR")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						ptbrData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				} else if (row[0].equals("en_ROW")) {
					int idx = dateList.indexOf(dateFormat
							.parse((String) row[1]));
					if (idx >= 0)
						enrowData.getCountList().set(idx,
								Integer.valueOf(row[2].toString()));
				}
			}

			Map<String, List<String>> map = postAutoArticleDao
					.findFileNameByPostIds(postDao
							.findMainPostIdsByCreatedDateAndStatus(currentTime,
									null, PostStatus.Hidden, null));
			Iterator<String> iter = map.keySet().iterator();
			List<String> refInfoList = new ArrayList<String>(0);
			while (iter.hasNext()) {
				refInfoList.clear();
				String key = iter.next();
				for (String s : map.get(key)) {
					refInfoList.add(s + "_" + key);
				}
				if (key.equalsIgnoreCase("de_DE") && !refInfoList.isEmpty()) {
					dedeData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("en_US")
						&& !refInfoList.isEmpty()) {
					enusData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("fr_FR")
						&& !refInfoList.isEmpty()) {
					frfrData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("ja_JP")
						&& !refInfoList.isEmpty()) {
					jajpData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("ko_KR")
						&& !refInfoList.isEmpty()) {
					kokrData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("zh_CN")
						&& !refInfoList.isEmpty()) {
					zhchData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("zh_TW")
						&& !refInfoList.isEmpty()) {
					zhtwData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("pt_BR")
						&& !refInfoList.isEmpty()) {
					ptbrData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				} else if (key.equalsIgnoreCase("en_ROW")
						&& !refInfoList.isEmpty()) {
					enrowData.setDetailList(adminDao.findAttributebyRefInfos(
							UserEvent.ExternalPost, refInfoList));
				}
			}
			analysisDataList.add(dedeData);
			analysisDataList.add(enusData);
			analysisDataList.add(frfrData);
			analysisDataList.add(jajpData);
			analysisDataList.add(kokrData);
			analysisDataList.add(zhchData);
			analysisDataList.add(zhtwData);
			analysisDataList.add(ptbrData);
			analysisDataList.add(enrowData);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return forward();
	}

	public Resolution checkFile() {
		if (adminDao.findbyRefInfo(UserEvent.ExternalPost, refInfo) != null)
			return json("File name exist");
		return json("pass");
	}

	@SuppressWarnings("unchecked")
	public Resolution saveImage() {
		if (croppedIdx == -1 || croppedImg == null || croppedZone == null)
			return new ErrorResolution(400, "Bad request");

		try {
			pageResult = (PageResult<ArticleData>) getContext().getRequest()
					.getSession().getAttribute("articlesResult");
			if (pageResult != null && !pageResult.getResults().isEmpty()) {
				pageResult.getResults().get(croppedIdx)
						.setCroppedImg(croppedImg);
				pageResult.getResults().get(croppedIdx)
						.setCroppedZone(croppedZone);
				if (croppedImg.isEmpty() || croppedZone.isEmpty())
					pageResult.getResults().get(croppedIdx)
							.setCropped(Boolean.FALSE);
				else
					pageResult.getResults().get(croppedIdx)
							.setCropped(Boolean.TRUE);
				getContext().getRequest().getSession()
						.setAttribute("articlesResult", pageResult);

				// log
				ArticleData aData = pageResult.getResults().get(croppedIdx);
				logger.info(String.format(
						"croppedZone x:%d, y:%d, width:%d, height:%d",
						aData.getCroppedX(), aData.getCroppedY(),
						aData.getCroppedWidth(), aData.getCroppedHeight()));

				return new StreamingResolution("text/html", "");
			}
		} catch (Exception e) {
			logger.error("[saveImage error]");
			logger.error(e.getMessage());
			return new ErrorResolution(400, "Bad request");
		}
		return new ErrorResolution(400, "Bad request");
	}

	@SuppressWarnings("unchecked")
	public Resolution editTitle() {
		if (title == null || title.isEmpty())
			return new ErrorResolution(400, "Bad request");
		
		try {
			pageResult = (PageResult<ArticleData>) getContext().getRequest()
					.getSession().getAttribute("articlesResult");
			if (pageResult != null && !pageResult.getResults().isEmpty()) {
				pageResult.getResults().get(croppedIdx).setTitle(title);
				if (content != null)
					pageResult.getResults().get(croppedIdx).setContent(content);
				return new StreamingResolution("text/html", "");
			}
		} catch (Exception e) {
			logger.error("[editTitle error]");
			logger.error(e.getMessage());
			return new ErrorResolution(400, "Bad request");
		}
		return new ErrorResolution(400, "Bad request");
	}

	@SuppressWarnings("unchecked")
	public boolean checkJsonResult(boolean bforce) throws JsonException {
		pageResult = (PageResult<ArticleData>) getContext().getRequest()
				.getSession().getAttribute("articlesResult");
		locale = (String) getContext().getRequest().getSession()
				.getAttribute("locale");
		if (jsonFile != null
				&& (pageResult == null || pageResult.getTotalSize() == 0)) {
			PinterestParser parser = new PinterestParser(this);
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(jsonFile.getInputStream(), writer, "UTF-8");
				String jsonString = writer.toString();
				JsonObject obj = new JsonObject(jsonString);
				List<ArticleData> articles = parser.getArticleListFromJson(obj,
						jsonFile.getFileName());
				List<ArticleData> newarticles = new ArrayList<ArticleData>();
				List<String> links = new ArrayList<String>();
				Map<String, ArticleData> articleMap = new HashMap<String, ArticleData>();
				for (ArticleData art : articles) {
					links.add(art.getUrl());
					articleMap.put(art.getUrl(), art);
				}
				if (links.size() > 0) {
					List<String> linksInDB = postAutoArticleDao
							.findLinkByLocaleAndLinks(locale, links);
					if (linksInDB != null) {
						articleMap.keySet().removeAll(linksInDB);
						newarticles.addAll(articleMap.values());
						// re-sort
						Collections.sort(newarticles,
								new Comparator<ArticleData>() {
									@Override
									public int compare(ArticleData o1,
											ArticleData o2) {
										return o1.getOrder() - o2.getOrder();
									}
								});
						int artidx = 0;
						for (ArticleData art : newarticles) {
							art.setIndex(artidx);
							artidx++;
						}
					} else
						newarticles.addAll(articles);
				}
				pageResult = new PageResult<ArticleData>(newarticles,
						newarticles.size());
				getContext().getRequest().getSession()
						.setAttribute("articlesResult", pageResult);
				getContext().getRequest().getSession()
						.setAttribute("importFile", jsonFile.getFileName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (pageResult == null)
			return false;

		return true;

	}

	public void clearAttribute() {
		getContext().getRequest().getSession()
				.removeAttribute("articlesResult");
		getContext().getRequest().getSession().removeAttribute("pageIdx");
		getContext().getRequest().getSession().removeAttribute("pageSize");
		getContext().getRequest().getSession().removeAttribute("checkIndexes");
		getContext().getRequest().getSession().removeAttribute("selectSize");
		getContext().getRequest().getSession().removeAttribute("userList");
		getContext().getRequest().getSession().removeAttribute("locale");
		getContext().getRequest().getSession().removeAttribute("importFile");
		getContext().getRequest().getSession().removeAttribute("circles");
	}

	public class AnalysisData {
		private String locale;
		private List<String> detailList;
		private List<Integer> countList;
		private List<String> dateList;

		public AnalysisData(String locale, List<Integer> countList,
				List<String> dateList) {
			this.locale = locale;
			this.countList = new ArrayList<Integer>(countList);
			this.dateList = new ArrayList<String>(dateList);
		}

		public String getLocale() {
			return locale;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}

		public List<Integer> getCountList() {
			return countList;
		}

		public void setCountList(List<Integer> countList) {
			this.countList = countList;
		}

		public List<String> getDateList() {
			return dateList;
		}

		public void setDateList(List<String> dateList) {
			this.dateList = dateList;
		}

		public void setDetailList(List<String> detailList) {
			this.detailList = detailList;
		}

		public String getDetail() {
			String detail = locale + "<br><br>";
			if (detailList == null || detailList.isEmpty())
				return detail;

			try {
				for (String jsonStr : detailList) {
					JsonObject jsonObj = new JsonObject(jsonStr);
					if (jsonObj != null) {
						if (jsonObj.has("Import File"))
							detail += "Import File: "
									+ jsonObj.get("Import File") + "<br>";
						if (jsonObj.has("User Name"))
							detail += "Administrator: "
									+ jsonObj.get("User Name") + "<br>";
						if (jsonObj.has("Circle"))
							detail += "Circle: " + jsonObj.get("Circle")
									+ "<br>";
						if (jsonObj.has("Duration"))
							detail += "Duration: " + jsonObj.get("Duration")
									+ "<br>";
						if (jsonObj.has("Per user post"))
							detail += "Per user post: "
									+ jsonObj.get("Per user post") + "<br>";
						if (jsonObj.has("Import File Time"))
							detail += "Import File Time: "
									+ jsonObj.get("Import File Time") + "<br>";
						if (jsonObj.has("FirstPostTime")) {
							detail += "First Post Create Time : "
									+ jsonObj.get("FirstPostTime") + "<br>";
						}
						if (jsonObj.has("LastPostTime")) {
							detail += "Last Post Create Time : : "
									+ jsonObj.get("LastPostTime") + "<br>";
						}
						if (jsonObj.has("TotalSuccessNum"))
							detail += "Total Posts Number: "
									+ jsonObj.get("TotalSuccessNum") + "<br>";
					}
					detail += "<br>";
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return detail;
		}

	}

	public void createAdmin(Long userId, UserEvent event, String refInfo,
			String attribute) {
		Admin admin = new Admin();
		admin.setShardId(userId);
		admin.setCreatorId(userId);
		admin.setEvent(event);
		admin.setRefInfo(refInfo);
		admin.setAttribute(attribute);
		adminDao.create(admin);
	}

	public Calendar getLocaleCalendar(String locale) {
		Calendar c = Calendar.getInstance();
		// System.out.println("current: "+c.getTime());

		TimeZone z = c.getTimeZone();
		if (z.equals(TimeZone.getTimeZone("GMT")))
			return c;

		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		// System.out.println("offset: " + offsetHrs);
		// System.out.println("offset: " + offsetMins);

		c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
		c.add(Calendar.MINUTE, (-offsetMins));

		// System.out.println("GMT Time: "+c.getTime());
		if (locale.equals("en_US"))
			c.add(Calendar.HOUR_OF_DAY, -8);
		else if (locale.equalsIgnoreCase("de_DE"))
			c.add(Calendar.HOUR_OF_DAY, 2);
		else if (locale.equalsIgnoreCase("fr_FR"))
			c.add(Calendar.HOUR_OF_DAY, 2);
		else if (locale.equalsIgnoreCase("zh_TW"))
			c.add(Calendar.HOUR_OF_DAY, 8);
		else if (locale.equalsIgnoreCase("zh_CN"))
			c.add(Calendar.HOUR_OF_DAY, 8);
		else if (locale.equalsIgnoreCase("ja_JP"))
			c.add(Calendar.HOUR_OF_DAY, 9);
		else if (locale.equalsIgnoreCase("ko_KR"))
			c.add(Calendar.HOUR_OF_DAY, 9);
		else if (locale.equalsIgnoreCase("en_ROW"))
			c.add(Calendar.HOUR_OF_DAY, 0);
		return c;
	}

	public Date transLocaleDate(String locale, Date date) {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		if (locale.equals("en_US"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
		else if (locale.equalsIgnoreCase("de_DE"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
		else if (locale.equalsIgnoreCase("fr_FR"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
		else if (locale.equalsIgnoreCase("zh_TW"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		else if (locale.equalsIgnoreCase("zh_CN"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		else if (locale.equalsIgnoreCase("ja_JP"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+9:00"));
		else if (locale.equalsIgnoreCase("ko_KR"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+9:00"));
		else if (locale.equalsIgnoreCase("en_ROW"))
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			SimpleDateFormat dateFormatLocal = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			date = dateFormatLocal.parse(dateFormatGmt.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	// testing api
	/*public Resolution getFace() {
		URLContentReader urlContentReader = BeanLocator
				.getBean("web.urlContentReader.noCache");
		String path = "http://54.64.170.46/backend/post/photo-selection.action?getFace";
		Map<String, String> params = new HashMap<String, String>();
		params.put("extUrl", extUrl);
		String returnJson = urlContentReader.post(path, params);
		return json(returnJson);
		// return new ErrorResolution(400, "Bad request");
	}

	private String extUrl;

	public String getExtUrl() {
		return extUrl;
	}

	public void setExtUrl(String extUrl) {
		this.extUrl = extUrl;
	}*/

}
