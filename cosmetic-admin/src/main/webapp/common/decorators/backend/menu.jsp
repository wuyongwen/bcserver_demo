<%@ include file="/common/taglibs.jsp"%>
<div id=content>
<s:form beanclass="${actionBean.class}">
<div id=top_menu class=clearfix>
<ul class=sf-menu>
    <!-- dropdown menu -->
    <li class=current>
        <a href="#a">User</a><!-- first level menu -->
        <span class="sf-sub-indicator"> &#x25BC;</span>
        <ul>
        	<c:if test="${actionBean.currentUser eq null}">
            <li><a href="<c:url value="/user/login.action"/>">Login</a></li>
            </c:if>
            <c:if test="${actionBean.currentUser ne null}">
            	<li><a href="<c:url value="/user/logout.action"/>">Logout</a></li>
            </c:if>
            <c:if test="${actionBean.currentUser ne null}">
            	<li><a href="<c:url value="/user/editCurrentUser.action"/>">User Info</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.userManagerAccess == true or actionBean.accessControl.reportManagerAccess == true}">
            	<li><a href="<c:url value="/user/userEdit.action"/>">User Management</a></li>
            	<li><a href="<c:url value="/user/reportedUser.action"/>">Reported User Management</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true}">
            	<li><a href="<c:url value="/user/deleteUser.action"/>">Delete User</a></li>
            	<li><a href="<c:url value="/user/blockDevice.action"/>">Block Device</a></li>
            	<li><a href="<c:url value="/user/autoCreateUser.action"/>">Auto Create User</a></li>
            	<li><a href="<c:url value="/user/searchDeepLinkLog.action"/>">Search Deep Link Log</a></li>
            </c:if>
        </ul>
    </li>
    <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.postManagerAccess == true or actionBean.accessControl.reportManagerAccess == true or actionBean.accessControl.reportAuditorAccess == true}">
    <li>
        <a href="#a">Curate</a><!-- first level menu -->
        <span class="sf-sub-indicator"> &#x25BC;</span>
        <ul>
    		<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.postManagerAccess == true}">
    			<li><a href="<c:url value="/post/listUserPost.action"/>">Post Management</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.reportManagerAccess == true}">
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=Qualified"/>">Approve UGC Post</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=QualifiedNail"/>">Approve UGC Nail</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=RevQualified"/>">Review Disapproved UGC</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=RevQualifiedNail"/>">Review Disapproved UGC Nail</a></li>
            	<li><a href="<c:url value="/post/ReportedPost.action"/>">Resolve Disputes</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=RetagScraped"/>">Cat/Tag Scraped Post</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=Pgc"/>">Cat/Tag PGC Post</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=Retag"/>">Retag Approved Post</a></li>
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=NewCat"/>">Review New Cat. Post</a></li>
            	<!-- 
            		<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=Trending"/>">Review Trending Post</a></li>
            	 -->
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=Disqualified"/>">Approve UGC Post (Low)</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.reportAuditorAccess == true}">
            	<li><a href="<c:url value="/post/DisputePostSystem.action?activity"/>">Trending Post Summary</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true}">
            	<li><a href="<c:url value="/post/DisputePostSystem.action?poolType=Violate"/>">Review Violated Post</a></li>
            	<li><a href="<c:url value="/post/listUserPost.action?clpost"/>">CL-Posts</a></li>
            	<li><a href="<c:url value="/post/post-default-tag-manager.action"/>">Default Tag Management</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.reportManagerAccess == true}">
            	<li><a href="<c:url value="/user/badge-program.action"/>">Review Star of Week</a></li>
           	</c:if>
        </ul>
    </li>
    </c:if>
	<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.postManagerAccess == true or actionBean.accessControl.reportManagerAccess == true}">
    <li>
        <a href="#a">Scrape</a><!-- first level menu -->
        <span class="sf-sub-indicator"> &#x25BC;</span>
        <ul>
    		<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.postManagerAccess == true}">
            	<li><a href="<c:url value="/post/externalPost.action"/>">External Post</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.reportManagerAccess == true}">
            	<li><a href="<c:url value="/post/externalPost.action?analysis"/>">External Post Summary</a></li>
            </c:if>
        </ul>
    </li>
    </c:if>
