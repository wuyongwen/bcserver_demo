<#import "../../mail_template.ftl" as c />
<@c.page>
     <table border="0" width="100%" cellpadding="0" cellspacing="0" align="center" style="border-spacing: 0;"><tr>
     <td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family: sans-serif; padding:15px 0; background:#ad57a2; color: #ffffff; border-collapse: collapse;"><div align="center">
       <table width="260" border="0" cellpadding="0" cellspacing="0">
         <tr>
           <td width="212"><img src="http://${websiteDomain}/images/title_beauty-Circle.png" width="250" height="64"></td>
         </tr>
       </table>
       </div></td></tr><tr>
	<td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 20px 20px;"><p class="content-title" style="font-size: 24px;  color: #333333; line-height: 34px; margin: 18px 0 18px; padding: 0;">歡迎來到玩美圈！<br>
	  <span style="color:#ad57a2">${member.account.account}</span>!</p>
    
           <table width="100%" border="0" cellpadding="0" cellspacing="0">
             <tr>
               <td class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;">你已擁有玩美圈（訊連科技）帳號！</td>
             </tr>
             <tr>
               <td class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;"><span >一同加入玩美圈的愛美社群行列！ </span></td>
             </tr>
           </table>
           
           <hr width="100%" size="1" style="color:#CCCCCC;">
                   <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    
    <td align="left" valign="top" style="font-size:13px; color:#666666; line-height:18px; text-align:left;">若你尚未利用e-mail帳號建立玩美圈，
      <a href="http://${websiteDomain}/api/user/confirm-mail.action?remove1&memberId=${member.id}&locale=zh_TW" style="color:#333333;">請通知我們</a>。</td>
  </tr>
</table>

                
                </td>
              </tr>
              </table>
</@c.page>