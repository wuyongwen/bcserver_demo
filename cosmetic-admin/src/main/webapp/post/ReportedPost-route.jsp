<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/post/reportPost.js?v=${randVer}" />"></script>
<h2 class=ico_mug>Post :: Reported Posts</h2>
<div class=clearfix>
<s:form beanclass="${actionBean.class}" method="get">
</s:form>
<label>Locale : </label>
<select id="reportLocale">
<c:forEach items="${actionBean.availableRegion}" var="region" varStatus="loop">
<c:choose>
	<c:when test="${region eq actionBean.selRegion}">
		<option value="${region}" selected>${region}</option>
	</c:when>
	<c:otherwise>
		<option value="${region}">${region}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>
&nbsp;&nbsp;|&nbsp;&nbsp;
<label>Review Target : </label>
<select id="selTargetType">
	<c:choose>
		<c:when test="${actionBean.targetType eq 'Post'}">
			<option value="All">All</option>
			<option value="Post" selected>Post</option>
			<option value="Comment">Comment</option>
		</c:when>
		<c:when test="${actionBean.targetType eq 'Comment'}">
			<option value="All">All</option>
			<option value="Post">Post</option>
			<option value="Comment" selected>Comment</option>
		</c:when>
		<c:otherwise>
			<option value="All" selected>All</option>
			<option value="Post">Post</option>
			<option value="Comment">Comment</option>
		</c:otherwise>
	</c:choose>
</select>
&nbsp;&nbsp;|&nbsp;&nbsp;
<label>Review Status : </label>
<select id="reportStatus">
	<c:choose>
		<c:when test="${actionBean.selStatus eq 'NewReported'}">
			<option value="NewReported" selected>NewReported</option>
			<option value="Reviewed">Reviewed</option>
		</c:when>
		<c:otherwise>
			<option value="NewReported">NewReported</option>
			<option value="Reviewed" selected>Reviewed</option>
		</c:otherwise>
	</c:choose>
