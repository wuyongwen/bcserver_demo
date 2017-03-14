<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/circle/circle.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Circle :: Circle Management</h2>
<div>
	<table class="form" width="100%">
        <tr>
          	<td width="5%">Circle Group :</td>
          	<td width="95%">
          		<input id="groupName" type="text" name="typeGroupName" value="${actionBean.typeGroupName}"/>
          		<div style="display:none;">
          			<input id="groupId" type="text" name="circleTypeGroupId" value="${actionBean.circleTypeGroupId}"/>
          		</div>
			</td>
        </tr>
        <tr>
          	<td width="5%">Sorting Order :</td>
          	<td width="95%">
          		<input id="typeGroupOrder" type="text" name="typeGroupOrder" value="${actionBean.typeGroupOrder}"/>
			</td>
        </tr>
        <tr style="display:none;">
			<td>Is Visible:</td>
			<td>
				<select name="isVisible" id="isVisible">
					<c:choose>
						<c:when test="${actionBean.isVisible}">
							<option value="false">No<option>
				    		<option value="true" selected>Yes<option>
						</c:when>
						<c:otherwise>
							<option value="false" selected>No<option>
				    		<option value="true">Yes<option>
						</c:otherwise>
					</c:choose>
			    <select>
			</td>
        </tr>
        <tr>
			<td>Icon File :</td>
	     	<td>
				<input id="iconInput" name="file" type="file" accept="image/*" iconId="${actionBean.iconId}">
			</td>
         </tr>
         <tr>
	     	<td>
				<img id="iconPreview" src="${actionBean.iconUrl}" style="max-width:180px;"/>
			</td>
         </tr>
         <tr>
			<td>Img File :</td>
	     	<td>
				<input id="imgInput" name="file" type="file" accept="image/*" imgUrl="${actionBean.imgUrl}">
			</td>
         </tr>
         <tr>
	     	<td>
				<img id="imgPreview" src="${actionBean.imgUrl}" style="max-width:180px;"/>
			</td>
         </tr>
        <tr>
			<td>Type Name:</td>
			<td>
		        <c:forEach items="${actionBean.availableLocale}" var="locale" varStatus="loop">
					<label>${locale} : </label><input class="typeName" id="${locale}" value="${actionBean.exTypeName[locale]}" />
					<c:if test="${actionBean.exTypeName[locale] ne null}">
						<c:choose>
							<c:when test="${!actionBean.exTypeVisible[locale]}">
								<input class="publish" type="button" value="Publish" id="${actionBean.exTypeId[locale]}"
								style="background-color: #4780AE; color: #FFF;"/>
							</c:when>
							<c:otherwise>
								<input class="unpublish" type="button" value="Unpublish" id="${actionBean.exTypeId[locale]}" 
								style="background-color: #162635; color: #FFF;" />
							</c:otherwise>
						</c:choose>
					</c:if>
					<br>
		        </c:forEach>
			</td>
        </tr>
        <tr>
	        <td></td>
	        <td><input type="button" id="create" value="create"/></td>
        </tr>
    </table>
</div>
<div id="progressDialog" title="Processing">
</div>