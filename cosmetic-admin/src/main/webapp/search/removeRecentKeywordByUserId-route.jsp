<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>Search :: Remove Recent Keyword By User Id</h2>
<font style="color:red;">* is required Field</font>
<s:form name="removeRecentKeywordByUserIdActionForm" id="removeRecentKeywordByUserIdActionForm" beanclass="${actionBean.class}">
	Type : <s:select name="type" id="type">
				<s:options-collection collection="${actionBean.typeKeywordList}" />
    		</s:select><font style="color:red;">*</font>&nbsp;&nbsp;|&nbsp;&nbsp;
	User Id : <input type="text" id="curUserId" name="curUserId" style="width:250px;" value="" placeholder="please enter an user id" onkeyup="value=value.replace(/[^\d]/g,'')"/><font style="color:red;">*</font>
    &nbsp;&nbsp;<s:submit name="route" class="button">Delete</s:submit>
</s:form>
<br/><br/>
<font>${actionBean.message}</font>