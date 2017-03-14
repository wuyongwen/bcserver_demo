<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<script src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/user/user.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/user/fanPage.js?v=${randVer}" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="<c:url value="/v2/common/css/fbfanpage.css?v=${randVer}"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/v2/common/css/post.css?v=${randVer}"/>">
<link rel="stylesheet" href="<c:url value="/v2/common/css/displaytag2.css?v=${randVer}"/>">
<link rel="stylesheet" href="<c:url value="/v2/common/css/preview.css?v=${randVer}" />">

    <!-- <script language="javascript" src="<c:url value="/v2/common/u-backend_files/jquery.min.js?v=${randVer}"/>"></script> -->

<div class=clearfix>
    <s:form beanclass="${actionBean.class}">
    <div class="page-header">Facebook Fanpage Link</div>
    <div id="normalView">
			<div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
				<div style="width:267px;float:right">  
				<div >
					<div class="post_create_cancelbtn bt_cancel" id="cancelBtn"><a href="javascript: void(0)"><i class="fa fa-minus padding5" style="color:#FFFFFF;"></i>Cancel</a></div>
				</div>
				<div>
					<div class="post_create_cancelbtn bt_pull" id="pullBtn"><a href="javascript: void(0)"><i class="fa fa-arrow-right padding5" style="color:#FFFFFF;"></i>Pull</a></div> 
				</div>
				<div>
					<div class="post_create_cancelbtn bt_add margin10" id="addBtn"><a href="javascript: void(0)"><i class="fa fa-plus padding5" style="color:#FFFFFF;"></i>Add Page</a></div>
				</div>
				<div class="clear"></div>
			</div>  
		</div> 

		<div style="width:70%;float:right">
			<div class="post_filter">
				<s:select name="fanPageNames" id="fanPageNameSel" class="add_title form-control select ng-pristine ng-valid">
					<c:forEach items="${actionBean.fanPageMap}" var="fanPage" varStatus="loop">
                          <s:option class="fanPageName" value='${fanPage.key}'>${fanPage.value}</s:option>
                    </c:forEach>
                </s:select>
			</div>
			<div class="group-select">
				<!-- <input name="input" type="text" class="add_title" value="" size="" maxlength="45"> -->
				<s:text name="fanPageLink" id="fanPageLink" class="add_title" value="" size=""/>
			</div>
         
		</div>
		<div class="clear"></div>

		<div class="padding10" id="date">
			&nbsp;Since Time:
			<input type="text" class="datepick" id="sincetimepicker" name="sincetimepicker" readonly='true'>
			&nbsp;Until Time:
			<input type="text" class="datepick" id="untiltimepicker" name="untiltimepicker" readonly='true'>
			&nbsp;
			<a href="javascript: void(0)" id="resettime">Clear Time</a>
		</div>

		<div class="fb_table" id="fb_table">
			<div class="fb_table_head">
				<div class="post_itemc tecxt_gray">
					<p style"min-height:16px;display:block;"></p>
					<input id="checkAll" style"margin:auto;" type="checkbox">
				</div>
				<div class="post_item1">Date</div>
				<div class="post_item2_head">Title</div>
				<div class="post_item1-2">Circle</div>
			</div>
			<div class="fb_list1">
				<div id="post1">
				</div>
				<div id="post2" class="circle_table_list2 bg_gray">
				</div>
				<div id="post3">
				</div>
				<div id="post4"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post5">
				</div>
				<div id="post6"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post7">
				</div>
				<div id="post8"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post9">
				</div>
				<div id="post10"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post11">
				</div>
				<div id="post12"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post13">
				</div>
				<div id="post14"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post15">
				</div>
				<div id="post16"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post17">
				</div>
				<div id="post18"  class="circle_table_list2 bg_gray">
				</div>
				<div id="post19">
				</div>
				<div id="post20"  class="circle_table_list2 bg_gray">
				</div>
				<div style="display:none">
					<input id="dataFanPage">
				</div>
			</div>
			
			<div class="clear"></div>
			<div>
				<a href="javascript: void(0)"  class="_nav_forward" id="prevBtn"></a>
				<a href="javascript: void(0)" class="_nav_backward" id="nextBtn"></a>
			</div>    
			<div class="profile_sep" style="display:none;"></div>    
		</div>
				
		<div class="padding10" id="radio">
			<input type="radio"  name="optradio" style="margin-left: 8px;" value="1">Directly publish all selected post
        	<input type="radio"  name="optradio" style="margin-left: 8px;" value="2">Send selected post into draft mode
		</div> 
		<div class="padding10">
			<input type="checkbox" id="autoPostCheck" style=" margin-left: 8px;">Always auto publish posts from fanpage daily
		</div>
		<div>
			<div class="post_create_cancelbtn bt_comfirm" id="confirmBtn"><a href="javascript: void(0)">Confirm</a></div>
		</div>
		<div id="searchProgress">
			FB post searching, please wait few minutes.
		</div>
		<div id="postProgress">
			Post creating, please wait few minutes.
		</div>

		<div class="clear"></div>
		<div class="border_black padding10"></div>


		<div class="padding10">Sort to your Circles</div>

		<div class="group-select">
			<!-- <select class="form-control select ng-pristine ng-valid" ng-model="currentLocaleType" ng-options="l as l.nameRC for l in localeTypes"></select> -->
			<s:select onchange="changeInput();" name="circleId" id="circleSel" class="form-control select ng-pristine ng-valid">
               	<option>-----</option>
                    <c:forEach items="${actionBean.circles.results}" var="circle" varStatus="loop">
                        <s:option class="circleId" value="${circle.id}">${circle.circleName}</s:option>
                    </c:forEach>
            </s:select>
		</div>
		<div class="group-select">
			<textarea id="add_hashtag" name="input" class="add_txt3" style="width:420px;"></textarea>
            <input id="last_circle" style="display: none;"></input>
		</div>
		<div>
			<div class="post_create_cancelbtn bt_comfirm" id="saveBtn"><a href="javascript: void(0)">Confirm</a></div>
		</div>
	</div>
	<div id="previewPostView">
		<div class="previewurl">
            <div class="previewurl_top">
                <div class="previewurl_coverart"><img src="${actionBean.currentUserAvatarUrl}"></div>
                <div class="previewurl_right">
                    <div class="previewurl_headline">${actionBean.currentUserName}</div>
