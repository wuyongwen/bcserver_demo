<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
 <script>
function btclick()
{
	autoCreateUser.hidden=true;
}
</script>
<h2 class=ico_mug>User :: Create User</h2>
<s:form beanclass="${actionBean.class}" method="post">
<table>
	<tr>
		<td>
			<label>Locale : </label>
			<select id="localeSel" name="localeSel">
				<c:forEach items="${actionBean.availableLocale}" var="locale" varStatus="loop">
					<option value="${locale}">${locale}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td>
			<label>Import User List: </label>
			<s:file name="userFile" accept=".xls,application/vnd.ms-excel"/>
		</td>
	</tr>
	<tr>
		<td>
			<label>Import Images Zip: </label>
			<s:file name="avatarFile" accept=".zip,application/zip"/>
			<input type="Submit" id="autoCreateUser" name="autoCreateUser" value="Auto Create User" onClick= "btclick();">
		</td>
	</tr>
	<tr>
		<td>
			<p style="color:#FF0000;">${errorMessage}</p>
		</td>
	</tr>
	<tr>
		<td>
			<p style="color:#007CD2;">${message}</p>
		</td>
	</tr>
</table>
</s:form>