</select><br>
<label>Search By Author ID : </label><input id="searchAuthorIdInput" type="number" value="${actionBean.searchAuthorId}"><input id="searchByAuthorBtn" type="button" value="Search"><br>
<label>Search By Reported ID : </label><input id="searchReportedIdInput" type="number" value="${actionBean.searchReportedId}"><input id="searchByReportedBtn" type="button" value="Search"><br>
<input id="undecidedAllBtn" style="font-size:14px;" class="button" type="button" value="Undecide All">
<input id="publishAllBtn" style="font-size:14px;" class="button" type="button" value="Publish All">
<input id="bannedAllBtn" style="font-size:14px;" class="button" type="button" value="Banned All"><br>
<div align="center" style="with:100%;">
<display:table id="row" name="actionBean.pageResult.results" requestURI="./ReportedPost.action" requestURIcontext="disable" pagesize="${actionBean.size}" sort="page" partialList="true" size="actionBean.pageResult.totalSize" export="false">
	<display:setProperty name="basic.show.header" value="false" />
	<display:setProperty name="paging.banner.placement" value="both" />
	<display:setProperty name="paging.banner.group_size" value="10" />
	<display:setProperty name="paging.banner.some_items_found" value=''/>
	<display:setProperty name="paging.banner.full" value='<span style="font-size:18px;" class="pagelinks"> <a href="{2}"><span style="font-size:22px;" class="icon">&#9668;</span></a> {0} <a href="{3}"><span <span style="font-size:22px;" class="icon">&#9658;</span></a></span>'/> 
	<display:setProperty name="paging.banner.first" value='<span style="font-size:18px;" class="pagelinks"> <span style="font-size:22px;" class="icon">&#9668;</span></a> {0} <a href="{3}"><span style="font-size:22px;" class="icon">&#9658;</span></a></span>'/>
	<display:setProperty name="paging.banner.last" value='<span style="font-size:18px;" class="pagelinks"> <a href="{2}"><span style="font-size:22px;" class="icon">&#9668;</span></a> {0} <span style="font-size:22px;" class="icon">&#9658;</span></span>'/>
	<display:column title="Cover" style="width:30%;">
		<c:choose>
			<c:when test="${row.type eq 'Post'}">
				<div align="center">
					<c:choose>
						<c:when test="${fn:length(row.post.attachments.files) > 1}">
							<img name="attachmentImg" pId="${row.post.postId}" src="${row.post.attachments.files[1].downloadUrl}" align="center" style="max-width:240px; max-height:240px; width:auto; height:auto;"/>
						</c:when>
						<c:when test="${fn:length(row.post.attachments.files) > 0}">
							<img name="attachmentImg" pId="${row.post.postId}" src="${row.post.attachments.files[0].downloadUrl}" align="center" style="max-width:240px; max-height:240px; width:auto; height:auto;"/>
						</c:when>
						<c:otherwise>
							<img name="attachmentImg" pId="${row.post.postId}" alt="No Image Available" align="center" style="max-width:240px; max-height:240px; width:auto; height:auto;"/>
						</c:otherwise>
					</c:choose>
				</div>
			</c:when>
			<c:otherwise>
				<img name="attachmentImg" pId="${row.comment.refId}" alt="No Image Available" align="center" style="max-width:240px; max-height:240px; width:auto; height:auto;"/>
			</c:otherwise>
		</c:choose>
	</display:column>
	<display:column title="Title" style="width:40%;">
	<table style="width: 100%;">
		<tr style="word-break: break-all;">
			<td>
				<c:choose>
					<c:when test="${row.type eq 'Post'}">
						<c:choose>
							<c:when test="${fn:length(row.post.attachments.files[0].redirectUrl) > 0}">
								<a style="font-size:20px;" href="${row.post.attachments.files[0].redirectUrl}" target="_blank">${row.post.title}</a><br>
							</c:when>
							<c:otherwise>
								<label style="color: 000000;font-size: 20px;font-weight: bold;">
								<a href="javascript:window.open('./queryPost.action?postId=${row.post.postId}')">${row.post.title}</a>
								<c:if test="${row.post.postSource eq 'contest'}"><font color="#cc0000">*[contest post]</font></c:if>
								</label><br>
							</c:otherwise>
						</c:choose>
						<c:if test="${fn:length(row.post.circles) > 0}">
							<label style="font-weight: bold;">${row.post.circles[0].translateCircleName}&nbsp; | &nbsp;</label>
						</c:if>
						<label>${row.post.twTime}&nbsp;(TW)</label><br>
						<label>Creator : ${row.post.creator.displayName}</label>&nbsp;<label class="authorName" style="cursor:pointer;" dir="ltr" uid="${row.post.creator.userId}" avatar="${row.post.creator.avatar}" email="${row.post.creator.email}" accSource="${row.post.creator.accountSource}">(${row.post.creator.userId})</label><br>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${row.comment.refType eq 'Post'}">
								<label style="color: 000000;font-size: 20px;font-weight: bold;"><a href="javascript:window.open('./queryPost.action?postId=${row.comment.refId}')">${row.comment.comment}</a></label><br>
							</c:when>
							<c:otherwise>
								<label style="color: 000000;font-size: 20px;font-weight: bold;">${row.comment.comment}</label><br>
							</c:otherwise>
						</c:choose>
						<label>${row.comment.lastModified}</label><br>
						<label>Creator : ${row.comment.creator.displayName}</label>&nbsp;<label class="authorName" style="cursor:pointer;" dir="ltr" uid="${row.comment.creator.userId}" avatar="${row.comment.creator.avatar}" email="${row.comment.creator.email}" accSource="${row.post.creator.accountSource}">(${ row.comment.creator.userId})</label><br>
					</c:otherwise>
				</c:choose>
				<label>Reported : </label>
				<c:forEach items="${row.reasons}" var="reason" varStatus="loop">
					<label>${reason.reporter.displayName}</label>&nbsp;<label class="authorName" style="cursor:pointer;" dir="ltr" uid="${reason.reporter.id}" avatar="${reason.reporter.avatarUrl}" email="" accSource="">(${ reason.reporter.id})</label>
					<c:if test="${loop.index < fn:length(row.reasons) - 1}">
						<c:out value="," />
					</c:if>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td>
				<c:choose>
					<c:when test="${row.type eq 'Post'}">
						<textarea rows="7" style="resize:none; font-size:16px; width: 100%;" readonly='true'>${row.post.content}</textarea>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>

	</table>
	</display:column>
	<display:column title="Action" style="width:30%;">
		<c:choose>
			<c:when test="${row.type eq 'Post'}">
				<c:set var="targetId" value="${row.post.postId}"/>
				<c:set var="targetType" value="Post"/>
			</c:when>
			<c:otherwise>
				<c:set var="targetId" value="${row.comment.commentId}"/>
				<c:set var="targetType" value="Comment"/>
			</c:otherwise>
		</c:choose>
		<table>
			<tr>
				<td style="vertical-align: middle;">
					<label style="color: 000000;font-size: 20px;">
						<input type="radio" name="action${targetId}" act="checkUndecided" targetType="${targetType}" value="${targetId}" style="transform: scale(3.5);" checked>
						&nbsp;Undecided
					</label>
				</td>
			</tr>
			<tr>
				<td style="vertical-align: middle;">
					<label style="color: 000000;font-size: 20px;">
						<input type="radio" name="action${targetId}" act="Published" targetType="${targetType}" value="${targetId}" style="transform: scale(3.5);">
						&nbsp;Published
					</label>
				</td>
			</tr>	
			<tr>
				<td style="vertical-align: middle;">
					<label style="color: 000000;font-size: 20px;">
						<input type="radio" name="action${targetId}" act="Banned" targetType="${targetType}" value="${targetId}" style="transform: scale(3.5);">
						&nbsp;Banned
					</label>
				</td>
			</tr>	
		</table>
	</display:column>
</display:table>
<div align="right" style="with:80%; margin:0 12% 0 10%;" id="saveButtonDiv">
<button style="font-size:14px;" class="button" id="handleReported">Submit</button>
</div>
<label id="tableTotalSize" style="display:none;">${actionBean.pageResult.totalSize}</label>
</div>
<div id="handleProgress" style="display:none;"></div>
</div>
<div id="authorDialog" title="Author property">
	<div style="min-height:150px;">
		<div style="float:left;width:150px; height:150px; border-radius: 75px; overflow:hidden;">
			<img id="authorAvatar" style="width:100%;height:100%;"/>
		</div>
		<div style="padding-left: 180px;">
			<table style="border-collapse:collapse;border-spacing:0;">
				<tr>
					<td>User ID :</td>
					<td>
						<label id="authorId"/>
					</td>
				</tr>
				<tr>
					<td>Name :</td>
					<td>
						<label id="authorName"/>
					</td>
				</tr>
				<tr>
					<td>Account Source :</td>
					<td>
						<label id="authorAccountSource"/>
					</td>
				</tr>
				<tr>
					<td>Email :</td>
					<td>
						<label id="authorEmail"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div style="padding-top: 50px;">
		<a id="allReportedForOneAuthor"><u>All reported posts/comments</u></a>
		&nbsp;&nbsp;|&nbsp;&nbsp;
		<a id="allReportingForOneAuthor"><u>All reporting posts/comments</u></a>
		&nbsp;&nbsp;|&nbsp;&nbsp;
		<a id="allRelatedPostCommentForOneAuthor"><u>All post/comment for this user</u></a>
		<br>
	</div>
</div>