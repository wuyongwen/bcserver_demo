<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0" />
        <meta name="format-detection" content="telephone=no">

        <link rel="stylesheet" type="text/css" href="<c:url value="/page/brandevent/css/main.css?v=${randVer}"/>">
        <link rel="stylesheet" type="text/css" href="<c:url value="/page/brandevent/css/mobile.css?v=${randVer}"/>">
        <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Open+Sans">
        <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/earlyaccess/cwtexhei.css">
        <title>2015 Estee-Lauder</title>
    </head>
    <body>
        <s:form beanclass="${actionBean.class}">
            <div><img width="100%" src="${actionBean.event.imageUrl}" /></div>
            <div id="block1">
            	<div class="title" style="text-align: center;">${actionBean.title}</div>
                <div class="title" style="text-align: center;">${actionBean.event.title}</div>
            </div>
            <a name="ask"></a>
            <c:choose>
                <c:when test="${actionBean.isSupportedLocale eq true}">
                    <c:choose>
                        <c:when test="${actionBean.eventStatus eq 'Upcoming'}">
                            <div class="actionButton_d">${actionBean.upcomingEvent}</div>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Ongoing'}">
                            <c:choose>
                                <c:when test="${actionBean.event.userStatus eq 'Joined' || actionBean.event.userStatus eq 'Selected' || actionBean.event.userStatus eq 'Redeemed'}">
                                    <div class="actionButton_d">${actionBean.ongoingEventJoined}</div>
                                </c:when>
                                <c:when test="${actionBean.event.joinNum >= actionBean.event.quantity}">
                                    <div class="actionButton_d">${actionBean.ongoingEventFull}</div>
                                </c:when>
                                <c:otherwise>
                                    <div id="action_check_all"  class="actionButton_n"><a href="ybc://event/${actionBean.brandEventId}?action=apply">${actionBean.ongoingEventNonJoin}</a></div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Expired'}">
                            <div class="actionButton_d">${actionBean.expiredEvent}</div>
                            <c:if test="${actionBean.event.userStatus eq 'Selected'}">
                                <div id="action_check_info" class="checkInfoButton"><a href="ybc://event/${actionBean.brandEventId}?action=result">${actionBean.expiredEventSelected}</a></div>
                            </c:if>
                        </c:when>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${actionBean.curUserLocale eq 'zh_TW'}">
                            <div class="unsupported">${actionBean.eventUnsupported}</div>
                        </c:when>
                        <c:otherwise>
                            <div class="unsupported_en">There are no events available in your country.</div>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            <h1>${actionBean.eventDescription}</h1>
            <p>${actionBean.event.description}</p>
            <h1>${actionBean.eventProductDuration}</h1>
            <p>${actionBean.startTime} ~ ${actionBean.endTime}</p>
            <h1>${actionBean.eventApplyDescription}</h1>
            <p>${actionBean.event.eventAttrJNode.applyDesc}</p>
            <p>&nbsp;</p>
        </s:form>
    </body>
</html>