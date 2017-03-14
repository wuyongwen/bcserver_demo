<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%><%@ include
	file="/common/taglibs.jsp"%>

<h2 class=ico_mug>User :: Reported Users</h2>
<div class=clearfix>
	<s:form beanclass="${actionBean.class}" method="get">
		<label>Region : </label>
		<s:select name="selRegion" id="selRegion">
			<s:option value="">--ALL--</s:option>
			<s:options-collection collection="${actionBean.userLocaleList}" />
		</s:select>
		&nbsp;&nbsp;|&nbsp;&nbsp;
		<label>Review Status : </label>
		<s:select name="selStatus" id="selStatus">
			<s:option value="REPORTED">NewReported</s:option>
			<s:option value="REVIEWING">Reviewing</s:option>
			<s:option value="REVIEWED">Reviewed</s:option>
			<s:option value="BANNED">Banned</s:option>
		</s:select>
		&nbsp;&nbsp;|&nbsp;&nbsp;
		<label>Reported Reason : </label>
		<c:if test="${actionBean.selStatus eq 'REVIEWING'}">
			<s:select name="selReason" id="selReason">
			<s:option value="PRETENDING">Pretending</s:option>
		</s:select>	
		</c:if>
		<c:if test="${actionBean.selStatus ne 'REVIEWING'}">
			<s:select name="selReason" id="selReason">
				<s:option value="SPAMMING">Spamming</s:option>
				<s:option value="GRAPHIC">Graphic & Sexual</s:option>
				<s:option value="ABUSIVE">Abusive & Harmful</s:option>
				<s:option value="PRETENDING">Pretending</s:option>
			</s:select>
		</c:if>
		<br />
		<label>Reporter ID : </label>
		<s:text style="width:100px;" name="reporterId"/>
		&nbsp;&nbsp;
		<s:submit class="button" name="list" value="search" />
	</s:form>	
	<display:table id="row" name="actionBean.pageResult.results" requestURI="reportedUser.action" pagesize="${actionBean.pageSize}" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
	    <display:column title="UserId" sortable="true" style="width:10%;">
	        <c:out value="${row.user.id}"/>
	    </display:column>
	    <display:column title="Avatar" sortable="false" style="width:10%;">
	        <img src="${row.user.avatarUrl}" height="50" width="50" />
	    </display:column>
	    <display:column title="Info" sortable="true" sortName="displayName" style="width:20%;">
	        DisplayName: ${row.user.displayName}<br>
	        Email: ${row.user.allEmailAccountList[0].email}<br>
	        Gender: ${row.user.gender}<br>	        
	    </display:column>
	    <display:column title="Region" sortable="false" style="width:10%;">
	        <c:out value="${row.user.region}"/>
	    </display:column>
	    <c:if test="${actionBean.selStatus eq 'REPORTED'}">
	    	<display:column title="Reporter" sortable="false" style="width:20%;">
	       		<c:out value="${row.reporter}"/>
	    	</display:column>
	    </c:if>
	    <c:if test="${actionBean.selStatus ne 'REPORTED'}">
	    	<display:column title="Reviewer" sortable="false" style="width:20%;">
	       		<c:out value="${row.reviewer}"/>
	    	</display:column>
	    </c:if>
	    <c:if test="${actionBean.selStatus eq 'REPORTED'}">
	    	<c:if test="${actionBean.selReason ne 'PRETENDING'}">
		    	<display:column title="Action" sortable="false" style="width:20%;">
		        	<input type="button" class="reviewedBtn" value="Reviewed" style="width: auto;" targetId="${row.user.id}">
		        	<input type="button" class="deleteBtn" value="delete" style="width: auto;" targetId="${row.user.id}">
		        	<input type="button" class="bannedBtn" value="Banned" style="width: auto;" targetId="${row.user.id}">
		    	</display:column>
	    	</c:if>
	    	<c:if test="${actionBean.selReason eq 'PRETENDING'}">
		    	<display:column title="Action" sortable="false" style="width:20%;">
		        	<input type="button" class="reviewedBtn" value="Reviewed" style="width: auto;" targetId="${row.user.id}">
		        	<input type="button" class="investigateBtn" value="Investigate" style="width: auto;" targetId="${row.user.id}">
		    	</display:column>
	    	</c:if>	
        </c:if>
        <c:if test="${actionBean.selStatus eq 'REVIEWING'}">
	    	<c:if test="${actionBean.selReason eq 'PRETENDING'}">
		    	<display:column title="Action" sortable="false" style="width:20%;">
		        	<input type="button" class="reviewedBtn" value="Reviewed" style="width: auto;" targetId="${row.user.id}">
		        	<input type="button" class="deleteBtn" value="delete" style="width: auto;" targetId="${row.user.id}">
		        	<input type="button" class="bannedBtn" value="Banned" style="width: auto;" targetId="${row.user.id}">
		    	</display:column>
	    	</c:if>
        </c:if>	
	</display:table>
</div>

<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/user/reportedUser.js?v=${randVer}" />"></script>