<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Post :: Post Management</h2>
<div class=clearfix>
        
<c:if test="${actionBean.isLogin == true}">
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/common/lib/cropper/cropper.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/cover.js?v=${randVer}" />"></script>
<link href="<c:url value="/common/lib/cropper/cropper.css" />" rel="stylesheet">
<link href="<c:url value="/common/lib/cropper/cropper.min.css" />" rel="stylesheet">
<link href="<c:url value="/post/cover.css" />" rel="stylesheet">
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/post/ckeditor/ckeditor.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/spinner/spin.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/listwallpost.js?v=${randVer}" />"></script>

<script langugage="JavaScript">
    function route()
    {
		window.location.href = "./CreatePost.action?postForm=mainpost";
    }    
</script>
<input id="newMainPostBtn" type="button" value="Create New Post" onclick="route()">
<c:if test="${actionBean.isAdmin == true}">
	<jsp:include page="/post/create_share_link.jsp" />
</c:if>
<c:if test="${actionBean.isAdmin == true || actionBean.accessControl.postManagerAccess == true}">
	<br><label>Search By Creator ID : </label><input id="searchCreatorIdInput" type="number" value="${actionBean.searchCreatorId}"><input id="searchByCreatorBtn" type="button" value="Search">
</c:if>
<br/>
<div id="postProgress"></div>
</c:if>

<script langugage="JavaScript">
	CKEDITOR.replace( "shareContentInput", 100 );
</script>

<jsp:include page="/post/main-post-list.jsp">
    <jsp:param name="subPostLink" value="queryPost.action?postId="/>
    <jsp:param name="postStatus" value="${actionBean.postStatus}"/>
</jsp:include>

</div>
