var checkedUids = {};
$(document).ready(function(){
	
	var opts = {lines: 13, length: 20, width: 10, radius: 30, corners: 1, rotate: 0, direction: 1, color: '#000', speed: 1, trail: 60, shadow: false, hwaccel: false, className: 'spinner', zIndex: 2e9, top: '50%', left: '50%'};
	var spinner = new Spinner(opts).spin($("#dashboard")[0]);
	
	$(".checkUser:checked").each(function(i, v) {
		var $this = $(v);
		var score = $this.parent().parent().find(".score").text();
		checkedUids[$this.attr("uid")] = score;
	});
	
	$(".nonPropagate").click(function(event) {
		event.stopPropagation();
	});
	
	$(".clickableTd").click(function() {
		$(this).find(".clickTarget").trigger("click");
	});
	
	$("#selLocale, #pageAction").change(function() {
		window.location.href = "./badge-program.action?selLocale=" + $("#selLocale").val() + "&pageAction=" + $("#pageAction").val();
	});
	
	$(".checkUser").click(function() {
		var $this = $(this);
		var checkedUid = $this.attr("uid");
		var score = $this.parent().parent().find(".score").text();
		
		if(this.checked)
			checkedUids[$this.attr("uid")] = score;
		else
			delete checkedUids[$this.attr("uid")];
	});
	
	var reload = function () {
		window.location.href = "./badge-program.action?selLocale=" + $("#selLocale").val();
	};
	
	$("#addCheckedUser").click(function() {
		spinner.spin($("#dashboard")[0]);
		if(checkedUids.length <= 0) {
			reload();
			return;
		}
			
		var apiUrl = "./badge-program.action";
		var data = "addSOW&selLocale=" + $("#selLocale").val();
		var checkedMap = encodeURIComponent(JSON.stringify(checkedUids));
		data += "&uidsJson=" + checkedMap;
		$.post(apiUrl, data, function(responseJson) {
			console.log(responseJson);
			reload();
		}).fail(function(e) {  	
			$(".pagelinks").hide();
			tableRow.hide();
			spinner.stop();
			$("#messageLbl").text("Error");
		});
	});
	spinner.stop();
});