<#import "../../mail_event_template.ftl" as c />
<@c.page>
	<table border="0" width="100%" cellpadding="0" cellspacing="0" align="center" style="border-spacing: 0;">
		<tbody>
			<tr>
     			<td bgcolor="#FFFFFF" style="text-align:center; width:100%; border-collapse: collapse; ">
     				<img src="http://${websiteDomain}/images/banner.jpg" style="width:100%;">
     			</td>
     		</tr>
     		<tr>
				<td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 20px 20px 20px;">
					<br>${eventUser.freeSampleHello}<br>
					<br>${eventUser.freeSampleDescriptionHome}<br>
					<br>${eventUser.freeSampleYourInfo}
					<br>${eventUser.freeSampleUserName}
					<br>${eventUser.freeSamplePhone}
					<br>${eventUser.freeSampleEmail}
					<br>${eventUser.freeSampleUserAddress}
					<br>${eventUser.freeSampleProdName}<br>
					<br>${eventUser.freeSampleThankForJoin}<br>
					<br>${eventUser.freeSampleEnd}<br>
					<br><hr width="100%" size="1" style="color:#CCCCCC;">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tbody>
							<tr>
								<td align="left" valign="top" style="font-size:13px; color:#666666; line-height:18px; text-align:left;">
									${eventUser.freeSampleAnyProblemHome}
									<br><a href="mailto:BeautyCircle_AppSupport@perfectcorp.com" style="color:#333333;">BeautyCircle_AppSupport@perfectcorp.com</a> 
								</td>
							</tr>
						</tbody>
					</table>
					<br>
				</td>
			</tr>
		</tbody>
	</table>
</@c.page>