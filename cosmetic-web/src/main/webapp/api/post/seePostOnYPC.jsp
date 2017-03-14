<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title>Redirecting to YPC app or perfect corp...</title>
	<meta property="al:ios:app_store_id" content="768469908"/>
	<meta property="al:ios:app_name" content="YouCam Perfect" />
	<meta property="al:ios:url" content="${actionBean.appUrl}" />
	<meta property="al:web:url"
	          content="http://www.perfectcorp.com/" />
	<meta property="al:android:url" content="${actionBean.appUrl}" />
	<meta property="al:android:app_name" content="YouCam Perfect" />
	<meta property="al:android:package" content="com.cyberlink.youperfect" />
	<meta property="al:web:should_fallback" content="false" /> 
</head>
<body>
<script>
    window.location = 'http://www.perfectcorp.com/' ;
</script>
</body>
</html>