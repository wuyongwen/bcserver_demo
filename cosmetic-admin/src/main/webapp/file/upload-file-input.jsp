<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script>
$(document).ready(function () {
    // remove Video item temporarily
    $("#fileType").find("option[value=Video]").remove();
});
</script>

<h2 class=ico_mug>File :: Upload File</h2>

<div>
    <s:form beanclass="${actionBean.class}">
        <label>FileType: <s:select id="fileType" name="fileType"><s:options-enumeration enum="com.cyberlink.cosmetic.modules.file.model.FileType" /></s:select></label><br/>
        <label>File: <s:file name="fileBean"/></label><br/> 
        <label>Metadata (JSON format): <s:textarea name="metadata" cols="45" rows="8">{}</s:textarea></label><br/> 
        <s:submit name="upload" value="Submit"/>
    </s:form>
</div>