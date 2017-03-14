<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Product :: User Reported Product Review List</h2>
<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true or actionBean.accessControl.reportManagerAccess == true}">
<link rel="stylesheet" href="colorbox.css" />
<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js"></script>
<script src="../product/js/jquery.colorbox.js"></script>
<script>
	$(document).ready(function(){
		//Examples of how to assign the Colorbox event to elements
		$(".userInfo").colorbox({width:"640", height:"480"});
	});
</script>
<s:form name="searchParameter" id="searchParameter" beanclass="com.cyberlink.cosmetic.action.backend.product.ReportedProdCommentManageAction">
<div>
Locale&nbsp;
<s:select name="locale" id="locale" onchange="changeLocale()">
	<s:options-collection collection="${actionBean.localeList}" />
</s:select>
&nbsp;|&nbsp;Review Status&nbsp;
<s:select name="reviewStatus" id="reviewStatus">
	<s:option value="">--ALL--</s:option>
	<s:options-enumeration enum="com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentStatus" />
</s:select>
</div>
<div>
<s:hidden name="showOnlyReportedComments" id="showOnlyReportedComments" value="${actionBean.showOnlyReportedComments}"/>
Or you can find ALL Comments from user ID:<s:text name="userId" id="userId"/>
&nbsp;
<s:submit name="route" value="Search"/>
</div>
<div>
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
</div>
<div>
<display:table id="dbCommentList" name="actionBean.dbCommentList.results" requestURI="">
	<display:column title="Author" style="vertical-align: baseline;">
		<img style="vertical-align:top; width:16px; height:16px;" src="../images/user.png"><a class="userInfo" href="userDetailPage.jsp?creatorId=${dbCommentList.user.id}">${dbCommentList.user.displayName}</a>
	</display:column>
	<display:column title="Reportor" style="width:250px">
		<c:forEach var="ticket" items="${dbCommentList.reportedTickets}">
			<div><img style="vertical-align:top; width:16px; height:16px;" src="../images/user.png">
			<c:choose>
				<c:when test="${ticket.reporter.displayName eq '' or ticket.reporter.displayName eq null}">**N/A**</c:when>
				<c:otherwise>${ticket.reporter.displayName}</c:otherwise>
			</c:choose>
			<fmt:formatDate var="reportCreateTime" value="${ticket.createdTime}" dateStyle="medium" />
			, (${reportCreateTime}),<br>${ticket.reportReason}
			</div>
		</c:forEach>
	</display:column>
	<display:column title="Review Content" property="comment" />
	<display:column title="CL Review Status" >
		<c:set var="needReview" value="false"/>
		<c:forEach var="curTicket" items="${dbCommentList.reportedTickets}">
			<c:if test="${curTicket.reviewStatus eq 'NewReported'}">
				<c:set var="needReview" value="true"/>
			</c:if>
		</c:forEach>
		<c:if test="${needReview eq true and fn:length(dbCommentList.reportedTickets) gt 0}">
			New Reported
		</c:if>
		<c:if test="${needReview eq false and fn:length(dbCommentList.reportedTickets) gt 0}">
			Reviewed
		</c:if>
		<c:if test="${fn:length(dbCommentList.reportedTickets) eq 0}">
			N/A
		</c:if>
	</display:column>
	<display:column title="CL reviewer">
		<c:set var="haveReviewer" value="false"/>
		<c:set var="reviewerName" value=""/>
		<c:forEach var="curTicket" items="${dbCommentList.reportedTickets}">
			<c:if test="${curTicket.reviewStatus eq 'Reviewed'}">
				<c:set var="haveReviewer" value="true"/>
				<c:set var="reviewerName" value="${curTicket.reviewer.displayName}"/>
			</c:if>
		</c:forEach>
		<c:if test="${haveReviewer eq true }">
			${reviewerName}
		</c:if>
		<c:if test="${haveReviewer eq false }">
			N/A
		</c:if>
		<c:remove var="haveReviewer"/>
		<c:remove var="reviewerName"/>
	</display:column>
	<display:column title="CL Review Decision">
		<c:if test="${needReview eq true or fn:length(dbCommentList.reportedTickets) eq 0}">
			<s:hidden name="reportCommentId" value="${dbCommentList.id}" />
			<s:select name="ticketReviewResult">
				<s:option value="notReviewed">--Please Select--</s:option>
				<s:options-enumeration enum="com.cyberlink.cosmetic.modules.product.model.ReportedProdCommentResult" />
			</s:select>
		</c:if>
		<c:if test="${needReview eq false }">
			<c:set var="reviewedStatus" value=""/>
			<c:forEach var="curTicket" items="${dbCommentList.reportedTickets}">
				<c:set var="reviewedStatus" value="${curTicket.reviewResult}"></c:set>
			</c:forEach>
			<div>${reviewedStatus}</div>
		</c:if>
	</display:column>
</display:table>
</div>
<c:if test="${actionBean.dbCommentList.totalSize > 0}">
Submit your changes <s:submit name="updateProdCommentStatus" value="Submit" id="updateProdCommentStatus"/>
</c:if>
</s:form>
</c:if>
<c:if test="${actionBean.currentUserAdmin == false and actionBean.accessControl.productManagerAccess == false and 
actionBean.accessControl.reportManagerAccess == false}">
	Sorry, you have no authority to access this page
</c:if>
<script type="text/javascript">
	function changeLocale() {
		document.getElementById('userId').value = '' ;
		document.getElementById('showOnlyReportedComments').value = '' ;
        document.querySelectorAll("input[id=changePageOffset]")[0].click();
    }
</script>