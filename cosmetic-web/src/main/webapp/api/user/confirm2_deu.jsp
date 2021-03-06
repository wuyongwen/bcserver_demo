<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="viewport" content="initial-scale=1.0">
<title>Welcome to Beauty Circle!</title>
<style type="text/css"></style>
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
												<td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 30px 20px;">
													<p class="content-title" style="font-size: 24px;  color: #333333; line-height: 34px; margin: 18px 0 18px; padding: 0;">Verifizierung erfolgreich</p>
													<table width="100%" border="0" cellpadding="0" cellspacing="0">
														<tbody>
															<tr>
																<td class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;">Herzlichen Glückwunsch! Deine E-Mail-Adresse  ${actionBean.email} wurde erfolgreich im Beauty Circle verifiziert!</td>
															</tr>
															<tr>
																<td class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;">
																	<span>Du bist jetzt ein Mitglied der Beauty Circle-Community!</span>
																</td>
															</tr>
														</tbody>
													</table><br>
													<hr width="100%" size="1" style="color:#CCCCCC;">
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tbody>
															<tr>    
																<td align="left" valign="top" style="font-size:13px; color:#666666; line-height:18px; text-align:left;">
																	If you didn't create an Beauty Circle account with this email address, 
																	<a href="mailto:BeautyCircle_AppSupport@cyberlink.com" style="color:#333333;">let us know</a>.
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
												<td style="border-collapse: collapse;"><br>
													${actionBean.footerLabel}<br><br>
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