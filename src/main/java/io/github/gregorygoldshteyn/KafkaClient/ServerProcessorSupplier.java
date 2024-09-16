package io.github.gregorygoldshteyn.kafka.chess;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

public class ServerProcessorSupplier implements ProcessorSupplier<String, String, String, String>{
	public String gameID;
	
	public ServerProcessorSupplier(){
		this.gameID = gameID;
	}
	
	public Processor<String, String, String, String> get(){
		return new ServerProcessor();
	}
}
