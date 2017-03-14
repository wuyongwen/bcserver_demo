$(document).ready(function() {
			
	$("#usersBtn").click(function() {
		var url = "trendUserManager.action?listUser";
		var shardId = $("#selShard option:selected").val();
		var data = "shardId=" + shardId;
		blockUIButton(true);
		$.post(url, data, function(responseJson) {
			var result =  responseJson["result"];
			if (result != null) {
				var html = '<div>'
					+ 'User List of SharId ' + shardId + ': <br><br>';
				for (var i = 0 ; i < result.length ; i++) {
					if (i == 0)
						html += "{ ";						
					if (i == result.length - 1)
						html += result[i] + " }";
					else
						html += result[i] + ", ";
				}
				html += '</div>';
				document.getElementById("resultDiv").innerHTML = html;
			} else {
				alert(responseJson);
			}
			blockUIButton(false);
		}).fail(function(e) {
			alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		    blockUIButton(false);
		});
	});
	
	$("#categoryBtn").click(function() {
		var url = "trendUserManager.action?listCategory";
		var userId = $("#categoryInput").val();
		var data = "userId=" +  userId;
		blockUIButton(true);
		$.post(url, data, function(responseJson) {
			var result =  responseJson["result"];
			if (result != null) {
				var html = '<div>'
					+ 'Category List of ' + userId + ': <br><br>';
				html += 'Conunt: ' + result.length + '<br>';
				for (var i = 0 ; i < result.length ; i++) {
					if (i == 0)
						html += "{ ";						
					if (i == result.length - 1)
						html += result[i] + " }";
					else
						html += result[i] + ", ";
				}
				html += '</div>';
				document.getElementById("resultDiv").innerHTML = html;
			} else {
				alert(responseJson);
			}
			blockUIButton(false);
		}).fail(function(e) {
			alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
			blockUIButton(false);
		});
	});
	
	$("#groupBtn").click(function() {
		var url = "trendUserManager.action?getGroup";
		var userId = $("#groupInput").val();
		var data = "userId=" +  userId;
		blockUIButton(true);
		$.post(url, data, function(responseJson) {
			var result =  responseJson["result"];
			if (result != null) {
				var html = '<div>'
					+ 'User Group of ' + userId + ': <br><br>';
				html += result;
				html += '</div>';
				document.getElementById("resultDiv").innerHTML = html;
			} else {
				alert(responseJson);
			}
			blockUIButton(false);
		}).fail(function(e) {
			alert("Failed : " + e.status + " " + e.statusText + "\nData: " + data);
		    blockUIButton(false);
		});
	});
	
	var blockUIButton = function(bBlock) {
		if (bBlock) {
			$("#usersBtn").prop("disabled", true);
			$("#categoryBtn").prop("disabled", true);
			$("#groupBtn").prop("disabled", true);
		} else {
			$("#usersBtn").prop("disabled", false);
			$("#categoryBtn").prop("disabled", false);
			$("#groupBtn").prop("disabled", false);
		}
	};
	
});