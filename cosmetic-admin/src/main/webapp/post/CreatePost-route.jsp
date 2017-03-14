<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script type="text/javascript" src="<c:url value="/common/lib/general.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/attachment.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/ckeditor/ckeditor.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/post.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/login.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/horoscope.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/smartTag.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/postType.js?v=${randVer}" />"></script>

<link href="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/themes/hot-sneaks/jquery-ui.css" />" rel="stylesheet">
<link href="<c:url value="/post/MultiForm.css?v=${randVer}" />" rel="stylesheet">

<c:if test="${actionBean.postForm eq 'mainpost'}">
<script src="<c:url value="/common/lib/cropper/cropper.js?v=${randVer}" />"></script>
<script src="<c:url value="/post/cover.js?v=${randVer}" />"></script>

<link href="<c:url value="/common/lib/cropper/cropper.css" />" rel="stylesheet">
<link href="<c:url value="/common/lib/cropper/cropper.min.css" />" rel="stylesheet">
<link href="<c:url value="/post/cover.css" />" rel="stylesheet">
</c:if>
<script>
	var postTypeMap = ${actionBean.postTypeMap};
</script>
<div id="msform">
	<div class="topField">
		<ul id="progressbar">
			<c:choose>
			    <c:when test="${actionBean.postForm eq 'mainpost'}">
					<li class="active">Post Description</li>
					<li>Post Content</li>
			    </c:when>
			    <c:otherwise>
			        <li class="active">Post Content</li>
			    </c:otherwise>
			</c:choose>			
			<li>Final</li>
		</ul>
	</div>
	<div class="bottoomField">
		<c:if test="${actionBean.postForm eq 'mainpost'}">
			<fieldset id="postDescription">
				<div class="leftField">
					<h2 class="fs-title">Post Description</h2>
					<h3 class="fs-subtitle">Title and Cover for this post</h3>
					<div name="importPostFromDocDiv" style="display:none;">
						<s:form beanclass="${actionBean.class}">
							<label>Import from Doc: </label>
							<s:file name="docxPost" accept="application/msword"/>
							<s:submit name="importDoc" id="importDoc" value="Upload and Post" />
							<br><a href="./template.doc">Download Post Template</a>
			          	</s:form>
			          	<br>&nbsp;--- Or create with online editor ---&nbsp;<br>
		          	</div>
					<label>Title : </label>
					<textarea name="title" rows="3" cols="80"></textarea><br>
					<label>Promote This Post : </label>
					<input id="promoteScoreInput" type="checkbox">
					<input id="promoteOrder" type="number" style="display:none;"><br>
					<div>
						<table style="width:40%; margin-left: auto;margin-right: auto;border-style: groove;border-width: 1px;">
							<tr>
								<td style="width:50%;border-style: groove;border-width: 1px;">
									<label>Circles : </label>
								</td>
								<td style="width:50%;border-style: groove;border-width: 1px;">
									<label>Smart Tags : </label>
								</td>
							</tr>
							<tr>
								<td style="width:50%;border-style: groove;border-width: 1px;">
									<div id="circleSelection" style="overflow-y:scroll;max-height:150px;min-height:150px;">
								    	<table>
											<c:forEach items="${actionBean.circles}" var="circle" varStatus="loop">
												<c:choose>
												    <c:when test="${loop.index != 0 && loop.index%2 != 0}">
												        	<td width="40%" align="left">
																<label><input type="checkbox" id="selCircles" name="selCircles" value="${circle.id}"/>${circle.circleName}</label><br>
															</td>
										        		</tr>
												    </c:when>
												    <c:otherwise>
												    	<tr>
												    		<td width="40%" align="left">
												        		<label><input type="checkbox" id="selCircles" name="selCircles" value="${circle.id}"/>${circle.circleName}</label><br>
											        		</td>
												    </c:otherwise>
												</c:choose>		
											</c:forEach>
										</table>
										<a href="./CreateCircle.action"> + Create new Circle for your account .</a>
									</div>
								</td>
								<td style="width:50%;border-style: groove;border-width: 1px;">
									<div class="postTagsDiv" style="min-height:150px;max-height:150px;">
										<div class="postTags" style="min-height:115px; max-height:115px; overflow: auto;"></div>
										<div class="addPostTagDiv" style="min-height: 35px; max-height: 35px;"></div>
									</div>
								</td>
							</tr>
						</table>
					</div><br>
					<div class="postContent" align="center">
						<table id="postContentTable" style="width=60%;">
						<tbody/>
						</table>			
					</div>
				</div>
				<div class="rightField">
					<input type="button" name="next" class="next action-button" value="Next" />
				</div>
			</fieldset>
		</c:if>
		<fieldset id="postContent">
			<div class="leftField">
				<h2 class="fs-title">Post Content</h2>
				<h3 class="fs-subtitle">Add content for this post</h3>
				<div class="postContent" align="center">
					<table id="postContentTable" style="width=100%;">
					<tbody/>
					</table>			
				</div>
			</div>
			<div class="rightField">
				<input type="button" name="next" class="next action-button" value="Next" />
				<input type="button" id="addSubPost" name="addSubPost" class="addSubPost action-button" value="Add SubPost" />
				<c:if test="${actionBean.postForm eq 'mainpost'}">
					<input type="button" name="previous" class="previous action-button" value="Previous" />
				</c:if>
			</div>
		</fieldset>
		<fieldset id="final">
			<div class="leftField">
				<h2 class="fs-title">Final</h2>
			    <h3 class="fs-subtitle">Preview and Post</h3>
			    <div style="display:none;">
					<table style="margin-left: auto;margin-right: auto;">
						<tr>
							<td>
								<label>Circle Tags : </label>
							</td>
							<td>
								<div id="circleTagNameDiv" style="overflow-y:scroll;max-height:150px;width:300px;"></div>
							</td>
						</tr>
					</table>
				</div><br>
				<div id="postPreviewDiv" ></div>
				<c:if test="${actionBean.postForm eq 'mainpost'}">
				<br><br>
				</c:if>
				<br>
			</div>
			<div class="rightField">
				<c:choose>
				    <c:when test="${actionBean.postForm eq 'mainpost'}">
				        <input type="button" id="createPostButton" name="createPostButton" class="createPostButton action-button" value="Post" />
				    </c:when>
				    <c:otherwise>
				        <input type="button" id="createSubPostButton" name="createSubPostButton" class="createPostButton action-button" value="Post" />
				    </c:otherwise>
				</c:choose>		
				<input type="button" name="previous" class="previous action-button" value="Previous" />
			</div>
		</fieldset>
	</div>
