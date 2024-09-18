package io.github.gregorygoldshteyn.kafka.chess.client;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;

import io.github.gregorygoldshteyn.kafka.chess.MessageHelper;

public class ClientProcessor implements Processor<String, String, String, String>{
	private System.Logger logger;
	
	public ClientProcessor(){
		logger = System.getLogger("ClientProcessorLogger");
	}
	
	public void parseServerCommand(String serverMessage){
		String[] tokens = serverMessage.split("\\s+");
		
		// A message with nothing in it should not exist
		// But in case one hits, just swallow it
		if(tokens.length < 1)
		{
			return;
		}

		String serverCommand = tokens[0];

		if(!MessageHelper.serverMessages.containsKey(serverCommand))
		{
			sendDidNotUnderstand(serverCommand, MessageHelper.UNKNOWN_COMMAND);
			return;
		}

		if(MessageHelper.serverMessages.get(serverCommand) > tokens.length)
		{
			sendDidNotUnderstand(serverCommand, MessageHelper.TOO_FEW_ARGS);
			return;
		}

		switch(serverCommand){
			case MessageHelper.GAME_START:
				onStartGame(tokens);
				break;
			case MessageHelper.GAME_RESULT:
				onGameResult(tokens);
				break;
			case MessageHelper.MOVE_ACCEPTED:
				onMoveAccepted(tokens);
				break;
			case MessageHelper.MOVE_REJECTED:
				onMoveRejected(tokens);
				break;
			case MessageHelper.DRAW_OFFER:
				onDrawOffer(tokens);
				break;
			default:
				sendDidNotUnderstand(serverMessage, MessageHelper.UNKNOWN_COMMAND);
		}
	}

	public void onStartGame(String[] args){
		logger.log(System.Logger.Level.INFO, "onStartGame called with: " + String.join(" ", args));
	}

	public void onGameResult(String[] args){

		logger.log(System.Logger.Level.INFO, "onGameResult called with: " + String.join(" ", args));
	}

	public void onMoveAccepted(String[] args){

		logger.log(System.Logger.Level.INFO, "onMoveAccepted called with: " + String.join(" ", args));
	}

	public void onMoveRejected(String[] args){

		logger.log(System.Logger.Level.INFO, "onMoveRejected called with: " + String.join(" ", args));
	}

	public void onDrawOffer(String[] args){
		logger.log(System.Logger.Level.INFO, "onDrawOffer called with: " + String.join(" ", args));
	}

	public void sendDidNotUnderstand(String serverMessage, String reason){
		logger.log(System.Logger.Level.INFO, "did not understand " + serverMessage + " " + reason);
	}

	public void close(){
		logger.log(System.Logger.Level.INFO, "closing client processor");
	}

	public void init(ProcessorContext<String, String> context){
		logger.log(System.Logger.Level.INFO, "init");
	}

	public void process(Record<String, String> record){
		parseServerCommand(record.value());
	}
}
