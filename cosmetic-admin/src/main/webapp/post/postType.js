$(document).ready(function(){

	$postType = $("#postType");
	$extLookUrlInput = $("#extLookUrlInput");
	$lookTypesDiv = $("#lookTypesDiv");
	$lookTypesSelection = $("#lookTypesSelection");
	
	$postType.on("change", function(){
		var postType = this.value;

		//hasExtLookUrl
		if(!postTypeMap[postType].hasExtLookUrl) {
			$extLookUrlInput.val("");
			$extLookTypeInput.val("").trigger("change");
			$extHoroscopeTypeInput.val("").trigger("change");
		}
		
		//hasHoroscopeTag
		if(postTypeMap[postType].hasHoroscopeTag)
			$extLookTypeInput.val("").trigger("change");

		//hasLookTag
		if(postTypeMap[postType].hasLookTag)
			$extHoroscopeTypeInput.val("").trigger("change");

		//hasLookTypeId
		if(postTypeMap[postType].hasLookTypeId) {
			$lookTypesSelection[0].selectedIndex = 1;
			$lookTypesDiv.show();
			$extHoroscopeTypeInput.val("").trigger("change");
		}
		else {
			$lookTypesSelection.val("");
			$lookTypesDiv.hide();
		}
	});
	
	$extLookUrlInput.on("change", function() {
		if(!postTypeMap[$postType.val()].hasExtLookUrl) 
			$postType.val("HOWTO");
	});

});