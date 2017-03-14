<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Product :: Search Product Link</h2>
<div>
	<s:form beanclass="com.cyberlink.cosmetic.action.backend.product.SearchProductStoreLinkAction">
		Locale :&nbsp;
		<s:select name="locale" id="locale">
			<s:options-collection collection="${actionBean.localeList}" />
		</s:select>
		&nbsp;
		Mall Name :&nbsp;<label id="MallName"></label>
		<br />
		Product ID:
		<input type="text" name="exProductId" value="${actionBean.exProductId}">
		<s:submit name="route" value="Search" class="button" /><br /><br />
		<table>
			<tr>
				<td style="width:140px;border-style: solid;">
					Product Store Link :
				</td>
				<td style="border-style: solid;">
					<c:if test="${actionBean.productStoreLink != null}">
						<a href="javascript:window.open('${actionBean.productStoreLink}');">${actionBean.productStoreLink}</a>
					</c:if>
					<c:if test="${actionBean.productStoreLink == null}">
						Product is not exist in store.
					</c:if>
				</td>
			</tr>
		</table>
	</s:form>
</div>
<script>
$(document).ready(function(){
	changeMallName($("#locale").val());
	$("#locale").change(function() {
		changeMallName($("#locale").val());
	});
	
	function changeMallName(locale){
		switch(locale){
			case 'zh_TW':
				$("#MallName").text("Yahoo");
				break;
			case 'zh_CN':
				$("#MallName").text("Taobao");
				break;
			default:
				$("#MallName").text("Amazon");
				break;
		}
	}
});
</script>