<%@ include file="/common/taglibs.jsp"%>
<div id=container class=container>
<div id=header>
<div id=profile_info><img id="avatar" alt="avatar" src="<c:url value="/common/theme/backend/images/avatar.jpg"/>">
<s:form beanclass="${actionBean.class}">
<p>welcome <strong>${actionBean.currentUserName}</strong>. </p>
</s:form>
</div>
<div id="restartServerTime_info">
<s:form beanclass="${actionBean.class}">
<s:hidden name="restartServerTime" id="restartServerTime" value="${actionBean.restartServerTime}"></s:hidden>
<label name="showRestartTime" id="showRestartTime" value="" />
</s:form>
</div>
<div id=logo>
<h1><a href="<c:url value="/index.action"/>"> </a></h1>
</div>
</div>
<script type="text/javascript">
	
	var restartServerTime = document.getElementById('restartServerTime').value;
	var myVar;
	if(restartServerTime != "")
	{
		restartServerTime = Math.floor(Number(restartServerTime)/1000);
		myTimer();
		myVar= setInterval(function () {myTimer()}, 1000);
	}
	
	function myTimer(){
		restartServerTime--;
		var showString = "Restart the server time remaining:";
		if( Math.floor(restartServerTime/86400) > 1 ){
			showString = showString + "More than " + Math.floor(restartServerTime/86400) + " days";
		}else if( Math.floor(restartServerTime/86400) == 1 ){
			showString = showString + "More than " + Math.floor(restartServerTime/86400) + " day";
		}else if((restartServerTime/300) > 1){
			var hh =  Math.floor(restartServerTime/3600);
			var mm =  Math.floor((restartServerTime/60)%60);
			var ss = (restartServerTime%60)%60;
			showString = showString + hh + ":" + mm + ":" + ss;
		}else if(restartServerTime > 0){
			var mm = (Math.floor(restartServerTime/60)%60);
			var ss = restartServerTime%60;
			showString = "<font color='red'>" + showString + mm + ":" + ss +"</font>";
		}else{
			clearInterval(myVar);
			showString = "<font color='red'>The server has restarted!</font>";
		}
		document.getElementById('showRestartTime').innerHTML = showString;
	}
</script>