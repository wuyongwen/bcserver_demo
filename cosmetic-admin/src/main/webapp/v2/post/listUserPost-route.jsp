<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ page import="org.displaytag.util.ParamEncoder"%>
<%!
    // "row" is the tableId of displaytag table.
    String pagingParam = new ParamEncoder("row").encodeParameterName(org.displaytag.tags.TableTagParameters.PARAMETER_PAGE);
%>

<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/profile.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/circle.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/post.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/displaytag2.css?v=${randVer}"/>">

<script src="<c:url value="/v2/post/post.js?v=${randVer}" />"></script>
<div class=clearfix>
    <s:form beanclass="${actionBean.class}" method="get">
        <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
            <div class="page-header">Posts</div>
            <div class="post_filter">
                <div class="post_filter_name">Post Status:</div>
                <s:select name="postStatus" class="post_filter_dropdw select ng-pristine ng-valid" id="postStatusSel">
                    <s:option value="-1">All</s:option>
                    <c:forEach items="${actionBean.availablePostStatus}" var="availablePostStatus" varStatus="loop">
                      <s:option value="${loop.index}">${availablePostStatus.value}</s:option>
                    </c:forEach>
                </s:select>
                <div class="post_filter_name">Circles:</div>
                <s:select name="circleId" class="post_filter_dropdw select ng-pristine ng-valid" id="circleSel">
                    <s:option value="-1">All</s:option>
                    <c:forEach items="${actionBean.circles.results}" var="circle" varStatus="loop">
                      <s:option value="${circle.id}">${circle.circleName}</s:option>
                    </c:forEach>
                </s:select>
                <div class="post_btn3"><a href="./create-posts.action">Write a Post</a></div>
            </div>
            <div class="engagement_hint">
            	<div class="engagement_hint_pink">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Likes, Comments & Circle In</div>
            </div>
            <br/>
            <display:table id="row" name="actionBean.pageResult.results" requestURI="./listUserPost.action" pagesize="20" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false" >
                <display:setProperty name="paging.banner.placement" value="bottom" />
                <display:setProperty name="paging.banner.full" value='<span class="pagelinks"><div class="current_page"><input name="page_num" type="text" id="textfield1" maxlength="5" class="_num" value="{5}">of <div class="totalPage">{6}</div><div></div><a href="{2}" class="_nav_forward"></a><a href="{3}" class="_nav_backward"></a></span>'/> 
                <display:setProperty name="paging.banner.first" value='<span class="pagelinks"><div class="current_page"><input name="page_num" type="text" id="textfield1" maxlength="5" class="_num" value="{5}">of <div class="totalPage">{6}</div></div><a href="{3}" class="_nav_backward"></a></span>'/>
                <display:setProperty name="paging.banner.last" value='<span class="pagelinks"><div class="current_page"><input name="page_num" type="text" id="textfield1" maxlength="5" class="_num" value="{5}">of <div class="totalPage">{6}</div></div><a href="{2}" class="_nav_forward"></a></span>'/>
                <display:setProperty name="paging.banner.onepage" value='<span class="pagelinks"><div class="current_page"><input name="page_num" type="text" id="textfield1" maxlength="5" class="_num" value="{5}">of <div class="totalPage">{6}</div></div></span>'/>
                <display:column title="Date" style="width:10%;">
                    <fmt:formatDate value="${row.createdTime}" pattern="yyyy-MM-dd" />
                </display:column>
                <display:column title="Title" style="width:28%;">
                    <c:out value="${row.title}" />
                </display:column>
                <display:column title="Circle" style="width:10%;">
                    <c:out value="${row.circles[0].circleName}" />
                </display:column>
                <display:column title="Status" style="width:10%;">
                	<div id = "engagement_${row.postId}">
                		<c:out value="${actionBean.availablePostStatus[row.status]}" />
                	</div>
                </display:column>
                <display:column title="Engagement" style="width:12%;">
                    <div id="graph_${row.postId}" name="graph" count="${actionBean.totalCountMap[row.postId]}"></div>
                </display:column> 
                <display:column title="Action" style="width:30%;">
                    <div class="post_item_btn3">
                        <a href="javascript: void(0);" id="${row.postId}" class="viewPost"><i class="fa fa-file-image-o" style="color:#FFFFFF"></i>&nbsp;View</a>
                    </div> 
                    <div class="post_item_btn1">
                        <a href="javascript: void(0);" id="${row.postId}" class="editPost"><i class="fa fa-pencil-square-o" style="color:#FFFFFF"></i>&nbsp;Edit</a>
                    </div>
                    <div class="post_item_btn2">
                        <a href="javascript: void(0);" id="${row.postId}" class="deletePost"><i class="fa fa-times" style="color:#FFFFFF"></i>&nbsp;Delete</a>
                    </div>
                </display:column>
            </display:table>
            <div class="clear"></div>
            <div class="profile_sep" style="display:none;"></div>    
        </div>
    </s:form>
</div>
<script type="text/javascript">
$(document).ready(function(){
    var originalPage = $("._num").val();

    $("#postStatusSel").change(function() {
        window.location.href = "./listUserPost.action?route&postStatus=" + $("#postStatusSel option:selected").val() + "&circleId=" + $("#circleSel option:selected").val();
    });
    
    $("#circleSel").change(function() {
        window.location.href = "./listUserPost.action?route&postStatus=" + $("#postStatusSel option:selected").val() + "&circleId=" + $("#circleSel option:selected").val();
    });

    $("._num").change(function() {
        var goPage = parseInt($(this).val());
        var totalPage = parseInt($(".totalPage").html());
        if (goPage > totalPage) {
            $(this).val(originalPage);
            return;
        }
        window.location.href = "./listUserPost.action?route&postStatus=" + $("#postStatusSel option:selected").val() + "&circleId=" + $("#circleSel option:selected").val() + "&<%=pagingParam%>=" + goPage;
    });

    //init  engagement
	var maxLimitTotalCount = ${actionBean.maxLimitTotalCount};
	$("div[name='graph']").each(function(){
		var count = parseInt($(this).attr("count"));
		var newDiv = $(document.createElement('div'));
		var totalPercent = Math.ceil(50*(parseFloat(count)/parseFloat(maxLimitTotalCount)));
		var newNumDiv = $(document.createElement('div'));
		if(count >= 1000000000000){
			count = Math.floor((parseFloat(count)/1000000000000)*10)/10;
			count = count + "T";
		}
		else if(count >= 1000000000){
			count = Math.floor((parseFloat(count)/1000000000)*10)/10;
			count = count + "G";
		}
		else if(count >= 1000000){
			count = Math.floor((parseFloat(count)/1000000)*10)/10;
			count = count + "M";
		}
		else if(count >= 1000){
			count = Math.floor((parseFloat(count)/1000)*10)/10;
			count = count + "k";
		}
		newNumDiv.attr('style','display:inline;width:22px;height:10px;margin-left:0px;float:left;font-size:80%;line-height:10px;-webkit-text-size-adjust:none;text-align:right;');
		newNumDiv.html(count);
		newNumDiv.appendTo(newDiv);
		var newBarDiv = $(document.createElement('div'));
		newBarDiv.attr('style','display:inline;background-color:pink;width:' + totalPercent + 'px;height:10px;line-height:10px;float:left;text-align:left;margin-left:10px;');
		newBarDiv.appendTo(newNumDiv);
		newBarDiv.appendTo(newDiv);
		newDiv.appendTo(this);
	});
});
</script>

