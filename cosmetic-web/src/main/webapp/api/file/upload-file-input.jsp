<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Upload File</title>
</head>
<body>
<h3>Upload File Form</h3>
<hr/>
<fieldset>
    <legend>Form</legend>   
    <s:form beanclass="${actionBean.class}">
        <label>token: <s:text name="token" value="00ed95b0-4065-4aad-a61c-3f1b570ffc53" size="50"/></label><br/>
        <label>fileType: <s:select name="fileType"><s:options-enumeration enum="com.cyberlink.cosmetic.modules.file.model.FileType" /></s:select></label><br/>
        <label>file: <s:file name="fileBean"/></label><br/> 
        <label>metadata: <s:textarea name="metadata" cols="45" rows="8">
{
  "fileSize": 777835,
  "md5": "9d377b10ce778c4938b3c7e2c63a229a",
  "width": 1024,
  "height": 768,
  "orientation": 1,
  "imageDescription": "test"
}</s:textarea></label><br/> 
        <s:submit name="submit" value="Submit"/>
    </s:form>
</fieldset>
</body>
</html>