var SAAgent = null;
var SASocket = null;
var CHANNELID = 227;
var tag = "SAP";
var ProviderAppName = "cdp";
var page="c1";
var res=0;
var dangerCtrl=0;
var gFileTransfer = null;
var FILE_PATH = 'file:///opt/usr/media/Sounds/test01.amr';
var gPeerAgent = null;
var gTransferId = 0;
function onerror(err)
{
	console.log("ONERROR: err [" + err.name + "] msg[" + err.message + "]");
}

function disconnect()
{
	try 
	{
		if (SASocket != null)
		{
			console.log(" DISCONNECT SASOCKET NOT NULL");
			SASocket.close();
			SASocket = null;
		}
	} catch(err) {
		console.log(" DISCONNECT ERROR: exception [" + err.name + "] msg[" + err.message + "]");
	}
}

var agentCallback = 
{
	onconnect : function(socket)
	{
		console.log( "agentCallback onconnect" + socket);
		SASocket = socket;
		SASocket.setDataReceiveListener(onreceive);
		SASocket.setSocketStatusListener(function(reason){
			console.log("Service connection lost, Reason : [" + reason + "]");
			disconnect();
		});
	},
	onerror : onerror
};

var peerAgentFindCallback =
{
	onpeeragentfound : function(peerAgent) 
	{
		try {
			if (peerAgent.appName == ProviderAppName) {
				SAAgent.setServiceConnectionListener(agentCallback);
				SAAgent.requestServiceConnection(peerAgent);
			    gPeerAgent = peerAgent;
			} else {
				alert("Not expected app!! : " + peerAgent.appName);
			}
		} catch(err) {
			console.log(" peerAgentFindCallback::onpeeragentfound exception [" + err.name + "] msg[" + err.message + "]");
		}
	},
	onerror : onerror
}

function ftCancel(id, successCb, errorCb) {
	if (SAAgent == null || gFileTransfer == null || gPeerAgent == null) {
		errorCb({
			name : 'NotConnectedError',
		    message : 'SAP is not connected'
		});
		return;
	}

	try {
		gFileTransfer.cancelFile(id);
		successCb();
	} catch (err) {
		console.log('cancelFile exception <' + err.name + '> : ' + err.message);
		window.setTimeout(function() {
			errorCb({
			    name : 'RequestFailedError',
			    message : 'cancel request failed'
			});
		}, 0);
	}
	
}

function cancelFile() {
	ftCancel(gTransferId,function() {
		console.log('Succeed to cancel file');
	}, function(err) {
		console.log('Failed to cancel File');
	});	
}

function sendFile(path) {
	console.log(path);
	ftSend(path, function(id) {
		console.log('Succeed to send file');
		gTransferId = id;
	}, function(err) {
		console.log('Failed to send File');
	});	
}
function ftSend(path, successCb, errorCb) {
	console.log(path);
	if (SAAgent == null || gFileTransfer == null || gPeerAgent == null) {
		errorCb({
			name : 'NotConnectedError',
		    message : 'SAP is not connected'
		});
		return;
	}
	
	try {
		var transferId = gFileTransfer.sendFile(gPeerAgent, path);
		successCb(transferId);
	} catch (err) {
		console.log('sendFile exception <' + err.name + '> : ' + err.message);
		window.setTimeout(function() {
			errorCb({
			    name : 'RequestFailedError',
			    message : 'send request failed'
			});
		}, 0);
	}
}
var ftSuccessCb = {
		onsuccess : function () {
			console.log('Succeed to connect');
		},
		onsendprogress : function (id, progress) {
			console.log('onprogress id : ' + id + ' progress : ' + progress);
		},
		onsendcomplete : function (id, localPath) {
		},
		onsenderror : function (errCode, id) {
			console.log('Failed to send File. id : ' + id + ' errorCode :' + errCode);
		}
	};

