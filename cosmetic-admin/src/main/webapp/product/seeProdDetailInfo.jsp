<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div>Product Detail Info</div>
<s:form id="table" beanclass="com.cyberlink.cosmetic.action.backend.product.ProductManageAction">
	<s:hidden name="productId" value="${actionBean.productId}" />
	<s:hidden name="locale" value="${actionBean.locale}" />
	<table>
		<tr>
			<td style="width:150px;">Product ID</td>
			<td style="width:500px">${actionBean.productItem.id}</td>
		</tr>
		<tr style="background-color:#def;">
			<td>Locale</td>
			<td>${actionBean.productItem.locale}</td>
		</tr>
		<tr>
			<td>Brand</td>
			<td>${actionBean.productItem.brand.brandName}</td>
		</tr>
		<tr style="background-color:#def;">
			<td>Category</td>
			<td>
				<c:forEach var="relProdType" items="${actionBean.productItem.relProductType}">
					<div>${relProdType.productType.typeName}</div>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td>Product Title</td>
			<td>${actionBean.productItem.productTitle}</td>
		</tr>
		<tr style="background-color:#def;">
			<td>Product Description</td>
			<td width="100" height="60">${actionBean.productItem.productDescription}</td>
		</tr>
		<tr>
			<td>Original Product Image</td>
			<td><img style="max-height:200px;" src="${actionBean.productItem.img_original}" /></td>
		</tr>
		<tr style="background-color:#def;">
			<td>Product Image Thumbnail</td>
			<td><img style="max-height:200px;" src="${actionBean.productItem.img_thumbnail}" /> </td>
		</tr>
		<tr>
			<td>Parameter to try on YouCamMakeUp</td>
			<td>${actionBean.productItem.trialOnYCMakeUp}</td>
		</tr>
		<tr style="background-color:#def;">
			<td>Product Store Provider</td>
			<td>${actionBean.productItem.store.storeName}</td>
		</tr>
		<tr>
			<td>Product Price</td>
			<td>${actionBean.productItem.price}</td>
		</tr>
		<tr style="background-color:#def;">
			<td>Product Store Link</td>
			<td><a target="_blank" href="${actionBean.productItem.productStoreLink}">${actionBean.productItem.productStoreLink}</a></td>
		</tr>
		<tr>
			<td>External Product ID</td>
			<td>${actionBean.productItem.extProdID}</td>
		</tr>
		<tr style="background-color:#def;">
			<td>On Shelf?</td>
			<td>${actionBean.productItem.onShelf}</td>
		</tr>
	</table>
	<s:submit name="updateProdRequest" value="Edit"/>
</s:form>