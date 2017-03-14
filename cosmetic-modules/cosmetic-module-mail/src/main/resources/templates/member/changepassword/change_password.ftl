<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>Hi ${member.displayName!""},</span></p>
            <p>Your password has been changed.</p>
            <p>This change is associated with your CyberLink account. Please use the new password to sign into DirectorZone as well as CyberLink.com next time.</p>
            <p><a href="http://${websiteDomain}/signIn/">Sign In</a></p>
            <p>If you have any questions or concerns about receiving this email, please <a href="http://${websiteDomain}/info/contactUs.jsp">contact us</a>.</p>
            <p>&nbsp;</p>
            <p><span>The DirectorZone Team</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>
        </td>
    </tr>
</table>
</@c.page>