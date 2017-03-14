<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="dateValue" class="java.util.Date"/>
<h2 class=ico_mug>Search :: Search People</h2>
<font style="color:red;">* is required Field</font>
<s:form name="searchPeopleForm" id="searchPeopleForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
			 	<s:options-collection collection="${actionBean.userLocales}" />
    		 </s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Keyword : <input type="text" id="keyword" name="keyword" style="width:250px;" value="${actionBean.keyword}" placeholder="please enter the keyword(e.g. jimmy)" /><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	User Type : <s:select name="type" id="type">
					<option value="none">None</option>
					<s:options-collection collection="${actionBean.userTypeList}" />
  				</s:select>
    &nbsp;&nbsp;<s:submit id="route" name="route" class="button">Search</s:submit>
    </br></br>
    <c:if test="${actionBean.maxPageNumber ne null}">
	Page Number : 	<s:select name="pageNumber" id="pageNumber" onchange="location.href='searchPeople.action?route&locale=${actionBean.locale}&keyword=${actionBean.keyword}&pageNumber=' + this.value +'&type=${actionBean.type}';" >
			 			<c:forEach var="i" begin="1" end="${actionBean.maxPageNumber}">
            				<option value="${i}" <c:if test="${actionBean.pageNumber == i}">selected</c:if>>${i}</option>
            			</c:forEach>
    		 		</s:select>
	</c:if>
</s:form>
<div>
	<display:table id="row" name="actionBean.userViewsResult.results" requestURI="searchPeople.action" sort="page" size="actionBean.userViewsResult.totalSize"  export="false">
		<display:column title="Id">
			${row.id}
		</display:column>
		<display:column title="Display Name">
			${row.displayName}
		</display:column>
		<display:column title="Avatar Image" >
			<c:if test="${row.avatarUrl ne null}">
				<a href="${row.avatarUrl}" target="_blank"><img src="${row.avatarUrl}" alt="${row.avatarUrl}" height="40px"></a>
			</c:if>
		</display:column>
		<display:column title="Follower Count">
			${row.followerCount}
		</display:column>
		<display:column title="Post Count">
			${row.postCount}
		</display:column>
		<display:column title="User Type">
			${row.userType}
		</display:column>
		<display:column title="UniqueId">
			${row.uniqueId}
		</display:column>
	</display:table>
</div>