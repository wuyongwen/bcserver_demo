<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Product :: User Reported Product Review List</h2>
<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true}">
<s:form name="submittingRecord" id="submittingRecord" beanclass="com.cyberlink.cosmetic.action.backend.product.ReportedProdCommentManageAction">
<c:if test="${actionBean.sizeOfReviewedBannedCommentList > 0}">
<div>Banned comments below &amp; send notification to comment creator</div>
<display:table id="reviewedBannedCommentList" name="actionBean.reviewedBannedCommentList" requestURI="">
	<display:column title="Comment ID" property="id" sortable="true"/>
	<display:column title="Comment" >
		<s:hidden name="reviewedBannedCommentIdList" value="${reviewedBannedCommentList.id}" />
		${reviewedBannedCommentList.comment}
	</display:column>
</display:table>
</c:if>
<br>
<c:if test="${actionBean.sizeOfReviewedPublishedCommentList > 0}">
<div>Close user reports related to below comments since CL reviewed OK.</div>
<display:table id="reviewedPublishedCommentList" name="actionBean.reviewedPublishedCommentList" requestURI="">
	<display:column title="Comment ID" property="id" sortable="true"/>
	<display:column title="Comment">
		<s:hidden name="reviewedPublishedCommentIdList" value="${reviewedPublishedCommentList.id}" />
		${reviewedPublishedCommentList.comment}
	</display:column>
</display:table>
</c:if>
<c:if test="${actionBean.sizeOfReviewedBannedCommentList > 0 or actionBean.sizeOfReviewedPublishedCommentList > 0}">
<s:submit name="sendNotification" value="Send Out Notification & Update Status" id="sendNotification"/>
</c:if>
<c:if test="${actionBean.sizeOfReviewedBannedCommentList eq 0 and actionBean.sizeOfReviewedPublishedCommentList eq 0}">
No reviewed tickets......
</c:if>
</s:form>
</c:if>
<c:if test="${actionBean.currentUserAdmin == false and actionBean.accessControl.productManagerAccess == false}">
	Sorry, you have no authority to access this page
</c:if>
<script type="text/javascript">
	function changeLocale() {
		document.getElementById('offset').value = 0 ;
		document.getElementById('limit').value = 20 ;
        document.querySelectorAll("input[id=changePageOffset]")[0].click();
    }
</script>