</div>
<div id="postEditorTemplate" style="display:none;border: 1px solid #000;padding:5px;margin:5px;">
	<div id="attachmentSelection" align="center" style="width: 640px; height: 130px; left: 0px; top: 0px;">
		<label>Attachments : </label><br>
		<div id="attachmentDisplay" style="width: 640px; height: 100px; left: 0px; top: 0px;overflow-y: auto;">
			<img style="vertical-align: top;" src="image/add-attachment.png" id="attachmentButton" value="Insert" />
		</div>
		<div id="attachmentMetadatas" style="display:none"></div>
	</div>
	<div id="extLookDiv">
	<c:if test="${actionBean.postForm eq 'mainpost'}">
		<div id="postTypeDiv">
		<label>Post Type : </label>
		<s:form beanclass="${actionBean.class}" style="display:inline">
			<s:select id="postType" name="postType"><s:options-enumeration enum="com.cyberlink.cosmetic.modules.post.model.PostType" /></s:select>
		</s:form>
			<span id="lookTypesDiv" style="display:none">
				<label>*Look Category:</label>
				<select id="lookTypesSelection">
				<option disabled selected value>- Look -</option>
				<c:forEach items="${actionBean.lookTypes}" var="lookTypes" varStatus="loop">
					<option value="${lookTypes.id}">${lookTypes.name}</option>
				</c:forEach>
				</select>
			</span><br>
		</div>
	</c:if>
		<label>External Look Url : </label><input id="extLookUrlInput" type="text"><br>
		<label>Look Type ： </label><input id="extLookTypeInput" type="text"></input><br>
		<label>Horoscope Type ：  </label>
		<select id="extHoroscopeTypeInput">
			<option value="" selected>Select a horoscope</option>
			<option value="ARIES">Aries (3/21-4/19)</option>
			<option value="TAURUS">Taurus (4/20-5/20)</option>
			<option value="GEMINI">Gemini (5/21-6/21)</option>
			<option value="CANCER">Cancer (6/22-7/22)</option>
			<option value="LEO">Leo (7/23-8/22)</option>
			<option value="VIRGO">Virgo (8/23-9/22)</option>
			<option value="LIBRA">Libra (9/23-10/23)</option>
			<option value="SCORPIO">Scorpio (10/24-11/21)</option>
			<option value="SAGITTARIUS">Sagittarius (11/22-12/21)</option>
			<option value="CAPRICORN">Capricorn (12/22-1/19)</option>
			<option value="AQUARIUS">Aquarius (1/20-2/18)</option>
			<option value="PISCES">Pisces (2/19-3/20)</option>
		</select><br/>
		<c:if test="${actionBean.postForm eq 'mainpost'}">
			<div id="horoscopeMasterArea" style="display: none;">
				<label>Master ID：</label>
				<input id="masterId" type="number" placeholder="Master ID">
				<input type="button" id="varifyHoroscopeMaster" value="Add Master" /><font color="red" id="invalidMasterId"></font><br/>
				<div id="horoscopeMaster">
				<table border="0">
					<tr>
						<td rowspan="3">
							<input id="masterAvatarInput" name="file" type="file" accept="image/*" style="width:200px"><br/>
							<img id="masterAvatarUrl" style="max-width:200px; max-height:200px;" src=""></img>
						</td>
						<td><input type="text" id="masterDisplayName" size="40" placeholder="Display name of horoscope master" /></td>
					</tr>
					<tr>
						<td><textarea id="masterDescription" rows="7" cols="42" placeholder="Describe the master here..."></textarea></td>
					</tr>
					<tr>
						<td><input type="text" id="masterExternalLink" size="45" placeholder="External link of horoscope master" /></td>
					</tr>
				</table>
				</div>
			</div>
		</c:if>
	</div>
	<label>Content : </label>
	<div id="postContentInput" style="min-height:200px; width:100%;"></div>
