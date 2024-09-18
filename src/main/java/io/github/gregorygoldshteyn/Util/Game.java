package io.github.gregorygoldshteyn.kafka.chess;

import java.util.UUID;

public class Game{
	// Debug logger
	private System.Logger logger;

	// Required for messaging
	public String gameID;
	public String whiteID;
	public String blackID;

	// Bitboards could be an imporvement in perf, storage, and messaging
	// But it's easier to debug using strings
	public String[][] boardState;

	// Implementation of a bitboard would be similar
	// But use bytes instead of strings
	public static class Piece{
		public static final String WHITE_KING = "K";
		public static final String WHITE_QUEEN = "Q";
		public static final String WHITE_BISHOP = "B";
		public static final String WHITE_KNIGHT = "N";
		public static final String WHITE_ROOK_CAN_CASTLE = "C";
		public static final String WHITE_ROOK_CANNOT_CASTLE = "R";
		public static final String WHITE_PAWN = "P";

		public static final String BLACK_KING = "k";
		public static final String BLACK_QUEEN = "q";
		public static final String BLACK_BISHOP = "b";
		public static final String BLACK_KNIGHT = "n";
		public static final String BLACK_ROOK_CAN_CASTLE = "c";
		public static final String BLACK_ROOK_CANNOT_CASTLE = "r";
		public static final String BLACK_PAWN = "p";

		public static final String EMPTY = " ";
	}
	
	// Game starts with white's turn
	public boolean whiteTurn;

	public Game(String gameID){
		logger = System.getLogger("GameLogger");
		this.gameID = gameID;
		this.whiteTurn = true;
		initBoard();

		logBoard(true, true);
	}
	
	// A chessboard is labled with letters on the columns and numbers on the rows
	// It makes the most sense for the white rook that can castle at 0,0 (A1)
	// The white king at 4,0 (E1) etc.
	// This means that the board is "sideways" in memory, with white starting
	// on the "left" side
	public void initBoard(){
		boardState = new String[][] {
			{ Piece.WHITE_ROOK_CAN_CASTLE, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_ROOK_CAN_CASTLE },
			{ Piece.WHITE_KNIGHT, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_KNIGHT },
			{ Piece.WHITE_BISHOP, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_BISHOP },
			{ Piece.WHITE_QUEEN, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_QUEEN },
			{ Piece.WHITE_KING, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_KING },
			{ Piece.WHITE_BISHOP, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_BISHOP },
			{ Piece.WHITE_KNIGHT, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_KNIGHT },
			{ Piece.WHITE_ROOK_CAN_CASTLE, Piece.WHITE_PAWN, Piece.EMPTY, Piece.EMPTY,
				Piece.EMPTY, Piece.EMPTY, Piece.BLACK_PAWN, Piece.BLACK_ROOK_CAN_CASTLE }
		};
	}

	public String getBoardString(boolean whitePerspective){
		StringBuilder boardBuilder = new StringBuilder(8 * 9);

		if(whitePerspective){
			for(int row=7; row >=0; row-=1){
				for(int col=0; col <=7; col+=1){
					boardBuilder.append(boardState[col][row]);
				}
				boardBuilder.append(System.getProperty("line.separator"));
			}
		}
		else{
			for(int row=0; row <=7; row+=1){
				for(int col=7; col >=0; col-=1){
					boardBuilder.append(boardState[col][row]);
				}
				boardBuilder.append(System.getProperty("line.separator"));
			}
		}

		return boardBuilder.toString().trim();

	}

	public String getBoardString(){
		StringBuilder boardBuilder = new StringBuilder(8 * 9);
			
		for(String[] col : boardState){
			boardBuilder.append(String.join("", col));
			boardBuilder.append(System.getProperty("line.separator"));
		}

		return boardBuilder.toString().trim();
	}

	public void logBoard(boolean logWhitePerspective, boolean logBlackPerspective){
		// If niether the black perspective or white perspective is requested, log as "in memory"
		
		if(!logWhitePerspective && !logBlackPerspective)
		{
			logger.log(System.Logger.Level.INFO, getBoardString());
		}

		if(logWhitePerspective){

			logger.log(System.Logger.Level.INFO, getBoardString(true));
		}

		if(logBlackPerspective){
			logger.log(System.Logger.Level.INFO, getBoardString(false));
		}
	}

	public void logBoard(){
		logBoard(false, false);
	}

	public static Game generateNewGame(){
		return new Game(UUID.randomUUID().toString());
	}
}
