<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.11.2.min.js"></script>
<b>Edit Product Info</b><br/><br/>
<s:form id="inputForm" beanclass="com.cyberlink.cosmetic.action.backend.product.ProductManageAction">
	<s:hidden name="productId" value="${actionBean.productId}" />
	<s:hidden name="locale" value="${actionBean.locale}" />
	<table>
		<tr>
			<td style="width:200px;">Product ID</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.id}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Locale</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.locale}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Brand</td>
			<td style="width:10px"></td>
			<td>
				<s:select name="brandId" id="brandId" value="${actionBean.productItem.brand.id}">
					<s:options-collection collection="${actionBean.brandList}" value="id" label="brandName"/>
				</s:select>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Category</td>
			<td style="width:10px"></td>
			<td>
				<c:set var="i" value="0"></c:set>
				<c:forEach var="relProdType" items="${actionBean.productItem.relProductType}">
					<div id="typeDropBox_${i}">
						<s:select name="newProdTypeId" id="newProdTypeId" value="${relProdType.productType.id}">
							<s:options-collection collection="${actionBean.prodTypeList}" value="id" label="typeName"/>
						</s:select>
						&nbsp;
						<c:if test="${i gt 0}">
							<s:button name="delCategory" onclick="RemoveThisDiv($(this))" value="Delete Category" />
						</c:if>
						<c:set var="i" value="${i+1}"></c:set>
					</div>
					
					<c:if test="${i eq actionBean.prodTypeSize}">
						<span id="additionalBox"></span>
						<s:button name="addNewSelectionBox" onclick="addCategory('${actionBean.productItem.locale}')" value="Add Category" />
					</c:if>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Product Title</td>
			<td style="width:10px"></td>
			<td><s:text name="productTitle" value="${actionBean.productItem.productTitle}" size="100" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Product Description</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.productDescription}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Original Product Image</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.img_original}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Product Image Thumbnail</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.img_thumbnail}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Parameter to try on YouCamMakeUp</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.trialOnYCMakeUp}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Product Store Provider</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.store.storeName}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Product Price</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.price}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>Product Store Link</td>
			<td style="width:10px"></td>
			<td><a target="_blank" href="${actionBean.productItem.productStoreLink}">${actionBean.productItem.productStoreLink}</a></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>External Product ID</td>
			<td style="width:10px"></td>
			<td>${actionBean.productItem.extProdID}</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>On Shelf?</td>
			<td style="width:10px"></td>
			<td>
				<s:select name="onShelf" id="onShelf" value="${actionBean.productItem.onShelf}">
					<s:option value="true">Yes</s:option>
					<s:option value="false">No</s:option>
				</s:select>
			</td>
		</tr>
	</table>
	<s:submit name="submitProductUpdates" value="submit"/>
	<!--<s:button name="" value="cancel" onclick="javascript:history.back();"/>-->
</s:form>
<script>
function addCategory( locale ){
	var target = '#additionalBox' ;
	$.getJSON( '/backend/product/ProductManage.action?addNewSelectionBox',
	{"locale":locale,},
	function (data) {
		if (data) {
			var selectionBox ;
			selectionBox = '<div id="typeDropBox' + '">';
			selectionBox += '<select name="newProdTypeId" id="newProdTypeId">' ;
			for(var i = 0; i<data.length; i++){
				selectionBox += '<option value="' + data[i].id + '">' + data[i].typeName + '</option>' ;
			}
			selectionBox += '</select>&nbsp;&nbsp;&nbsp;' ;
			//add delete button
			selectionBox += '<input type="button" name="delCategory" onclick="RemoveThisDiv(' + '$(this)' + ')" value="Delete Category" />' ;
			selectionBox += '</div>' ;
			$(target).append(selectionBox);
		}
	});
	
}

function RemoveThisDiv( obj ){
	obj.parent().remove();
}

function RemoveDiv( index ){
	var i = '#typeDropBox_' + index ;
 	$(i).remove(); 
}
</script>