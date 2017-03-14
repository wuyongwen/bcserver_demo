<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="dateValue" class="java.util.Date"/>
<h2 class=ico_mug>Search :: Search Post</h2>
<font style="color:red;">* is required Field</font>
<s:form name="searchPostForm" id="searchPostForm" beanclass="${actionBean.class}">
  	Post Id : <input type="text" id="postId" name="postId" style="width:200px;" value="" placeholder=""  onkeyup="value=value.replace(/[^\d]/g,'')"/><font style="color:red;">*</font>
  	<hr style="width:100%;"/>
	Locale : <s:select name="locale" id="locale">
			 	<s:options-collection collection="${actionBean.userLocales}" />
    		 </s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Keyword : <input type="text" id="keyword" name="keyword" style="width:250px;" value="${actionBean.keyword}" placeholder="please enter the keyword(e.g. beauty)" /><font style="color:red;">*</font>
  	&nbsp;&nbsp;</br><s:submit id="route" name="route" class="button">Search</s:submit>&nbsp;&nbsp;
    </br></br>
    <c:if test="${actionBean.maxPageNumber ne null}">
	Page Number : 	<s:select name="pageNumber" id="pageNumber" onchange="location.href='searchPost.action?route&locale=${actionBean.locale}&keyword=${actionBean.keyword}&pageNumber=' + this.value;" >
			 			<c:forEach var="i" begin="1" end="${actionBean.maxPageNumber}">
            				<option value="${i}" <c:if test="${actionBean.pageNumber == i}">selected</c:if>>${i}</option>
            			</c:forEach>
    		 		</s:select>
	</c:if>
</s:form>
<div>
	<c:if test="${actionBean.post ne null}">
	 	<display:table id="row" name="actionBean.post" requestURI="searchPost.action" sort="page" size="1"  export="false">
			<display:column title="Post Id" style="width:160px;">
				<a href="javascript:window.open('../post/queryPost.action?postId=${row.id}')">${row.id}</a>
			</display:column>
			<display:column title="Locale" style="width:60px;">
				${row.locale}
			</display:column>
			<display:column title="Creator Id" style="width:80px;">
				${row.creatorId}
			</display:column>
			<display:column title="Circle[Id : Name]" style="width:180px;">
				<c:if test="${row.circle ne null}">
						[${row.circle.id} : ${row.circle.circleName}]
				</c:if>
			</display:column>
			<display:column title="Post Image">
				<a href="${actionBean.postImageUrl}" target="_blank"><img src="${actionBean.postImageUrl}"  height="40px"></a>
			</display:column>
			<display:column title="Title">
				${row.title}
			</display:column>
			<display:column title="Status" style="width:70px;">
				${row.postStatus}
			</display:column>
			<display:column title="created Time" style="width:60px;">
					<fmt:formatDate value="${row.createdTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</display:column>
		</display:table>
	</c:if>
	<c:if test="${actionbean.post eq null}">
		<display:table id="row" name="actionBean.postViewResult.results" requestURI="searchPost.action" sort="page" size="actionBean.postViewResult.totalSize"  export="false">
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
	</c:if>
</div>