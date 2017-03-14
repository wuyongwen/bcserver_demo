<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script type="text/javascript" src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script type="text/javascript" src="<c:url value="/post/post.js?v=${randVer}" />"></script>
<link href="<c:url value="/post/MultiForm.css?v=${randVer}" />" rel="stylesheet">

<div class="goTop" style="display: none;"></div>
<c:if test="${actionBean.isLogin == true}">
<c:if test="${actionBean.postStatus eq 'Hidden' || actionBean.postStatus eq 'Drafted'}">
<input name="publishPostBtn" id="publishPostBtn" type="button" value="Publish" pId="${actionBean.mainPost.postId}" isProm="${actionBean.mainPost.promoteScore > 0 ? 1 : 0}">
<c:if test="${actionBean.mainPost.creator.userType eq 'CL'}">
<input name="submitPostBtn" id="submitPostBtn" type="button" style="display:none;" "Submit" pId="${actionBean.mainPost.postId}">
</c:if>
</c:if>
</c:if>
<div class="qPostDiv">
	<c:if test="${actionBean.currentUserId == actionBean.mainPost.creator.userId || actionBean.currentUserAdmin || actionBean.accessControl.postManagerAccess == true}">
		<input type="button" class="postModify" sId="${actionBean.postId}" value="Modify">
		<input type="button" class="postDelete" sId="${actionBean.postId}" value="Delete">
		<c:if test="${actionBean.isTrendingPost eq true}">
			<input type="button" class="postRemove" sId="${actionBean.postId}" value="Remove">
		</c:if>
	</c:if>	
	<div class="lPostHeader">
		<div class="lAvatarDiv">
			<img class="lAvatar" src="${actionBean.mainPost.creator.avatar}"></img>
		</div>
		<div class="lNameDate">
			<label class="lUserName">${actionBean.mainPost.creator.displayName}</label>
			<label class="lLastModifiedDate"><fmt:formatDate value="${actionBean.mainPost.createdTime}" pattern="yyyy-MM-dd HH:mm" /></label>
		</div>
	</div>
	<div class="lPostTitleDiv">
		${actionBean.mainPost.title}
	</div>
	<div class="llPhotoBody">
		<c:choose>
		<c:when test="${fn:length(actionBean.mainPost.attachments.files) > 2}">
				<c:forEach items="${actionBean.mainPost.attachments.files}" var="file" varStatus="loop">
					<c:if test="${file.fileType eq 'Photo'}">
						<c:choose>
							<c:when test="${file.redirectUrl eq ''}">
								<img class="lPhoto" src="${file.downloadUrl}"></img>
							</c:when>
							<c:otherwise>
								<a href="${file.redirectUrl}"><img class="lPhoto" src="${file.downloadUrl}"></img></a>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:forEach>
			</c:when>
			<c:when test="${fn:length(actionBean.mainPost.attachments.files) > 1}">
				<c:forEach items="${actionBean.mainPost.attachments.files}" var="file" varStatus="loop">
					<c:if test="${file.fileType ne 'PostCover'}">
						<c:choose>
							<c:when test="${file.redirectUrl eq ''}">
								<img class="lPhoto" src="${file.downloadUrl}"></img>
							</c:when>
							<c:otherwise>
								<a href="${file.redirectUrl}"><img class="lPhoto" src="${file.downloadUrl}"></img></a>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${file.redirectUrl eq ''}">
						<img class="lPhoto" src="${actionBean.mainPost.attachments.files[0].downloadUrl}"></img>
					</c:when>
					<c:otherwise>
						<a href="${actionBean.mainPost.attachments.files[0].redirectUrl}"><img class="lPhoto" src="${actionBean.mainPost.attachments.files[0].downloadUrl}"></img></a>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="lPostContentDiv">
		${actionBean.mainPost.content}
	</div>
	<div class="lPostKeywordDiv">
		<c:if test="${not empty actionBean.mainPost.tags}">
			<c:if test="${not empty actionBean.mainPost.tags.keywords}">
				Keywords : [
				<c:forEach items="${actionBean.mainPost.tags.keywords}" var="word" varStatus="loopStatus">
					<c:if test="${loopStatus.index != 0}">
						,
					</c:if>
					${word}
				</c:forEach>
				]
			</c:if>
		</c:if>
	</div>
	<h1 class="divider">&nbsp;</h1>
	<c:forEach items="${actionBean.pageResult.results}" var="result" varStatus="loopStatus">
		<div>
			<c:if test="${actionBean.currentUserId == actionBean.mainPost.creator.userId || actionBean.currentUserAdmin || actionBean.accessControl.postManagerAccess == true}">
				<input type="button" class="postModify" sId="${result.subPostId}" value="Modify">
				<input type="button" class="postDelete" sId="${result.subPostId}" pId="${actionBean.postId}" value="Delete">
			</c:if>	
			<div class="llPhotoBody">
				<c:forEach items="${result.attachments.files}" var="file">
					<c:choose>
						<c:when test="${file.redirectUrl eq ''}">
							<img class="lPhoto" src="${file.downloadUrl}"></img>
						</c:when>
						<c:otherwise>
							<a href="${file.redirectUrl}"><img class="lPhoto" src="${file.downloadUrl}"></img></a>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
			<div class="lPostContentDiv">
				${result.content}
			</div>
			<h1 class="divider">&nbsp;</h1>
		</div>
	</c:forEach>
	<div class="lPostInteractDiv">
		<input class="lLikeAction" type="button" value="Like"><input class="lCommentAction"  type="button" value="Comment">
		<input type="button" class="listLikeAction" value="${actionBean.mainPost.likeCount} likes">
		<c:if test="${actionBean.mainPost.commentCount > 10}">
			<input type="button" class="listCommentAction" value="See ${actionBean.mainPost.commentCount - 10} more comments ..." tSize="${actionBean.mainPost.commentCount}" offset="10" pId="${actionBean.mainPost.postId}">
		</c:if>
	</div>
	<jsp:include page="/post/queryPost-list-comment.jsp" />
</div>