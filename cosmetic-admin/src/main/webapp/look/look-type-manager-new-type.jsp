<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/look/look.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Look :: Look Type Management</h2>
<div>
	<table class="form" width="100%">
		<tr>
			<td width="5%">Look Type :</td>
		</tr>
        <tr>
        	<td>Type Name :</td>
          	<td width="95%">
          		<input id="typeName" type="text" name="typeName" value="${actionBean.typeName}"/>
          		<div style="display:none;">
          			<input id="lookTypeId" type="text" name="lookTypeId" value="${actionBean.lookTypeId}"/>
          		</div>
			</td>
        </tr>
        <tr>
        	<td>Type Code Name :</td>
          	<td width="95%">
          		<input id="typeCodeName" type="text" name="typeCodeName" value="${actionBean.typeCodeName}"/><br>
          		<b>(Rule : Type name (in language en_US) in upper case, replace space with "_". Ex: "New Type"/"新類別" = "NEW_TYPE")</b>
			</td>
        </tr>
        <tr>
        	<td>Type Locale :</td>
        	<td>
	        	<select name="locale" id="locale">
				    <c:forEach items="${actionBean.availableLocale}" var="loc">
				    	<option value="${loc}" ${loc == actionBean.locale ? 'selected' : ''}>${loc}</option>
				    </c:forEach>
			    </select>
        	</td>
        </tr>
        <tr>
        	<td>Visible :</td>
        	<td>
	        	<select name="isVisible" id="isVisible">
	        		<c:choose>
						<c:when test="${actionBean.isVisible}">
							<option value="true" selected >Yes</option>
			    			<option value="false" >No</option>
						</c:when>
						<c:otherwise>
							<option value="true" >Yes</option>
			    			<option value="false" selected >No</option>
						</c:otherwise>
					</c:choose>
			    </select>
        	</td>
        </tr>
        <tr>
			<td>Background Image File :</td>
	     	<td>
				<input id="iconInput" name="file" type="file" accept="image/*" iconId="${actionBean.iconId}">
			</td>
         </tr>
         <tr>
	     	<td>
				<img id="iconPreview" src="${actionBean.iconUrl}"/>
			</td>
         </tr>
        <tr>
	        <td><input type="button" id="create" value="create"/></td>
        </tr>
    </table>
</div>
<div id="progressDialog" title="Processing">
</div>