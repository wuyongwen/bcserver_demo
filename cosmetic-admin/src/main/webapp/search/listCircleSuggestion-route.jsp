<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Search :: List Circle Suggestion</h2>
<font style="color:red;">* is required Field</font>
<s:form name="listCircleSuggestionForm" id="listCircleSuggestionForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
				<s:options-collection collection="${actionBean.userLocales}" />
    		</s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Keyword : <input type="text" id="keyword" name="keyword" style="width:250px;" value="${actionBean.keyword}" placeholder="please enter the keyword(e.g. eye)" /><font style="color:red;">*</font>
    &nbsp;&nbsp;<s:submit name="route" class="button">Search</s:submit>
</s:form>
<br/>
<div>
	<display:table id="row" name="actionBean.suggestCircleViewResult.results" requestURI="listCircleSuggestion.action" sort="page" size="actionBean.suggestCircleViewResult.totalSize"  export="false">
		<display:column title="Id" style="line-height:40px;width:200px;">
			${row.id}
		</display:column>
		<display:column title="Icon Image" style="line-height:40px;text-align:center;width:60px;">
			<c:if test="${row.iconUrl ne null}">
				<a href="${row.iconUrl}" target="_blank"><img src="${row.iconUrl}" alt="${row.iconUrl}" height="40px"></a>
			</c:if>
		</display:column>
		<display:column title="Circle Name" style="line-height:40px;">
			${row.circleName}
		</display:column>
		<display:column title="Post Count" style="line-height:40px;">
			${row.postCount}
		</display:column>
		<display:column title="Creator Name" style="line-height:40px;">
			${row.creatorName}
		</display:column>
	</display:table>
</div>
