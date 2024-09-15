package io.github.gregorygoldshteyn.kafka.chess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageHelper{

	// Messages that the server tells the client
	// The motivation behind this map between human readable
	// labels and single chars is to reduce the overhead of messages
	// while still being able to tell what the messages mean
	public static final String GAME_START = "S";
	public static final String GAME_RESULT = "R";
	public static final String MOVE_ACCEPTED = "A";
	public static final String MOVE_REJECTED = "J";
	public static final String DRAW_OFFER = "D";

	public static final Map<String, Integer> serverMessages = Map.ofEntries(
			Map.entry(GAME_START, 3),
			Map.entry(GAME_RESULT, 3),
			Map.entry(MOVE_ACCEPTED, 3),
			Map.entry(MOVE_REJECTED, 3),
			Map.entry(DRAW_OFFER, 1)
		);

	// Messages that the client tells the server
	public static final String REQUEST_NEW_GAME = "R";
	public static final String LOADED = "L";
	public static final String CONCEDED = "C";
	public static final String DISCONNECTED = "D";
	public static final String OFFER_DRAW = "O";
	public static final String ACCEPT_DRAW = "A";
	public static final String DID_NOT_UNDERSTAND_MESSAGE = "X";

	public static final Set<String> clientMessages = Set.of(
			REQUEST_NEW_GAME,
			LOADED,
			CONCEDED,
			DISCONNECTED,
			OFFER_DRAW,
			ACCEPT_DRAW
		);

	// Messages that the client can send to
	// let the server know that it did not understand
	// the message
	// For human readability, and since errors should
	// rarely be generated, these can be longer than
	// single characters
	public static final String UNKNOWN_COMMAND = "Unknown command";
	public static final String TOO_FEW_ARGS = "Too few arguments";
}
