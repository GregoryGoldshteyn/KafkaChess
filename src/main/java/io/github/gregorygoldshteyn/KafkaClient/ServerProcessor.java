package io.github.gregorygoldshteyn.kafka.chess;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;

public class ServerProcessor implements Processor<String, String, String, String>{
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

	}

	public void onLoaded(String[] args){

	}

	public void onConceded(String[] args){

	}

	public void onDisconnected(String[] args){

	}

	public void onOfferDraw(String[] args){

	}

	public void onAcceptDraw(String[] args){

	}

	public void onDidNotUnderstand(String[] args){

	}

	public void sendDidNotUnderstand(String request, String reason){
		
	}

	public void close(){

	}

	public void init(ProcessorContext<String, String> context){

	}

	public void process(Record<String, String> record){

	}
}
