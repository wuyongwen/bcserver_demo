<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<link href="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/themes/hot-sneaks/jquery-ui.css" />" rel="stylesheet">
<link href="<c:url value="/post/externalPost.css" />" rel="stylesheet">
<script>
$(document).ready(function(){	
	$("#detailDialog").dialog({
		autoOpen: false,
        maxHeight: 500,
        width: 674,
        height: 680,
        modal: true,
        draggable: false,
        close: function(event, ui)
        {
            $(this).empty();
        }
	});
	
	$("#analysisDiv").find("[id='detailTd']").each(function(){
		var $this = $(this);
		$this.click(function() {
			$("#detailDialog").dialog("open");
			$("#detailDialog").append($this.attr("detail"));
		});
	});
	
	$("#analysisDiv").find("[id='count']").each(function(){
		var $this = $(this);
		if ($this.text() != 0)
			$this.css("font-weight", "bold");
	});
});
</script>
<h2 class=ico_mug>Post :: External Post Summary</h2>
<s:form beanclass="${actionBean.class}" method="post">
	<div id="analysisDiv" align="center" style="font-size:16px; width:100%;">
	<table>
		<tr>
		<c:forEach items="${actionBean.analysisDataList}" var="analysisData">
			<td id="detailTd" colspan="3" detail="${analysisData.detail}">
				<div id="detailDiv" align="center" style="font-size:18px; width:115px; height:40px;">
			        <span style="position: relative; top: 30%;"> ${analysisData.locale}</span>
			    </div>
			</td>
		</c:forEach>
		</tr>
		<tr>
		<c:forEach items="${actionBean.analysisDataList}" var="analysisData">
			<td>
				<table border="1" id="dateTable">
			    <c:forEach items="${analysisData.dateList}" var="value">
			        <tr>
			        	<td>
			           		<div align="center" style="min-width:55px; height:20px; width:auto;">
			           			<span style="position: relative; top: 10%;">${value}</span>
			        		</div>
			        	</td>
			        </tr>
			    </c:forEach>
			    </table>
			</td>
			<td>
				<table border="1" id="countTable">
			    <c:forEach items="${analysisData.countList}" var="value">
			        <tr>
			        	<td>
			        		<div align="center" style="min-width:60px; height:20px; width:auto;">
			        			<span id="count" style="position: relative; top: 10%;">${value}</span>
			        		</div>
			        	</td>
			        </tr>
			    </c:forEach>
			    </table>
			</td>
			<td>
				<label>&nbsp;&nbsp;</label>
			</td>
		</c:forEach>
		</tr>
	</table>
	</div>
</s:form>
<div id=detailDialog title="Detail">
</div>