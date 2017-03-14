<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.9.2/jquery-ui.min.js" />"></script>
<script src="<c:url value="/common/lib/general.js" />"></script>

<h2 class=ico_mug>Campaign :: Campaign Group Event Management</h2>
<div>
	<s:form beanclass="${actionBean.class}">
	<input type="hidden" name="campaignGroupName" value="${actionBean.campaignGroupName}"/>
	<table class="form" width="100%">
		<input type="hidden" name="campaignGroup.id" value="${actionBean.campaignGroup.id}"/>
		<tr>
        	<td>Locale :</td>
          	<td>
          		<c:forEach var="postLocale" items="${actionBean.postLocaleList}" >
          			<input class="checkbox" type="checkbox" name="localeList" value="${postLocale}" 
          				<c:if test="${actionBean.campaignGroupLocaleList != null}">
		          			<c:forEach items="${actionBean.campaignGroupLocaleList}" var="campaignGroupLocale">
			          			<c:if test="${fn:containsIgnoreCase(campaignGroupLocale, postLocale)}">
			        				checked="checked"
	   	 						</c:if>
		          			</c:forEach>
	          			</c:if>
	          			<c:if test="${actionBean.campaignGroupLocaleList == null}">
	          				checked="checked"
	          			</c:if>
          			>${postLocale}&nbsp;&nbsp;&nbsp;&nbsp;
          		</c:forEach>
			</td>
        </tr>
        <tr>
        	<td width="200px">Campaign Group Name:</td>
          	<td>
          		<input type="text" name="campaignGroup.name" style="width:600px" placeholder="please enter group name(e.g. product)" value="${actionBean.campaignGroup.name}"/>
			</td>
        </tr>
        <tr>
        	<td>Rotation Period :</td>
          	<td>
          		<input type="text" name="campaignGroup.rotationPeriod" style="width:600px" placeholder="please enter period(e.g. 10000)" 
          			<c:if test="${actionBean.campaignGroup.rotationPeriod != null}">
	          			value="${actionBean.campaignGroup.rotationPeriod}"
	          		</c:if>
	          		<c:if test="${actionBean.campaignGroup.rotationPeriod == null}">
	          			value="10000"
	          		</c:if>
          		onkeydown="if(event.keyCode==13)event.keyCode=9" onKeyPRess="if((event.keyCode<48 || event.keyCode>57)) event.returnValue=false"/>
			</td>
        </tr>
        <tr>
        	<td colspan="2">
        	<c:if test="${actionBean.isUpdate}">
	        	<s:submit name="updateGroupEvent" class="button" value="Update"></s:submit>
        	</c:if>
        	<c:if test="${actionBean.isCreate}">
	        	<s:submit name="createGroupEvent" class="button" value="Create"></s:submit>
        	</c:if>
        	<s:submit name="cancelGroupEvent" class="button" value="Cancle"></s:submit>
        	</td>
        </tr>
    </table>
    </s:form>
</div>