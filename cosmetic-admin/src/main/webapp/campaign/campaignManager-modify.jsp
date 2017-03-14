<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="randVer"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<script src="<c:url value="/common/lib/general.js" />"></script>
<script src="<c:url value="/campaign/campaignManager.js?v=${randVer} " />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.js" />"></script>
<script src="<c:url value="/common/lib/timepicker/jquery-ui-sliderAccess.js" />"></script>
<link href="<c:url value="/common/lib/timepicker/jquery-ui-timepicker-addon.css" />" rel="stylesheet">

<h2 class=ico_mug>Campaign :: Campaign Management</h2>
<div>
	<s:form beanclass="${actionBean.class}">
	<input type="hidden" name="campaignGroupId" value="${actionBean.campaignGroupId}"/>
	<input type="hidden" name="campaignGroupName" value="${actionBean.campaignGroupName}"/>
	<table class="form" width="100%">
		<input type="hidden" name="campaign.id" value="${actionBean.campaign.id}"/>
		<input type="hidden" name="campaign.groupId" value="${actionBean.campaignGroupId}"/>
		<tr style="display:none;">
          <td>File:
          	<input id="campFileInput" name="file" type="file" accept="image/*">
          	<s:text name="campaign.fileId" id="fileId" value="${actionBean.campaign.fileId}"/>
          </td>
        </tr>
        <tr style="display:none;">
          <td>
          	<div style=" height:200px; overflow:hidden;">
          		<img id="imgfile" style="max-height:98%;min-height:98%;border-color:#000000;border-width:3px;border-style:solid;" src="${actionBean.imgfileOriginalUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
          <td>File 720P:
          	<input id="campFile720Input" name="file" type="file" accept="image/*">
          	<s:text name="campaign.file720Id" id="file720Id" value="${actionBean.campaign.file720Id}"/>
          </td>
        </tr>
        <tr>
          <td>
          	<div style=" height:200px; overflow:hidden;">
          		<img id="imgfile720" style="max-height:98%;min-height:98%;border-color:#000000;border-width:3px;border-style:solid;" src="${actionBean.imgfile720OriginalUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
          <td>File 1080P:
          	<input id="campFile1080Input" name="file" type="file" accept="image/*">
          	<s:text name="campaign.file1080Id" id="file1080Id" value="${actionBean.campaign.file1080Id}"/>
          </td>
        </tr>
        <tr>
          <td>
          	<div style=" height:200px; overflow:hidden;">
          		<img id="imgfile1080" style="max-height:98%;min-height:98%;border-color:#000000;border-width:3px;border-style:solid;" src="${actionBean.imgfile1080OriginalUrl}"/>
       		</div>
     	  </td>
        </tr>
        <tr>
        	<td>Link :
          		<input type="text" name="campaign.link" style="width:600px" placeholder="" value="${actionBean.campaign.link}"/>
			</td>
        </tr>
        <tr>
        	<td>End Date :
          		<input type="text" id="datetimepicker" name="campaign.endDate" value="${actionBean.campaign.endDate}" style="width:200px;">
          		<span id="hint"></span>
			</td>
        </tr>
        <tr style="text-align:center;">
        	<td colspan="2">
        	<c:if test="${actionBean.isUpdate}">
	        	<s:submit id="saveFileEdit" name="update" class="button" value="Update" />
        	</c:if>
        	<c:if test="${actionBean.isCreate}">
	        	<s:submit id="saveFileEdit" name="create" class="button" value="Create" />
        	</c:if>
        	&nbsp;&nbsp;&nbsp;&nbsp;
        	<s:submit name="cancel" class="button" value="Cancle" />
        	</td>
        </tr>
    </table>
    <div id="editProgress"></div>
    </s:form>
</div>