<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Event :: Event Redeem Management</h2>
<div>
	<display:table id="row" name="actionBean.pageResult.results"
		requestURI="EventRedeemManager.action"
		pagesize="${actionBean.defaultPageSize}" sort="page"
		partialList="true" size="actionBean.pageResult.totalSize"
		export="true">
		<display:setProperty name="export.types" value="excel" />
		<display:setProperty name="export.excel.include_header" value="true" />
		<display:setProperty name="export.excel.filename"
			value="EventRedeemedUser.xls" />
		<display:setProperty name="export.csv" value="false" />
		<display:setProperty name="export.xml" value="false" />
		<display:column title="Id">
			${row.id}
		</display:column>
		<display:column title="Display Name">
			${row.displayName}
		</display:column>
		<display:column title="Real Name">
			${row.name}
		</display:column>
		<display:column title="Birthday">
			${row.birthDayString}
		</display:column>
		<display:column title="Phone">
			${row.phone}
		</display:column>
		<display:column title="Email">
			${row.mail}
		</display:column>
		<display:column title="Store Location">
			${row.storeLocation}
		</display:column>
		<display:column title="Store Name">
			${row.storeName}
		</display:column>
		<display:column title="Store Address">
			${row.storeAddress}
		</display:column>
	</display:table>
</div>
<div id="progressDialog" title="Processing">
</div>