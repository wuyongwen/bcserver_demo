<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>

<h2 class=ico_mug>Circle :: Circle Management</h2>

<script language="javascript">  
function resetPage(target) {  
	
	var l = document.getElementById("locale");
	var locale = l.options[l.selectedIndex].value;	
	
	var url = location.origin + "/backend/circle/circle-manage.action?route&locale=" + locale;
	if (target == "locale") {
		window.location.href = url;
	    return true;  
	}
	
	var t = document.getElementById("circleType");
	if (t.selectedIndex >= 0) {
		var type = t.options[t.selectedIndex].value;	
		if (type) {
			url += "&circleType=";
			url += type; 
		}
	}
	if (target == "circleType") {
		window.location.href = url;
	    return true;  
	}

	var c = document.getElementById("circle");
	if (c.selectedIndex >= 0) {
		var circle = c.options[c.selectedIndex].value;	
		if (circle) {
			url += "&circle=";
			url += circle; 
		}
	}
	if (target == "circle") {
		window.location.href = url;
	    return true;  
	}

	var tg = document.getElementById("circleTagGroup");
	if (tg.selectedIndex >= 0) {
		var tagGroup = tg.options[tg.selectedIndex].value;	
		if (tagGroup) {
			url += "&circleTagGroup=";
			url += tagGroup; 
		}
	}
	window.location.href = url;
    return true;  
} 
</script>

<div>
	<s:form beanclass="${actionBean.class}">
		Locale
		<s:select name="locale" id="locale" onchange="resetPage(this.id)">
			<s:option value="">None</s:option>
			<s:options-collection collection="${actionBean.postLocaleList}" />
		</s:select>
		<br/>
		Circle Type
		<s:select name="circleType" id="circleType" onchange="resetPage(this.id)">
			<s:options-collection collection="${actionBean.circleTypeDefaultList}" value="id" label="label"/>
			<s:options-collection collection="${actionBean.circleTypeList}" value="id" label="circleTypeName"/>
		</s:select>
		&nbsp;|&nbsp;Circle
		<s:select name="circle" id="circle" onchange="resetPage(this.id)">
			<s:options-collection collection="${actionBean.circleDefaultList}" value="id" label="label"/>
			<s:options-collection collection="${actionBean.circleList}" value="id" label="circleName"/>
		</s:select>
		&nbsp;|&nbsp;Tag Group
		<s:select name="circleTagGroup" id="circleTagGroup" onchange="resetPage(this.id)">
			<s:options-collection collection="${actionBean.circleTagGroupDefaultList}" value="id" label="label"/>
			<s:options-collection collection="${actionBean.circleTagGroupList}" value="id" label="circleTagGroupName"/>
		</s:select>
		&nbsp;|&nbsp;Circle Tag
		<s:select name="circleTag" id="circleTag">
			<s:options-collection collection="${actionBean.circleTagDefaultList}" value="id" label="label"/>
			<s:options-collection collection="${actionBean.circleTagList}" value="id" label="circleTagName"/>
		</s:select><br/>
		<td>Input New Create Name: </td>
		<td><s:text name="name"/></td>
		<s:submit name="create" value="Submit"/>
	</s:form>
</div>