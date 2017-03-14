<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#import "/templates/dz_macro.ftl" as dz/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>
  
<table width="100%" border="0" cellspacing="2" cellpadding="2">
  <tr> 
    <td colspan="2">Message from: <a href='mailto:${mailContactUs.senderEmail}'>${mailContactUs.senderName} &lt;${mailContactUs.senderEmail}&gt;</a> 
    </td>
  </tr>
  <tr> 
    <td width="37%"><li>Question/ Comment:</li></td>
    <td width="63%">${mailContactUs.content}</td>
  </tr>
  
</table>
</body>
</html>