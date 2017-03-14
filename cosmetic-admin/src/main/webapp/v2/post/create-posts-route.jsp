<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/post.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/preview.css?v=${randVer}" />">

<script src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/common/scripts/moment.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/common/scripts/moment-timezone-with-data-2010-2020.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/common/scripts/twitter-text-1.13.0.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/post/post.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/post/ckeditor/ckeditor.js?v=${randVer}" />"></script>

<script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js"></script>
<script src="https://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">

<script type="text/javascript">
    $(document).ready(function(){
        $("#postPreviewView").toggle(false);
        $("#backBtn").toggle(false);

        CKEDITOR.replace($(".add_url")[0]);
    });

</script>

<div class=clearfix>
    <s:form beanclass="${actionBean.class}">
        <div class="page-header">Create Post</div>
        <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
            <div id="createPostView">
                <div class="clear"></div>
                <div class="group-select">
                    <label>Title:&nbsp;<span class="red">* </span></label>
                    <br>
                    <s:text name="title" id="title" class="add_title" maxlength="45"/>
                </div>
                <div class="group-select">
                    <label>Pick a circle</label>
                    <s:select name="circleId" id="circleSel" class="form-control select ng-pristine ng-valid">
                        <c:forEach items="${actionBean.circles.results}" var="circle" varStatus="loop">
                          <s:option value="${circle.id}">${circle.circleName}</s:option>
                        </c:forEach>
                    </s:select>
                </div>
                <div id="postContent">
                    <div class="add-paragraph">
                        <div class="add_info" style="padding-left:10px; margin-bottom:20px;"><span style="font-size:16px; color:#000000;">Instructions:</span><br> 
                        • Upload at least one photo before createing a post. <br>
                        • The first photo will be the cover of your post.<br> 
                        • You can also add URL inline in the text area.</div>
                        <div class="ctn_sep" style="background:#f0f0f0; margin:0 0 10px 0; height:2px;"></div>
                        <div id="photoSet">
                            <div class="add_image_btn2" id="0">
                                <div class="delete_paragraph" hidden="true"><a href="javascript: void(0);" ><i class="fa fa-times" style="color:#FFFFFF;"></i></a></div>
                                <h2 style="margin-left:15px;">Paragraph 1</h2>
                                <ul>
                                    <li>
                                        <div class="postImgContainer">
                                            <img class="postImg">
                                            <input class="postImgInput" name="file" type="file" accept="image/*">
                                        </div>
                                        <div class="addRedirectUrlBtn"><a href="javascript: void(0);">Add URL</a></div>
                                        <input class="redirectUrl" name="input" type="text" maxlength="1024" hidden="true" isWidget="false">
                                    </li>
                                </ul>
                                <div class="add_url_box">
                                    <div class="add_url"></div>
                                </div>
                                <div class="clear"></div>
                                <div class="ctn_sep" style="background:#f0f0f0; margin:0 0 10px 0; height:2px;"></div>
                            </div>
                        </div>
                    </div>
                    <div class="add-paragraph_btn"><a href="javascript: void(0);"><i class="fa fa-plus" style="color:#FFFFFF;"></i>&nbsp;&nbsp;Add Paragraph</a></div>
					<c:set var="currentUserStatus" value="${actionBean.currentUserStatus}"/>
					<c:choose>
					    <c:when test="${currentUserStatus!='Hidden'}">
	                    <div style="font-size: 15px">
	          				<p><span>Schedule posting</span></p>
	        				<label>
	           					<input id="scheduleCheck" type="checkbox" value="checkbox"><span> Publish on specified date & time</span></input>
	        				</label><br>
							<input type="text" id="scheduleDateTime" readonly='true' zone="${actionBean.currentUser.region}" style="display:none;">
	      					</div>
	      				</div>
					    </c:when>
					</c:choose>
          			<div style="height:30px;"></div>
                </div>
            </div>
            <div id="postPreviewView">
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
                        <div class="subPost">
                            <img src="<c:url value="/v2/common/images/theme_cover.jpg" />">
                            <p class="previewContent">Dressing up for work can sometimes be a bore, especially when you work somewhere where the dress code is strict and corporate.</p>  
                            <p class="link"><a href="#" class="previewRedirectUrl" target="_blank">http://www.pinterest.com/pin/</a></p>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>    
                <div style="height:30px;"></div>
            </div>
            <div class="ctn_sep"></div>
            <div class="post_create_btn">
                <div class="post_create_publishbtn" id="publishBtn"><a href="javascript: void(0);">Publish</a></div>
                <div class="post_create_previewhbtn" id="previewBtn"><a href="javascript: void(0);">Preview</a></div>
                <div class="post_create_cancelbtn2" id="backBtn"><a href="javascript: void(0);">Back</a></div>
                <div class="post_create_previewhbtn" id="draftBtn"><a href="javascript: void(0);">Save Draft</a></div>
                <div class="post_create_cancelbtn" id="cancelBtn"><a href="javascript: void(0);">Cancel</a></div>
                <div class="clear"></div>
            </div>
        </div>
    </s:form>
    <div id="postProgress"></div>
    <div id="redirectDialog" title="Add URL">
        <div style="height:20px;"></div>
        <div class="url_ctn">
            <div class="notion">1. If you want to share a web page, please fill in the url.</div>
        </div>
        <div class="url_ctn">
            <div class="notion">2. If you want to share a video, please fill in YouTube link or Youku link.</div>
        </div>
        <div class="url_ctn">
            <div class="notion">(We will parse the video thumnail automatically)</div>
        </div>
        <div class="url_ctn">
            <div class="notion">3. If you want to add a widget, please fill in the url and check "Add Widget".</div>
        </div>
        <div style="height:20px;">
        	<label>
	           <input id="widgetCheck" type="checkbox" value="checkbox"><span style="font-size:10px;"> Add Widget</span></input>
	        </label>
        </div>
        <div class="url_ctn">
            <div class="url_ctn_title">Url:</div>
            <input name="input" type="text" class="url_ctn_urlname" id="inputUrl" value="http://" size="" maxlength="1024">
        </div>
    </div>
</div>