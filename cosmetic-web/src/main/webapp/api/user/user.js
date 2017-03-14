$(document).ready(function(){
	$(".ok_btn").click(function(event) {
		var password = $("input[name=password]").val();
		var retPassword = $("input[name=reTPassword]").val();
		if(password.length < 6 || password.length > 20) {
			event.preventDefault();
			alert("Password must have 6 ~ 20 characters !");
		}
		else if(password == retPassword) {
			event.preventDefault();
			$("#submitBtn").click();
		}
		else {
			event.preventDefault();
			alert("Password not match");
		}
	});
});