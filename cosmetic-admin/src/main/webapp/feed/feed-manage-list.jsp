<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<link href="<c:url value="/post/MultiForm.css" />" rel="stylesheet">
<script type="text/javascript" src="<c:url value="/common/lib/general.js" />"></script>
<script type="text/javascript" src="<c:url value="/post/post.js" />"></script>

<%
String subPostLink = "../post/queryPost.action?postId=";
%>

<h2 class=ico_mug>Feed :: List My Feed</h2>

<s:form beanclass="${actionBean.class}" method="get">
    UserId
    <s:text name="userId" />
    &nbsp;<s:submit name="list" value="Go"/>
</s:form>

<div class="goTop" style="display: none;"></div>
<display:table id="row" name="actionBean.pageResult.results" requestURI="feed-manage.action" pagesize="20" sort="external" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
<display:column title="Post">
    <c:choose>
        <c:when test="${fn:length(row.attachments.files[0].redirectUrl) > 0}">
            <div class="lPostDiv" href="${row.attachments.files[0].redirectUrl}" target="_blank">
        </c:when>
        <c:otherwise>
            <div class="lPostDiv" href="<%=subPostLink%>${row.postId}">
        </c:otherwise>
    </c:choose>
    <div class="lPostHeader">
        <div class="lAvatarDiv">
            <img class="lAvatar" src="${row.creator.avatar}"></img>
        </div>
        <div class="lNameDate">
            <label class="lUserName">${row.creator.displayName}</label>
            <label class="lLastModifiedDate"><fmt:formatDate value="${row.lastModified}" pattern="yyyy-MM-dd HH:mm" /></label>
        </div>
    </div>
    <div class="lPostTitleDiv">
        <c:out value="${row.title}"/>
    </div>
    <div class="llCoverBody">
        <img class="lCover" src="${row.attachments.files[0].downloadUrl}"></img>
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
