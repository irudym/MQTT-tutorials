/**
 * 
 */
package mqttSim;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.util.Random;





/**
 * @author igor
 *
 */
public class mqttSim implements MqttCallback {

	private String pBrokerUrl;
	private String pName;
	private MqttClient pClient;
	private MqttConnectOptions pOptions;
	private int pInterval = 500;
	
	/**
	 * 
	 */
	public mqttSim(String name, String url, String clientId) throws MqttException {
		pBrokerUrl = url;
		pName = name;
		
		String will = new String("sensor/"+ pName + "/status");
		try {
			pOptions = new MqttConnectOptions();
			pOptions.setCleanSession(false);
			pOptions.setWill(will, "offline".getBytes(), 2, true);
			
			//create pClient instance
			pClient = new MqttClient(pBrokerUrl, clientId);
			pClient.setCallback(this);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void publish(String topic, String payload) throws MqttException {
		try {
			pClient.publish(topic,payload.getBytes(),2,true);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void subscribe(String topic) throws MqttException {
		pClient.subscribe(topic);
	}
	
	public void connect() throws MqttException {
		try {
			pClient.connect();
			publish("sensor/" + pName + "/status","online");
		} catch (MqttException e){
			e.printStackTrace();
		}
	}
	
	public void disconnect() throws MqttException {
		try {
			publish("sensor/" + pName + "/status","offline");
			pClient.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public int getInterval() {
		return pInterval;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
	 */
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//debug
		System.out.println("Topic:" + topic);
		System.out.println("Message:" + new String(message.getPayload(),"UTF-8"));
		
		pInterval = Integer.parseInt(new String(message.getPayload()));

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException {
		try {
			Random rnd = new Random();
			int temp;
			
			mqttSim simulator = new mqttSim("temp1","tcp://cloud2logic.com","client1");
			simulator.connect();
			simulator.subscribe("sensor/temp1/interval");
			
			while(true) {
				temp = rnd.nextInt(50);
				Thread.sleep(simulator.getInterval());
				
				simulator.publish("sensor/temp1/temperature", Integer.toString(temp));
			}	
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
