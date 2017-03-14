<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title> Launching... </title>
    <meta name="Generator" content="NPP-Plugin">
    <meta name="Author" content="">
    <meta name="Keywords" content="">
    <meta name="Description" content="">
    <link rel="stylesheet" type="text/css" href="./biz.css">

    <script src="jquery-1.11.3.min.js"></script>
    <script>
    var YCN_ITUNES = "https://itunes.apple.com/us/app/apple-store/id1051710880";
    var YCN_MARKET = "market://details?id=com.perfectcorp.ycn";
    var YCN_BAIDU = "http://mobile.baidu.com/simple?action=content&docid=8121949&ala=";
    var WEBSITE = "http://www.perfectcorp.com/"

    $(document).ready(function() {

        if (navigator.userAgent.match(/iPhone|iPad|iPod/)) {
            // iOS
            if(navigator.userAgent.toLowerCase().match(/MicroMessenger/i) == "micromessenger") {
                // Weixin
                $("#popweixin").css("display", "block");           
            }
            window.location.replace(YCN_ITUNES);

        } else if (navigator.userAgent.match(/Android/)) {
            // Android
            if(navigator.userAgent.toLowerCase().match(/MicroMessenger/i) == "micromessenger") {
                // Weixin
                window.location.replace(YCN_BAIDU); 
            }
            else {
                window.location.replace(YCN_MARKET);
            }

        } else {
            // Not mobile
            window.location.replace(WEBSITE);

        }
    });
    </script>
</head>

<body>
    <div id='popweixin'>
        <div class='tip top2bottom animate-delay-1'>
            <img src='./hint.png'/>
        </div>
    </div>
</body>

</html>
