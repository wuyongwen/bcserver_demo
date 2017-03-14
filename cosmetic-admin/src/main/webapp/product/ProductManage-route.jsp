<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="/product/js/productMng.js" />"></script>
<style type="text/css">
.brandEditDiv, .productTypeRmvDiv, .productTypeEditDiv {
   border-radius: 5px; 
   border: 1px solid #d6d6d6;
   	background-image: url("./image/pencil-icon.png");
   background-position: right center; 
   background-repeat: no-repeat;
   background-color: #e8e8e8;
   background-size:contain;
   padding-left:5px;
   min-height: 20px;
}
 </style>

<h2 class=ico_mug>Product :: Product Management</h2>
<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true}">
<s:form name="searchParameter" id="searchParameter" beanclass="com.cyberlink.cosmetic.action.backend.product.ProductManageAction">
<table>
<tr>
<td>
	Locale
	<s:select name="locale" id="locale" onchange="changeLocale()">
		<s:options-collection collection="${actionBean.localeList}" />
	</s:select>
	<br/>
	Brand
	<s:select name="brandIdForQuery" id="brandIdForQuery">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.brandList}" value="id" label="brandName"/>
	</s:select>
	<div id="brandsSelDiv" style="display:none;">
		<s:select name="selBrandId" id="selBrandId">
			<s:options-collection collection="${actionBean.brandList}" value="id" label="brandName"/>
		</s:select>
	</div>
	&nbsp;|&nbsp;Category
	<s:select name="typeId" id="typeId">
		<s:option value="">--ALL--</s:option>
		<s:options-collection collection="${actionBean.prodTypeList}" value="id" label="typeName"/>
	</s:select>
	<div id="categorySelEDiv" style="display:none;">
		<s:select name="selTypeId" id="selTypeId">
			<s:option disabled="disabled" selected="selected" value="empty">--Select one--</s:option>
			<s:options-collection collection="${actionBean.prodTypeList}" value="id" label="typeName"/>
		</s:select>
	</div>
	<div id="categorySelRDiv" style="display:none;">
		<s:select name="selTypeId" id="selTypeId">
			<s:option disabled="disabled" selected="selected" value="empty">--Select one--</s:option>
			<s:option value="remove">--Remove--</s:option>
			<s:options-collection collection="${actionBean.prodTypeList}" value="id" label="typeName"/>
		</s:select>
	</div>
	&nbsp;|&nbsp;Price Range
	<s:select name="priceRangeId" id="priceRangeId" >
		<s:option value="">--ALL--</s:option>
		<c:forEach var="i" items="${actionBean.storePriceRangeList}">
			<s:option value="${i.id}">
				${i.priceMin} to 
				<fmt:formatNumber value="${i.priceMax}" minFractionDigits="1" maxFractionDigits="1" type="number"/>
			</s:option>
		</c:forEach>
	</s:select>
	&nbsp;|&nbsp;On Shelf?
	<s:select name="onShelfForQuery">
		<s:option value="">---ALL-</s:option>
		<s:option value="true">Yes</s:option>
		<s:option value="false">No</s:option>
	</s:select>
	&nbsp;<s:submit name="route" onclick="cleanKeyword()" value="Go"/>
</td>
</tr>
<tr>
<td>
	Or Search By Keyword
	<s:text name="searchKeyword" id="searchKeyword" />
	<s:submit name="searchByKeyword" value="Search"/>
</td>
</tr>
</table>
<table>
<tr>
<td>
	Total <b>${actionBean.productList.totalSize}</b> items in result.&nbsp;|&nbsp;
		Go to page No.
		<s:select name="offset" id="offset">
			<c:forEach var="opts" begin="1" end="${actionBean.pages}">
				<s:option value="${(opts-1)*actionBean.limit}">${opts}</s:option>
			</c:forEach>		
		</s:select>
		&nbsp;&nbsp;|&nbsp;&nbsp;Show&nbsp;
		<s:select name="limit" id="limit">
			<s:option value="10">10</s:option>
			<s:option value="20">20</s:option>
			<s:option value="50">50</s:option>
			<s:option value="100">100</s:option>
		</s:select>
		recs/page
		<s:submit name="route" value="Go" id="changePageOffset"/>
		&nbsp;|&nbsp;<input type="button" id="applyAllChange" value="" style="display:none;"/>
