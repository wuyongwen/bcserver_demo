<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Search :: List Post Tag Suggestion</h2>
<font style="color:red;">* is required Field</font>
<s:form name="listPostTagSuggestionForm" id="listPostTagSuggestionForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
				<s:options-collection collection="${actionBean.userLocales}" />
    		</s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	Tag : <input type="text" id="tag" name="tag" style="width:250px;" value="${actionBean.tag}" placeholder="please enter the tag value(e.g. beauty)" /><font style="color:red;">*</font>
    &nbsp;&nbsp;<s:submit name="route" class="button">Search</s:submit>
</s:form>
<br/>
<div>
	<display:table id="row" name="actionBean.suggestResult.results" requestURI="listPostTagSuggestion.action" sort="page" size="actionBean.suggestResult.totalSize" >
		<display:column title="Tag Suggestion">
			${row}
		</display:column>
	</display:table>
</div>