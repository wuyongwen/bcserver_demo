<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/profile.css?v=${randVer}" />">
<link rel="stylesheet" href="https://ajax.aspnetcdn.com/ajax/jquery.ui/1.11.4/themes/smoothness/jquery-ui.css">

<script src="<c:url value="/v2/common/scripts/jquery.qrcode.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/common/scripts/qrcode.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/user/user.js?v=${randVer}" />"></script>

<script type="text/javascript">
  $(document).ready(function(){
    
    $("#nameLimit").html($("#displayName").val().length + "/500 characters<br>");

    $("#descLimit").html($("#description").val().length + "/500 characters<br>");

    if ($(".icon > img").attr("src") != "") {
      if($(".icon > img").height() > $(".icon > img").width() || $(".icon > img").height() > $(".icon").height()) {
          $(".icon > img").height("100%");
          $(".icon > img").width("auto");
      }
      else if($(".icon > img").height() < $(".icon > img").width() || $(".icon > img").width() > $(".icon").width()){
          $(".icon > img").height("auto");
          $(".icon > img").width("100%");
      }

      $(".icon").height($(".icon > img").height());
      $(".icon").width($(".icon > img").width());
      $(".icon > input").height($(".icon").height());
      $(".icon > input").width($(".icon").width());      
    }

    if ($(".banner > img").attr("src") != "") {
      if($(".banner > img").height() > $(".banner > img").width() || $(".banner > img").height() > $(".banner").height()) {
          $(".banner > img").height("100%");
          $(".banner > img").width("auto");
      }
      else if($(".banner > img").height() < $(".banner > img").width() || $(".banner > img").width() > $(".banner").width()){
          $(".banner > img").height("auto");
          $(".banner > img").width("100%");
      }

      $(".banner").height($(".banner > img").height());
      $(".banner").width($(".banner > img").width());
      $(".banner > input").height($(".banner").height());
      $(".banner > input").width($(".banner").width());      
    }


    if ($(".icon2 > img").attr("src") != "") {
      if($(".icon2 > img").height() > $(".icon2 > img").width() || $(".icon2 > img").height() > $(".icon2").height()) {
          $(".icon2 > img").height("100%");
          $(".icon2 > img").width("auto");
      }
      else if($(".icon2 > img").height() < $(".icon2 > img").width() || $(".icon2 > img").width() > $(".icon2").width()){
          $(".icon2 > img").height("auto");
          $(".icon2 > img").width("100%");
      }

      $(".icon2").height($(".icon2 > img").height());
      $(".icon2").width($(".icon2 > img").width());
      $(".icon2 > input").height($(".icon2").height());
      $(".icon2 > input").width($(".icon2").width());      
    }

    if ($(".banner2 > img").attr("src") != "") {
      if($(".banner2 > img").height() > $(".banner2 > img").width() || $(".banner2 > img").height() > $(".banner2").height()) {
          $(".banner2 > img").height("100%");
          $(".banner2 > img").width("auto");
      }
      else if($(".banner2 > img").height() < $(".banner2 > img").width() || $(".banner2 > img").width() > $(".banner2").width()){
          $(".banner2 > img").height("auto");
          $(".banner2 > img").width("100%");
      }

      $(".banner2").height($(".banner2 > img").height());
      $(".banner2").width($(".banner2 > img").width());
      $(".banner2 > input").height($(".banner2").height());
      $(".banner2 > input").width($(".banner2").width());      
    }
  });
</script>