<!--                             <div class="previewurl_timeinfo">
                                <div class="previewurl_icon"><img src="<c:url value="/v2/common/images/time_icon.gif" />"></div>
                                <div class="previewurl_time">7 hours</div>
                                <div class="clear"></div>
                            </div> -->
                </div>
                <div class="clear"></div>
            </div>
            <div class="previewurl_ctntoptitle" id="postPreviewTitle"></div>
            <div class="preview_ctn">
                <div class="post">
                    <img src="<c:url value="/v2/common/images/theme_cover.jpg" />">
                    <p class="previewContent">Dressing up for work can sometimes be a bore, especially when you work somewhere where the dress code is strict and corporate.</p>  
                    <p class="link"><a href="javascript: void(0)" class="previewRedirectUrl" target="_blank">http://www.pinterest.com/pin/</a></p>
                </div>
            </div>
            <div class="clear"></div>
        </div>    
        <div style="height:30px;"></div>
        <div class="post_create_cancelbtn" id="backBtn"><a href="javascript: void(0);">Back</a></div>
	</div>
		
		<div class="clear"></div>
            	<div class="needed_data">
            		<div id="mapping">
            			<s:text name="autoPost" id="autoPost" class="autoPost" style="display: none;" />
            			<c:forEach var='item' items='${actionBean.myMap}' varStatus="loop">
            			  <div class="circleId_keyword" style="display: none;">
   							<input id="key" value="${item.key}" style="display: none;"></input>
   							<input id="value" value="${item.value}" style="display: none;"></input><br>
   						  </div>
						</c:forEach>
						<c:forEach items="${actionBean.circles.results}" var="circle" varStatus="loop">
                          <div class="circleId_circleName" style="display: none;">
   							<input id="cId" value="${circle.id}" style="display: none;"></input>
   							<input id="cName" value="${circle.circleName}" style="display: none;"></input><br>
   						  </div>
                        </c:forEach>
            		</div>
            		
            	</div>
            </div>
    </s:form>
</div>