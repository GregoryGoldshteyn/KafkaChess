package io.github.gregorygoldshteyn.kafka.chess.client;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

public class ClientProcessorSupplier implements ProcessorSupplier<String, String, String, String>{
	public ClientProcessorSupplier(){
		
	}
	
	public Processor<String, String, String, String> get(){
		return new ClientProcessor();
	}
}
