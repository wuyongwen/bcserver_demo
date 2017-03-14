<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Miscellaneous :: Tab Management</h2>
<div>
<s:form name="searchLocaleForm" id="searchLocaleForm" beanclass="${actionBean.class}">
<table class="form">
	<tr>
       <td>Search by locale : </td>
       <td><s:text name="searchLocale"/></td>
       <td><s:submit name="searchByLocale" value="Search"/></td>
	</tr>
</table>
</s:form>
	<display:table id="row" name="actionBean.pageResult.results" requestURI="DiscoverTabManage.action" pagesize="100" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
		<display:column title="Id" sortable="true">
			${row.id}
		</display:column>
		<display:column title="User Locale">
			${row.inputLocale}
		</display:column>
		<display:column title="Discover Tab">
			${row.discoverTabString}
		</display:column>
		<display:column title="Trending Tab">
			${row.trendingTabString}
		</display:column>
		<display:column title="Action">
			<a href="./DiscoverTabManage.action?editRoute&localeId=${row.id}">Edit</a>
		</display:column>
	</display:table>
</div>