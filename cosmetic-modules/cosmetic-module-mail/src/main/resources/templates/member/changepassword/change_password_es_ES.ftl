<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>Estimado/a ${member.displayName!""}:</span></p>
            <p>Su contraseña ha cambiado.</p>
            <p>Este cambio está asociado a su cuenta de CyberLink. Utilice la nueva contraseña para iniciar sesión la próxima vez en DirectorZone y en CyberLink.com.</p>
            <p><a href="http://${websiteDomain}/signIn/">Iniciar sesión</a></p>
            <p>Si tiene alguna pregunta o duda sobre la recepción de este mensaje, póngase en <a href="http://${websiteDomain}/info/contactUs.jsp">contacto con nosotros</a>.</p>
            <p>&nbsp;</p>
            <p><span>El equipo de DirectorZone</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>
        </td>
    </tr>
</table>
</@c.page>