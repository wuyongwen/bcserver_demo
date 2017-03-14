package com.cyberlink.cosmetic.modules.post.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.binary.Base64;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.utils.colorthief.ColorThief;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.ArticleData;
import com.cyberlink.cosmetic.modules.post.service.AutoPostService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.AdminDao;
import com.cyberlink.cosmetic.modules.user.model.Admin;
import com.cyberlink.cosmetic.modules.user.model.Admin.UserEvent;
import com.restfb.json.JsonObject;


public class AutoPostServiceImpl extends AbstractService implements AutoPostService{
	
    private FileService fileService;
    private PostService postService;
    private AdminDao adminDao;
	
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

	public AdminDao getAdminDao() {
		return adminDao;
	}

	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}


	private static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs
	
	String requestHeader;
	Queue<PostTask> postQueue = new LinkedList<PostTask>();
	RunnableExecutePostQueue postExecuteRunable;
	private Thread postExecuteThread;
	
	private class RunnableExecutePostQueue implements Runnable {
		private Boolean isRunning = Boolean.FALSE;
		private String taskStatus = "Auto Post Idle...";

		@Override
		public void run() {
			if (isRunning)
				return;
			isRunning = Boolean.TRUE;
			logger.info("Auto Post Start");
			do {
				try {
					PostTask task = postQueue.poll();
					if (task != null) {
						try {
							taskStatus = String.format("The reqest time %s task is executing. Locale: %s, Account Number: %d, Article Number: %d, Start Time: %s, Duration: %d min, Per Account Post %d, Circle id: %d, ", 
														task.getRequestTime().toString(), task.getPostRegion(), task.getUserList().size(), task.getArticleSelNumber(), task.getStartTime().toString(),
														task.getPostDuration(), task.getPostNumber(), task.getPostCircles().iterator().next());
							logger.info(taskStatus);
							autoPost(task);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					} else 
						stop();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					stop();
				}
				
			} while(isRunning);
			postExecuteThread = null;
			isRunning = Boolean.FALSE;
			taskStatus = "Auto Post Idle...";
			logger.info("All tasks have been done. Auto Post Stop");
		}
		
		public void start() {
			if (postExecuteThread == null) {
				postExecuteThread = new Thread(this, "AutoPostExecute");
				postExecuteThread.setDaemon(false);
				postExecuteThread.setPriority(Thread.MAX_PRIORITY);
				postExecuteThread.start();
			}
		}
		
		public void stop() {
        	isRunning = Boolean.FALSE;
        }
		
		public String getTaskStatus() {
			return taskStatus;
		}
		
		public Boolean getIsRunning() {
			return isRunning;
		}
	}
	
	private void autoPost(PostTask task) {
		try {
		List<Long> userList = task.getUserList();
		List<ArticleData> articleList = task.getArticleList();
		Date startTime = task.getStartTime();
		String postRegion = task.getPostRegion();
		int postDuration = task.getPostDuration();
		int postNumber = task.getPostNumber();
		List<Long> postCircles = task.getPostCircles();
		String tags = null;
		String defaultPostSource = "native_posting";
		Long promoteScore = null;
	
		// shuffle created time
		List<Integer> shufeMinList= new ArrayList<Integer>(0);		
		Random ran = new Random();	
		shufeMinList.clear();
		while(shufeMinList.size() < postNumber) {
			Integer next = ran.nextInt(postDuration) + 1;
			if (shufeMinList.contains(next))
				continue;
			shufeMinList.add(next);
		}
		
		// shuffle user list
		List<Integer> shufUserList= new ArrayList<Integer>(0);	
		Random ranU = new Random();
		shufUserList.clear();
		while(shufUserList.size() < userList.size()) {
			Integer next = ranU.nextInt(userList.size());
			if (shufUserList.contains(next))
				continue;
			shufUserList.add(next);
		}
		
		int userIdx = 0;
		int postCount = 0;
		int loopCount = 0;
		int totalSuccess = 0;
		int idx = 0;
		Date lastCreateTime = null;
		
		for (ArticleData art : articleList) {
			try{
				if (art.getChecked()) {
					if (userIdx >= userList.size()) {
						userIdx = 0;
						loopCount++;
					}
					
					Date createdTime = new Date(startTime.getTime() + (postDuration * ONE_MINUTE_IN_MILLIS) * (userIdx + loopCount * userList.size()) + (shufeMinList.get(postCount)) *  ONE_MINUTE_IN_MILLIS);
					Long userId = userList.get(shufUserList.get(userIdx));
					logger.info("stat get attachsJson");
					String attachsJson = getAttachments(userId, art);
					logger.info("end get attachsJson");
					if (attachsJson.isEmpty()) {
						logger.error("Error: attachsJson is empty!");
						logger.error(String.format("Fail Article - title: %s, Content: %s, ", art.getTitle(), art.getContent()));
						continue;
					}
					Map<String, String> params = new HashMap<String, String>();
					params.put("userId", userId.toString());
					params.put("locale", postRegion);
					params.put("title", art.getTitle());
					params.put("content", art.getContent());
					params.put("tags", tags);
					params.put("attachments", attachsJson);
					params.put("circleIds", postCircles.iterator().next().toString());
					params.put("postStatus", PostStatus.Hidden.toString());
					params.put("postSource", defaultPostSource);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
					params.put("createdTime", sdf.format(createdTime));
					params.put("articleType", art.getArticleType().toString());
					params.put("articleId", art.getArticleId());
					params.put("link", art.getUrl());
					params.put("importFile", art.getImportFile());
					URLContentReader urlContentReader = BeanLocator.getBean("web.urlContentReader.noCache");
					String backend = Constants.getBackendPath();
					String path = "";
					if (backend.equals("/backend/"))
						path = "http://localhost:8080" + "/backend/" + "v3.0/post/create-post.action";
					else if (backend.equals("/"))
						path = "http://localhost:8080" + "/" + "v3.0/post/create-post.action";
					else if (backend.equals(":8080/backend/"))
						// NOTE: development need to modify your own localhost
						path = "http://localhost:8080" + "/backend/" + "v3.0/post/create-post.action";
					
					logger.info("stat post reaquest");							
					String returnJson = urlContentReader.post(path, params);
					logger.info("end post reaquest");
					if (returnJson.contains("ErrorCode")) {
						logger.error("Error: Post fail! " + returnJson);
						logger.error(String.format("Fail Article - Title: %s, Content: %s, ", art.getTitle(), art.getContent()));
						continue;
					}
					if (returnJson.contains("Warning")) {
						logger.error(returnJson);
						continue;
					}
					lastCreateTime = createdTime;
					JsonObject jsonObj = new JsonObject(returnJson);
					logger.info(String.format("%d, createdTime: %s, userId: %d, postId: %s", idx, createdTime.toString(), userId, jsonObj.get("postId")));
					postCount++;
					totalSuccess++;
				} else
					continue;
				
				if (postCount == postNumber) {
					postCount = 0;
					userIdx++;
					
					// shuffle created time	
					ran = new Random();	
					shufeMinList.clear();
					while(shufeMinList.size() < postNumber) {
						Integer next = ran.nextInt(postDuration) + 1;
						if (shufeMinList.contains(next))
							continue;
						shufeMinList.add(next);
					}
				}				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			idx++;
		}
		// update admin
		try {
			if (lastCreateTime != null){
				Admin adimn = adminDao.findbyRefInfo(UserEvent.ExternalPost, articleList.get(0).getImportFile()+"_"+postRegion);
				if (adimn != null) {
					String jsonString = adimn.getAttribute();
					if (jsonString != null || !jsonString.isEmpty()) {
						JsonObject jsonObj = new JsonObject(adimn.getAttribute());
						SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
						jsonObj.put("LastPostTime", dateFormatGmt.format(lastCreateTime));
						if (!jsonObj.has("FirstPostTime"))
							jsonObj.put("FirstPostTime", dateFormatGmt.format(lastCreateTime));
						else if(jsonObj.get("FirstPostTime").equals("Not Yet"))
							jsonObj.put("FirstPostTime", dateFormatGmt.format(lastCreateTime));
						jsonObj.put("TotalSuccessNum", totalSuccess);
						adimn.setAttribute(jsonObj.toString());
						adminDao.update(adimn);
					}
				}
			}
		} catch(Exception e) {
			logger.info(String.format("The reqest time %s task update admin fail.", task.getRequestTime().toString()));
			logger.error(e.getMessage(), e);
		}
		
		logger.info(String.format("The reqest time %s task is Done. totalSuccess: %d", task.getRequestTime().toString(), totalSuccess));
		} catch (Exception e) {
			logger.error(String.format("The reqest time %s task is Fail.", task.getRequestTime().toString()));
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getAttachments(Long userId, ArticleData art) {

		try {
			logger.info("stat getDataUrl");
			Map<String, String> imgPhotoSet = getDataUrl(art, FileType.Photo);
			if (imgPhotoSet.isEmpty()) {
				logger.error("getDataUrl fail");
				return "";
			}
			logger.info("end getDataUrl");

			List<String> attachments = new ArrayList<String>();
			logger.info("stat create ImageFile");
			attachments.add(createImageFile(userId, imgPhotoSet.get("dataUrl"),
					imgPhotoSet.get("metadata"), FileType.Photo));
			logger.info("end create ImageFile");

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
	
	public Map<String, String> getDataUrl(ArticleData art, FileType fileType) {

		try {
			String mimeType;
			int pushbackLimit = 100;
			String imgUrl = art.getImage();
			String artUrl = art.getUrl();
			boolean bCrop = art.getCropped();
			if (art.getbRemoveLink())
				artUrl = "";
			String dataUrl = "";
			String metadata = "";

			if (!art.getCroppedImg().isEmpty()) {
				dataUrl = art.getCroppedImg();
				String imageDataBytes = dataUrl
						.substring(dataUrl.indexOf(",") + 1);
				InputStream stream = new ByteArrayInputStream(
						Base64.decodeBase64(imageDataBytes));
				BufferedImage image = ImageIO.read(stream);
				String rgbHexString = ColorThief.createRGBHexString(ColorThief.getColor(image));
				metadata = getMetadata(image, fileType, artUrl, rgbHexString);
			} else {
				URL url = new URL(imgUrl);
				logger.info("url decode");
				imgUrl = URLDecoder.decode(imgUrl, "UTF-8");
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("User-Agent", requestHeader);
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(10000);
				logger.info("start getInputStream");
				InputStream urlStream = connection.getInputStream();
				PushbackInputStream pushUrlStream = new PushbackInputStream(
						urlStream, pushbackLimit);
				byte[] firstBytes = new byte[pushbackLimit];
				pushUrlStream.read(firstBytes);
				pushUrlStream.unread(firstBytes);
				logger.info("end getInputStream");

				ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
				logger.info("guessContentTypeFromStream");
				mimeType = URLConnection.guessContentTypeFromStream(bais);
				if (mimeType.startsWith("image/")) {
					logger.info("read image");
					BufferedImage inputImage = ImageIO.read(pushUrlStream);
					BufferedImage outputImage = inputImage;
					String rgbHexString = ColorThief.createRGBHexString(ColorThief.getColor(inputImage));
					int width = 0;
					int heigth = 0;
					width = inputImage.getWidth();
					heigth = inputImage.getHeight();
					logger.info("get inputImage size");
					if (bCrop) {
						logger.info("crop image");
						int x = 0;
						int y = 0;
						int lenX = width;
						int lenY = heigth;
						if (art.getCroppedX() > 0)
							x = art.getCroppedX();
						if (art.getCroppedY() > 0)
							y = art.getCroppedY();
						if (art.getCroppedWidth() > 0)
							lenX = art.getCroppedWidth();
						if (art.getCroppedHeight() > 0)
							lenY = art.getCroppedHeight();

						outputImage = inputImage.getSubimage(x, y, lenX, lenY);
						width = outputImage.getWidth();
						heigth = outputImage.getHeight();
						logger.info("get outputImage size");
					}
					String imageType = mimeType.substring("image/".length());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(outputImage, imageType, baos);
					baos.flush();
					logger.info("encodeBase64");
					dataUrl = "data:" + mimeType + ";base64,"
							+ Base64.encodeBase64String(baos.toByteArray());
					baos.close();
					metadata = getMetadata(outputImage, fileType, artUrl, rgbHexString);
				}
			}

			if (!dataUrl.isEmpty() && !metadata.isEmpty()) {
				Map<String, String> result = new HashMap<String, String>();
				result.put("dataUrl", dataUrl);
				result.put("metadata", metadata);
				logger.info("put data in map");
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return Collections.<String, String> emptyMap();
	}
	
	public String getMetadata(BufferedImage image, FileType fileType, String url, String rgbHexString) {
		logger.info("get metadata");
		String metadata = "";
		int width = image.getWidth();
        int heigth = image.getHeight();
		if (fileType.equals(FileType.Photo))
        	metadata = String.format("{\"width\":%d,\"height\":%d,\"redirectUrl\":\"%s\",\"imageDescription\":\"\",\"dominantedColor\":\"%s\"}", width, heigth, url, rgbHexString.toUpperCase());
        else
        	metadata = String.format("{\"width\":%d,\"height\":%d,\"redirectUrl\":\"\",\"imageDescription\":\"\",\"dominantedColor\":\"%s\"}", width, heigth, rgbHexString.toUpperCase());
		return metadata;
	}
	
	public String createImageFile(Long userId, String dataUrl, String metadata, FileType fileType) {
		FileItem fileItem = null;
		try {
            fileItem = fileService.createImageFile(userId, dataUrl, metadata, fileType);
        } catch (Exception e) {
        	logger.error("createImageFile fail");
        	logger.error(e.getMessage(), e);
            return "";
        }
		
		if(fileItem == null) {
			logger.error("createImageFile fail");
            return "";
		}
		
		try {
		String attachment = "{\"fileId\":" + String.valueOf(fileItem.getFile().getId());
        attachment += ",\"metadata\":" +  fileItem.getMetadata();
        attachment += "}";
		
		return attachment;
		} catch (Exception e) {
			logger.error("createImageFile fail");
        	logger.error(e.getMessage(), e);
            return "";
        }
	}
	
	@Override
	public void startAutoPostThread() {
		if (postExecuteRunable == null)
			postExecuteRunable = new RunnableExecutePostQueue();
		postExecuteRunable.start();
	}

	@Override
	public void stopAutoPostThread() {
		if (postExecuteRunable != null) {
			logger.info("Auto Post Force Stop!");
			logger.info(String.format("Remaining Task: %d.", postQueue.size()) + " " + postExecuteRunable.getTaskStatus());
			postExecuteRunable.stop();
			postExecuteRunable = null;
		}
		logger.info("Auto Post Stop");
	}

	@Override
	public void pushTask(PostTask task) {
		postQueue.offer(task);
		logger.info(getStatus());
	}

	@Override
	public String getStatus() {
		if (postExecuteRunable == null)
			return String.format("Remaining Task: %d.", postQueue.size()) + " " + "There is no task executing.";
		
		if (postExecuteRunable.getIsRunning())
			return String.format("Remaining Task: %d.", postQueue.size()) + " " + postExecuteRunable.getTaskStatus();
		else
			return String.format("Remaining Task: %d.", postQueue.size()) + " " + "There is no task executing.";
	}

	@Override
	public void setRequestHeader(String header) {
		this.requestHeader = header;
		
	}
	
}