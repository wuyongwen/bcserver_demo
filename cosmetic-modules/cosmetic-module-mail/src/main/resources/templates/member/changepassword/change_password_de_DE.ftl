<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>Hallo ${member.displayName!""},</span></p>
            <p> Ihr Passwort wurde geändert.</p>
            <p> Diese Änderung bezieht sich auf Ihr CyberLink-Konto. Melden Sie sich das nächste Mal mit dem neuen Passwort bei DirectorZone und de.CyberLink.com an.</p>
            <p> <a href="http://${websiteDomain}/signIn/">Einloggen</a></p>
            <p>Wenn Sie Fragen oder Bedenken nach Erhalt dieser E-Mail haben, <a href="http://${websiteDomain}/info/contactUs.jsp">kontaktieren Sie uns bitte</a>. </p>
            <p>  </p>
            <p><span>Ihr DirectorZone-Team</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>
        </td>
    </tr>
</table>
</@c.page>