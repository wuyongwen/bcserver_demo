<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<div class=clearfix>
<s:form id="createCircleForm" beanclass="${actionBean.class}">
    <table class="form">
        <tr>
          <td>Circle Name : </td>
          <td>
		    <input id="circleNameInput" name="circleName" type="text">
		  </td>
        </tr>
        <tr>
          <td>Circle Description : </td>
          <td>
          	<input id="descriptionInput" name="description" type="text">
          </td>
        </tr>
        <tr>
          <td>Circle Category Id : </td>
          <td>
          	<s:select name="circleTypeId" id="circleTypeId">
		    	<s:options-collection collection="${actionBean.circleTypes}" value="id" label="circleTypeName"/>
		    </s:select>
          </td>
        </tr>
        <tr>
          <td>Secret Circle : </td>
          <td>
          	<s:select name="isSecret" id="isSecret">
			    <s:option value="true">Secret</s:option>
			    <s:option value="false">Public</s:option>
		    </s:select>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <s:submit name="create" id="createCircle" value="Create"/>
            <s:submit name="cancel" value="Cancel"/>
          </td>
        </tr>
    </table>
</s:form>