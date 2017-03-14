<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="dateValue" class="java.util.Date"/>
<h2 class=ico_mug>Search :: Search Circle</h2>
<font style="color:red;">* is required Field</font>
<s:form name="searchCircleForm" id="searchCircleForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
			 	<s:options-collection collection="${actionBean.userLocales}" />
    		 </s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Keyword : <input type="text" id="keyword" name="keyword" style="width:250px;" value="${actionBean.keyword}" placeholder="please enter the keyword(e.g. eye)"/><font style="color:red;">*</font>
    &nbsp;&nbsp;<s:submit id="route" name="route" class="button">Search</s:submit>
    </br></br>
    <c:if test="${actionBean.maxPageNumber ne null}">
	Page Number : 	<s:select name="pageNumber" id="pageNumber"  onchange="location.href='searchCircle.action?route&locale=${actionBean.locale}&keyword=${actionBean.keyword}&pageNumber=' + this.value;" >
			 			<c:forEach var="i" begin="1" end="${actionBean.maxPageNumber}">
            				<option value="${i}" <c:if test="${actionBean.pageNumber == i}">selected</c:if>>${i}</option>
            			</c:forEach>
    		 		</s:select>
	</c:if>
</s:form>
<div>
	<display:table id="row" name="actionBean.circleViewResult.results" requestURI="searchCircle.action" sort="page" size="actionBean.circleViewResult.totalSize"  export="false">
		<display:column title="Id">
			${row.id}
		</display:column>
		<display:column title="Icon Image">
			<c:if test="${row.iconUrl ne null}">
				<a href="${row.iconUrl}" target="_blank"><img src="${row.iconUrl}" alt="${row.iconUrl}" height="40px"></a>
			</c:if>
		</display:column>
		<display:column title="Circle Name">
			${row.circleName}
		</display:column>
		<display:column title="Circle Type Id">
			${row.circleTypeId}
		</display:column>
		<display:column title="Description">
			${row.description}
		</display:column>
		<display:column title="PostThumbnails">
			<c:if test="${row.postThumbnails ne null}">
				<c:forEach var="postThumbnail" items="${row.postThumbnails}">
					<a href="${postThumbnail}" target="_blank"><img src="${postThumbnail}" alt="${postThumbnail}" height="40px"></a>
				</c:forEach>
			</c:if>
		</display:column>
		<display:column title="Is Editable">
			${row.isEditable}
		</display:column>
		<display:column title="Is Followed">
			${row.isFollowed}
		</display:column>
		<display:column title="Is secret">
			${row.isSecret}
		</display:column>
		<display:column title="Post Count">
			${row.postCount}
		</display:column>
		<display:column title="Follower Count">
			${row.followerCount}
		</display:column>
		<display:column title="Default Type">
			${row.defaultType}
		</display:column>
		<display:column title="Creator Id" >
			${row.circleCreatorId}
		</display:column>
		<display:column title="Creator Name">
			${row.creatorName}
		</display:column>
		<display:column title="Last Modified Time">
			<jsp:setProperty name="dateValue" property="time" value="${row.lastModified}"/>
			<fmt:formatDate value="${dateValue}" pattern="yyyy-MM-dd HH:mm:ss"/>
		</display:column>
	</display:table>
</div>