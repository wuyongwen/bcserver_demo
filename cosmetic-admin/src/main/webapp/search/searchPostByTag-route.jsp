<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="dateValue" class="java.util.Date"/>
<h2 class=ico_mug>Search :: Search Post By Tag</h2>
<font style="color:red;">* is required Field</font>
<s:form name="searchPostByTagForm" id="searchPostByTagForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
			 	<s:options-collection collection="${actionBean.userLocales}" />
    		 </s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Tag : <input type="text" id="tag" name="tag" style="width:250px;" value="${actionBean.tag}" placeholder="please enter the tag value(e.g. beauty)" /><font style="color:red;">*</font>
  	&nbsp;&nbsp;<s:submit id="route" name="route" class="button">Search</s:submit>&nbsp;&nbsp;
    </br></br>
    <c:if test="${actionBean.maxPageNumber ne null}">
	Page Number : 	<s:select name="pageNumber" id="pageNumber" onchange="location.href='searchPostByTag.action?route&locale=${actionBean.locale}&tag=${actionBean.tag}&pageNumber=' + this.value;" >
			 			<c:forEach var="i" begin="1" end="${actionBean.maxPageNumber}">
            				<option value="${i}" <c:if test="${actionBean.pageNumber == i}">selected</c:if>>${i}</option>
            			</c:forEach>
    		 		</s:select>
	</c:if>
</s:form>
<div>
	<display:table id="row" name="actionBean.postViewResult.results" requestURI="searchPostByTag.action" sort="page" size="actionBean.postViewResult.totalSize"  export="false">
		<display:column title="Post Id" style="width:160px;">
			${row.postId}
		</display:column>
		<display:column title="Locale" style="width:60px;">
			<c:if test="${actionBean.extraPostDataMap[row.postId] ne null}">
				${actionBean.extraPostDataMap[row.postId]['locale']}
			</c:if>
		</display:column>
		<display:column title="Creator Id" style="width:80px;">
			${row.creator.userId}
		</display:column>
		<display:column title="Circle(Id:Name)" style="width:180px;">
			<c:if test="${row.circles ne null}">
				<c:forEach var="circle" items="${row.circles}">
					[${circle.circleId} : ${circle.circleName}]&nbsp;
				</c:forEach>
			</c:if>
		</display:column>
		<display:column title="Post Image">
			<c:if test="${actionBean.extraPostDataMap[row.postId] ne null}">
				<a href="${actionBean.extraPostDataMap[row.postId]['postImageUrl']}" target="_blank"><img src="${actionBean.extraPostDataMap[row.postId]['postImageUrl']}"  height="40px"></a>
			</c:if>
		</display:column>
		<display:column title="Title">
			${row.title}
		</display:column>
		<display:column title="Status" style="width:70px;">
			${row.status}
		</display:column>
		<display:column title="created Time" style="width:60px;">
				<jsp:setProperty name="dateValue" property="time" value="${row.createdTime}"/>
				<fmt:formatDate value="${dateValue}" pattern="yyyy-MM-dd HH:mm:ss"/>
		</display:column>
	</display:table>
</div>