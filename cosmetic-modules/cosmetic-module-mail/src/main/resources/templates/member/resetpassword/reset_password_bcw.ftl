<#import "../../mail_template.ftl" as c />
<@c.page>
<table border="0" width="100%" cellpadding="0" cellspacing="0" align="center" style="border-spacing: 0;">
	<tr>
		<td bgcolor="#191717" style="text-align:center; width:100%; border-collapse: collapse; ">
			<img src="http://${websiteDomain}/images/title1.jpg" width="300" style="";>
		</td>
	</tr>
	<tr>
		<td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family:'Segoe UI',Arial, Helvetica, sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 40px 20px;">
			<p class="content-title" style="font-size: 16px;  color: #333333; line-height: 40px; margin: 18px 0 10px; padding: 0;">${content1}</p>                
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
	      		<tr>
	        		<td colspan="2" class="content-block-guidelines" style="font-family: 'Segoe UI',Arial, Helvetica, sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;">
	        			${content2}
	        			<br>
	        			<br>
	        			${content3}
	        			<br>
	        			<a style="color:#408dd5; text-wrap:normal" href="${resetUrl}">${pageName}</a>
	        			<br>
	        			<br>
	        			${content5}
	        			<br>
	        			<br>
	        			${end}
	        		</td>
	        	</tr>
	        	<tr>
	        		<td>
	        		</td>
	        	</tr>
	        	<tr>
	        		<td colspan="2">
	        		</td>
	       		</tr>
	        	<tr>
	        		<td colspan="2" class="content-block-guidelines" style="font-family: 'Segoe UI',Arial, Helvetica, sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;">
	        		</td>
	        	</tr>
	      		<tr>
	      		</tr>
			</table>
			<hr width="100%" size="1" style="color:#CCCCCC;">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" valign="top" style="font-size:13px; color:#666666; line-height:18px; text-align:left;">
						${anyProblem}
					</td>
				</tr>
			</table>
			<br>
		</td>
	</tr>
</table>
</@c.page>
