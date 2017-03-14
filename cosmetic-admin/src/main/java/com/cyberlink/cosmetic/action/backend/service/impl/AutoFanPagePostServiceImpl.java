package com.cyberlink.cosmetic.action.backend.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.utils.colorthief.ColorThief;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.FanPageUserDao;
import com.cyberlink.cosmetic.modules.user.model.FanPageUser;
import com.cyberlink.cosmetic.action.backend.service.AutoFanPagePostService;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.RawAPIResponse;
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONObject;

public class AutoFanPagePostServiceImpl extends AbstractService implements
		AutoFanPagePostService {

	private FanPageUserDao fanPageUserDao;
	private PostService postService;
	private FileService fileService;
	private NotifyService notifyService;
	private String appToken;
	private AppName appName = AppName.BACKEND_V2;
	
	static private Boolean isRunning = Boolean.TRUE;
	static final String CRONEXPRESSION = "0 0 2,14 * * ? *";

	public FanPageUserDao getFanPageUserDao() {
		return fanPageUserDao;
	}

	public void setFanPageUserDao(FanPageUserDao fanPageUserDao) {
		this.fanPageUserDao = fanPageUserDao;
	}

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public PostService getPostService() {
		return postService;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public NotifyService getNotifyService() {
		return notifyService;
	}

	public void setNotifyService(NotifyService notifyService) {
		this.notifyService = notifyService;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	@Override
	public void start() {
		isRunning = Boolean.TRUE;
	}

	@Override
	public void stop() {
		isRunning = Boolean.FALSE;
	}

	@Override
	public String getStatus() {
		if (!isRunning)
			return "AutoFanPagePostService isn't running";
		else
			return "AutoFanPagePostService is running";
	}

	@Override
	@BackgroundJob(cronExpression = CRONEXPRESSION)
	public void exec() {

		logger.info(getStatus());
		if (!isRunning)
			return;

		List<FanPageUser> fanPageUsers = fanPageUserDao.listAllFanPageUser();
		if (fanPageUsers == null)
			return;

		for (FanPageUser fanPageUser : fanPageUsers) {
			if (!fanPageUser.getAutoPost())
				continue;

			logger.info(String.format("FB fan page user, userId: %d",
					fanPageUser.getUserId()));
			String fanPageString = fanPageUser.getFanPage();
			JsonObject fanPageObj = null;
			if (fanPageString == null || fanPageString.isEmpty())
				continue;
			try {
				fanPageObj = new JsonObject(fanPageString);
			} catch (Exception e) {
				continue;
			}

			Iterator<?> fanPageIDs = fanPageObj.keys();
			while (fanPageIDs.hasNext()) {
				try {
					String fanPageId = (String) fanPageIDs.next();
					String lastPostTime = fanPageObj.getJsonObject(fanPageId)
							.getString("lastPostTime");
					Date sinceTime = null;
					if (lastPostTime != null && !lastPostTime.isEmpty())
						sinceTime = new Date(Long.valueOf(lastPostTime));
					else {
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.HOUR, -720); // default search one
														// month.
						sinceTime = cal.getTime();
					}
					logger.info(String.format(
							"Start posting FB fan page, fanPageId: %s",
							fanPageId));

					JsonObject queryResult = new JsonObject();
					do {
						try {
							String nextUrl = null;
							if (queryResult.has("next"))
								nextUrl = queryResult.getString("next");

							queryResult = doQuery(fanPageId, fanPageUser, 100,
									sinceTime, null, nextUrl, null);
							if (!queryResult.has("data"))
								break;

							JsonObject createResult = createPost(
									queryResult.toString(), lastPostTime,
									PostStatus.Published);
							if (createResult.has("errorMsg")) {
								logger.error("FB auto post fail, fanPageID: "
										+ fanPageId);
								break;
							}
							if (createResult.has("lastPostTime"))
								lastPostTime = createResult
										.getString("lastPostTime");
						} catch (Exception e) {
							break;
						}
						Thread.sleep(1000);
					} while (queryResult.has("next"));

					if (lastPostTime != null && !lastPostTime.isEmpty()) {
						Date lastTime = new Date(Long.valueOf(lastPostTime));
						if (lastTime.compareTo(sinceTime) > 0) {
							JsonObject subObj = fanPageObj
									.getJsonObject(fanPageId);
							fanPageObj.put(fanPageId,
									subObj.put("lastPostTime", lastPostTime));
							fanPageUser.setFanPage(fanPageObj.toString());
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
			fanPageUser.setFanPage(fanPageObj.toString());
			fanPageUserDao.update(fanPageUser);
		}
		logger.info("AutoFanPagePostService is completed");
		return;
	}

	@Override
	public String getFanpageId(String fanPageName) {
		String fanpageId = "";
		Facebook facebook = null;
		try {
			facebook = new FacebookFactory().getInstance();
			facebook.setOAuthAppId("", "");
			facebook.setOAuthAccessToken(new AccessToken(appToken));

			RawAPIResponse res = facebook.callGetAPI("/v2.0/" + fanPageName);
			JSONObject fanpageInfo = res.asJSONObject();
			fanpageId = fanpageInfo.getString("id");
		} catch (Exception e) {
			logger.error("getFanpageId fail.");
			logger.error(e.getMessage());
		}

		if (fanpageId == null)
			fanpageId = "";

		return fanpageId;
	}

	@Override
	public JsonObject getFanpageObject(String fanPageLink) {
		if (fanPageLink == null || fanPageLink.isEmpty())
			return null;

		// get fanPage name
		String fanPageName = "";
		if (fanPageLink.contains("/")) {
			String[] checkcount = fanPageLink.split("/");
			int count = 2;
			for (int j = 0; j < checkcount.length && j < count; j++) {
				if (checkcount[j].equalsIgnoreCase("")) {
					count++;
				}
			}
			fanPageName = fanPageLink.split("/")[count];
		}
		if (fanPageName == null || fanPageName.isEmpty())
			return null;
		Pattern pattern = Pattern.compile("-[0-9]+");
		Matcher matcher = pattern.matcher(fanPageName);
		if (matcher.find())
			fanPageName = matcher.group().substring(1);

		JsonObject obj = null;
		Facebook facebook = null;
		try {
			facebook = new FacebookFactory().getInstance();
			facebook.setOAuthAppId("", "");
			facebook.setOAuthAccessToken(new AccessToken(appToken));

			RawAPIResponse res = facebook.callGetAPI("/v2.0/" + fanPageName);
			JSONObject fanpageInfo = res.asJSONObject();
			String fanpageId = fanpageInfo.getString("id");

			if (fanpageId != null && !fanpageId.isEmpty()) {
				JsonObject fanpageObj = new JsonObject();
				fanpageObj.put("name", fanpageInfo.getString("name"));
				fanpageObj.put("lastPostTime", "");

				obj = new JsonObject();
				obj.put(fanpageId, fanpageObj);
			}
		} catch (Exception e) {
			logger.error("getFanpageId fail.");
			logger.error(e.getMessage());
		}

		return obj;
	}
	
	@Override
	public JsonObject getFanpageObject(String fanPageName, String lastPostTime) {
		if (fanPageName == null || fanPageName.isEmpty())
			return null;
		
		JsonObject obj = null;
		Facebook facebook = null;
		try {
			facebook = new FacebookFactory().getInstance();
			facebook.setOAuthAppId("", "");
			facebook.setOAuthAccessToken(new AccessToken(appToken));

			RawAPIResponse res = facebook.callGetAPI("/v2.0/" + fanPageName);
			JSONObject fanpageInfo = res.asJSONObject();
			String fanpageId = fanpageInfo.getString("id");

			if (fanpageId != null && !fanpageId.isEmpty()) {
				JsonObject fanpageObj = new JsonObject();
				fanpageObj.put("name", fanpageInfo.getString("name"));
				fanpageObj.put("lastPostTime", lastPostTime);

				obj = new JsonObject();
				obj.put(fanpageId, fanpageObj);
			}
		} catch (Exception e) {
			logger.error("getFanpageId fail.");
			logger.error(e.getMessage());
		}

		return obj;
	}

	@Override
	public JsonObject doQuery(String fanPageId, FanPageUser fanPageUser,
			Integer limit, Date sinceTime, Date untilTime, String nextUrl,
			JsonArray dataArray) {
		// sinceTime = new Date(1446422400000L); //
		// untilTime = new Date(1446803458000L); //

		if (fanPageId == null || fanPageId.isEmpty()) {
			logger.error("No fan page id.");
			return error("This is an invalid fanpage.");
		}

		// list posts
		Facebook facebook = null;
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId("", "");
		facebook.setOAuthAccessToken(new AccessToken(appToken));

		JsonObject result = new JsonObject();
		try {
			JSONObject resultObj = new JSONObject();
			if (nextUrl == null || nextUrl.isEmpty()) {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("fields",
						"full_picture,picture,message,link,type");
				if (limit != null)
					parameters.put("limit", limit.toString());
				if (sinceTime != null)
					parameters.put("since",
							String.valueOf(sinceTime.getTime() / 1000));
				if (untilTime != null)
					parameters.put("until",
							String.valueOf(untilTime.getTime() / 1000));
				RawAPIResponse postRes = facebook.callGetAPI("/v2.0/"
						+ fanPageId + "/posts", parameters);
				resultObj = postRes.asJSONObject();
			} else {
				URLConnection connection = new URL(nextUrl).openConnection();
				connection.setRequestProperty("Accept-Charset", "UTF-8");
				java.io.BufferedReader rd = new java.io.BufferedReader(
						new java.io.InputStreamReader(
								connection.getInputStream(), "UTF-8"));
				String line;
				if ((line = rd.readLine()) != null) {
					resultObj = new JSONObject(line);
				}
				rd.close();
			}

			if (resultObj.has("error"))
				return error("FB service error");

			Long until = null;
			if (resultObj.has("data")) {
				JSONArray datas = resultObj.getJSONArray("data");
				String tagCircleStr = fanPageUser.getTagCircleMap();
				if (tagCircleStr == null || tagCircleStr.isEmpty())
					return error("tagCircleMap is empty.");
				JsonObject tagCircleMap = new JsonObject(tagCircleStr);
				JsonObject mappingResult = postMapping(fanPageUser, datas,
						tagCircleMap, limit, dataArray);
				dataArray = mappingResult.getJsonArray("data");
				result.put("data", dataArray);

				if (mappingResult.has("untilTime"))
					until = Long.valueOf(mappingResult.getString("untilTime")) / 1000 - 1;
			}
			if (resultObj.has("paging")) {
				JSONObject paging = resultObj.getJSONObject("paging");
				nextUrl = paging.getString("next");
				if (until != null)
					nextUrl = nextUrl.replaceFirst("until=[0-9]+", "until="
							+ until.toString());
				result.put("next", nextUrl);
			} else {
				nextUrl = "";
			}
		} catch (Exception e) {
			logger.equals(e.getMessage());
			return error("This is an invalid fanpage.");
		}

		if (dataArray.length() == limit || nextUrl == null || nextUrl.isEmpty())
			return result;
		else
			return doQuery(fanPageId, fanPageUser, limit, sinceTime, untilTime,
					nextUrl, dataArray);
	}

	@Override
	public JsonObject createPost(String postData, String lastPostTime,
			PostStatus postStatus) {
		JsonArray datas = null;
		try {
			datas = new JsonObject(postData).getJsonArray("data");
		} catch (Exception e) {
			return error("postData parsing error.");
		}

		if (datas.length() <= 0)
			return error("postData is empty.");

		for (int i = 0; i < datas.length(); i++) {
			try {
				JsonObject data = datas.getJsonObject(i);
				if (data.has("title") && data.has("content")
						&& data.has("imgUrl") && data.has("createdTime")
						&& data.has("circleId") && data.has("userId")
						&& data.has("locale")) {
					String defaultPostSource = "rss_posting";
					String title = data.getString("title");
					String content = data.getString("content");
					
					String tags = "{\"userDefTags\":[";
					Set<String> hashTagSet = postService.extractHashtagsFromText(content);
					if (hashTagSet != null && hashTagSet.size() > 0) {
						for (String hashTag : hashTagSet) {
							tags += "\"" + hashTag + "\",";
						}
						if (tags.endsWith(","))
							tags = tags.substring(0, tags.length() - 1);
					}
					tags += "]}";
					
					String imageUrl = data.getString("imgUrl");
					String createdTimeString = data.getString("createdTime");
					Date createTime = new Date(Long.valueOf(createdTimeString));
					String userId = data.getString("userId");
					String locale = data.getString("locale");

					String circleId = data.getString("circleId");
					if (circleId == null || circleId.isEmpty()
							|| circleId.equalsIgnoreCase("null")) {
						logger.error("Error: circleId is empty!");
						logger.error(String.format(
								"Fail FB post - userId: %s, title: %s", userId,
								title));
						continue;
					}
					List<Long> postCircleId = new ArrayList<Long>();
					postCircleId.add(Long.valueOf(circleId));

					String attachsJson = "";
					if (data.has("redirectUrl")) {
						String redirectUrl = data.getString("redirectUrl");
						redirectUrl = URLDecoder.decode(redirectUrl, "UTF-8");
						attachsJson = getAttachments(Long.valueOf(userId),
								imageUrl, redirectUrl);
					} else {
						attachsJson = getAttachments(Long.valueOf(userId),
								imageUrl, null);
					}
					if (attachsJson.isEmpty()) {
						logger.error("Error: attachsJson is empty!");
						logger.error(String.format(
								"Fail FB post - userId: %s, title: %s", userId,
								title));
						continue;
					}

					PostApiResult<Post> result = postService.createPost(
							Long.valueOf(userId), locale, null, title, content,
							postCircleId, attachsJson, tags, postStatus,
							defaultPostSource, appName, null, null, null, null,
							createTime, null);
					if (!result.success()) {
						logger.error("Error: create FB post fail!");
						logger.error(result.getErrorDef().message());
						continue;
					}

					if (PostStatus.Published.equals(result.getResult().getPostStatus())) {
						notifyService.addFriendNotifyByType(NotifyType.AddPost
								.toString(), Long.valueOf(userId), result
								.getResult().getId(), null);
					}
					if (lastPostTime == null
							|| lastPostTime.isEmpty()
							|| lastPostTime.equalsIgnoreCase("null")
							|| (createdTimeString != null
									&& !createdTimeString.isEmpty() && Long
									.valueOf(lastPostTime) < Long
									.valueOf(createdTimeString))) {
						lastPostTime = createdTimeString;
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				continue;
			}
		}

		return new JsonObject().put("lastPostTime", lastPostTime);
	}

	private JsonObject error(String msg) {
		JsonObject result = new JsonObject();
		try {
			result.put("errorMsg", msg);
		} catch (Exception e) {
		}
		return result;
	}

	private String getAttachments(Long userId, String imageUrl,
			String redirectUrl) {

		try {
			Map<String, String> imgPhotoSet = getDataUrl(imageUrl, redirectUrl,
					FileType.Photo);
			if (imgPhotoSet.isEmpty()) {
				logger.error("getDataUrl fail, imageUrl: " + imageUrl);
				return "";
			}

			List<String> attachments = new ArrayList<String>();
			attachments.add(createImageFile(userId, imgPhotoSet.get("dataUrl"),
					imgPhotoSet.get("metadata"), FileType.Photo));

			String attachsJson = "";
			if (attachments != null && attachments.size() > 0) {
				attachsJson += "{\"files\":[";
				for (int idx = 0; idx < attachments.size(); idx++) {
					String attcStr = attachments.get(idx);
					if (attcStr.isEmpty())
						return "";

					try {
						JsonObject obj = new JsonObject(attcStr);
						String fileType = "";
						Long fileId = null;

						if (obj.has("fileId")) {
							fileId = Long.valueOf(obj.getString("fileId"));
							obj.remove("fileId");
						}
						if (obj.has("fileType")) {
							fileType = obj.getString("fileType");
							obj.remove("fileType");
						}

						attachsJson += "{";
						if (!fileId.equals((long) 0))
							attachsJson += "\"fileId\":" + fileId.toString()
									+ ",";
						attachsJson += "\"fileType\":\"" + fileType
								+ "\",\"metadata\":";
						attachsJson += obj.toString() + "}";
						if (idx < attachments.size() - 1)
							attachsJson += ",";
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						return "";
					}
				}
				attachsJson += "]}";
			}

			return attachsJson;

		} catch (Exception e) {
			logger.error("get attachments fail");
			logger.error(e.getMessage(), e);
			return "";
		}
	}

	private Map<String, String> getDataUrl(String imageUrl, String redirectUrl,
			FileType fileType) {

		try {
			String mimeType;
			int pushbackLimit = 100;
			String dataUrl = "";
			String metadata = "";

			URL url = new URL(imageUrl);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(10000);
			InputStream urlStream = connection.getInputStream();
			PushbackInputStream pushUrlStream = new PushbackInputStream(
					urlStream, pushbackLimit);
			byte[] firstBytes = new byte[pushbackLimit];
			pushUrlStream.read(firstBytes);
			pushUrlStream.unread(firstBytes);

			ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
			mimeType = URLConnection.guessContentTypeFromStream(bais);
			if (mimeType == null)
				mimeType = connection.getContentType();
			if (mimeType.startsWith("image/")) {
				BufferedImage inputImage = ImageIO.read(pushUrlStream);
				String rgbHexString = ColorThief.createRGBHexString(ColorThief
						.getColor(inputImage));
				String imageType = mimeType.substring("image/".length());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(inputImage, imageType, baos);
				baos.flush();
				dataUrl = "data:" + mimeType + ";base64,"
						+ Base64.encodeBase64String(baos.toByteArray());
				baos.close();
				metadata = getMetadata(inputImage, fileType, redirectUrl,
						rgbHexString);
			}
			if (!dataUrl.isEmpty() && !metadata.isEmpty()) {
				Map<String, String> result = new HashMap<String, String>();
				result.put("dataUrl", dataUrl);
				result.put("metadata", metadata);
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return Collections.<String, String> emptyMap();
	}

	private String getMetadata(BufferedImage image, FileType fileType,
			String url, String rgbHexString) {
		String metadata = "";
		int width = image.getWidth();
		int heigth = image.getHeight();
		if (width < 200 || heigth < 100)
			return "";

		if (fileType.equals(FileType.Photo) && url != null && !url.isEmpty())
			metadata = String
					.format("{\"width\":%d,\"height\":%d,\"redirectUrl\":\"%s\",\"imageDescription\":\"\",\"dominantedColor\":\"%s\"}",
							width, heigth, url, rgbHexString.toUpperCase());
		else
			metadata = String
					.format("{\"width\":%d,\"height\":%d,\"redirectUrl\":\"\",\"imageDescription\":\"\",\"dominantedColor\":\"%s\"}",
							width, heigth, rgbHexString.toUpperCase());
		return metadata;
	}

	private String createImageFile(Long userId, String dataUrl,
			String metadata, FileType fileType) {
		FileItem fileItem = null;
		try {
			fileItem = fileService.createImageFile(userId, dataUrl, metadata,
					fileType);
		} catch (Exception e) {
			logger.error("createImageFile fail");
			logger.error(e.getMessage(), e);
			return "";
		}

		if (fileItem == null) {
			logger.error("createImageFile fail");
			return "";
		}

		try {
			String attachment = "{\"fileId\":"
					+ String.valueOf(fileItem.getFile().getId());
			attachment += ",\"metadata\":" + fileItem.getMetadata();
			attachment += "}";

			return attachment;
		} catch (Exception e) {
			logger.error("createImageFile fail");
			logger.error(e.getMessage(), e);
			return "";
		}
	}

	private JsonObject postMapping(FanPageUser fanPageUser, JSONArray datas,
			JsonObject tagCircleMap, Integer limit, JsonArray returnArray) {

		if (datas == null)
			return null;
		if (returnArray == null)
			returnArray = new JsonArray();

		Long untilTime = null;

		for (int i = 0; i < datas.length(); i++) {
			if (limit != null && returnArray.length() >= limit) {
				JsonObject returnObj = new JsonObject();
				returnObj.put("data", returnArray);
				if (untilTime != null)
					returnObj.put("untilTime", untilTime.toString());
				return returnObj;
			}

			try {
				JSONObject data = datas.getJSONObject(i);
				if (data.has("full_picture") && data.has("message")
						&& data.has("type") && data.has("id")
						&& data.has("created_time")) {
					String type = data.getString("type");
					// don't need to create post with FB videos, status normally
					// don't have pictures
					if (type.equalsIgnoreCase("video")
							|| type.equalsIgnoreCase("status")) {
						continue;
					}

					String createdTimeString = data.getString("created_time");
					DateTimeFormatter parser = ISODateTimeFormat
							.dateTimeNoMillis();
					DateTime postCreatedTime = parser
							.parseDateTime(createdTimeString);
					Long createTimeInLong = postCreatedTime.getMillis();

					String thumbnail = data.getString("full_picture");

					String description = data.getString("message");
					description = description.replaceAll("\n", "<br>");

					String title = data.getString("message");
					title = title.replaceAll("\r", "\\\\r")
							.replaceAll("\u202C", "").replaceAll("\n", "\\\\n")
							.replaceAll("\"", "\\\\\"").replaceAll("", "")
							.replaceAll("	", "");
					if (title.length() <= 20)
						title = title.replaceAll("\\\\r", " ").replaceAll(
								"\\\\n", "");
					else
						title = title.replaceAll("\\\\r", " ")
								.replaceAll("\\\\n", "").substring(0, 20)
								+ "...";

					if (tagCircleMap == null)
						break;
					String circleId = null;
					String defaultCircleId = null;
					Iterator<?> keys = tagCircleMap.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						String keywordStr = tagCircleMap.getString(key);
						if (keywordStr == null || keywordStr.isEmpty())
							continue;

						String[] keywords = keywordStr.split("\\s*,\\s*");
						for (int idx = 0; idx < keywords.length; idx++) {
							if (defaultKey.equals(keywords[idx]))
								defaultCircleId = key;
							if (description.contains(keywords[idx])) {
								circleId = key;
								break;
							}
						}
					}

					// default circle
					if (circleId == null)
						circleId = defaultCircleId;
					
					JsonObject returnObj = new JsonObject();
					if (title.isEmpty() || description.isEmpty()
							|| thumbnail.isEmpty() || createTimeInLong == null
							|| circleId == null || circleId.isEmpty())
						continue;
					returnObj.put("title", title);
					returnObj.put("content", description);
					returnObj.put("imgUrl", thumbnail);
					returnObj.put("createdTime", createTimeInLong.toString());
					returnObj.put("circleId", circleId);
					returnObj.put("userId", fanPageUser.getUserId().toString());
					returnObj.put("locale", fanPageUser.getLocale());
					if (type.equalsIgnoreCase("link") && data.has("link")) {
						String link = data.getString("link");
						returnObj.put("redirectUrl", link);
					}
					returnArray.put(returnObj);
					if (untilTime == null)
						untilTime = createTimeInLong;
					else if (untilTime.compareTo(createTimeInLong) > 0)
						untilTime = createTimeInLong;
				}
			} catch (Exception e) {
				logger.error("FB post mapping error");
				logger.error(e.getMessage());
				continue;
			}
		}

		JsonObject returnObj = new JsonObject();
		returnObj.put("data", returnArray);
		if (untilTime != null)
			returnObj.put("untilTime", untilTime.toString());
		return returnObj;
	}
}