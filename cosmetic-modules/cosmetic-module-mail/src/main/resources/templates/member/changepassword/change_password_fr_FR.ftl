<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>Bonjour ${member.displayName!""},</span></p>
            <p> Votre mot de passe a été changé.</p>
            <p> Cette modification est associée avec votre compte CyberLink. Veuillez utiliser le nouveau mot de passe pour vous connecter à DirectorZone ainsi qu’à CyberLink.com la prochaine fois.</p>
            <p> <a href="http://${websiteDomain}/signIn/">Connexion</a></p>
            <p> Si vous avez des questions ou des inquiétudes sur la réception de ce message, veuillez <a href="http://${websiteDomain}/info/contactUs.jsp">nous contacter</a>.</p>
            <p>  </p>
            <p><span>L’équipe DirectorZone</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>
        </td>
    </tr>
</table>
</@c.page>