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
        <link rel="stylesheet" type="text/css" href="<c:url value="/page/brandevent/css/coupon.css?v=${randVer}"/>">

        <title>2015 Estee-Lauder</title>

        <script type="text/javascript">
            function goToProductLink() {
                if ('${actionBean.event.productAttrJNode.productLink}'.indexOf('ybc://') == 0) {
                    location.replace('${actionBean.event.productAttrJNode.productLink}');
                }
                else {
                    window.open('${actionBean.event.productAttrJNode.productLink}');
                }
            }
        </script>
    </head>
    <body>
        <s:form beanclass="${actionBean.class}">
            <div><img width="100%" src="${actionBean.event.imageUrl}" /></div>
            <div id="block1">
                <div class="title">${actionBean.event.title}</div>
                <div class="content">${actionBean.event.description}</div>
            </div>
            <a name="ask"></a>
            <c:choose>
                <c:when test="${actionBean.eventStatus eq 'Upcoming'}">
                    <div id="count_down_pre">${actionBean.upcomingEventDescription}<span style="color: #eb6d94;">${actionBean.remainDays}</span>${actionBean.upcomingEventRemainDaysDescription}</div>
                </c:when>
                <c:when test="${actionBean.eventStatus eq 'Ongoing'}">
                    <div id="count_down">${actionBean.ongoingEventDescription}<br /><span class="join">${actionBean.ongoingEventJoinNumberDescription}</span></div>
                </c:when>
                <c:when test="${actionBean.eventStatus eq 'Deleted' or actionBean.eventStatus eq 'Drawing' or actionBean.eventStatus eq 'Expired'}">
                	<c:choose>
                		<c:when test="${actionBean.event.receiveType eq 'Coupon' && actionBean.event.userStatus eq 'Selected'}">
                		</c:when>
                		<c:otherwise>
                			<div id="count_down_expired">${actionBean.expiredEventDescription}</div>
                		</c:otherwise>
                	</c:choose>
                </c:when>
            </c:choose>
            <div width="100%" height="10px" border="0px">&nbsp;</div>
            <c:choose>
                <c:when test="${actionBean.isSupportedLocale eq true}">
                    <c:choose>
                        <c:when test="${actionBean.eventStatus eq 'Upcoming'}">
                            <div class="actionButton_d">${actionBean.upcomingEventButton}</div>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Ongoing'}">
                            <c:choose>
                                <c:when test="${actionBean.event.userStatus eq 'NonJoin'}">
                                    <div id="action_join" class="actionButton_n"><a href="ybc://free_sample_apply/${actionBean.brandEventId}">${actionBean.ongoingEventNonJoinButton}</a></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="actionButton_d">${actionBean.ongoingEventButton}</div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Drawing'}">
                            <div class="actionButton_d">${actionBean.drawingEventButton}</div>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Expired'}">
                        	<div id="action_check_all" class="actionButton_n"><a href="ybc://free_sample_listuser/${actionBean.brandEventId}">${actionBean.expiredEventButton}</a></div>
								<c:if test="${actionBean.event.userStatus eq 'Selected'}">
									<c:choose>
										<c:when test="${actionBean.event.receiveType eq 'Coupon'}">
	                        				<div class="serial_box">
	                        					<div class="serial_title">${actionBean.couponReceiveTitle}</div>
	                        					<div class="serial_down"></div>
	                        			
	                        					<div class="serial_body">
	                        						<div class="serial_name">${actionBean.couponReceiveDescription}</div>
	                        						<div class="serial_number">${actionBean.couponCode}</div>
	                        						<div class="serial_due">${actionBean.couponReceiveEndTime}</div>
	                        						<div class="_tab"><a class="serial_btn" href="${actionBean.event.websiteUrl}">${actionBean.couponUsingbtn}</a></div>
	                        					</div>
	                        				</div>
	                        				<div class="clear"></div>
										</c:when>
	                        			<c:otherwise>
	                        				<div id="action_check_info" class="checkInfoButton"><a href="ybc://free_sample_message/${actionBean.brandEventId}">${actionBean.expiredEventSelectedButton}</a></div>
	                            		</c:otherwise>
									</c:choose>
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
                            <div class="unsupported_en">This event is not available in your country.</div>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            
            <div class="line"  style="border-top:dashed 1px #a969b6;margin: 10px auto;"></div>
            <c:if test="${actionBean.event.receiveType ne 'Coupon'}">
	            <h1>${actionBean.eventProductInfo}</h1>
	            <div id="block2">    	
	                <div id="box_new">
	                    <c:choose>
	                        <c:when test="${actionBean.event.productAttrJNode.productLink eq ''}">
	                            <table width="100%" border="0" cellpadding="0" cellspacing="0">
	                        </c:when>
	                        <c:otherwise>
	                            <table width="100%" border="0" cellpadding="0" cellspacing="0" onClick="goToProductLink();" style="cursor: pointer;">
	                        </c:otherwise>
	                    </c:choose>
	                        <tr>
	                            <td width="35%" align="center" valign="middle" class="box_img_td_new"><img class="product_img" src="${actionBean.event.productAttrJNode.thumbnailUrl}"/></td>
	                            <td class="box_description_td" >
	                                <table width="100%" border="0" cellpadding="0" cellspacing="0">
	                                    <tr><td class="title"><h3>${actionBean.event.productAttrJNode.brandName}</h3></td></tr>
	                                    <tr><td class="type"><h4>${actionBean.event.productAttrJNode.category}</h4></td></tr>
	                                    <tr><td class="description"><h6>${actionBean.event.productAttrJNode.name}</h6></td></tr>	                                    
	                                    <tr><td class="price"><h2>${actionBean.event.productAttrJNode.price}</h2></td></tr>
	                                </table>
	                            </td>
	                        </tr>
	                    </table>
	                </div>
	            </div>
			</c:if>
            <h1>${actionBean.eventProductDuration}</h1>
            <p>${actionBean.durationString}</p>
            <h1>${actionBean.eventProductName}</h1>
            <p>${actionBean.event.prodName}</p>
            <h1>${actionBean.eventProductQuantity}</h1>
            <p>${actionBean.event.quantity} ${actionBean.eventProductQuantityUnit}</p>
            <h1>${actionBean.eventProductDescription}</h1>
            <p id="productDescription">${actionBean.event.prodDesc}</p>
            <h1>${actionBean.eventProductDetail}</h1>
            <p>${actionBean.event.prodDetail}</p>
            <h1>${actionBean.eventDescription}</h1>
            <p>${actionBean.event.eventAttrJNode.eventTypeDesc}</p>
            <h1>${actionBean.eventApplyDescription}</h1>
            <p>${actionBean.event.eventAttrJNode.applyDesc}</p>
            <h1>${actionBean.eventReceiveDescription}</h1>
            <p>${actionBean.event.eventAttrJNode.receiveDesc}</p>
            <c:choose>
                <c:when test="${actionBean.isSupportedLocale eq true}">
                    <c:choose>
                        <c:when test="${actionBean.eventStatus eq 'Upcoming'}">
                            <div class="actionButton_d">${actionBean.upcomingEventButton}</div>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Ongoing'}">
                            <c:choose>
                                <c:when test="${actionBean.event.userStatus eq 'NonJoin'}">
                                    <div id="action_join" class="actionButton_n"><a href="ybc://free_sample_apply/${actionBean.brandEventId}">${actionBean.ongoingEventNonJoinButton}</a></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="actionButton_d">${actionBean.ongoingEventButton}</div>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Drawing'}">
                            <div class="actionButton_d">${actionBean.drawingEventButton}</div>
                        </c:when>
                        <c:when test="${actionBean.eventStatus eq 'Expired'}">
                        	<div id="action_check_all" class="actionButton_n"><a href="ybc://free_sample_listuser/${actionBean.brandEventId}">${actionBean.expiredEventButton}</a></div>
								<c:if test="${actionBean.event.userStatus eq 'Selected'}">
									<c:choose>
										<c:when test="${actionBean.event.receiveType eq 'Coupon'}">
	                        				<div class="serial_box">
	                        					<div class="serial_title">${actionBean.couponReceiveTitle}</div>
	                        					<div class="serial_down"></div>
	                        			
	                        					<div class="serial_body">
	                        						<div class="serial_name">${actionBean.couponReceiveDescription}</div>
	                        						<div class="serial_number">${actionBean.couponCode}</div>
	                        						<div class="serial_due">${actionBean.couponReceiveEndTime}</div>
	                        						<div class="_tab"><a class="serial_btn" href="${actionBean.event.websiteUrl}">${actionBean.couponUsingbtn}</a></div>
	                        					</div>
	                        				</div>
	                        				<div class="clear"></div>
										</c:when>
	                        			<c:otherwise>
	                        				<div id="action_check_info" class="checkInfoButton"><a href="ybc://free_sample_message/${actionBean.brandEventId}">${actionBean.expiredEventSelectedButton}</a></div>
	                            		</c:otherwise>
									</c:choose>
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
                            <div class="unsupported_en">This event is not available in your country.</div>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty actionBean.event.comment}">
                <h1>${actionBean.eventComment}</h1>
                <p>${actionBean.event.comment}</p>
            </c:if>
            
            <p>&nbsp;</p>
        </s:form>
    </body>
</html>