<div class=clearfix>
  <s:form beanclass="${actionBean.class}">
  <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
    <c:if test="${actionBean.currentUserAdmin != true and (actionBean.accessControl.postManagerAccess != true or actionBean.accessControl.circleManagerAccess != true)}">
      <script type="text/javascript">
        alert("You don't have permission to access BeautyCircle Account Console. Please contact system administrator.");
        window.location.href = "./logout.action";
      </script>
    </c:if>
    <div class="page-header">Public Information </div>
    <div class="clear"></div>
    <div class="group-select">
      <div class="profile_col">
        <div class="profile_L_col">Display name<span class="red">* </span></div>
        <div class="profile_R_col">
          <s:text name="user.displayName" class="add_title3" maxlength="500" id="displayName"/>
        </div>
        <div class="clear"></div>
      </div>
      <div id="nameLimit" class="word_counts2">0/500 characters<br></div>
      <div class="profile_space"></div>
      <div class="profile_col">
        <div class="profile_L_col">About</div>
        <div class="profile_R_col">
          <s:textarea name="user.description" class="add_txt3" id="description"/>
        </div>
        <div class="clear"></div>
      </div>
      <div class="profile_space"></div>
      <div id="descLimit" class="word_counts2">0/500 characters<br></div>
      <div class="profile_space"></div>
      <div class="profile_col">
        <div class="profile_L_col2">
          <div class="profile_L_col2_title">Profile photo<span class="red">* </span> </div>
          <div class="profile_L_col2_des">400 x 400</div>
          <div class="icon">
            <!-- <div class="icon_X"><i class="fa fa-times" style="color:#FFFFFF;"></i></div> -->
            <img src="${actionBean.user.avatarUrl}">
            <input id="usrAvatarInput" name="file" type="file" accept="image/*">
            <s:text name="user.avatarId" id="avatarId" style="display:none;"/>
          </div>
        </div>
        <div class="profile_R_col2">
          <div class="profile_L_col2_title">Cover Photo<span class="red">* </span></div>
          <div class="profile_L_col2_des">1080 x 590</div>
          <div class="banner">
            <!-- <div class="banner_X"><i class="fa fa-times" style="color:#FFFFFF;"></i></div> -->
            <img src="${actionBean.user.coverUrl}">
            <input id="usrCoverInput" name="file" type="file" accept="image/*">
            <s:text name="user.coverId" id="coverId" style="display:none;"/>
          </div>
        </div>
        <div class="clear"></div>
        <br/><br/>
        <div class="profile_L_col2">
          <div class="profile_L_col2_title">Icon photo</div>
          <div class="profile_L_col2_des">250 x 200</div>
          <div class="icon2">
            <!-- <div class="icon_X"><i class="fa fa-times" style="color:#FFFFFF;"></i></div> -->
            <img name="iconUrl" src="${actionBean.iconUrl}">
            <input id="usrIconInput" name="file" type="file" accept="image/*">
            <s:text name="iconId" id="iconId" style="display:none;"/>
          </div>
        </div>
        <div class="profile_R_col2">
          <div class="profile_L_col2_title">Background Photo</div>
          <div class="profile_L_col2_des">1080 x 590</div>
          <div class="banner2">
            <!-- <div class="banner_X"><i class="fa fa-times" style="color:#FFFFFF;"></i></div> -->
            <img name="bgImageUrl" src="${actionBean.bgImageUrl}">
            <input id="usrBgImageInput" name="file" type="file" accept="image/*">
            <s:text name="bgImageId" id="bgImageId" style="display:none;"/>
          </div>
        </div>
        <div class="clear"></div>
      </div>
    </div>
    <div class="profile_sep" style="display:none;"></div>    
    <div class="page-header">Register Info </div>
    <div class="group-select">
      <div class="profile_col">
        <div class="profile_L_col">User ID</div>
        <div class="profile_R_col" id="userId">${actionBean.user.id}</div>
        <div class="clear"></div>
      </div>
      <div class="profile_space"></div>
      <div class="profile_col">
        <div class="profile_L_col">Country </div>
        <div class="profile_R_col">${actionBean.user.region}</div>
        <div class="clear"></div>
      </div>
      <div class="profile_space"></div>
      <div class="profile_col">
        <div class="profile_L_col">Email </div>
        <div class="profile_R_col">${actionBean.user.allEmailAccountList[0].email}</div>
        <div class="clear"></div>
      </div>
      <div class="profile_space"></div>
      <div class="profile_col">
        <div class="profile_L_col">Password </div>
        <div class="profile_R_col_changepwd">
          <s:submit name="changePassword" class="change_btn" value="Change"/>  
        </div>
        <div class="clear"></div>
      </div>
      <div class="profile_space"></div> 
      <div class="profile_col">
        <div class="profile_L_col">QR Code</div>
        <div class="profile_R_col"><span class="QR"></span><span class="profile_info">(This QR code link to your Beauty Circle profile page)</span></div>
        <div class="clear"></div>
      </div>            
      <div class="clear"></div>
    </div>
    <div class="profile_sep"></div> 
    <div style="height:30px;"></div> 
    <s:submit name="save" id="saveUsrEdit" class="broadcast_btn" value="Save"/>  
    <div style="height:20px;"></div>
    </div>
  </s:form>

  <script type="text/javascript">
    var url = "http://service.perfectcorp.com/ap/deeplink.jsp?appName=YMK&appUrl=ymkbc://me/" + $("#userId").html();
    $(".QR").qrcode({
      "width": 128,
      "height": 128,
      "text": url
    });
  </script>
  <div id="editProgress"></div>
</div>