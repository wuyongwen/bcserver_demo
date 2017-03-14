<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div id="postCommentsDiv">
	<c:forEach items="${actionBean.commentPageResult.results}" var="comment" varStatus="loopStatus">
		<div class="lPostComment">
			<c:if test="${actionBean.isAdmin == true}">
				<input type="button" class="commentDelete" sId="${comment.refId}" cId="${comment.commentId}" uId="${comment.creator.userId}" value="Delete">
			</c:if>
			<div class="commentAvatarDiv">
				<img class="lCmtAvatar" src="${comment.creator.avatar}"></img>
			</div>
			<div class="commentContentBody">
				<label class="lCmtUserName">${comment.creator.displayName}</label>
				<label class="lCmtComment">${comment.comment}</label>
				<div class="lCmtFooterDiv">
					<label class="lCmtLastModifiedDate">${comment.lastModified}</label>
					<s:link class="lCmtLikeAction" href="">
						<s:param name="commentId" value="${comment.commentId}"/>
			            <c:out value="Like"/>
       				</s:link>
				</div>
			</div>
		</div>
	</c:forEach>
</div>