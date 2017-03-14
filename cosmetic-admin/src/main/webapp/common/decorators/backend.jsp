<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<%@ include file="backend/meta.jsp"%>
<title><decorator:title default="Beauty Circle Administration Console" /></title>
<decorator:head />
<%@ include file="backend/styles.jsp"%>
<%@ include file="backend/scripts.jsp"%>
</head>
<body
	<decorator:getProperty property="body.id" writeEntireProperty="true"/>
	<decorator:getProperty property="body.class" writeEntireProperty="true"/>>

<%@ include file="backend/header.jsp"%>

<div id=content><%@ include file="backend/menu.jsp"%>
<div id=content_main class=clearfix style="word-break: break-word;">
<div id=main_panel_container>
<div id=dashboard>
<decorator:body />
</div>
</div>
</div>
</div>

<%@ include file="backend/footer.jsp"%>
</body>
</html>
