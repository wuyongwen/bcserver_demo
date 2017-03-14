$(document).ready(function(){
	
	$("#loginDialog").dialog({
		autoOpen: false,
		maxWidth: 300,
        maxHeight: 250,
        width: 300,
        height: 250,
        modal: true,
        title: "Please Login..."
	});
	
	var genLoginParams = function(email, password){
		var data = "";
		data += "email="+email+"&password="+password+"&loginType=panel";
		return data;
	};
	
	$("#loginButton").on("click", function() { //send parameters(email, password) to login
		var email = $("#email").val();
		var password = $("#password").val();
		var data = genLoginParams(email, password);

		$.post("../user/login.action?login", data, function(response) {
			if(response!=null && response.length > 0){
				alert("Login Successfully!\nPlease click the Post button to create your post.");
				$("#loginDialog").dialog("close");
			}
		}).fail(function(e) {
			alert("Failed to login: " + e.responseText);
		});

		$("#loginDialog").dialog("close");
	});

	$("#cancelButton").on("click", function() {
		$("#email").empty();
		$("#password").empty();
		$("#loginDialog").dialog("close");
	});

});