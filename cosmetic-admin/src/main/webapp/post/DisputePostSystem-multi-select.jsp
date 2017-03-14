<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<c:forEach var="idx" begin="1" end="${actionBean.pageSize}">
	<c:choose>
		<c:when test="${idx % 2 != 0}">
			<c:set var="trClass" value="odd"/>
		</c:when>
		<c:otherwise>
			<c:set var="trClass" value="even"/>
		</c:otherwise>
	</c:choose>
	<tr class="${trClass} postRow">
		<td style="width:17%;">
			<div align="center" style="position: relative;">
				<img class="postPhoto" id="attachmentImg" pId="" src="" alt="No Image Available" align="center" style="max-width:80%; max-height:240px; width:auto; height:auto;">
				<span class="lookTypeImg" id="lookTypeImg">
			</div>
		</td>
		<td style="min-width:607px; word-break: break-all;">
			<label id="title" style="color: 000000;font-size: 20px;font-weight: bold;"></label>
			<c:choose>
				<c:when test="${actionBean.poolType.multiCategory}">
					<label id="resolution"></label>
				</c:when>
				<c:otherwise>
					<label id="postId"></label>
				</c:otherwise>
			</c:choose>
			<br>
			<label id="circle" style="font-weight: bold;"></label><label>&nbsp; | &nbsp;</label>
			<label>Creator : </label><label class="authorName" style="cursor:pointer;" dir="ltr" reporterId="${actionBean.curUserId}" id="postCreator"></label><label>&nbsp; | &nbsp;</label>
			<label id="twCreateTime"></label><label>&nbsp;(TW)</label>
			<label>&nbsp; | &nbsp;</label><label id="likeCount"></label>
			<label>&nbsp; | &nbsp;</label><label id="circleInCount"></label>
			<label>&nbsp; | &nbsp;</label><label id="popularity"></label>
			<div style="display:none;">
				<label>Score : </label><label id="photoScore">&nbsp;</label><br>
				<label>ScoreDate : </label><label id="scoreDate">&nbsp;</label><br>
			</div>
			<div class="availableCirDiv" style="border: 1px solid #C3C3C3; border-radius: 10px; padding: 1px; margin: 0px;">
				<div class="availableCircles" mselect="true"></div>
			</div>
			<textarea id="content" rows="3" style="resize:none; font-size:16px; width: 99%;border-radius: 10px;" readonly="true"></textarea>
		</td>
		<td style="width:20%; vertical-align: middle; padding-left:5px;height: 240px;">
			<div class="postTagsDiv" style="border: 1px solid #C3C3C3; border-radius: 10px; margin-top:3px;height: 100%;">
				<div class="postTags" style="min-height: 88%; max-height: 115px; overflow: auto;"></div>
				<div class="addPostTagDiv" style="min-height: 10%"></div>
			</div>
		</td>
		<td style="width:20%; vertical-align: middle; padding:0px 5px 5px 0px;">
			<c:choose>
				<c:when test="${actionBean.poolType.undecidedWord == null}">
					<div style="display:none;min-height: 40px;max-height: 40px; padding-bottom:14px;">
						<label style="color: 000000;font-size: 20px;">
							<input id="checkBoxUndecided" type="radio" name="" act="checkUndecided" value="" style="transform: scale(3.5); margin:20px;" checked="">${actionBean.poolType.undecidedWord}
						</label>
					</div>
				</c:when>
				<c:otherwise>
					<div style="min-height: 40px;max-height: 40px; padding-bottom:14px;">
						<label style="color: 000000;font-size: 20px;">
							<input id="checkBoxUndecided" type="radio" name="" act="checkUndecided" value="" style="transform: scale(3.5); margin:20px;" checked="">${actionBean.poolType.undecidedWord}
						</label>
					</div>
				</c:otherwise>
			</c:choose>
			<div style="min-height: 40px;max-height: 40px; padding-bottom:14px;">
				<label style="color: 000000;font-size: 20px;">
					<input id="checkBoxCatTrendDis" type="radio" name="" act="checkCategoryTrend" value="" style="transform: scale(3.5); margin:20px;">Cat. & Trend
				</label>
			</div>
			<div style="min-height: 40px;max-height: 40px; padding-bottom:14px;">
				<label style="color: 000000;font-size: 20px;">
					<input id="checkBoxCat" type="radio" name="" act="checkCategory" value="" style="transform: scale(3.5); margin:20px;">Category Only
				</label>
			</div>
			<div style="display:none;min-height: 40px;max-height: 40px; padding-bottom:4px;">
				<label style="color: 000000;font-size: 20px;">
					<input id="checkBoxSelCir" type="radio" name="" act="checkSelfieCircle" value="" style="transform: scale(3.5); margin:20px;">Selfie Only
				</label>
			</div>
			<div style="min-height: 40px;max-height: 40px; padding-bottom:14px;">
				<label style="color: 000000;font-size: 20px;">
					<input id="checkBoxAbandon" type="radio" name="" act="checkAbandon" value="" style="transform: scale(3.5); margin:20px;">Abandon
				</label>
			</div>
			<div style="min-height: 40px;max-height: 40px; padding-top:5px;" class="rating">
				<div id="r3" class="rate" style="display:inline-block;width:35px; height: 35px; margin-left:5%; margin-right: 5%; background-image:url(../common/theme/backend/images/r3.png);" value="3"></div>
				<div id="r2" class="rate" style="display:inline-block;width:35px; height: 35px; margin-left:5%; margin-right: 5%;background-image:url(../common/theme/backend/images/r2.png);" value="2"></div>
				<div id="r1" class="rate selected" style="display:inline-block;width:35px; height: 35px; margin-left:5%; margin-right: 5%;background-image:url(../common/theme/backend/images/r1.png);" value="1"></div>
				<c:if test="${actionBean.poolType.enablePush}">
					<div id="push" class="push" style="text-align: center;display:inline-block;width:35px; height: 35px;vertical-align: top;" value=false>
						<label style="font-weight: bold;cursor: pointer;">Now</label>
					</div>
				</c:if>
			</div>
		</td>
	</tr>
</c:forEach>