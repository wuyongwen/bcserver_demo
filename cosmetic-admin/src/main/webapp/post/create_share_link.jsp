<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<input id="shareLinkBtn" type="button" value="Share Link">
<div id="shareLinkDialog" style="display:none;">
	<table>
	<tr>
		<td style="width:80%;">
			<table>
				<tr>
					<td>
						<label>Share Link :</label>
					</td>
					<td>
						<input style="width: 100%;" type="text" id="sharedLinkInput">
					</td>
				</tr>
				<tr>
					<td>
						<label>Title : </label>
					</td>
					<td>
						<textarea style="width: 100%;" name="title" rows="3" cols="50"></textarea><br>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div id=coverSelection style="width: 100%;">
							<label>Cover Description:</label><input type="text" id="coverDescription" name="coverDescription"><br/>
							<label>Cover From File Selection :</label><input id="inputCoverImage" name="file" type="file" accept="image/*">
							<input type="text" id=coverRedirectUrl name="coverRedirectUrl" style="display:none;">
							<div id="croppingArea" class="img-container">
								<input class="action-button" type="button" id="confirmCover" value="Add Cover Photo" style="width: auto;"><br>
								<img class="cropper" alt="Picture" id="coverOriImg">
							</div><br>
							<label>Cover From Url Selection :</label><br>
							<div id="externalCovers" style="border-width:1px; border-color:#C5C5C5;border-style:solid; width: 100%; height: 100px; left: 0px; top: 0px;overflow-y: auto;">
							</div>
						</div><br>
						<div id=attachmentSelection style="display:none">
							<div id="attachmentMetadatas" ></div>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<label>Circles : </label>
					</td>
					<td>
						<div id="circleSelection" style="overflow-y:scroll;max-height:150px;">
					    	<table>
								<c:forEach items="${actionBean.circles}" var="circle" varStatus="loop">
									<c:choose>
									    <c:when test="${loop.index != 0 && loop.index%2 != 0}">
									        	<td width="100px" align="left">
													<label><input type="checkbox" id="selCircles" name="selCircles" value="${circle.id}"/>${circle.circleName}</label><br>
												</td>
							        		</tr>
									    </c:when>
									    <c:otherwise>
									    	<tr>
									    		<td width="100px" align="left">
									        		<label><input type="checkbox" id="selCircles" name="selCircles" value="${circle.id}"/>${circle.circleName}</label><br>
								        		</td>
									    </c:otherwise>
									</c:choose>		
								</c:forEach>
							</table>
							<a href="./CreateCircle.action"> + Create new Circle for your account .</a>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<label>Content :</label>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div id="shareContentInput" contenteditable="true" style="min-height:100px; max-height:100px; width:100%;"></div>
					</td>
				</tr>
			</table>
		</td>
		<td style="width:20%;">
			<input style="width: 20%;bottom: 0px; right: 10px;position: absolute;" type="button" id="createShareLinkButton" name="createShareLinkButton" class="createShareLinkButton action-button" value="Post" />
		</td>
	</tr>
	</table>
</div>