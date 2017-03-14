<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Event :: Event Management</h2>
<s:form name="detailEventForm" id="detailEventForm" beanclass="${actionBean.class}">

</s:form>
<div align="center" style="with:100%;">
	<div style="max-width:720px; border-radius: 10px;background-color: #FFFFFF;box-shadow: 0px 0px 15px 1px; padding: 17px 35px 12px 35px;">
		<div>
		<table align="right" style="font-size:20px;">
			<tr>
				<td>
					<a href="./EventManager.action?detailEdit=&brandEventId=${actionBean.brandEventId}">Edit</a>&nbsp;|&nbsp;
				</td>
				<td>
					<a href="./EventUserManager.action?brandEventId=${actionBean.brandEventId}&receiveType=${actionBean.brandEventDetail.event.receiveType}" target="_blank">Participants</a>&nbsp;
				</td>
				<td>
					<c:if test="${actionBean.brandEventDetail.status eq 'Drawing'}">
						|&nbsp;<a href="./EventDrawManager.action?brandEventId=${actionBean.brandEventId}&isSend=false" target="_blank">Draw</a>&nbsp;
        			</c:if>
        			<c:if test="${actionBean.brandEventDetail.status eq 'Expired'}">
						|&nbsp;<a href="./EventDrawManager.action?brandEventId=${actionBean.brandEventId}&isSend=true" target="_blank">Draw</a>&nbsp;
        			</c:if>
				</td>
				<td>
					<c:if test="${actionBean.brandEventDetail.status eq 'Expired' and actionBean.brandEventDetail.event.receiveType eq 'Store'}">
						|&nbsp;<a href="./EventRedeemManager.action?brandEventId=${actionBean.brandEventId}" target="_blank">Redeem</a>&nbsp;
        			</c:if>
				</td>
			</tr>
		</table>
		</div>
		</br>
		</br>
		<div>
			<img src="${actionBean.brandEventDetail.event.imageUrl}" align="center" style="min-width:29px; min-height:21px; max-width:720px; width:auto; height:auto;"/>
		</div>
		</br>
		<div style="font-size:24px; line-height:26px;" align="left">
			<b>${actionBean.brandEventDetail.event.title}</b>
		</div>
		</br>
		<div style="font-size:18px; line-height:20px;" align="left"  >
			${actionBean.brandEventDetail.event.description}
		</div>
		</br>
		<div style="font-size:22px; line-height:22px; border-color:#a969b6; border-style:solid; border-width:2px; " >
		<c:choose>
                <c:when test="${actionBean.brandEventDetail.status eq 'Upcoming'}">
                    <div id="count_down_pre">距離活動開始倒數<span style="color: #a969b6;">${actionBean.brandEventDetail.remainDays}</span>天</div>
                </c:when>
                <c:when test="${actionBean.brandEventDetail.status eq 'Ongoing'}">
                    <div id="count_down">只剩${actionBean.brandEventDetail.remainDays}天${actionBean.brandEventDetail.remainHours}小時${actionBean.brandEventDetail.remainMinutes}分鐘<br /><span class="join">已參與${actionBean.brandEventDetail.event.joinNum}人</span></div>
                </c:when>
                <c:when test="${actionBean.brandEventDetail.status eq 'Deleted' or actionBean.brandEventDetail.status eq 'Drawing' or actionBean.brandEventDetail.status eq 'Expired'}">
                    <div id="count_down_expired">活動已結束</div>
                </c:when>
        </c:choose>
        </div>
        <div align="left">
        	<h1>Id</h1>
        	<p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.id}</p>
        	<h1>Brand UserId</h1>
        	<p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.brandId}</p>
        	<h1>活動期間</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.startTime} ~ ${actionBean.brandEventDetail.endTime}</p>
            <h1>客戶寄信時間</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.companySendDate}</p>
            <h1>得獎名單公布時間</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.drawTime}</p>
            <h1>領獎期間</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.receiveBeginDate} ~ ${actionBean.brandEventDetail.receiveEndDate}</p>
            <h1>好康贈品</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.prodName}</p>
            <h1>產品特色</h1>
            <p id="productDescription" style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.prodDescription}</p>
            <h1>好康內容</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.prodDetail}</p>
            <h1>贈品數量</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.quantity}</p>
            <h1>參與人數</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.joinNum}</p>
            <h1>申請資格&nbsp;(${actionBean.brandEventDetail.event.applyType})</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.eventAttrJNode.applyDesc}</p>
            <h1>活動方式&nbsp;(${actionBean.brandEventDetail.event.eventType})</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.eventAttrJNode.eventTypeDesc}</p>
            <h1>領取方式&nbsp;(${actionBean.brandEventDetail.event.receiveType})</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.eventAttrJNode.receiveDesc}</p>
            <h1>附註</h1>
            <p>${actionBean.brandEventDetail.event.comment}</p>
            <h1>客戶聯繫</h1>
            <c:forEach items="${actionBean.brandEventDetail.companyEmail}" var="companyEmail" varStatus="loop">
            	<p><a style="font-size:18px; line-height:20px;" href="mailto:${companyEmail}">${companyEmail}</a></p>
            </c:forEach>
            <h1>PF mail</h1>
            <c:forEach items="${actionBean.brandEventDetail.pfEmail}" var="pfEmail" varStatus="loop">
            	<p><a style="font-size:18px; line-height:20px;" href="mailto:${pfEmail}">${pfEmail}</a></p>
            </c:forEach>
            <h1>客戶是否要求密件副本</h1>
            <c:if test="${actionBean.brandEventDetail.isBcc eq true}">
            	<p style="font-size:18px; line-height:20px;">是</p>
            </c:if>
            <c:if test="${actionBean.brandEventDetail.isBcc eq false}">
            	<p style="font-size:18px; line-height:20px;">否</p>
            </c:if>
            <h1>客戶信件已寄送</h1>
            <c:if test="${actionBean.brandEventDetail.isSent eq true}">
            	<p style="font-size:18px; line-height:20px;">是</p>
            </c:if>
            <c:if test="${actionBean.brandEventDetail.isSent eq false}">
            	<p style="font-size:18px; line-height:20px;">否</p>
            </c:if>
            <h1>Priority</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.brandEventDetail.event.priority}</p>
            <h1>Post Id</h1>
            <p style="font-size:18px; line-height:20px;">${actionBean.postId}</p>
		</div>
	</div>
</div>