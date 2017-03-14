<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/profile.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/circle.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/post.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/manage.css?v=${randVer}" />">

<script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.11.3.min.js"></script>
<script src="https://ajax.aspnetcdn.com/ajax/jquery.ui/1.11.4/jquery-ui.min.js"></script>
<script src="<c:url value="/v2/user/user.js?v=${randVer}" />"></script>

<script type="text/javascript">
  $(function() {
    $( ".sortable" ).sortable({
      items: "li:not(.unsortableItem)"
    });
    $( ".sortable" ).disableSelection();

  });

  function displayItem(item) {
    $(item).toggleClass("hiddenItem sortableItem ui-sortable-handle");
    $(item).attr("onclick", "hideItem(this)")
    $(".display_ctn2 ul").append($(item));
  }

  function hideItem(item) {
    $(item).toggleClass("hiddenItem sortableItem ui-sortable-handle");
    $(item).attr("onclick", "displayItem(this)")
    $(".display_ctn1 ul").append($(item));
  } 

  function save() {
    var array = [];

    $(".sortable li").each(function(i, el){
        array.push($(el).text());
    });
    var data = "?save=&visibleTabsString=" + array;

    window.location.href = "./update-user-tab.action" + data;
  }      
</script>

<div class=clearfix>
    <s:form beanclass="${actionBean.class}">
        <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
            <div class="page-header">Manage	tabs</div>
            <div class="group-select" style="margin-bottom:10px;">
                <div class="add_info" style="padding-left:10px; margin-bottom:20px;">
                        • You can click on the tabs to display or hide. Drag the tabs to change the order. <br>
                        • You can hide Likes, Products, and Following tabs, but Circles, Posts and Followers tabs must be displayed. In addition, you can change the order for Likes, Products, Followers and Following tabs.<br> 
                        • Note: These changes will only go into effect after your account has been activated.
                </div>
            </div>
            <div class="part">
                <div class="display1_tt">Display</div>
                <div class=" display2">
                    <div class="display_ctn2">
                        <ul class="sortable">
                            <c:forEach items="${actionBean.visibleTabs}" var="visibleTab" varStatus="loop">
                                <c:choose>
                                    <c:when test="${visibleTab eq 'CIRCLE' || visibleTab eq 'POST'}">
                                        <li class="unsortableItem">${visibleTab}</li>
                                    </c:when>
                                    <c:when test="${visibleTab eq 'FOLLOWER'}">
                                        <li class="sortableItem">${visibleTab}</li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="sortableItem" onclick="hideItem(this)">${visibleTab}</li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>   
            <div class="part">
                <div class="display1_tt">Hidden</div>
                <div class=" display1">
                    <div class="display_ctn1">
                        <ul>
                            <c:forEach items="${actionBean.invisibleTabs}" var="invisibleTab" varStatus="loop">
                                <li class="hiddenItem" onclick="displayItem(this)">${invisibleTab}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="clear"></div>
            <div class="profile_sep" style="display:none;"></div>
            <div style="height:80px;"></div>   
            <div class="broadcast_btn"><a href="javascript: void(0);" onclick="save()">Save</a></div>
            <div style="height:20px;"></div> 
        </div>
    </s:form>
</div>