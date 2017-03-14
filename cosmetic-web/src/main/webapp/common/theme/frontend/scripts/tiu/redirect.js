var IS_IPAD = navigator.userAgent.match(/iPad/i) != null;
var IS_IPHONE = !IS_IPAD && ((navigator.userAgent.match(/iPhone/i) != null) || (navigator.userAgent.match(/iPod/i) != null));
var IS_IOS = IS_IPAD || IS_IPHONE;
var IS_ANDROID = !IS_IOS && navigator.userAgent.match(/android/i) != null;
var IS_MOBILE = IS_IOS || IS_ANDROID;

function open(userId) {    
    // If it's not an universal app, use IS_IPAD or IS_IPHONE
    if (IS_IOS) {
        window.location = 'cyberlinkU://invite?tab=addFriend&userId='+userId;    
        
        setTimeout(function() {    
            // If the user is still here, open the App Store
            if (!document.webkitHidden) {
                window.location = 'https://itunes.apple.com/app/u-you/id888621464';
            }
        }, 25);
        
    } else if (IS_ANDROID) {    
        // Instead of using the actual URL scheme, use 'intent://' for better UX
        window.location = 'intent://invite?tab=addFriend&userId=' + userId + '#Intent;package=com.cyberlink.U;scheme=cyberlinkU;end;';
    } else {
    	window.location = 'http://www.cyberlink.com/u';
    }
    
}
