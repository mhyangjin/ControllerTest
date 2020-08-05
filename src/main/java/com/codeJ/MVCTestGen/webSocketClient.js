var stompClient = null;

window.onload=connect;

function connect() {
    var socket = new SockJS('/websocket');
	var result="";
    stompClient = Stomp.over(socket);
	stompClient.connect({},function(frame) {
		console.log('Connected::: ' + frame);
		stompClient.subscribe('/subscribe/mvcTest', mvcTestResult) ;
		});
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
}

function mvcTestResult (mvcTest) {
	var results = "";
	console.log("receive: Result:" + mvcTest.body);
	results =JSON.parse(mvcTest.body);
//	console.log("receive: Result:" + results.controller );
//	console.log("receive: Result:" + JSON.stringify(results.mvcResult));
//	var modelResult=JSON.parse(results.mvcResult);
	var  sendBtn= document.getElementById(results.controller + "_result");
	if ( sendBtn !== null) {
      
		var resultString= "<fieldset style = \"width:88%\">"
                         +  "<text id=\"" + results.controller + "_result" +"\"> "
                         + results.Result+ ":" + results.ERR +"</text><br>"
        if (results.Result == 200 ) {
		   resultString=resultString  + "<text>" +JSON.stringify(results.mvcResult.model)+ "</text></<fieldset>";
        }
		$(sendBtn).replaceWith(resultString);
	}
}

function getAllChild(thisNode) {
    var nodes=[];
    var resultNodes=[];
    nodes=thisNode.childNodes;
    console.log("getAllChild thisNode:" + thisNode.nodeName);
    $(nodes).each(function(index,node){
        console.log("getAllChild return:" + node.toString() + node.nodeName);
        resultNodes.push(node);
        if ( node.hasChildNodes())
            resultNodes=resultNodes.concat(getAllChild(node));
    });
    
   return resultNodes;
}

function makeJSonParameters(btName) {
    var JsonObject={};
    JsonObject.ControllerName = btName.id;
    
    thisNode=btName.parentNode;
    console.log("THISNODE:" + thisNode.nodeName );
    //FORM Node까지 찾기
    while(thisNode.nodeName != "FORM") {
        console.log("  THISNODE:" +thisNode.nodeName );
        thisNode=thisNode.parentNode;
    }
    var requestArgs=[];
    var jSonClasses=[];
    var Nodes = [];
    
    Nodes =getAllChild(thisNode);
    //FORM이하의 노드를 검색해서  parameter를 만든다.
    $(Nodes).each(function( index, object) {
        console.log("node:" + object.nodeName);
        if (( object.nodeName == "INPUT") && ( object.type =="text") )
        {
            var param={};
            param[object.name]= object.value;
//            console.log("requestArgs:" + requestArgs);
            requestArgs.push(JSON.stringify(param));
        }
        
        var jSonClass={};
        if (object.nodeName == "TEXTAREA") {
            jSonClass[object.name]=object.value.split("\n");
            jSonClasses.push(JSON.stringify(jSonClass));
 //           console.log("TEXTAREA:" + jSonClass[object.name]);
        }
    });
    JsonObject.JSonClassString=jSonClasses;
    JsonObject.RequestString=requestArgs;
//    console.log("makeJSonParameters:" + JsonObject.toString);
    return JsonObject;
}

function sendMVCTest(btName) {
   	stompClient.send('/mvcTest',{},JSON.stringify(makeJSonParameters(btName)));
	
}

$(function() {
	$("#connect").click(function() { connect(); })
	$("#disconnect").click(function() { disconnect(); })
	$("form").on('submit', function (e) {
        e.preventDefault();
    });
})