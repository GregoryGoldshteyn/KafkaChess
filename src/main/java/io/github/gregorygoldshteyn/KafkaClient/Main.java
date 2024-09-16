package io.github.gregorygoldshteyn.kafka.chess.client;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import io.github.gregorygoldshteyn.kafka.chess.Game;

public class Main{
	public static Properties initProperties(){
		Properties props = new Properties();
        	props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-chess-client");
        	props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        	props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        	props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		return props;
	}

	public static void main(String[] args){
		String playerID = "testId";
		if(args.length > 0){
			playerID = args[0];
		}
		final String serverOutput = "streams-server-output";
		final String playerInput = "streams-player-input";
		final StreamsBuilder builder = new StreamsBuilder();
		final ArrayList<Game> games = new ArrayList<Game>();
		final Properties props = initProperties();	
		final ClientProducer clientProducer = new ClientProducer(props, playerInput, playerID);

		KStream<String, String> serverOutputStream = builder.<String, String>stream(serverOutput)
			.filter(new Predicate<String, String>(){
					@Override
				 	public boolean test(String k, String v){
						for(Game game : games){
							if(k.equals(game.gameID)){
								return true;
							}
						}
						return false;
					}	
				});

		serverOutputStream.process(new ClientProcessorSupplier());
		
		final Topology topology = builder.build();
        	final KafkaStreams streams = new KafkaStreams(topology, props);
        	final CountDownLatch latch = new CountDownLatch(1);
		
		System.out.println(topology.describe());
        	// attach shutdown handler to catch control-c
        	Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            		@Override
            		public void run() {
                		streams.close();
				clientProducer.close();
                		latch.countDown();
            		}
        	});

        	try {
            		streams.start();
            		latch.await();
        	} catch (Throwable e) {
            		System.exit(1);
        	}
        	System.exit(0);
	}
}