</td>
</tr>
</table>

<display:table id="product" name="actionBean.productList.results" requestURI="" >
	<display:column title="Product ID" sortable="true" >
	<div class="productIdDiv">
		${product.id}
	</div>
	</display:column>
	<display:column title="Image" style="text-align:center">
		<img src="${product.img_original}" style="max-height:160px;"/>
	</display:column>
	<display:column title="Brand" sortable="true">
		<div class="brandEditDiv" value="${product.brand.id}">${product.brand.brandName}</div>
	</display:column>
	<display:column title="Category">
		<c:forEach var="relProdType" items="${product.relProductType}" varStatus="status">
			<c:choose>
				    <c:when test="${status.index == 0}">
				    	<div class="productTypeEditDiv" value="${relProdType.productType.id}">${relProdType.productType.typeName}</div>
				    </c:when>
				    <c:otherwise>
				        <div class="productTypeRmvDiv" value="${relProdType.productType.id}">${relProdType.productType.typeName}</div>
				    </c:otherwise>
				</c:choose>
		</c:forEach>
		<input type="button" class="addCategory" style="width: 100%" value="Add Category">
	</display:column>
	<display:column title="Product Title" sortable="true" >
		<textarea class="productTitleDiv" rows="10" style="width:90%;" title="${product.productTitle}">${product.productTitle}</textarea>
	</display:column>
	<display:column title="Price" property="price" sortable="true" />
	<display:column title="On Shelf">
		<div class="shelfDiv">
			<s:select name="onShelf" id="onShelf">
				<c:choose>
				    <c:when test="${product.onShelf == true}">
				    	<option value="1" selected>Yes</option>
				    	<option value="0">No</option>
				    </c:when>
				    <c:otherwise>
				        <option value="1">Yes</option>
				    	<option value="0" selected>No</option>
				    </c:otherwise>
				</c:choose>				
			</s:select>
		</div>
	</display:column>
	<display:column title="Action">
		<a target="_blank" href="${product.productStoreLink}" >Store Page</a>
		<br/>
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProductManageAction" event="seeProductDetail">
			<s:param name="productId" value="${product.id}"/>
			<s:param name="locale" value="${actionBean.locale}"/>
			See Detail
		</s:link>
		<br/>
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProductManageAction" event="updateProdRequest">
			<s:param name="productId" value="${product.id}"/>
			<s:param name="locale" value="${actionBean.locale}"/>
			Edit
		</s:link>
		&nbsp;|&nbsp;
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProductManageAction" event="deleteProduct"
		onclick="return confirm('Delete ${product.productName}? Will also being remove from shelf');">
			<s:param name="productId" value="${product.id}"/>
			Delete
		</s:link>
		<br/>
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProdCommentRatingManageAction" >
			<s:param name="productId" value="${product.id}" />
			See User's Comment
		</s:link>
	</display:column>
</display:table>
</s:form>
<div id="productEditProgress"></div>
</c:if>
<c:if test="${actionBean.currentUserAdmin == false and actionBean.accessControl.productManagerAccess == false}">
Need to login
</c:if>
<script type="text/javascript">
	function cleanKeyword(){
		document.getElementById('searchKeyword').value = null ;
	}
</script>

<script type="text/javascript">
	function changeLocale() {
		document.getElementById('brandIdForQuery').value = '' ;
		document.getElementById('typeId').value = '' ;
		document.getElementById('offset').value = 0 ;
		document.getElementById('limit').value = 100 ;
		document.getElementById('priceRangeId').value = '' ;
		document.getElementById('searchKeyword').value = '' ;
        document.querySelectorAll("input[id=changePageOffset]")[0].click();
    }
</script>