package io.github.gregorygoldshteyn.kafka.chess;

import java.util.UUID;

public class Game{
	public String gameID;
	public String whiteID;
	public String blackID;
	public String[][] boardState;

	public Game(String gameID){
		this.gameID = gameID;
	}

	public static Game generateNewGame(){
		return new Game(UUID.randomUUID().toString());
	}
}
