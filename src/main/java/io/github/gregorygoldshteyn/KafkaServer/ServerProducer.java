package io.github.gregorygoldshteyn.kafka.chess.server;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ServerProducer{
	private KafkaProducer<String, String> producer;
	private String serverTopic;

	public ServerProducer(Properties props, String serverTopic){
		this.producer = new KafkaProducer<String, String>(props);
		this.serverTopic = serverTopic;
	}

	public void close(){
		producer.close();
	}

	public void sendToClients(String target, String message){
		producer.send(new ProducerRecord(serverTopic, target, message));
	}
}
