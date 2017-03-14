<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title> Launching... </title>
    <meta name="Generator" content="NPP-Plugin">
    <meta name="Author" content="">
    <meta name="Keywords" content="">
    <meta name="Description" content="">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script>
    var YMK_ITUNES = "itms://itunes.apple.com/app/id863844475";
    var YCP_ITUNES = "itms://itunes.apple.com/app/id768469908";
    var YMK_MARKET = "market://details?id=com.cyberlink.youcammakeup";
    var YCP_MARKET = "market://details?id=com.cyberlink.youperfect";
    var WEBSITE = "http://www.perfectcorp.com/"

    $(document).ready(function() {

        if (navigator.userAgent.match(/iPhone|iPad|iPod/)) {
            // iOS
            window.location.replace(YMK_ITUNES);

        } else if (navigator.userAgent.match(/Android/)) {
            // Android
            window.location.replace(YMK_MARKET);

        } else {
            // Not mobile
            window.location.replace(WEBSITE);

        }
    });
    </script>
</head>

<body>
    <script type="text/javascript">
    </script>
</body>

</html>
