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
		// The total number of pieces should be 16 or fewer
		// This is to implement bitboard storage with fewest bytes
		// This is plenty for the 12 pieces of chess and the 2 special cases
		// in each color (castling, en passant)
		public static final String WHITE_KING = "K";
		public static final String WHITE_QUEEN = "Q";
		public static final String WHITE_BISHOP = "B";
		public static final String WHITE_KNIGHT = "N";
		public static final String WHITE_ROOK_CAN_CASTLE = "C";
		public static final String WHITE_ROOK_CANNOT_CASTLE = "R";
		public static final String WHITE_PAWN = "P";
		public static final String WHITE_PAWN_EN_PASSANT = "E";

		public static final String BLACK_KING = "k";
		public static final String BLACK_QUEEN = "q";
		public static final String BLACK_BISHOP = "b";
		public static final String BLACK_KNIGHT = "n";
		public static final String BLACK_ROOK_CAN_CASTLE = "c";
		public static final String BLACK_ROOK_CANNOT_CASTLE = "r";
		public static final String BLACK_PAWN = "p";
		public static final String BLACK_PAWN_EN_PASSANT = "e";

		public static final String EMPTY = " ";

		public static boolean isWhitePiece(String piece){
			if(piece.equals(WHITE_KING) || 
				piece.equals(WHITE_QUEEN) ||
				piece.equals(WHITE_BISHOP) ||
				piece.equals(WHITE_KNIGHT) ||
				piece.equals(WHITE_ROOK_CAN_CASTLE) ||
				piece.equals(WHITE_ROOK_CANNOT_CASTLE) ||
				piece.equals(WHITE_PAWN))
			{
				return true;
			}

			return false;
		}

		public static boolean isBlackPiece(String piece){
			if(piece.equals(EMPTY)){
				return false;
			}

			if(isWhitePiece(piece)){
				return false;
			}

			return true;
		}

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

	public boolean makeMove(int startCol, int startRow, int endCol, int endRow){
		// White can only move white pieces
		if(whiteTurn){
			if(!Piece.isWhitePiece(boardState[startCol][startRow])){
				return false;
			}
		}

		// Black can only move black pieces
		else{
			if(!Piece.isBlackPiece(boardState[startCol][startRow])){
				return false;
			}
		}
		
		// Can the peice at the location actually make that move
		if(!isLegalPieceMove(startCol, startRow, endCol, endRow)){
			return false;
		}

		return true;
	}

	public boolean willKingNotBeInCheck(int startCol, int startRow, int endCol, int endRow){
		String[][] nextState = getCopyOfBoardState();

		nextState[endCol][endRow] = nextState[startCol][startRow];
		nextState[startCol][startRow] = Piece.EMPTY;

		if(isKingInCheckByAnyPiece(this.whiteTurn, nextState)){
			return false;
		}

		return true;
	}


	public String[][] getCopyOfBoardState(){
		String[][] retState = new String[8][8];

		for(int col = 0; col < 8; col += 1){
			for(int row = 0; row < 8; row += 1){
				retState[col][row] = boardState[col][row];
			}
		}

		return retState;
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

	public boolean isOnBoard(int col, int row){
		return col < 8 && col >= 0 && row < 8 && row >= 0;
	}

	public boolean isMoveOnBoard(int startCol, int startRow, int endCol, int endRow){	
		if(!isOnBoard(startCol, startRow) || !isOnBoard(endCol, endRow)){
			return false;
		}
		if(startCol - endCol == 0 && startRow - endRow == 0){
			return false;
		}

		return true;
	}

	public boolean isOrthogonalMove(int startCol, int startRow, int endCol, int endRow){
		if(!isMoveOnBoard(startCol, startRow, endCol, endRow)){
			return false;
		}

		if(startCol == endCol && startRow != endRow){
			return true;
		}

		if(startRow == endRow && startCol != endCol){
			return true;
		}

		return false;
	}

	public boolean isDiagonalMove(int startCol, int startRow, int endCol, int endRow){	
		if(!isMoveOnBoard(startCol, startRow, endCol, endRow)){
			return false;
		}

		if(startCol - endCol == startRow - endRow){
			return true;
		}

		if(startCol - endCol == (startRow - endRow) * -1){
			return true;
		}

		return false;
	}

	public boolean isKnightMove(int startCol, int startRow, int endCol, int endRow){
		if(!isMoveOnBoard(startCol, startRow, endCol, endRow)){
			return false;
		}

		if(startCol - endCol == 2 || startCol - endCol == -2){
			if(startRow - endRow == 1 || startRow - endRow == -1){
				return true;
			}
		}

		if(startCol - endCol == 1 || startCol - endCol == -1){
			if(startRow - endRow == 2 || startRow - endRow == -2){
				return true;
			}
		}

		return false;
	}

	public boolean isLegalKingMove(int startCol, int startRow, int endCol, int endRow){
		if(!isMoveOnBoard(startCol, startRow, endCol, endRow)){
			return false;
		}

		// Check castling
		if(startRow == endRow && canCastle(startRow, endCol)){
			return true;
		}

		// A king can only move 1 row and column in one move...
		if(startCol - endCol > 1 || startCol - endCol < -1){
			return false;
		}

		if(startRow - endRow > 1 || startRow - endRow < -1){
			return false;
		}

		// ... but it can move as a queen for that move
		return isLegalQueenMove(startCol, startRow, endCol, endRow);
	}

	public boolean canCastle(int kingRow, int rookCol){
		// A king can only castle if it has not moved
		if(kingRow != 0 && kingRow != 7){
			return false;
		}
		
		// A king can only castle with a rook that has not moved
		if(rookCol != 0 && rookCol != 7){
			return false;
		}

		String castleRook = kingRow == 0 ? 
			Piece.WHITE_ROOK_CAN_CASTLE :
			Piece.BLACK_ROOK_CAN_CASTLE;

		if(!castleRook.equals(boardState[rookCol][kingRow])){
			return false;
		}

		// A king can only castle if it would not be in check
		// at any point during castling
		// These are the two squares toward either rook
		// The squares between the king and rook must also be empty
		boolean isWhiteKing = kingRow == 0;
		
		if(rookCol == 0){
			if(isKingInCheckByAnyPiece(3, kingRow, isWhiteKing)){
				return false;
			}
			if(isKingInCheckByAnyPiece(2, kingRow, isWhiteKing)){
				return false;
			}
			if(Piece.EMPTY.equals(boardState[3][kingRow])){
				return false;
			}
			if(Piece.EMPTY.equals(boardState[2][kingRow])){
				return false;
			}
			if(Piece.EMPTY.equals(boardState[1][kingRow])){
				return false;
			}
		}
		else{
			if(isKingInCheckByAnyPiece(5, kingRow, isWhiteKing)){
				return false;
			}
			if(isKingInCheckByAnyPiece(6, kingRow, isWhiteKing)){
				return false;
			}
			if(Piece.EMPTY.equals(boardState[5][kingRow])){
				return false;
			}
			if(Piece.EMPTY.equals(boardState[6][kingRow])){
				return false;
			}
		}

		return true;
	}

	public boolean isKingInCheckByAnyPiece(boolean isWhiteKing, String[][] boardState){
		for(int col = 0; col < 8; col += 1){
			for(int row = 0; row < 8; row += 1){
				if(boardState[col][row].equals(Piece.WHITE_KING) &&
						isWhiteKing){
					return isKingInCheckByAnyPiece(col, row, 
							isWhiteKing, boardState);
				}
				if(boardState[col][row].equals(Piece.BLACK_KING) &&
						!isWhiteKing){
					return isKingInCheckByAnyPiece(col, row, 
							isWhiteKing, boardState);
				}
			}
		}

		return false;
	}

	public boolean isKingInCheckByAnyPiece(int kingCol, int kingRow, boolean isWhiteKing){
		return isKingInCheckByAnyPiece(kingCol, kingRow, isWhiteKing, this.boardState);
	}

	public boolean isKingInCheckByAnyPiece(int kingCol, int kingRow,
			boolean isWhiteKing, String[][] boardState){
		for(int col = 0; col < 7; col += 1){
			for(int row = 0; row < 7; row += 1){
				if(isKingInCheckByPiece(kingCol, kingRow, col, row, isWhiteKing, boardState)){
					return true;
				}
			}
		}

		return false;
	}
	
	// The "isWhiteKing" value is important for checking empty squares when castling
	public boolean isKingInCheckByPiece(int kingCol, int kingRow, 
			int pieceCol, int pieceRow, 
			boolean isWhiteKing, String[][] boardState){
		String previousPiece = boardState[kingCol][kingRow];
		if(isWhiteKing){
			if(!Piece.isBlackPiece(boardState[pieceCol][pieceRow])){
				return false;
			}
			
			boardState[kingCol][kingRow] = Piece.WHITE_KING;
			if(isLegalPieceMove(pieceCol, pieceRow, kingCol, kingRow)){
				boardState[kingCol][kingRow] = previousPiece;
				return true;
			}
		}
		else{
			if(!Piece.isWhitePiece(boardState[pieceCol][pieceRow])){
				return false;
			}

			boardState[kingCol][kingRow] = Piece.BLACK_KING;
			if(isLegalPieceMove(pieceCol, pieceRow, kingCol, kingRow)){
				boardState[kingCol][kingRow] = previousPiece;
				return true;
			}
		}

		boardState[kingCol][kingRow] = previousPiece;
		return false;
	}

	public boolean isLegalPieceMove(int startCol, int startRow, int endCol, int endRow){
		switch(boardState[startCol][startRow]){
			case Piece.WHITE_ROOK_CAN_CASTLE:
			case Piece.WHITE_ROOK_CANNOT_CASTLE:
			case Piece.BLACK_ROOK_CAN_CASTLE:
			case Piece.BLACK_ROOK_CANNOT_CASTLE:
				return isLegalRookMove(startCol, startRow, endCol, endRow);
			case Piece.WHITE_KNIGHT:
			case Piece.BLACK_KNIGHT:
				return isLegalKnightMove(startCol, startRow, endCol, endRow);
			case Piece.WHITE_BISHOP:
			case Piece.BLACK_BISHOP:
				return isLegalBishopMove(startCol, startRow, endCol, endRow);
			case Piece.WHITE_QUEEN:
			case Piece.BLACK_QUEEN:
				return isLegalQueenMove(startCol, startRow, endCol, endRow);
			case Piece.WHITE_KING:
			case Piece.BLACK_KING:
				return isLegalKingMove(startCol, startRow, endCol, endRow);
			case Piece.WHITE_PAWN:
			case Piece.BLACK_PAWN:
				return isLegalPawnMove(startCol, startRow, endCol, endRow);
			default:
				return false;
		}
	}

	public boolean isLegalQueenMove(int startCol, int startRow, int endCol, int endRow){
		return isLegalRookMove(startCol, startRow, endCol, endRow) ||
			isLegalBishopMove(startCol, startRow, endCol, endRow);
	}

	public boolean isLegalPawnMove(int startCol, int startRow, int endCol, int endRow){
		if(!isMoveOnBoard(startCol, startRow, endCol, endRow)){
			return false;
		}

		// Unlike other pieces, pawns can only move in one direction based on their color
		// White pawns move "right" (row increasing) and black pawns move "left" (row decreasing)
		boolean whitePawn = boardState[startCol][startRow].equals(Piece.WHITE_PAWN);
		boolean isAttack = false;

		// A pawn can only attack one column over
		if(startCol - endCol > 1 || startCol - endCol < -1){
			return false;
		}

		// A move is on the same column. An attack would target another column
		if(startCol != endCol){
			isAttack = true;
		}

		if(whitePawn){
			// A pawn must move forward in moving or attacking
			if(endRow - startRow < 1){
				return false;
			}

			if(isAttack){
				// There are two possible pawn captures
				// One is the standard diagonal capture
				// The other is en passant
				if(!Piece.isBlackPiece(boardState[endCol][endRow]) && 
					!Piece.BLACK_PAWN_EN_PASSANT.equals(
						boardState[endCol][startRow])){
					return false;
				}
			}
			
			else{
				// Whether a pawn moves 1 sqaure or two the space in front
				// of the pawn must be empty to move forward
				if(!Piece.EMPTY.equals(boardState[endCol][startRow+1])){
					return false;
				}
				
				// A pawn can never move 3 or more squares
				if(endRow - startRow > 2)
				{
					return false;
				}

				// On a pawn's first move, it may move two squares
				if(endRow - startRow == 2){
					if(startRow != 1){
						return false;
					}
					if(!Piece.EMPTY.equals(boardState[endCol][startRow+2])){
						return false;
					}
				}
			}
		}
		else {
			// A pawn must move forward in moving or attacking
			if(endRow - startRow > -1){
				return false;
			}

			if(isAttack){
				// There are two possible pawn captures
				// One is the standard diagonal capture
				// The other is en passant
				if(!Piece.isWhitePiece(boardState[endCol][endRow]) && 
					!Piece.WHITE_PAWN_EN_PASSANT.equals(
						boardState[endCol][startRow])){
					return false;
				}
			}
			
			else{
				// Whether a pawn moves 1 sqaure or two the space in front
				// of the pawn must be empty to move forward
				if(!Piece.EMPTY.equals(boardState[endCol][startRow-1])){
					return false;
				}
				
				// A pawn can never move 3 or more squares
				if(endRow - startRow < -2)
				{
					return false;
				}

				// On a pawn's first move, it may move two squares
				if(endRow - startRow == -2){
					if(startRow != 6){
						return false;
					}
					if(!Piece.EMPTY.equals(boardState[endCol][startRow-2])){
						return false;
					}
				}
			}
		}

		return willKingNotBeInCheck(startRow, startCol, endRow, endCol);
	}

	public boolean isLegalRookMove(int startCol, int startRow, int endCol, int endRow){
		if(!isOrthogonalMove(startCol, startRow, endCol, endRow)){
			return false;
		}

		if(!willNotTakeFriendlyPiece(startCol, startRow, endCol, endRow)){
			return false;
		}
		
		// Moving along columns (left to right from a player perspective)
		int delta = endCol - startCol;

		// Moving along rows (up and down from a player perspective)
		if(delta == 0){
			delta = endRow - startRow;
			if(delta > 0){
				for(int checkRow = startRow + 1; checkRow < endRow; checkRow+=1){
					if(!boardState[endCol][checkRow].equals(Piece.EMPTY)){
						return false;
					}
				}
			}
			else{
				for(int checkRow = startRow - 1; checkRow > endRow; checkRow-=1){
					if(!boardState[endCol][checkRow].equals(Piece.EMPTY)){
						return false;
					}
				}
			}
		}
		else{
			if(delta > 0){
				for(int checkCol = startCol + 1; checkCol < endCol; checkCol+=1){
					if(!boardState[checkCol][endRow].equals(Piece.EMPTY)){
						return false;
					}
				}
			}
			else{
				for(int checkCol = startCol - 1; checkCol > endCol; checkCol-=1){
					if(!boardState[checkCol][endRow].equals(Piece.EMPTY)){
						return false;
					}
				}
			}
		}

		return willKingNotBeInCheck(startRow, startCol, endRow, endCol);
	}

	public boolean isLegalBishopMove(int startCol, int startRow, int endCol, int endRow){
		if(!isDiagonalMove(startCol, startRow, endCol, endRow)){
			return false;
		}

		if(!willNotTakeFriendlyPiece(startCol, startRow, endCol, endRow)){
			return false;
		}

		int dCol = endCol - startCol > 0 ? 1 : -1;
		int dRow = endRow - startRow > 0 ? 1 : -1;

		for(int checkCol = startCol + dCol, checkRow = startRow + dRow;
				checkCol != endCol;
				checkCol += dCol, checkRow += dRow){
			if(!boardState[checkCol][checkRow].equals(Piece.EMPTY)){
				return false;
			}
		}

		return willKingNotBeInCheck(startRow, startCol, endRow, endCol);
	}

	public boolean isLegalKnightMove(int startCol, int startRow, int endCol, int endRow){
		if(!isKnightMove(startCol, startRow, endCol, endRow)){
			return false;
		}

		if(!willNotTakeFriendlyPiece(startCol, startRow, endCol, endRow)){
			return false;
		}

		return willKingNotBeInCheck(startRow, startCol, endRow, endCol);
	}

	// If the end sqaure is empty or an enemy peice
	public boolean willNotTakeFriendlyPiece(int startCol, int startRow, int endCol, int endRow){
		if(Piece.isWhitePiece(boardState[startCol][startRow]) && 
			!Piece.isWhitePiece(boardState[endCol][endRow])){
			return true;
		}

		if(Piece.isBlackPiece(boardState[startCol][startRow]) && 
			!Piece.isBlackPiece(boardState[endCol][endRow])){
			return true;
		}

		return false;
	}


	public static Game generateNewGame(){
		return new Game(UUID.randomUUID().toString());
	}
}
