package com.cyberlink.store;

public class ProductInfo {
	private String id;
	private String title;
	private String brand;
	private String imgThumb;
	private String imgOriginal;
	private String link;
	private String price;
	private String description;
	private String type;
	private String locale;
	private String onShelf; 
	private String storeID;
	private String typeID;
	private String pkID;//used be Db's table field ID
	private String brandID;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getImgThumb() {
		return imgThumb;
	}
	public void setImgThumb(String imgThumb) {
		this.imgThumb = imgThumb;
	}
	public String getImgOriginal() {
		return imgOriginal;
	}
	public void setImgOriginal(String imgOriginal) {
		this.imgOriginal = imgOriginal;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getOnShelf() {
		return onShelf;
	}
	public void setOnShelf(String onShelf) {
		this.onShelf = onShelf;
	}
	public String getStoreID() {
		return storeID;
	}
	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}
	public String getTypeID() {
		return typeID;
	}
	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}
	public String getPkID() {
		return pkID;
	}
	public void setPkID(String pkID) {
		this.pkID = pkID;
	}
	public String getBrandID() {
		return brandID;
	}
	public void setBrandID(String brandID) {
		this.brandID = brandID;
	}
	
}