function ftInit(successCb, errorCb) {
	if (SAAgent == null) {
		errorCb({
		    name : 'NetworkError',
		    message : 'Connection failed'
		});
		return;
	}

	var filesendcallback = {
		onprogress : successCb.onsendprogress,
		oncomplete : successCb.onsendcomplete,
		onerror : successCb.onsenderror
	};
	
	try {
		gFileTransfer = SAAgent.getSAFileTransfer();
		gFileTransfer.setFileSendListener(filesendcallback);
		successCb.onsuccess();
	} catch (err) {
		console.log('getSAFileTransfer exception <' + err.name + '> : ' + err.message);
		window.setTimeout(function() {
			errorCb({
			    name : 'NetworkError',
			    message : 'Connection failed'
			});
		}, 0);
	}
}
function onsuccess(agents) {
	try {
		if (agents.length > 0) {
			SAAgent = agents[0];
			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();
			ftInit(ftSuccessCb, function(err) {
				console.log('Failed to get File Transfer');
			});
			console.log(" onsuccess " + SAAgent.name);
		} else {
			alert("Not found SAAgent!!");
			console.log("onsuccess else");
		}
	} catch(err) {
		console.log("onsuccess exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function connect() 
{
	if (SASocket) 
	{
		alert('Already connected!');
        return false;
    }
	try
	{
		webapis.sa.requestSAAgent(onsuccess, onerror);
	}
	catch(err)
	{
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}
var flag =0;
function onreceive(channelId, data) {
	console.log(data);
	var obj = jQuery.parseJSON(data);
	var result = obj.code;
	if(result == "location")
	{
		res = obj.data;
		circleXY(res, page);
	}
	if(result == "direction")
	{
		res = obj.data;
		circleXY(res, page);
	}
}

function fetch(data)
{
	try
	{
		console.log(data);
		SASocket.sendData(CHANNELID, data);
	} 
	catch(err)
	{
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
		connect();
	}
}


function backHistory()
{
	tau.back();
}
function circleXY(radius,id,flagAcc)
{
   if(flagAcc == 0) {
      if(radius<10&&radius>-10)
      {
         return;
      }
      //console.log(radius);
      if(radius>0)
      {
          res = 3+res;
      }
      else
      {
          res = res-3;
      }
   } 
   
    var x, y,x1,y1,x2,y2;
   
    var rad = res*3.14/180;
    
    x = 154+150*Math.cos(rad);
    y = 155+150*Math.sin(rad);
    
    x1 = 154+150*Math.cos(rad/2+40);
    y1 = 155+150*Math.sin(rad/2+40);

    x2 = 154+150*Math.cos(rad/3+10);
    y2 = 155+150*Math.sin(rad/3+10);
   
    //  158px (반지금)
    
    var id2, id3;
    
    id2 = id.replace('c', 'd');
    id3 = id.replace('c', 'e');
    
    var img = document.getElementById(id);
    var img2 = document.getElementById(id2);
    var img3 = document.getElementById(id3);

    img.style.left=x+"px";
    img.style.top=y+"px";
    img2.style.left=x1+"px";
    img2.style.top=y1+"px";
    img3.style.left=x2+"px";
    img3.style.top=y2+"px";
}
function goToBlur() {
	page = "c4";
	tau.changePage("#blurMap");
	circleXY(res, page,1);
}

function backToMap() {
	page = "c3";
	tau.changePage("#map");
	circleXY(res, page,1);
}

function backToFriend() {
	page = "c5";
	tau.changePage("#friend");
	circleXY(res, page,1);
}

function getTime() {
	var current_dt = tizen.time.getCurrentDateTime();
	//alert(current_dt.getHours()+"+"+current_dt.getMinutes());
	
	var hour = current_dt.getHours();
	if(hour < 10) {
		hour ="0"+hour.toString();
	}
	var min = current_dt.getMinutes();
	if(min < 10){
		min = "0"+min.toString();
	}
	var date = current_dt.getDate();
	var month = current_dt.getMonth();
			
	document.getElementById("time").innerHTML = hour+" : "+min+"<br>"+month+"월 "+date+"일 ";
}

var spotFinish=0, spotControl=0, spot_t;
function getSpot() {
	if(spotControl==0){
		//$("spotID").css("background-image","url(../images/exer/spot2.png)");
		//document.getElementById("spotID").style.backgroundImage= "url(icon.png)";
		document.getElementById("spotID").style.width = "65px";
		document.getElementById("spotID").style.height = "25px";
	}else if(spotControl==1){
		//document.getElementById("spotID").style.backgroundImage= "url('../images/exer/spot3.png')";	
		document.getElementById("spotID").style.width = "103px";
		document.getElementById("spotID").style.height = "47px";	
	}else if(spotControl==2){	//document.getElementById("spotID").style.backgroundImage= "url('../images/exer/spot4.png')";	
		document.getElementById("spotID").style.width = "119px";
		document.getElementById("spotID").style.height = "66px";
	}else if(spotControl==3){//document.getElementById("spotID").style.backgroundImage= "url('../images/exer/spot5.png')";	
		document.getElementById("spotID").style.width = "141px";
		document.getElementById("spotID").style.height = "99px";	
	}else if(spotControl==4){
		//document.getElementById("spotID").style.backgroundImage= "url('../images/exer/spot6.png')";	
		document.getElementById("spotID").style.width = "141px";
		document.getElementById("spotID").style.height = "124px";
	}else if(spotControl==5){	//document.getElementById("spotID").style.backgroundImage= "url('../images/exer/spot example2.png')";	
		document.getElementById("spotID").style.width = "141px";
		document.getElementById("spotID").style.height = "133px";
		gotoSpeed();
		//spotFinish=1;
		//clearInterval(spot_t);
	}
	spotControl++;
}

function gotoSpeed(){
	tau.changePage("#speedup");
}

function backToExer() {
	tau.changePage("#exer");
}


function backToMap2() {
	dangerCtrl=1;
	tau.changePage("#map");
}