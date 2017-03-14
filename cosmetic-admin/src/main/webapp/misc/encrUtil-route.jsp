<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Miscellaneous :: Encryption Utility</h2>
<div class=clearfix>

<s:form beanclass="${actionBean.class}" method="get">
<td>
	<s:text name="decrString" id="decrString" />
	&nbsp;<s:submit name="encrypt" value="Encrypt"/>
</td>
<td>
	<s:text name="encrString" id="encrString" />
	&nbsp;<s:submit name="decrypt" value="Decrypt"/>
</td>
</s:form>
<label id="result">${actionBean.result}</label>
</div>
