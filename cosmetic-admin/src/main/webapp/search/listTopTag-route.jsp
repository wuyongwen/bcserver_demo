<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Search :: List Top Tag</h2>
<s:form name="listTopTagForm" id="listTopTagForm" beanclass="${actionBean.class}">
	Locale : <s:select name="locale" id="locale">
			 	<s:options-collection collection="${actionBean.userLocales}" />
    		 </s:select>&nbsp;&nbsp;|&nbsp;&nbsp;
	Tag Number : <input type="text" id="topNumber" name="topNumber" value="${actionBean.topNumber}" onkeyup="value=value.replace(/[^\d]/g,'')" />
    &nbsp;&nbsp;<s:submit name="route" class="button">Search</s:submit>
</s:form>
<div>
	<display:table id="row" name="actionBean.orderTagList" requestURI="listTopTag.action" pagesize="100" sort="page" partialList="true" size="${fn:length(actionBean.orderTagList)}" export="false" >
		<display:column title="Order" style="width:10%;">
			${row.index}
		</display:column>
		<display:column title="Keyword">
			${row.tag}
		</display:column>
	</display:table>
</div>
