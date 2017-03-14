<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<s:form partial="true" beanclass="com.cyberlink.cosmetic.action.backend.product.BrandManageAction" >
	<s:select name="brandIndexId">
		<s:option value="">--ALL--</s:option>
		<s:option value="">--LALALA--</s:option>
		<s:options-collection collection="${actionBean.brandIndexList.results}" value="id" label="index"/>
	</s:select>
</s:form>