<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%><%@ include
	file="/common/taglibs.jsp"%>

<link rel="stylesheet"
	href="<c:url value="/v2/common/css/profile.css?v=${randVer}" />">
<link rel="stylesheet"
	href="<c:url value="/v2/common/css/circle.css?v=${randVer}" />">
<link rel="stylesheet"
	href="<c:url value="/v2/common/css/post.css?v=${randVer}" />">
<link rel="stylesheet"
	href="<c:url value="manage.css" />">
<link rel="stylesheet"
	href="<c:url value="/v2/common/css/msg-index.css" />">

<script
	src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.11.3.min.js"></script>
<script
	src="https://ajax.aspnetcdn.com/ajax/jquery.ui/1.11.4/jquery-ui.min.js"></script>
<script src="<c:url value="/v2/user/user.js?v=${randVer}" />"></script>

<script type="text/javascript">
  $(function() {
    $( ".discoverTabSortable" ).sortable({
      items: "li:not(.discoverTabUnsortableItem)"
    });
    $( ".discoverTabSortable" ).disableSelection();

    $( ".trendingTabsSortable" ).sortable({
        items: "li:not(.trendingTabsUnsortableItem)"
      });
      $( ".trendingTabsSortable" ).disableSelection();
  });

  var defaultTrendingTabsValues = ["LOOK","EDITORIAL","BEAUTYIST","BEAUTY_TIP"];
  
  function displayDiscoverTabsItem(item) {
	    $(item).toggleClass("hiddenItem sortableItem ui-sortable-handle");
	    $(item).attr("onclick", "hideDiscoverTabsItem(this)")
	    $(".displayDiscoverTabs_ctn2 ul").append($(item));
    	var trendingItem = $(item).clone();
	    if(defaultTrendingTabsValues.indexOf(trendingItem.text()) > -1){
	    	trendingItem.attr("onclick", "hideTrendingTabsItem(this)");
	    	$(".displayTrendingTabs_ctn2 ul").append(function(){return trendingItem;});
	    }else{
	    	trendingItem.attr("onclick", "displayTrendingTabsItem(this)");
	    	$(".displayTrendingTabs_ctn1 ul").append(function(){return trendingItem;});
	    }
	  }
  
  function hideDiscoverTabsItem(item) {
	    $(item).toggleClass("hiddenItem sortableItem ui-sortable-handle");
	    $(item).attr("onclick", "displayDiscoverTabsItem(this)")
	    $(".displayDiscoverTabs_ctn1 ul").append($(item));
	    
	    $('.displayTrendingTabs_ctn2 ul').each(function(){
	        // this is inner scope, in reference to the .phrase element
	        var phrase = '';
	        $(this).find('li').each(function(){
	        	if($(this).text() == $(item).text()){
	        		$(this).remove();
	        	}
	        });
	    });
	    
	    $('.displayTrendingTabs_ctn1 ul').each(function(){
	        // this is inner scope, in reference to the .phrase element
	        var phrase = '';
	        $(this).find('li').each(function(){
	        	if($(this).text() == $(item).text()){
	        		$(this).remove();
	        	}
	        });
	    });
	  }
  
  function displayTrendingTabsItem(item) {
    $(item).toggleClass("hiddenItem sortableItem ui-sortable-handle");
    $(item).attr("onclick", "hideTrendingTabsItem(this)")
    $(".displayTrendingTabs_ctn2 ul").append($(item));
  } 
  
  function hideTrendingTabsItem(item) {
    $(item).toggleClass("hiddenItem sortableItem ui-sortable-handle");
    $(item).attr("onclick", "displayTrendingTabsItem(this)")
    $(".displayTrendingTabs_ctn1 ul").append($(item));
  } 

  function save() {
    var discoverTabsArray = [];
    var trendingTabsArray = [];

    $(".discoverTabSortable li").each(function(i, el){
    	discoverTabsArray.push($(el).text());
    });
    $(".trendingTabsSortable li").each(function(i, el){
    	trendingTabsArray.push($(el).text());
    });
    var data = "?save=&visibleDiscoverTabsString=" + discoverTabsArray + "&visibleTrendingTabsString=" + trendingTabsArray +"&localeId=" + $('input[name="localeId"]').val();

    window.location.href = "./DiscoverTabManage.action" + data;
  }      
</script>

<s:form name="editLocaleForm" id="editLocaleForm"
	beanclass="${actionBean.class}">
	<s:hidden name="localeId" id="localeId" />
	<div class=clearfix>
		<s:form beanclass="${actionBean.class}">
			<div id="broadcastMessageApp" ng-app="broadcastMessageApp"
				ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
				<div class="page-header">Manage tabs</div>
				<div class="group-select" style="margin-bottom: 10px;">
					<div class="add_info"
						style="padding-left: 10px; margin-bottom: 20px;">
						• You can click on the tabs to display or hide. Drag the tabs to
						change the order. <br> • Note: These
						changes will only go into effect after your account has been
						activated.
					</div>
				</div>
				<diV>
					<div style="font-size: 20px;">Discover Tabs :</div>
					<br />
					<div class="part">
						<div class="display1_tt">Display</div>
						<div class=" display2">
							<div class="displayDiscoverTabs_ctn2">
								<ul class="discoverTabSortable">
									<c:forEach items="${actionBean.discoverTabs}" var="discoverTab"
										varStatus="loop">
										<li class="sortableItem" onclick="hideDiscoverTabsItem(this)">${discoverTab}</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
					<div class="part">
						<div class="display1_tt">Hidden</div>
						<div class=" display1">
							<div class="displayDiscoverTabs_ctn1">
								<ul>
									<c:forEach items="${actionBean.invisibleDiscoverTabs}"
										var="invisibleDiscoverTab" varStatus="loop">
										<li class="hiddenItem" onclick="displayDiscoverTabsItem(this)">${invisibleDiscoverTab}</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div  style="clear:both;"><br /></div>
				<div>
					<div style="font-size: 20px;">Trending Tabs :</div>
					<br />
					<div class="part">
						<div class="display1_tt">Display</div>
						<div class=" display2">
							<div class="displayTrendingTabs_ctn2">
								<ul class="trendingTabsSortable">
									<c:forEach items="${actionBean.trendingTabs}" var="trendingTab"
										varStatus="loop">
										<li class="sortableItem" onclick="hideTrendingTabsItem(this)">${trendingTab}</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
					<div class="part">
						<div class="display1_tt">Hidden</div>
						<div class=" display1">
							<div class="displayTrendingTabs_ctn1">
								<ul>
									<c:forEach items="${actionBean.invisibleTrendingTabs}"
										var="invisibleTrendingTab" varStatus="loop">
										<li class="hiddenItem" onclick="displayTrendingTabsItem(this)">${invisibleTrendingTab}</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div class="clear"></div>
				<div class="profile_sep" style="display: none;"></div>
				<div style="height: 80px;"></div>
				<div class="broadcast_btn">
					<a href="javascript: void(0);" onclick="save()">Save</a>
				</div>
				<div style="height: 20px;"></div>
			</div>
		</s:form>
	</div>
</s:form>