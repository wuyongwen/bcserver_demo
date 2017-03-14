<#import "../../mail_template.ftl" as c />
<@c.page>
<table width="600" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left">
            <p><span>안녕하세요 고객님 ${member.displayName!""},</span></p>
            <p>고객님의 비밀번호가 변경되었습니다.</p>
            <p>비밀번호 변경은 CyberLink 계정과 연동되어 있습니다. 이후 DirectorZone 과 CyberLink.com 에 로그인 시 두 곳 모두 새롭게 변경된 비밀번호를 사용해 주십시오.</p>
            <p><a href="http://${websiteDomain}/signIn/">로그인</a></p>
            <p>문의사항이 있으시면 <a href="http://${websiteDomain}/info/contactUs.jsp">저희에게 즉시 연락을 주십시오</a>.</p>
            <p>&nbsp;</p>
            <p><span>DirectorZone 팀</span></p>
            <p><span><a href="http://${websiteDomain}/">http://${websiteDomain}/</a></span></p>
        </td>
    </tr>
</table>
</@c.page>