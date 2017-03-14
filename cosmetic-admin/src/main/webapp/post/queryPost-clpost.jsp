<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Post :: CL-Posts</h2>
<div class=clearfix>
<s:form beanclass="${actionBean.class}" method="get">
</s:form>

<c:if test="${actionBean.isLogin == true}">

<input id="newSubPostBtn" type="button" value="Create New SubPost" pId="${actionBean.mainPost.postId}">
</c:if>

<jsp:include page="sub-post-list.jsp" />

</div>
