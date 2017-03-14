<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>User :: User Login</h2>
<div class=clearfix>
<s:form beanclass="${actionBean.class}" method="get">
    <table class="form">
        <tr>
          <td>Email:</td>
          <td><s:text name="email" style="width:400px;"/></td><!-- (2) -->
        </tr>
        <tr>
          <td>Password:</td>
          <td><s:password name="password" style="width:400px;"/></td>
        </tr>

        <tr>
          <td>&nbsp;</td>
          <td>
            <s:submit name="login" value="Login"/><!-- (3) -->
            <s:submit name="cancel" value="Cancel"/>
          </td>
        </tr>
    </table>
</s:form>

</div>
