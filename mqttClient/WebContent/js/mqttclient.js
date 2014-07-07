$(document).ready(function() {
	var mqttClient = new Messaging.Client("cloud2logic.com",1884,"dash1");
	
	// set callback handlers
	mqttClient.onConnectionLost = onConnectionLost;
	mqttClient.onMessageArrived = onMessageArrived;
	
	//connect to server
	mqttClient.connect({onSuccess:onConnect});
	
	//called when the connection is made
	function onConnect() {
		console.log("OnConnect");
		
		mqttClient.subscribe("sensor/temp1/temperature");
	}
	
	// called when the client loses its connection
	function onConnectionLost(responseObject) {
	  if (responseObject.errorCode !== 0) {
	    console.log("onConnectionLost:"+responseObject.errorMessage);
	  }
	}

	// called when a message arrives
	function onMessageArrived(message) {
	  console.log("onMessageArrived:"+message.payloadString);
	  
	  gauge.refresh(parseFloat(message.payloadString));
	}
	
	var gauge = new JustGage({
		id: "gauge",
		value: 0,
		min: -20,
		max: 60,
		title: "Temperature",
		showMinMax: false,
		label: "Celsius"
	});
	
	$('#set_btn').on('click',function() {
		var val = $('.times').val();
		var message = new Messaging.Message(val);
		message.destinationName = "sensor/temp1/interval";
		mqttClient.send(message);
	});
});