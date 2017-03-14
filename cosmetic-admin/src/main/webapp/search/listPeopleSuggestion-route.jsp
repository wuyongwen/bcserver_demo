<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Search :: List People Suggestion</h2>
<font style="color:red;">* is required Field</font>
<s:form name="listPeopleSuggestionForm" id="listPeopleSuggestionForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
				<s:options-collection collection="${actionBean.userLocales}" />
    		</s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Keyword : <input type="text" id="keyword" name="keyword" style="width:250px;" value="${actionBean.keyword}" placeholder="please enter the keyword(e.g. jimmy)" /><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	User Type : <s:select name="type" id="type">
					<option value="none">None</option>
					<s:options-collection collection="${actionBean.userTypeList}" />
   				</s:select>
    &nbsp;&nbsp;<s:submit name="route" class="button">Search</s:submit>
</s:form>
<br/>
<div>
	<display:table id="row" name="actionBean.suggestUserViewResult.results" requestURI="listPeopleSuggestion.action" sort="page" size="actionBean.suggestUserViewResult.totalSize" >
		<display:column title="Id" style="line-height:40px;width:200px;">
			${row.id}
		</display:column>
		<display:column title="Avatar Image" style="line-height:40px;text-align:center;width:60px;">
			<c:if test="${row.avatarUrl ne null}">
				<a href="${row.avatarUrl}" target="_blank"><img src="${row.avatarUrl}" alt="${row.avatarUrl}" height="40px"></a>
			</c:if>
		</display:column>
		<display:column title="Display Name" style="line-height:40px;">
			${row.displayName}
		</display:column>
		<display:column title="Follower Count" style="line-height:40px;">
			${row.followerCount}
		</display:column>
		<display:column title="Post Count" style="line-height:40px;">
			${row.postCount}
		</display:column>
		<display:column title="User Type" style="line-height:40px;">
			${row.userType}
		</display:column>
		<display:column title="Unique ID" style="line-height:40px;">
			${row.uniqueId}
		</display:column>
	</display:table>
</div>