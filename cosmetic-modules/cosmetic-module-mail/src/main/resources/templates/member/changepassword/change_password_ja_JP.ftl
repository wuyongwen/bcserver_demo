<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>こんにちは ${member.displayName!""} さん</span></p>
            <p> パスワードが変更されました。</p>
            <p> この変更内容はあなたの CyberLink アカウントに関連するものです。次回から DirectorZone および jp.Cyberlink.com にサインインされる際は、新しいパスワードをご使用ください。</p>
            <p> <a href="http://${websiteDomain}/signIn/">サインイン</a></p>
            <p> この電子メールについてお尋ねになりたいことがありましたら、<a href="http://${websiteDomain}/info/contactUs.jsp">弊社までお問い合わせください</a>。</p>
            <p>  </p>
            <p><span>DirectorZone チーム</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>                
        </td>
    </tr>
</table>
</@c.page>