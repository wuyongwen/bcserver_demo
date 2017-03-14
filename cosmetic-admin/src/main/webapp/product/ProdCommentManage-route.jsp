<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="/backend/product/js/prototype.js"></script>
<h2 class=ico_mug>Product :: Users' Comment</h2>
<b>Product Info</b>
<display:table id="product" name="actionBean.targetProductItem" requestURI="" defaultsort ="1">
	<display:column title="Product ID" property="productId" />
	<display:column title="Image" style="text-align:center">
		<img src="${actionBean.targetProductItem.imgOriginal}" style="max-height:160px;"/>
	</display:column>
	<display:column title="Brand" property="brandName" />
	<display:column title="Category">
		<c:forEach var="ProdType" items="${actionBean.targetProductItem.typeName}">
			<div>${ProdType}</div>
		</c:forEach>
	</display:column>
	<display:column title="Product Name" property="productName" />
	<display:column title="Price" property="recommendedPrice" />
	<display:column title="AVG Rating" property="rating" />
</display:table>
<div>
	<s:form id="offsetLimit" beanclass="com.cyberlink.cosmetic.action.backend.product.ProdCommentRatingManageAction">
		<s:hidden name="productId" id="productId" value="${actionBean.productId}" />
		Total <b>${actionBean.commentList.totalSize}</b> comments in result.
		<br/>
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
		<s:submit name="submit" value="Go"/>
	</s:form>
</div>
<display:table id="comments" name="actionBean.commentList.results" requestURI="" defaultsort ="1">
	<display:column title="User ID" property="user.id" sortable="true" />
	<display:column title="User Avatar"  >
		<img src="${comments.user.avatarUrl}" style="max-height:120px;"/>
	</display:column>
	<display:column title="User Name" property="user.displayName" sortable="true" />
	<display:column title="User Comment" property="comment" sortable="true" />
	<display:column title="Gived Rating" property="rating" sortable="true" />
	<display:column title="Action">
		<s:link beanclass="com.cyberlink.cosmetic.action.backend.product.ProdCommentRatingManageAction" event="deleteComment"
		onclick="return confirm('Delete ${comments.id}?');">
			<s:param name="commentId" value="${comments.id}"/>
			Delete
		</s:link>
	</display:column>
</display:table>