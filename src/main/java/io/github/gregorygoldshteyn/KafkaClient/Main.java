package io.github.gregorygoldshteyn.kafka.chess;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

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
		final String gameID = "1234";
		final String serverOutput = "streams-server-output";
		final String playerInput = "streams-player-input";
		final StreamsBuilder builder = new StreamsBuilder();
		final Properties props = initProperties();

		KStream<String, String> serverOutputStream = builder.<String, String>stream(serverOutput)
			.filter(new Predicate<String, String>(){
					@Override
				 	public boolean test(String k, String v){
						if(k.equals(gameID)){
							return true;
						}
						return false;
					}	
				});

		serverOutputStream.process(new ProcessorSupplier<String, String>(){
					@Override
					public Processor<String, String> get(){
						return new Processor<String, String>(){
							@Override
							public void process(String k, String v){
								System.out.println(k);
								System.out.println(v);
							}

							@Override
							public void close()
							{

							}
							@Override
							public void init(ProcessorContext c)
							{

							}
						};
					}
			});
		final Topology topology = builder.build();
		
        	final KafkaStreams streams = new KafkaStreams(topology, props);
        	final CountDownLatch latch = new CountDownLatch(1);
		
		System.out.println(topology.describe());
        	// attach shutdown handler to catch control-c
        	Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            		@Override
            		public void run() {
                		streams.close();
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
