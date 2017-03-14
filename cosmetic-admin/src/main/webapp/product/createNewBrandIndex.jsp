<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<div>Create New Brand Index</div>
<s:form id="table" beanclass="com.cyberlink.cosmetic.action.backend.product.BrandIndexManageAction">
<s:errors />
	<table>
		<tr>
			<td>Brand Index Character</td>
			<td><s:text name="indexCharacter" /></td>
		</tr>
		<tr>
			<td>Locale</td>
			<td>
				<s:select name="locale" id="locale">
					<s:options-collection collection="${actionBean.localeList}" />
				</s:select>
			</td>
		</tr>	
	</table>
	<s:submit name="submitNewBrandIndex" value="submit"/>
	<s:submit name="route" value="cancel"/>
</s:form>