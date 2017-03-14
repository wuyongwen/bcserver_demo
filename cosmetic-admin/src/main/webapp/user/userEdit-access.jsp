<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<h2 class=ico_mug>User :: User Edit</h2>
<div class=clearfix>
<s:form beanclass="${actionBean.class}">
    <table class="form">
        <tr>
          <td>
          <s:checkbox name="userManagerAccess" value="true" checked="false"/> User Manager
          &nbsp;|&nbsp;
          <s:checkbox name="postManagerAccess" value="true" checked="false"/> Post Manager
          &nbsp;|&nbsp;
          <s:checkbox name="circleManagerAccess" value="true" checked="false"/> Circle Manager
          &nbsp;|&nbsp;
          <s:checkbox name="productManagerAccess" value="true" checked="false"/> Product Manager
          &nbsp;|&nbsp;
          <s:checkbox name="eventManagerAccess" value="true" checked="false"/> Event Manager
          &nbsp;|&nbsp;
          <s:checkbox name="reportManagerAccess" value="true" checked="false"/> Report Manager
          &nbsp;|&nbsp;
          <s:checkbox name="reportAuditorAccess" value="true" checked="false"/> Report Auditor
          &nbsp;|&nbsp;
          <s:checkbox name="apkManagerAccess" value="true" checked="false"/> APK Manager
          </td><!-- (2) -->        
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <s:submit name="saveAccess" value="Save">
            	Save
            	<s:param name="userId" value="${actionBean.userId}"/>
            </s:submit>
            <s:submit name="cancel" value="Cancel"/>
          </td>
        </tr>
    </table>
</s:form>

</div>
