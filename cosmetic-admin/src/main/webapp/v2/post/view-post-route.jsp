<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/post.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/preview.css?v=${randVer}" />">

<script src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script src="<c:url value="/v2/post/post.js?v=${randVer}" />"></script>

<script type="text/javascript">
    $(document).ready(function(){


        if ($("#postStatus").html() == "Published") {
            $("#publishBtn2").toggle(false);
            $("#draftBtn2").toggle(true);
        }
        else if ($("#postStatus").html() == "Drafted") {
            $("#publishBtn2").toggle(true);
            $("#draftBtn2").toggle(false);
        }
        else if ($("#postStatus").html() == "Hidden") {
            $("#publishBtn2").toggle(false);
            $("#draftBtn2").toggle(false);
        }
    });
</script>

<div class=clearfix>
    <s:form beanclass="${actionBean.class}">
        <div id="postId" style="display:none;">${actionBean.postId}</div>
        <div id="postStatus" style="display:none;">${actionBean.posts[0].postStatus}</div>
        <div id="postCreatedTime" style="display:none;">
        	<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${actionBean.posts[0].createdTime}" /> +0
		</div>
        <div class="page-header">Create Post</div>
        <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
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
                    <div class="previewurl_ctntoptitle" id="postPreviewTitle">${actionBean.posts[0].title}</div>
                    <div class="preview_ctn">
                        <c:forEach items="${actionBean.posts}" var="post" varStatus="loop">
                            <div class="subPost">
                                <img src="${post.attachments[0].attachmentFile.fileItems[0].originalUrl}">
                                <p class="previewContent">${post.content}</p>
                                <p class="link"><a href="${post.redirectUrl}" class="previewRedirectUrl" target="_blank">${post.redirectUrl}</a></p>  
                            </div>
                        </c:forEach>
                    </div>
                    <div class="clear"></div>
                </div>    
                <div style="height:30px;"></div>
            </div>
            <div class="ctn_sep"></div>
            <div class="post_create_btn">
                <div class="post_create_publishbtn" id="publishBtn2"><a href="javascript: void(0);">Publish</a></div>
                <div class="post_view_editbtn" id="editBtn"><a href="javascript: void(0);">Edit</a></div>
                <div class="post_create_previewhbtn" id="draftBtn2"><a href="javascript: void(0);">Unpublished</a></div>
                <div class="post_view_deletebtn" id="deleteBtn"><a href="javascript: void(0);">Delete</a></div>
                <div class="post_create_cancelbtn" id="cancelBtn"><a href="javascript: void(0);">Back</a></div>
                <div class="clear"></div>
            </div>
        </div>
    </s:form>
    <div id="postProgress"></div>
</div>