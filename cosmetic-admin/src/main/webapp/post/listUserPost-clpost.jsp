<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/post/spinner/spin.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/listclpost.js?v=${randVer}" />"></script>

<h2 class=ico_mug>Post :: CL-Posts</h2>
<div class=clearfix>
<s:form beanclass="${actionBean.class}" method="get">
</s:form>

<c:if test="${actionBean.isLogin == true}">
<script src="<c:url value="/common/lib/cropper/cropper.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/cover.js?v=${randVer}" />"></script>
<link href="<c:url value="/common/lib/cropper/cropper.css" />" rel="stylesheet">
<link href="<c:url value="/common/lib/cropper/cropper.min.css" />" rel="stylesheet">
<link href="<c:url value="/post/cover.css" />" rel="stylesheet">
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/post/ckeditor/ckeditor.js?v=${randVer}" />"></script>

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
<br/>
<div id="postProgress"></div>
</c:if>

<script langugage="JavaScript">
	CKEDITOR.replace( "shareContentInput" );
</script>

<br>
<label>Region : </label>
<select id="clPostLocaleSel">
<c:forEach items="${actionBean.availableRegion}" var="region" varStatus="loop">
<c:choose>
	<c:when test="${region eq actionBean.userLocale}">
		<option value="${region}" selected>${region}</option>
	</c:when>
	<c:otherwise>
		<option value="${region}">${region}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>
&nbsp;&nbsp;|&nbsp;&nbsp;
<label>User Type : </label>
<select id="clPostUserTypeSel">
	<c:choose>
		<c:when test="${actionBean.postType eq 'CL'}">
			<option value="CL" selected>CL</option>
			<option value="EXPERT">Expert</option>
		</c:when>
		<c:otherwise>
			<option value="CL">CL</option>
			<option value="EXPERT" selected>Expert</option>
		</c:otherwise>
	</c:choose>
</select>
&nbsp;&nbsp;|&nbsp;&nbsp;
<jsp:include page="/post/main-post-list.jsp">
    <jsp:param name="subPostLink" value="queryPost.action?clpost&postId="/>
    <jsp:param name="postStatus" value="${actionBean.postStatus}"/>
</jsp:include>

</div>
