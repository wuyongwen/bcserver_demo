<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/decorators/backend/styles.jsp"%>

<table border="1" style="width: 50%">
<p><font color="red">${actionBean.operMessage}</font></p>
<c:forEach var="entry" items="${actionBean.postServiceStatus}">
	<tr>
		<td><c:out value="${entry.key}"/></td>
		<td>:</td>
		<td colspan="3"><c:out value="${entry.value}"/></td>
	</tr>
</c:forEach>
</table>
<table>
	<tr>
		<td>Score Backend Service</td>
		<td>:</td>
		<td><button onclick="action('startScoreService')">Start</button></td>
		<td colspan="2"><button onclick="action('stopScoreService')">Stop</button></td>
	</tr>
</table>
<table>
	<tr>
		<s:form id="photoProcessForm" beanclass="${actionBean.class}">
			<td>Photo Process Service</td>
			<td>:</td>
			<td>
			    Handler Count : <s:text name="handlerCount" value="2"></s:text><br>
			    Thread Count : <s:text name="threadCount" value="2"></s:text>
			</td>
			<td><s:submit name="startProcessService" value="Start"/></td>
			<td><s:submit name="stopProcessService" value="Stop"/></td>
		</s:form>
	</tr>
</table>
<table>
	<tr>
		<s:form id="photoScoreForm" beanclass="${actionBean.class}">
			<td>Run For</td>
			<td>:</td>
			<td>
			    <s:select name="runTimeUnit">
				  <s:option value="Month">Month</s:option>
				  <s:option value="Hour">Hour</s:option>
				  <s:option value="Minute">Minute</s:option>
				</s:select>
			</td>
			<td>
			    <s:text name="runDuration" value="3"></s:text>
			</td>
			<td><s:submit name="runInWindows" value="Run"/></td>
		</s:form>
	</tr>
</table>

<script>
function action(action) {
    window.location = "./photo-selection.action?" + action;
}
</script>