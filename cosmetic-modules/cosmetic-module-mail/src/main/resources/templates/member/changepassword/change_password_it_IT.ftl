<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>Ciao ${member.displayName!""},</span></p>
            <p>La password e stata cambiata.</p>
            <p>Questo cambiamento riguarda anche il tuo profilo CyberLink. D'ora in poi, utilizza questa nuova password per accedere sia a DirectorZone che a CyberLink.com.</p>
            <p><a href="http://${websiteDomain}/signIn/">Accedi ora</a></p>
            <p>Per qualsiasi domanda riguardante il ricevimento di questa email, ti preghiamo di <a href="http://${websiteDomain}/info/contactUs.jsp">contattarci</a>.</p>
            <p>&nbsp;</p>
            <p><span>la squadra DirectorZone</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>
        </td>
    </tr>
</table>
</@c.page>