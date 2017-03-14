$(document).ready(function(){

	$(".deleteCircle").click(function() {
        var data = "circleId=" + $(this).get(0).id;
        $.post("list-circle-by-user.action?deleteCircle", data, function(responseJson) {
            window.location.href = "./list-circle-by-user.action";
         }).fail(function(e) {
             alert("Failed : " + e.status + " " + e.statusText)
         });
    });

    $(".editCircle").click(function() {
        var data = "?circleId=" + $(this).get(0).id;
        window.location.href = "./update-circle.action" + data;
    });

    $(".save_btn").click(function() {
        if ($("#circleName").val() == "" || $("#description").val() == "") {
            alert("Please enter circle name and description");
            return false;
        }
        if ($("#description").val().length > 180) {
            alert("Description max Length:" + 180);
            return false;
        }
	});

	$("#circleName").change(function() {
        $("#nameLimit").html($("#circleName").val().length + "/30 characters<br>");
    });

    $("#description").change(function() {
        $("#descLimit").html($("#description").val().length + "/180 characters<br>");
    });
});

