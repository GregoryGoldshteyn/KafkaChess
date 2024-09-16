package io.github.gregorygoldshteyn.kafka.chess.client;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ClientProducer{
	private KafkaProducer<String, String> producer;
	private String clientTopic;
	private String clientID;

	public ClientProducer(Properties props, String clientTopic, String clientID){
		this.producer = new KafkaProducer<String, String>(props);
		this.clientTopic = clientTopic;
		this.clientID = clientID;
	}

	public void close(){
		producer.close();
	}

	public void sendToServer(String message){
		producer.send(new ProducerRecord(clientTopic, clientID, message));
	}
}
