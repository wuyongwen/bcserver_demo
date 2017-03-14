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
	<td bgcolor="#FFFFFF" class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 20px 20px;"><p class="content-title" style="font-size: 24px;  color: #333333; line-height: 34px; margin: 18px 0 18px; padding: 0;">Добро пожаловать в Beauty Circle!<br>
	  <span style="color:#ad57a2">${member.account.account}</span>!</p>
    
           <table width="100%" border="0" cellpadding="0" cellspacing="0">
             <tr>
               <td class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;">Вы создали аккаунт Beauty Circle (CyberLink)!</td>
             </tr>
             <tr>
               <td class="content-block-guidelines" style="font-family: sans-serif; text-align:left; font-size: 16px; line-height: 24px; color: #333333; border-collapse: collapse; padding: 0px 0px 10px;"><span >Приятного времяпрепровождения в сообществе любителей красоты Beauty Circle!</span></td>
             </tr>
           </table>
           
           <hr width="100%" size="1" style="color:#CCCCCC;">
                   <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    
    <td align="left" valign="top" style="font-size:13px; color:#666666; line-height:18px; text-align:left;">Если Вам не удалось создать аккаунт Beauty Circle на этот эл. адрес, 
      <a href="http://${websiteDomain}/api/user/confirm-mail.action?remove1&memberId=${member.id}&locale=ru_RU" style="color:#333333;">сообщите нам </a>об этом. </td>
  </tr>
</table>

                
                </td>
              </tr>
              </table>

</@c.page>