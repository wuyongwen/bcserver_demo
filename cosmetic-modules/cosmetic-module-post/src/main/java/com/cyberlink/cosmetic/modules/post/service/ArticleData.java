package com.cyberlink.cosmetic.modules.post.service;

import java.util.HashSet;
import java.util.Set;

import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle.ArticleType;
import com.restfb.json.JsonObject;

public class ArticleData {
	private String sourceUrl = "";
	private String title = "";
	private String content = "";
	private Set<String> images = new HashSet<String>();
	private int index = 0;
	private int order = 0;
	private boolean checked = false;
	private boolean bRemoveLink = false;
	private ArticleType articleType = ArticleType.Unkown;
	private String articleId = "";
	private String importFile = "";
	private boolean bCropped = false;
	private String croppedImg = "";
	private JsonObject croppedZone = null;

	public String getImportFile() {
		return importFile;
	}

	public void setImportFile(String importFile) {
		this.importFile = importFile;
	}

	public void setUrl(String url) {
		this.sourceUrl = url;
	}

	public String getUrl() {
		return this.sourceUrl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setImages(Set<String> images) {
		this.images = images;
	}

	public void addImage(String img) {
		this.images.add(img);
	}

	public Set<String> getImages() {
		return this.images;
	}

	public String getImage() {
		if (this.images.iterator().hasNext())
			return this.images.iterator().next();
		return "";
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean getChecked() {
		return this.checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean getbRemoveLink() {
		return bRemoveLink;
	}

	public void setbRemoveLink(boolean bRemoveLink) {
		this.bRemoveLink = bRemoveLink;
	}

	public ArticleType getArticleType() {
		return this.articleType;
	}

	public void setArticleType(ArticleType articleType) {
		this.articleType = articleType;
	}

	public String getArticleId() {
		return this.articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getCroppedImg() {
		return croppedImg;
	}

	public void setCroppedImg(String croppedImg) {
		this.croppedImg = croppedImg;
	}

	public boolean getCropped() {
		return bCropped;
	}

	public void setCropped(boolean bCropped) {
		this.bCropped = bCropped;
	}

	public void setCroppedZone(String croppedZone) {	
		try {
			if (croppedZone.isEmpty()) {
				this.croppedZone = null;
				return;
			}
			this.croppedZone = new JsonObject(croppedZone);
		} catch (Exception e) {
			this.croppedZone = null;
		}
	}
	
	public String getCroppedZone() {	
		if (croppedZone == null)
			return "";
		return croppedZone.toString();
	}

	public int getCroppedX() {
		try {
			if (croppedZone != null && croppedZone.has("x"))
				return croppedZone.getInt("x");
		} catch (Exception e) {

		}
		return 0;
	}

	public int getCroppedY() {
		try {
			if (croppedZone != null && croppedZone.has("y"))
				return croppedZone.getInt("y");
		} catch (Exception e) {

		}
		return 0;
	}

	public int getCroppedWidth() {
		try {
			if (croppedZone != null && croppedZone.has("width"))
				return croppedZone.getInt("width");
		} catch (Exception e) {

		}
		return 0;
	}

	public int getCroppedHeight() {
		try {
			if (croppedZone != null && croppedZone.has("height"))
				return croppedZone.getInt("height");
		} catch (Exception e) {

		}
		return 0;
	}

}