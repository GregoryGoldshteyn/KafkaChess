package io.github.gregorygoldshteyn.kafka.chess.server;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Main{
	public static Properties initProperties(){
		Properties props = new Properties();
        	props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-chess-server");
        	props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        	props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        	props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		
        	props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, new StringSerializer().getClass());
        	props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new StringSerializer().getClass());
		
		return props;
	}

	public static void main(String[] args){
		final String gameID = "1234";
		final String serverOutput = "streams-server-output";
		final String playerInput = "streams-player-input";
		
		final StreamsBuilder builder = new StreamsBuilder();
		final Properties props = initProperties();
		final ServerProducer serverProducer = new ServerProducer(props, serverOutput);

		final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		String consoleLine = "";

		KStream<String, String> playerInputStream = builder.<String, String>stream(playerInput);

		playerInputStream.process(new ServerProcessorSupplier());
		
		final Topology topology = builder.build();
        	final KafkaStreams streams = new KafkaStreams(topology, props);
        	final CountDownLatch latch = new CountDownLatch(1);
		
		System.out.println(topology.describe());
        	// attach shutdown handler to catch control-c
        	Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            		@Override
            		public void run() {
				try{
					consoleReader.close();
				} catch(Throwable e){

				} finally {
                			streams.close();
                			latch.countDown();
				}
            		}
        	});

        	try {
            		streams.start();
			do{
				consoleLine = consoleReader.readLine();
				if(consoleLine == null || consoleLine.length() < 1){
					continue;
				}
				String[] consoleLineArgs = consoleLine.split("\\s+", 2);

				serverProducer.sendToClients(consoleLineArgs[0], consoleLineArgs[1]);
			}
			while(consoleLine.length() > 0);

            		latch.await();
        	} catch (Throwable e) {
            		System.exit(1);
        	}
        	System.exit(0);
	}
}
