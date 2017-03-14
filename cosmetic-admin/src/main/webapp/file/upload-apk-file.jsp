<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<script src="<c:url value="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js" />"></script>
<script src="<c:url value="../post/spinner/spin.js" />"></script>
<script>
$(document).ready(function(){
	$("[name='uploadApk']").click(function() {
		var opts = {ines: 15 , length: 11, width: 6 , radius: 19 , scale: 1 , corners: 1 , color: '#000' , opacity: 0.1 , rotate: 0 , direction: 1 , speed: 1.8 , trail: 71 , fps: 20 , zIndex: 2e9 , className: 'spinner' , top: '40%' , left: '50%' , shadow: false , hwaccel: false , position: 'absolute' }
		var spinner = new Spinner(opts).spin($("#spinner").get(0));
	});
});
</script>

<h2 class=ico_mug>File :: Upload APK File</h2>

<div>
    <s:form beanclass="${actionBean.class}">
        <label>Apk Type: <s:select id="apkType" name="apkType">
        <s:option value="ybc">YBC</s:option>
        <s:option value="ycn">YCN</s:option>
        <s:option value="ycp">YCP</s:option>
        <s:option value="ymk">YMK</s:option>
        </s:select></label><br/>
        <label>File: <s:file name="fileBean" accept=".apk"/></label><br/> 
        <s:submit name="uploadApk" value="Submit"/>
    </s:form>
    <div>
    	<b>Download URL: </b><a href="${actionBean.originalUrl}">${actionBean.originalUrl}</a>
    </div>
</div>
<div id="spinner" class="spinner"></div>