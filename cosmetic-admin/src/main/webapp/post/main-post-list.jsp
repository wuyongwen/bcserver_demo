<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<link href="<c:url value="/post/MultiForm.css?v=${randVer}" />" rel="stylesheet">
<script type="text/javascript" src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script type="text/javascript" src="<c:url value="/post/attachment.js?v=${randVer}" />"></script>
<script type="text/javascript" src="<c:url value="/post/post.js?v=${randVer}" />"></script>

<label>Post Status : </label>
<select id="postStatusSel">
<c:forEach items="${actionBean.availablePostStatus}" var="postStatus" varStatus="loop">
<c:choose>
	<c:when test="${postStatus eq actionBean.postStatus}">
		<option value="${postStatus}" selected>${postStatus}</option>
	</c:when>
	<c:otherwise>
		<option value="${postStatus}">${postStatus}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>

<s:form beanclass="${actionBean.class}" method="post">
<div class="goTop" style="display: none;"></div>
<display:table id="row" name="actionBean.pageResult.results" requestURI="listUserPost.action" pagesize="20" sort="external" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
<display:column title="Post" sortable="true" sortName="row.createdTime">
	<div class="lPostDiv" href="${param.subPostLink}${row.postId}">
	<c:if test="${actionBean.isOwnerUser == true}">
		<c:if test="${param.postStatus eq 'Drafted' && row.creator.userType eq 'CL'}">
			<input name="submitPostBtn" id="submitPostBtn" type="button" value="Submit" pId="${row.postId}">
		</c:if>
		<c:if test="${param.postStatus eq 'Hidden' || param.postStatus eq 'Drafted'}">
			<input name="publishPostBtn" id="publishPostBtn" type="button" value="Publish" pId="${row.postId}" isProm="${row.promoteScore > 0 ? 1 : 0}")>
		</c:if>
		<input type="button" class="postModify" sId="${row.postId}" value="Modify">
		<input type="button" class="postDelete" sId="${row.postId}" value="Delete">
	</c:if>	
	<div class="lPostHeader">
		<div class="lAvatarDiv">
			<img class="lAvatar" src="${row.creator.avatar}"></img>
		</div>
		<div class="lNameDate">
			<label class="lUserName">${row.creator.displayName}</label>
			<label class="lLastModifiedDate"><fmt:formatDate value="${row.createdTime}" pattern="yyyy-MM-dd HH:mm" /></label>
		</div>
	</div>
	<div class="lPostTitleDiv">
		<c:if test="${row.promoteScore > 0}">
			<font color="red">[Promoted]</font>
		</c:if>
		<c:out value="${row.title}"/>
	</div>
	<div class="llCoverBody">
		<c:if test="${fn:length(row.attachments.files) gt 0}">
			<img class="lCover" src="${row.attachments.files[0].downloadUrl}"></img>
		</c:if>
	</div>
	<div class="lPostContentDiv">
		${row.content}
	</div>
	<div class="lLikeCountDiv">
		${row.likeCount} likes&nbsp;&nbsp;|&nbsp;&nbsp;${row.commentCount} comments
	</div>
	<div class="lPostInteractDiv">
		<input class="lLikeAction" type="button" value="Like"><input class="lCommentAction"  type="button" value="Comment">
	</div>
</div>
</display:column>
</display:table>
</s:form>

