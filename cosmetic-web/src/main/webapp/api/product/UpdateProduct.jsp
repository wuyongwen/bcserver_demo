<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Create New Product</title>
</head>
<body>
	<fieldset>
	<legend>Update Product</legend>
	<s:form beanclass="com.cyberlink.cosmetic.action.api.product.UpdateProductAction">
		<label>brandID: <s:text name="brandID" size="20" /></label><br/>
		<label>storeID: <s:text name="storeID" size="60" /></label><br/>
		<label>prodName: <s:text name="prodName" size="20" value="Liner"/></label><br/>
		<label>displayTitle: <s:text name="displayTitle" size="60" value="Gooood Liner"/></label><br/>
		<label>description: <s:text name="description" size="60" value="The Ultimate in Liner"/></label><br/>
		<label>IMG_original: <s:text name="IMG_original" size="60" value="123.jpg"/></label><br/>
		<label>IMG_thumbnail<s:text name="IMG_thumbnail" size="60" value="123_s.jpg"/></label><br/>
		<label>BARCODE: <s:text name="BARCODE" size="60" /></label><br/>
		<label>productStoreLink: <s:text name="productStoreLink" size="60" value="http://tw.yahoo.com"/></label><br/>
		<label>price: <s:text name="price" size="60" /></label><br/>
		<label>extProdID: <s:text name="extProdID" size="60" /></label><br/>
		<label>OnShelf: <s:text name="onShelf" size="60" value="1" /></label><br/>
		<label>trialOnYMK: <s:text name="trialOnYMK" size="60" /></label><br/>
		<s:submit name="submit" value="Submit"/>
	</s:form>
	</fieldset>
</body>
</html>