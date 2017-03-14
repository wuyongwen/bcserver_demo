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
				<c:when test="${actionBean.poolType.revived}">
					<label id="postId"></label>
				</c:when>
				<c:otherwise>
					<label id="resolution"></label>
				</c:otherwise>
			</c:choose>
			<br>
			<label id="circle" style="font-weight: bold;"></label><label>&nbsp; | &nbsp;</label>
			<label>Creator : </label><label class="authorName" style="cursor:pointer;" dir="ltr" reporterId="${actionBean.curUserId}" id="postCreator"></label><label>&nbsp; | &nbsp;</label>
			<label id="twCreateTime"></label><label>&nbsp;(TW)</label>
			<c:if test="${actionBean.poolType.revived}">
				<label>&nbsp; | &nbsp;</label><label id="likeCount"></label>
				<label>&nbsp; | &nbsp;</label><label id="circleInCount"></label>
				<label style="display: none;">&nbsp; | &nbsp;</label><label id="popularity" style="display: none;"></label>
			</c:if>
			<div style="display:none;">
				<label>Score : </label><label id="photoScore">&nbsp;</label><br>
				<label>ScoreDate : </label><label id="scoreDate">&nbsp;</label><br>
			</div>
			<div class="availableCirDiv" style="border: 1px solid #C3C3C3; border-radius: 10px; padding: 1px; margin: 2px;">
				<table>
					<tr class="availableCircles"></tr>
				</table>
			</div>
			<div class="postTagsDiv" style="border: 1px solid #C3C3C3; border-radius: 10px; padding: 1px;">
				<table>
					<tr class="fPostTags" />
				</table>
			</div>
			<textarea id="content" rows="3" style="resize:none; font-size:16px; width: 99%;border-radius: 10px;" readonly="true"></textarea>
		</td>
		<c:if test="${actionBean.poolType.revived}">
			<td style="height: 240px; width:9%; vertical-align: middle; padding-left:5px;">
				<div class="nonPropagate clickableTd" style="min-height: 80%; max-height: 80%; border: 1px solid #C3C3C3; border-radius: 10px; ">
					<label class="nonPropagate" style="display: block; margin:80px 0px 0px 0px; color: 000000;font-size: 20px;">
						<input class="nonPropagate" id="checkChangeKeyword" type="radio" name="" act="checkChangeKeyword" value="" style="transform: scale(3.5); margin:20px;">OK
					</label>
				</div>
				<div class="nonPropagate clickableTd" style="min-height: 20%; max-height: 20%; border: 1px solid #C3C3C3; border-radius: 10px; ">
					<label class="nonPropagate" style="display: block; margin:3px 0px; color: 000000;font-size: 20px;">
						<input class="nonPropagate" id="checkDel" type="radio" name="" act="checkDel" value="" style="transform: scale(3.5); margin:20px;">DEL
					</label>
				</div>
			</td>
		</c:if>
		<td style="width:20%; vertical-align: middle; padding-left:5px;height: 240px;">
			<div class="extCirDiv" style="background-color:#162635;border: 0px solid #28161f; border-radius: 10px;height: 43%;">
			</div>
			<div class="postTagsDiv" style="border: 1px solid #C3C3C3; border-radius: 10px; margin-top:3px;height: 54%;">
				<div class="postTags" style="min-height: 70%; max-height: 115px; overflow: auto;"></div>
				<div class="addPostTagDiv" style="min-height: 10%"></div>
			</div>
		</td>
		<c:if test="${not actionBean.poolType.revived}">
			<td style="width:20%; vertical-align: middle; padding:0px 5px 5px 0px;">
				<div style="min-height: 40px;max-height: 40px; padding-bottom:14px;">
					<label style="color: 000000;font-size: 20px;">
						<input id="checkBoxUndecided" type="radio" name="" act="checkUndecided" value="" style="transform: scale(3.5); margin:20px;" checked="">Undecided
					</label>
				</div>
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
				</div>
			</td>
		</c:if>
	</tr>
</c:forEach>