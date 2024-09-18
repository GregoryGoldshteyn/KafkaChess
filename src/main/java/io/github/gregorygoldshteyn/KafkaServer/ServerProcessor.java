package io.github.gregorygoldshteyn.kafka.chess.server;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;

import io.github.gregorygoldshteyn.kafka.chess.MessageHelper;

public class ServerProcessor implements Processor<String, String, String, String>{
	private System.Logger logger;

	public ServerProcessor(){
		logger = System.getLogger("ServerProcessorLogger");
	}
	
	public void parseClientRequest(String clientMessage){
		String[] tokens = clientMessage.split("\\s+");
		
		// A message with nothing in it should not exist
		// But in case one hits, just swallow it
		if(tokens.length < 1)
		{
			return;
		}

		String clientRequest = tokens[0];

		if(!MessageHelper.clientMessages.contains(clientRequest))
		{
			sendDidNotUnderstand(clientRequest, MessageHelper.UNKNOWN_COMMAND);
			return;
		}

		switch(clientRequest){
			case MessageHelper.REQUEST_NEW_GAME:
				onRequestNewGame(tokens);
				break;
			case MessageHelper.LOADED:
				onLoaded(tokens);
				break;
			case MessageHelper.CONCEDED:
				onConceded(tokens);
				break;
			case MessageHelper.DISCONNECTED:
				onDisconnected(tokens);
				break;
			case MessageHelper.OFFER_DRAW:
				onOfferDraw(tokens);
				break;
			case MessageHelper.ACCEPT_DRAW:
				onAcceptDraw(tokens);
				break;
			case MessageHelper.DID_NOT_UNDERSTAND_MESSAGE:
				onDidNotUnderstand(tokens);
				break;
			default:
				break;
		}
	}

	public void onRequestNewGame(String[] args){
		logger.log(System.Logger.Level.INFO, "onRequestNewGame called with: " + String.join(" ", args));
	}

	public void onLoaded(String[] args){
		logger.log(System.Logger.Level.INFO, "onLoaded called with: " + String.join(" ", args));
	}

	public void onConceded(String[] args){
		logger.log(System.Logger.Level.INFO, "onConceded called with: " + String.join(" ", args));
	}

	public void onDisconnected(String[] args){
		logger.log(System.Logger.Level.INFO, "onDisconnected called with: " + String.join(" ", args));
	}

	public void onOfferDraw(String[] args){
		logger.log(System.Logger.Level.INFO, "onOfferDraw called with: " + String.join(" ", args));
	}

	public void onAcceptDraw(String[] args){
		logger.log(System.Logger.Level.INFO, "onAcceptDraw called with: " + String.join(" ", args));
	}

	public void onDidNotUnderstand(String[] args){
		logger.log(System.Logger.Level.INFO, "onDidNotUnderstand called with: " + String.join(" ", args));
	}

	public void sendDidNotUnderstand(String request, String reason){
		logger.log(System.Logger.Level.INFO, "did not understand " + request + " " + reason);		
	}

	public void close(){

	}

	public void init(ProcessorContext<String, String> context){

	}

	public void process(Record<String, String> record){
		parseClientRequest(record.value());
	}
}
