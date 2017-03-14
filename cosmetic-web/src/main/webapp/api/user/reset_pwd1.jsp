<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script src="<c:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js" />"></script>
<script src="<c:url value="/api/user/user.js" />"></script>
<meta name="viewport" content="initial-scale=1.0">
<meta name="format-detection" content="telephone=no">
<title>Welcome to Beauty Circle!</title>
<style type="text/css">
body {
	background-color: #F5F5F5;
}
.password_input{
	border:1px solid #cccccc;
	width: 97.5%;
	height: 30px;
	line-height: 30px;
	font-size: 16px;
	color: #545454;
	margin-bottom: 10px;
	padding: 0 1%;
}
.ok_btn{
	width:100%;
	line-height:30px;
	height:30px;
	text-align: center;
	margin-top: 10px;
	background: #999999;
	color: #FFFFFF;
}
.ok_btn a{ color:#FFFFFF; display:block; text-decoration:none;}
</style>
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="-webkit-text-size-adjust: none; -ms-text-size-adjust: none;  margin: 0; padding: 0;">
<style type="text/css">
a:visited {
color: #009ee0; text-decoration: none;
}
p a:hover {
color: #b9d3ee;
}
.blue-button:visited {
color: #ffffff;
}
.blue-button:hover {
background-color: #0097d6;
}
.yellow-button:visited {
color: #333333;
}
.yellow-button:hover {
background-color: #ffd200;
}
.footer-links a:visited {
color: #999999; text-decoration: underline;
}
.footer-links a:hover {
text-decoration: none;
}
@media screen and (max-device-width: 650px) {
  table[class="email-container"] {
    width: 100% !important;
  }
  table[class="fluid"] {
    width: 100% !important;
  }
  img[class="fluid"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="force-col-center"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="force-col-center"] {
    margin: auto !important;
  }
  td[class="force-col"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="force-col-center"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="force-col-center"] {
    text-align: center !important;
  }
  img[class="col-3-img-l"] {
    float: left; margin: 0 15px 15px 0;
  }
  img[class="col-3-img-r"] {
    float: right; margin: 0 0 15px 15px;
  }
}
@media screen and (max-width: 650px) {

  table[class="email-container"] {
    width: 100% !important;
  }
  table[class="fluid"] {
    width: 100% !important;
  }
  img[class="fluid"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="force-col-center"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="force-col-center"] {
    margin: auto !important;
  }
  td[class="force-col"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="force-col-center"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="force-col-center"] {
    text-align: center !important;
  }
  img[class="col-3-img-l"] {
    float: left; margin: 0 15px 15px 0;
  }
  img[class="col-3-img-r"] {
    float: right; margin: 0 0 15px 15px;
  }
}
@media screen and (max-device-width: 425px) {

  div[class="hh-visible"] {
    display: block !important;
  }
  div[class="hh-center"] {
    text-align: center; width: 100% !important;
  }
  table[class="hh-fluid"] {
    width: 100% !important;
  }
  img[class="hh-fluid"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="hh-force-col-center"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="hh-force-col-center"] {
    margin: auto !important;
  }
  td[class="hh-force-col"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="hh-force-col-center"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="hh-force-col-center"] {
    text-align: center !important;
  }
  img[class="col-3-img-l"] {
    float: none !important; margin: 15px auto !important; text-align: center !important;
  }
  img[class="col-3-img-r"] {
    float: none !important; margin: 15px auto !important; text-align: center !important;
  }
  table[class="button"] {
    width: 100% !important; text-align: center !important;
  }
}
@media screen and (max-width: 425px) {
  td[class="hide"] {
    display: none !important;
  }
  div[class="hh-visible"] {
    display: block !important;
  }
  div[class="hh-center"] {
    text-align: center; width: 100% !important;
  }
  table[class="hh-fluid"] {
    width: 100% !important;
  }
  img[class="hh-fluid"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="hh-force-col-center"] {
    width: 100% !important; max-width: 100% !important; height: auto !important;
  }
  img[class="hh-force-col-center"] {
    margin: auto !important;
  }
  td[class="hh-force-col"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="hh-force-col-center"] {
    display: block !important; width: 100% !important; clear: both;
  }
  td[class="hh-force-col-center"] {
    text-align: center !important;
  }
  img[class="col-3-img-l"] {
    float: none !important; margin: 15px auto !important; text-align: center !important;
  }
  img[class="col-3-img-r"] {
    float: none !important; margin: 15px auto !important; text-align: center !important;
  }
  table[class="button"] {
    width: 100% !important; text-align: center !important;
  }
}
</style>
    <!-- German Version -->
    <!-- English Version -->
    <table cellpadding="0" cellspacing="0" border="0" height="100%" width="100%" style="border-spacing: 0; background: #ffffff;">
		<tbody>
			<tr>
				<td bgcolor="#F5F5F5" style="border-collapse: collapse;">
					<table border="0" width="700" cellpadding="0" cellspacing="0" align="center" class="email-container" style="margin-top: 0px; border-spacing: 0; border: 0px solid;">
						<tbody>
							<tr>
								<td style="border-collapse: collapse;">            
									<table border="0" width="100%" cellpadding="0" cellspacing="0" align="center" style="border-spacing: 0;">
										<tbody>
											<tr>
												<td bgcolor="#191717" style="text-align:center; width:100%; border-collapse: collapse; ">
       												<img src="./../../images/title1.jpg" width="300" style="">
       											</td>
     										</tr>
											<tr>
												<td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 20px 20px;">
													<p class="content-title" style="font-size: 24px;  color: #333333; line-height: 34px; margin: 18px 0 18px; padding: 0;"> ${actionBean.resetPasswordLabel} </p>
													<s:form beanclass="${actionBean.class}">
														<div>
															<input type="password" class="password_input" name="password" placeholder="${actionBean.newPasswordLabel}">
															<input type="password" class="password_input" name="reTPassword" placeholder="${actionBean.retypePasswordLabel}">
															<input type="text" name="memberId" value="${actionBean.memberId}" style="display: none;">
															<input type="hidden" name="locale" value="${actionBean.locale}">
														</div>
														<div class="ok_btn">
															<a id="formAction" href="#" style="color:#FFFFFF;">OK</a>
														</div>
														<s:submit id="submitBtn" name="resetpwd2" value="Reset" style="display: none;"></s:submit>
													</s:form><br><br><br>
												</td>
											</tr>
										</tbody>
									</table>
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
	<table width="700" border="0" align="center" class="email-container" cellpadding="0" cellspacing="0" bgcolor="#cfd5e1" style="border-spacing: 0; background: #666666;">
		<tbody>
			<tr>
				<td style="border-collapse: collapse;">
					<table border="0" width="690" cellpadding="0" cellspacing="0" align="center" class="email-container" bgcolor="#666666" style="border-spacing: 0;">
						<tbody>
							<tr>
								<td style="border-collapse: collapse;">
									<table border="0" width="100%" cellpadding="0" cellspacing="0" align="center" style="font-family: sans-serif; color: #FFFFFF; font-size: 12px; text-align: center; border-spacing: 0;">
										<tbody>
											<tr>
												<td style="border-collapse: collapse;">
													<br>${actionBean.footerLabel}<br><br>
												</td>
											</tr>
										</tbody>
									</table>
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
	
	<!-- French Version -->
    <!-- Spanish Version -->
    <!-- Italian Version -->
    <!-- Portuguese Version -->
    <!-- Turkish Version -->
    <!-- Japanese Version -->
    <!-- Chinese Version -->
	<img src="./Welcome to Beauty Circle!_files/p.gif" width="1" height="1">

</body>