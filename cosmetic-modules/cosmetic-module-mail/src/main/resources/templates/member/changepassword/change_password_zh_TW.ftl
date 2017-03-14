<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>${member.displayName!""} 您好：</span></p>
            <p> 您的密碼已變更。</p>
            <p> 此變更會一併影響您的 CyberLink 帳戶。之後請使用新密碼登入 DirectorZone 和 CyberLink.com。</p>
            <p> <a href="http://${websiteDomain}/signIn/">登入</a></p>
            <p> 如果您對於收到此電子郵件有任何問題，請<a href="http://${websiteDomain}/info/contactUs.jsp">與我們聯絡</a>。</p>
            <p>  </p>
            <p><span>DirectorZone 小組</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>                
        </td>
    </tr>
</table>
</@c.page>