</div>
<c:choose>
    <c:when test="${actionBean.postForm eq 'mainpost'}">
        <label id="relPostId" style="display: none;"/>
    </c:when>
    <c:otherwise>
        <label id="relPostId" style="display: none;" value="${actionBean.mainPostId}"/>
    </c:otherwise>
</c:choose>		
	
<div id="postProgress"></div>

<div id="prePostHeaderTemplate" style="display:none;">
	<div class="lPostHeader">
		<div class="lAvatarDiv">
			<img class="lAvatar" src="${actionBean.userAvater}"></img>
		</div>
		<div class="lNameDate">
			<label class="lUserName">${actionBean.userDisplayName}</label>
			<label class="lLastModifiedDate">Now</label>
		</div>
	</div>
	<div class="lPostTitleDiv"></div>
</div>

<div id="prePostContentTemplate" style="display:none;">
	<div class="llCoverBody">
		<img class="lCover"></img>
	</div>
	<div class="lPostContentDiv"></div>
</div>

<div id="dialog" title="Dialog Form">
	<div style="display: none;"><label>From Url:</label><input type="text" id="photoUrl" name="photoUrl"><input type="button" id="getImage" value="Get Image" /><br/></div>
	<label>From File :</label><input id="inputAttachImage" name="file" type="file" accept="image/*"><br/>
	<label>Image Description:</label><input type="text" id="imageDescription" name="imageDescription"><br/>
	<label>Redirect Url:</label>
	<select id="redirectUrlProtocol">
		<option value="http://" selected>http://</option>
		<option value="https://">https://</option>
	</select>
	<input type="text" id="redirectUrl" name="redirectUrl"><br/>
	<label>Look Store Url ： </label>
	<input type="text" id="storeUrl" name="storeUrl"><br/>
	<img id="attachmentImg" style="max-width:600px; max-height:200px"/><br/>
	<input class="action-button" style="width: auto; display: none;" id="addAttachImgBut" type="button" value="Add Photo Attachment">
</div>

<div id="removeAttachmentdialog" title="Remove Attachment">
	<label>Remove this photo ?</label><br>
	<img id="toRemoveImg" style="max-width:600px; max-height:300px"/><br>
	<input id="yes" type="button" value="Yes">
	<input id="no" type="button" value="No">
</div>

<div id="loginDialog" title="Login panel" style="display:none;">
	<b>You need to login!</b>
    <table>
        <tr>
          <td>Email:</td>
          <td><input type="text" id="email" name="email"/></td>
        </tr>
        <tr>
          <td>Password:</td>
          <td><input type="password" id="password" name="password"/></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
		    <input id="loginButton" name="loginButton" type="button" value="Login">
			<input id="cancelButton" name="cancelButton" type="button" value="Cancel">
          </td>
        </tr>
    </table>
</div>