<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<link rel="stylesheet" href="<c:url value="/v2/common/css/profile.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/circle.css?v=${randVer}" />">
<link rel="stylesheet" href="<c:url value="/v2/common/css/displaytag.css?v=${randVer}"/>">

<script src="<c:url value="/v2/circle/circle.js?v=${randVer}" />"></script>

<div class=clearfix>
    <s:form beanclass="${actionBean.class}" method="get">
        <div id="broadcastMessageApp" ng-app="broadcastMessageApp" ng-controller="broadcastMessageCtrl" ngcloak="" class="ng-scope">
        	<div class="page-header">Circles</div>
            <div class="item_btn3"><a href="./create-circle.action"><i class="fa fa-plus" style="color:#FFFFFF;"></i>&nbsp;&nbsp;Create a circle</a></div>
            <div style="display: none;"></div>
            <display:table id="row" name="actionBean.circles.results" requestURI="./list-circle-by-user.action" pagesize="20" sort="page" partialList="true" size="actionBean.circles.totalSize" export="false" >
                <display:column title="Circle Name" style="width:73%;">
                    <c:out value="${row.circleName}"/>
                </display:column>
                <display:column title="Action" style="width:27%;">
                    <div class="item_btn1">
                        <a class="editCircle" href="javascript: void(0);" id="${row.id}"><i class="fa fa-pencil-square-o" style="color:#FFFFFF"></i>&nbsp;Edit</a>
                    </div>
                    <div class="item_btn2">
                        <a class="deleteCircle" href="javascript: void(0);" id="${row.id}"><i class="fa fa-times" style="color:#FFFFFF"></i>&nbsp;Delete</a>
                    </div> 
                </display:column>
            </display:table>
            <div class="clear"></div>
        	<div class="profile_sep" style="display:none;"></div>    
        </div>
    </s:form>
</div>