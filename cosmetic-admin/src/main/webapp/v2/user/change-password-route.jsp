<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/post.css?v=${randVer}" />">
<link rel="stylesheet" href="https://ajax.aspnetcdn.com/ajax/jquery.ui/1.11.4/themes/smoothness/jquery-ui.css">

<script type="text/javascript">
    $(document).ready(function(){
        $(".save_btn").click(function(){
            if ($("#oldPwd").val() == "" || $("#newPwd1").val() == "" || $("#newPwd2").val() == "") {
                alert("Please enter old password, new password and confirm password");
                return false;
            }

            if ($("#newPwd1").val() !=  $("#newPwd2").val()) {
                alert("Please double-check your new password.");
                return false;
            }
        });
    });
</script>

<div class=clearfix>
    <s:form beanclass="${actionBean.class}">
        <div class="page-header">Change Password</div>
        <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
            <div class="clear"></div>
            <div class="group-select">
                <label>Current Password:<span class="info"></span></label>
                <br>
                <s:password id="oldPwd" name="oldPassword" class="add_title3" maxlength="20"/>
            </div>
            <div class="group-select">
                <label>New Password (6-20):<span class="info"></span></label>
                <br>
                <input id="newPwd1" name="input" type="password" class="add_title" size="" maxlength="20">
                <br>
                <label>Confirm Password:<span class="info"></span></label>
                <br>
                <s:password id="newPwd2" name="newPassword" class="add_title" maxlength="20"/> 
            </div>
            <div class="ctn_sep"></div>
            <div class="save_cancel">
                <s:submit name="changePassword" class="save_btn" value="Save"/>
                <s:submit name="cancel" class="cancel_btn" value="Cancel"/>
            </div>
        </div>
    </s:form>
</div>