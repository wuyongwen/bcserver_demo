$(document).ready(
		function() {

			var requery = function() {
				var linkUrl;
				if ($("#selStatus option:selected").val() == "REVIEWING") {
					linkUrl = "./reportedUser.action?selRegion="
							+ $("#selRegion option:selected").val()
							+ "&selStatus=REVIEWING" + "&selReason=PRETENDING";
				} else {
					linkUrl = "./reportedUser.action?selRegion="
							+ $("#selRegion option:selected").val()
							+ "&selStatus="
							+ $("#selStatus option:selected").val()
							+ "&selReason="
							+ $("#selReason option:selected").val();
				}
				window.location.href = linkUrl;
			};

			$("#selRegion").change(function() {
				requery();
			});

			$("#selStatus").change(function() {
				requery();
			});

			$("#selReason").change(function() {
				requery();
			});

			$("#row").contents().find(".reviewedBtn").each(
					function() {
						var $this = $(this);
						$this.click(function() {
							$("#row").contents().find("input[type='button']").prop("disabled", true);
							$.post("./reportedUser.action?reviewed"
									+ "&targetId=" + $this.attr("targetId")
									+ "&selStatus="
									+ $("#selStatus option:selected").val()
									+ "&selReason="
									+ $("#selReason option:selected").val(),
									function(result) {
										location.reload();
									});
						});
					});
			
			$("#row").contents().find(".investigateBtn").each(
					function() {
						var $this = $(this);
						$this.click(function() {
							$("#row").contents().find("input[type='button']").prop("disabled", true);
							$.post("./reportedUser.action?reviewing"
									+ "&targetId=" + $this.attr("targetId")
									+ "&selStatus="
									+ $("#selStatus option:selected").val()
									+ "&selReason="
									+ $("#selReason option:selected").val(),
									function(result) {
										location.reload();
									});
						});
					});
			
			$("#row").contents().find(".bannedBtn").each(
					function() {
						var $this = $(this);
						$this.click(function() {
							var r = confirm("This action will delete this user account and block the device.\nAre you sure to continue?");
							if (r == true) {
								$("#row").contents().find("input[type='button']").prop("disabled", true);
								$.post("./reportedUser.action?banned"
										+ "&targetId=" + $this.attr("targetId")
										+ "&selStatus="
										+ $("#selStatus option:selected").val()
										+ "&selReason="
										+ $("#selReason option:selected").val()
										+ "&isBlockDevice=true",
										function(result) {
											location.reload();
										});
							} else {
							}
						});
					});
			
			$("#row").contents().find(".deleteBtn").each(
					function() {
						var $this = $(this);
						$this.click(function() {
							var r = confirm("This action will delete this user account.\nAre you sure to continue?");
							if (r == true) {
								$("#row").contents().find("input[type='button']").prop("disabled", true);
								$.post("./reportedUser.action?banned"
										+ "&targetId=" + $this.attr("targetId")
										+ "&selStatus="
										+ $("#selStatus option:selected").val()
										+ "&selReason="
										+ $("#selReason option:selected").val()
										+ "&isBlockDevice=false",
										function(result) {
											location.reload();
										});
							} else {
							}
						});
					});

		});
