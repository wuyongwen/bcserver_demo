<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/profile.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/circle.css?v=${randVer}" />">

<script src="<c:url value="/v2/circle/circle.js?v=${randVer}" />"></script>

<script type="text/javascript">
  $(document).ready(function(){

    $("#nameLimit").html($("#circleName").val().length + "/30 characters<br>");

    $("#descLimit").html($("#description").val().length + "/180 characters<br>");
  });
</script>

<div class=clearfix>
  <s:form beanclass="${actionBean.class}">
    <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
      <div class="page-header">Edit Circle	</div>
        <s:text name="circleId" style="display: none;"/>
        <div class="group-select">
          <div class="profile_col">
            <div class="profile_L_col">Circle Name </div>
            <div class="profile_R_col">
              <s:text name="circleName" id="circleName" class="add_title3" maxlength="30"/>
            </div>
            <div class="clear"></div>
          </div>
          <div id="nameLimit" class="word_counts2">6 / 30 characters<br></div>
          <div class="profile_space"></div>
          <div class="profile_col">
            <div class="profile_L_col">Circle Description</div>
            <div class="profile_R_col">
              <s:textarea name="description" id="description" class="add_txt3-1"/>
            </div>
            <div class="clear"></div>
          </div>
          <div id="descLimit" class="word_counts2">6/180 characters<br></div>
          <div class="profile_space"></div>
          <div class="profile_col">
            <div class="profile_L_col">Circle Category </div>
            <div class="profile_R_col">
              <s:select name="circleTypeId" class="form-control select ng-pristine ng-valid">
                <c:forEach items="${actionBean.availableCircleType}" var="circleType" varStatus="loop">
                  <s:option value="${circleType.id}">${circleType.circleTypeName}</s:option>
                </c:forEach>
              </s:select>
            </div>
            <div class="clear"></div>
          </div>
          <div class="profile_space"></div>
          <div class="profile_col">
            <div class="profile_L_col">Secret Circle</div>
            <div class="profile_R_col">
              <s:select name="isSecret" class="form-control select ng-pristine ng-valid">
                <s:option value="false">Public</s:option>
                <s:option value="true">Secret</s:option>
              </s:select>
            </div>
          <div class="clear"></div>
        </div>
        <div class="profile_space"></div> 
        <div style="height:30px"></div>
        <div class="profile_sep"></div>
        <div class="save_cancel">
          <s:submit name="save" value="Save" class="save_btn"/>
          <s:submit name="cancel" value="Cancel" class="cancel_btn"/>
        </div>
      </div>
      <div class="clear"></div>
      <div class="profile_sep" style="display:none;"></div>    
    </div>
  </s:form>
</div>