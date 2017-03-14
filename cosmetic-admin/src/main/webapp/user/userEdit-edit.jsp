<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/common/lib/cropper/cropper.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>
<script src="<c:url value="/user/user.js?v=${randVer} " />"></script>
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

<h2 class=ico_mug>User :: User Edit</h2>
<div class=clearfix>
<s:form id="userEditForm" beanclass="${actionBean.class}">
    <div><s:hidden name="userId"/></div>

    <table class="form">
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
          <td>BgImage:</td>
          <td>
          	<s:text name="bgImageId" id="bgImageId" style="display:none;"/>
          	<input id="usrBgImageInput" name="file" type="file" accept="image/*">
          	<s:textarea name="bgImageUrl" id="bgImageUrl" style="max-height:100px; width:250px;"/>
          </td>
        </tr>
        <tr colspan="2">
          <td>
          	<div style="width:400px; height:300px;">
          		<img id="bgImage" style="max-height: 100%;max-width: 100%;" src="${actionBean.bgImageUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
          <td>Icon:</td>
          <td>
          	<s:text name="iconId" id="iconId" style="display:none;"/>
          	<input id="usrIconInput" name="file" type="file" accept="image/*">
          	<s:textarea name="iconUrl" id="iconUrl" style="max-height:100px; width:250px;"/>
          </td>
        </tr>
        <tr colspan="2">
          <td>
          	<div style="width:400px; height:300px;">
          		<img id="icon" style="max-height: 100%;max-width: 100%;" src="${actionBean.iconUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
          <td>WebsiteURL</td>
          <td><s:text name="websiteUrl" size="50"/></td>
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
          <td>userType</td>
          <td>
		    <s:select name="user.userType" id="userType">
			    <s:option value="Normal">Normal</s:option>
			    <s:option value="Expert">Expert</s:option>
			    <s:option value="CL">CL</s:option>
			    <s:option value="Blogger">Blogger</s:option>
				<s:option value="Master">Master</s:option>
				<s:option value="Brand">Brand</s:option>
				<s:option value="Publisher">Publisher</s:option>
				<s:option value="Celebrity">Celebrity</s:option>
				<s:option value="LiveBrand">LiveBrand</s:option>
				<s:option value="Anchor">Anchor</s:option>
		    </s:select>
		  </td>

        </tr>
        <c:if test="${actionBean.user.userType == 'Brand'}">
	        <tr>
	          <td>brandType</td>
	          <td>
			    <s:select name="brandType" id="brandType">
				    <s:option value="BEAUTY">BEAUTY</s:option>
				    <s:option value="LUXURY">LUXURY</s:option>
			    </s:select>
			  </td>
	
	        </tr>
        </c:if>
        <tr>
          <td>description</td>
          <td><s:textarea name="user.description" id="description" style="min-height:100px; max-height:100px; width:100%;"/></td>
        </tr>
        <tr>
          <td>Promote Score</td>
          <td>
          	<s:text name="promoteScore" id="promoteScore"/>
          </td>
        </tr>

        <c:if test="${actionBean.isAdmin == true}">
          <tr>
          	<td>Set User As Admin</td>
          	<td>
          		<s:checkbox name="isTargetAdmin" value="true" />
          	</td>
          </tr>
        </c:if>

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
