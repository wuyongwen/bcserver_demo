<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/post/defaultTag.js?v=${randVer}" />"></script>

<h2 class=ico_mug>Post :: Post Default Tag Management</h2>
<div>
	<table class="form" width="100%">
		<tr>
			<td width="5%">Default Tag :</td>
		</tr>
        <tr>
        	<td>Tag Name :</td>
          	<td width="95%">
          		<input id="tagName" type="text" name="tagName" value="${actionBean.tagName}"/>
          		<div style="display:none;">
          			<input id="defaultTagId" type="text" name="defaultTagId" value="${actionBean.defaultTagId}"/>
          		</div>
			</td>
        </tr>
        <tr>
        	<td>Tag Locale :</td>
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
	        	<select name="isDeleted" id="isDeleted">
	        		<c:choose>
						<c:when test="${actionBean.isDeleted}">
							<option value="true" >Yes</option>
			    			<option value="false" selected >No</option>
						</c:when>
						<c:otherwise>
							<option value="true" selected >Yes</option>
			    			<option value="false" >No</option>
						</c:otherwise>
					</c:choose>
			    </select>
        	</td>
        </tr>
        <tr>
	        <td><input type="button" id="create" value="create"/></td>
        </tr>
    </table>
</div>
<div id="progressDialog" title="Processing">
</div>