</ul>
<ul class=sf-menu>

	<li class=current>
		<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true}">
        <a href="#a">Product</a>
        <span class="sf-sub-indicator"> &#x25BC;</span>
        <ul>
			<c:if test="${actionBean.currentUserAdmin == true}">
            <li><a href="<c:url value="/product/PriceRangeManage.action"/>">Price Range Management</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.productManagerAccess == true}">
            <li><a href="<c:url value="/product/BrandManage.action"/>">Brand Management</a></li>
            <li><a href="<c:url value="/product/BrandIndexManage.action"/>">Brand Index Management</a></li>
            <li><a href="<c:url value="/product/ProdTypeManage.action"/>">Category Management</a></li>
            <li><a href="<c:url value="/product/ProductManage.action"/>">Product Info Management</a></li>
            <li><a href="<c:url value="/product/SearchProductStoreLink.action"/>">Search Product Link</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true}">
            <li><a href="<c:url value="/product/ProdChangeLog.action"/>">Product Change Log</a></li>
            </c:if>
            <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.reportManagerAccess == true}">
            <li><a href="<c:url value="/product/ReportedProdCommentManage.action"/>">Bad Comment Manage</a></li>
            </c:if>
        </ul>
        </c:if>
    </li>
    <c:if test="${actionBean.currentUserAdmin == true}">
	<li><a href="#a">Search</a> 
		<span class="sf-sub-indicator"> &#x25BC;</span>
		<ul>
			<li><a href="<c:url value="/search/searchCircle.action"/>">Search Circle</a></li>
			<li><a href="<c:url value="/search/searchPeople.action"/>">Search People</a></li>
			<li><a href="<c:url value="/search/searchPost.action"/>">Search Post</a></li>
			<li><a href="<c:url value="/search/searchPostByTag.action"/>">Search Post By Tag</a></li>
			<li><a href="<c:url value="/search/listTopPostKeyword.action"/>">List Top Post Keyword</a></li>
			<li><a href="<c:url value="/search/listTopTag.action"/>">List Top Post Tag</a></li>
			<li><a href="<c:url value="/search/listCircleSuggestion.action"/>">List Circle Suggestion</a></li>
			<li><a href="<c:url value="/search/listPeopleSuggestion.action"/>">List People Suggestion</a></li>
			<li><a href="<c:url value="/search/listPostKeywordSuggestion.action"/>">List Post Keyword Suggestion</a></li>
			<li><a href="<c:url value="/search/listPostTagSuggestion.action"/>">List Post Tag Suggestion</a></li>
			<li><a href="<c:url value="/search/listRecentKeywordByUserId.action"/>">List Recent Keyword By User Id</a></li>
			<li><a href="<c:url value="/search/removeRecentKeywordByUserId.action"/>">Remove Recent Keyword By User Id</a></li>
		</ul>
	</li>
	</c:if>
    <c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.eventManagerAccess == true}">
	<li><a href="#a">Event</a> 
		<span class="sf-sub-indicator"> &#x25BC;</span>
			<ul>
				<li><a href="<c:url value="/event/EventManager.action"/>">Event Management</a></li>
				<li><a href="<c:url value="/event/EventBeautyInsightManager.action"/>">Beauty Buzz Management</a></li>
				<li><a href="<c:url value="/event/HoroscopeManager.action"/>">Horoscope Management</a></li>
			</ul>
	</li>
	</c:if>
	<c:if test="${actionBean.currentUserAdmin == true}">
	<li><a href="#a">Circle</a> 
		<span class="sf-sub-indicator"> &#x25BC;</span>
			<ul>
				<li><a href="<c:url value="/circle/circle-manage.action"/>">Circle Management</a></li>
				<li><a href="<c:url value="/circle/list-circle-by-user.action"/>">List User Circle</a></li>
				<li><a href="<c:url value="/circle/circle-type-group-manager.action"/>">Circle Type Group</a></li>
			</ul>
	</li>
	</c:if>
	<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.apkManagerAccess == true}">
		<li><a href="#a">File</a> 
	</c:if>	
			<span class="sf-sub-indicator"> &#x25BC;</span>
			<ul>
				<c:if test="${actionBean.currentUserAdmin == true}">
					<li><a href="<c:url value="/file/file-manage.action?uploadInput"/>">Upload File</a></li>
					<li><a href="<c:url value="/file/copy-file.action"/>">Copy Files to Test Bucket</a></li>
					<li><a href="<c:url value="/file/file-manage.action"/>">File Management</a></li>
				</c:if>
				<c:if test="${actionBean.currentUserAdmin == true or actionBean.accessControl.apkManagerAccess == true}">
					<li><a href="<c:url value="/file/file-manage.action?uploadInputApk"/>">Upload APK File</a></li>
				</c:if>
			</ul>
		</li>
	<c:if test="${actionBean.currentUserAdmin == true}">
		<li><a href="#a">Campaign</a> 
			<span class="sf-sub-indicator"> &#x25BC;</span>
	    	<ul>
				<li> <a href="<c:url value="/campaign/campaignManager.action"/>">Campaign Management</a></li>
			</ul>
	    </li>
	</c:if>
	<c:if test="${actionBean.currentUserAdmin == true}">
	    <li>
	        <a href="<c:url value="/feed/feed-manage.action"/>">Feed</a>
	        <span class="sf-sub-indicator"> &#x25BC;</span>
	    	<ul>
				<li> <a href="<c:url value="/feed/trendUserManager.action"/>">Trend User Manager</a></li>
				<li> <a href="<c:url value="/post/DisputePostSystem.action?poolType=TrendingTest"/>">List Trending</a></li>
				<li> <a href="<c:url value="/feed/importTrending.action"/>">Import Trending</a></li>
			</ul>
	    </li>
	</c:if>
	<c:if test="${actionBean.currentUserAdmin == true}">
		<li><a href="#a">Look</a> 
			<span class="sf-sub-indicator"> &#x25BC;</span>
			<ul>
				<li><a href="<c:url value="/look/look-type-manager.action"/>">Type Management</a></li>
			</ul>
		</li>
	</c:if>
	
	<c:if test="${actionBean.currentUserAdmin == true}">
	<li><a href="#a">Miscellaneous</a> 
		<span class="sf-sub-indicator"> &#x25BC;</span>
			<ul>
				<li><a href="<c:url value="/misc/DiscoverTabManage.action"/>">Tab Management</a></li>
				<li><a href="<c:url value="/misc/encrUtil.action"/>">Encryption Utility</a></li>
				<li><a href="<c:url value="/misc/serverRestartManage.action"/>">Server Restart Management</a></li>
				<li><a href="<c:url value="/misc/verifyEmail.action"/>">Verify Email Utility</a></li>
				<li><a href="<c:url value="/misc/FacebookAdManage.action"/>">Facebook Ad. Management</a></li>
			</ul>
	</li>
	</c:if>
</ul>
</div>
</s:form>
</div>