<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<script src="<c:url value="/common/lib/cropper/cropper.js" />"></script>
<script src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script src="<c:url value="/user/user.js?v=${randVer}" />"></script>
<script language="javascript">  
function checkchars() {
	var field = document.getElementById("description");
	if (field.value.length > 1024) {
	alert("Description max Length:" + 1024);
		return false;
	}
	return true;  
}
</script>
<link href="<c:url value="/common/lib/cropper/cropper.css" />" rel="stylesheet">
<link href="<c:url value="/common/lib/cropper/cropper.min.css" />" rel="stylesheet">

<h2 class=ico_mug>User :: Edit Current User</h2>
<div class=clearfix>
<s:form id="userEditForm" beanclass="${actionBean.class}">
    <table class="form">
        <tr>
          <td>User ID:</td>
          <td>${actionBean.user.id}</td><!-- (2) -->
        </tr>

        <tr>
          <td>DisplayName:</td>
          <td><s:text name="user.displayName"/></td><!-- (2) -->
        </tr>
        <tr>
          <td>gender:</td>
          <td>
		    <s:select name="user.gender" id="gender">
			    <s:option value="Male">Male</s:option>
			    <s:option value="Female">Female</s:option>
			    <s:option value="Unspecified">Unspecified</s:option>
		    </s:select>
		  </td>
        </tr>
        <tr>
          <td>AvatarURL:</td>
          <td>
          	<s:text name="user.avatarUrl" id="avatarUrl" style="display:none;"/>
          	<input id="usrAvatarInput" name="file" type="file" accept="image/*">
          </td>
          <td>
          	<s:text name="user.avatarId" id="avatarId"/>
          </td>
        </tr>
        <tr colspan="2">
          <td>
          	<div style="width:200px; height:200px; border-radius: 100px; overflow:hidden;">
          		<img id="usrAvatar" style="max-height: 100%;max-width: 100%;min-height: 100%;min-width: 100%;" src="${actionBean.user.avatarUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
          <td>CoverURL:</td>
          <td>
          	<s:text name="user.coverUrl" id="coverUrl" style="display:none;"/>
          	<input id="usrCoverInput" name="file" type="file" accept="image/*">
          </td>
          <td>
          	<s:text name="user.coverId" id="coverId"/>
          </td>

        </tr>
        <tr colspan="2">
          <td>
          	<div style="width:400px; height:300px;">
          		<img id="usrCover" style="max-height: 100%;max-width: 100%;" src="${actionBean.user.coverUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
          <td>region</td>
          <td>
		    <s:select name="user.region" id="locale">
		    	<s:options-collection collection="${actionBean.userLocaleList}" />
		    </s:select>
		  </td>
        </tr>
        <tr>
          <td>birthDay</td>
          <td><s:text name="user.birthDay"/></td>
        </tr>
        <tr>
          <td>description</td>
          <td><s:textarea name="user.description" id="description" style="min-height:100px; max-height:100px; width:100%;"/></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <s:submit name="save" id="saveUsrEdit" value="Save" onclick="return checkchars();"/><!-- (3) -->
            <s:submit name="cancel" value="Cancel"/>
          </td>
        </tr>
    </table>
</s:form>
<div id="croppingArea" class="img-container">
	<input class="action-button" type="button" id="confirmAvatar" value="Set Avatar" style="width: auto;"><br>
	<input class="action-button" type="button" id="confirmCover" value="Set Cover" style="width: auto;"><br>
	<img class="cropper" alt="Picture">
</div>
<div id="editProgress"></div>
</div>
