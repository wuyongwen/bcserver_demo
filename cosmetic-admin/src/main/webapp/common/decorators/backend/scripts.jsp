<%@ include file="/common/taglibs.jsp"%>
<script language="javascript" src='http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.9.1.min.js'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/jquery-ui-1.10.3.custom.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/custom.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/excanvas.pack.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/jquery.flot.pack.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/jquery.markitup.pack.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/set.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/superfish.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/jquery.validate.min.js"/>'></script>
<script language="javascript" src='<c:url value="/common/theme/backend/scripts/additional-methods.min.js"/>'></script>
<script language="javascript">
    $(function() {
        
        $('.button-create').button({
        	icons: {primary: 'ui-icon-circle-plus'}
        });
        $('.button-delete').button({
            icons: {primary: 'ui-icon-circle-minus'}
        });
        $('.button-update').button({
            icons: {primary: 'ui-icon-pencil'}
        });
        $('.button-back').button({
            icons: {primary: 'ui-icon-arrowreturnthick-1-w'}
        });
        $('.button-contact').button({
            icons: {primary: 'ui-icon-contact'}
        });
        $('.button-suggestion').button({
            icons: {primary: 'ui-icon-zoomin'}
        });
        $('.button-person').button({
            icons: {primary: 'ui-icon-person'}
        });
        $('.button-misc').button({
            icons: {primary: 'ui-icon-gear'}
    });
        $('.button').button({});
    });
</script>