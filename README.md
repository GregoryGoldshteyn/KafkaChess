# KafkaChess
A small app for learning more about Apache Kafka.

## TODO:
- Finish client
  - Methods for listening to the server
  - Methods for sending info to the server
  - Interface for sending/recieving info
    - CLI (parse chess notation)
    - GUI (Quarkus to host react frontend. Maybe spring boot?)
- Finish server
  - Methods for listening to the client
  - Methods for sending info to the client
  - Game engine for playing chess
- Optimization
  - Server can choose partitions to reduce overhead of consumers getting info from other games
  - Implementation of communication with byte arrays to reduce communication overhead
- Misc
  - Use pipelines on server to save games to storage (deep learning, reference)
  - Use pipelines on server to convert between notation types

## Notes:
### Chessboard storage
Kafka stores data as byte arrays. To reduce message size, the minimal number of bytes required to represent a chess position should be used. The most popular minimal representation of the chess board is the bitboard + piece info. The implementation is as follows:
- The board is represented by 64 bits with a 1 indicating a piece is on the square and a 0 an empty square.
- A list of the pieces at each position with each peice type (and color) represented by 4 bits

With 32 pieces, 16 bytes are required on a full board. Every 2 captures, one fewer byte is required. At 3 pieces remaining (the minimal required for the game to continue) a game state only requires 10 bytes. Additionally, since 12 pieces use 4 bits, there are 4 open slots, which can be used for castling information on the rooks and kings.

Another possiblity is huffman encoding
- 1 bit represents an empty space
- 3 bits for each pawn
- 5 bits for knights and bishops
- 6 bits for rooks, kings, and queens

Which also adds up to 192 bits or 24 bytes in total, and could save space in pawn heavy endgames, but is more complicated than the bitboard for no additional space savings.

### Move Notation
Algebriac notation is commonly used for over the board chess. The goal of this notation is to record moves with no ambiguity and the fewest characters possible.

Portable Game Notation implements algebriac notation, but includes some formatting and metadata that provides context for computers and humans.

The longest possible move in algebraic notation is from the rare circumstance in which one of three pieces which can all move to the same spot moves to that spot. In that case, the start square and end sqaure need to both be referenced explicity (h1h3).

For a bitboard, there are two possible ways to denote a move. One always uses 2 bytes, the other usually suses 2 bytes, unless there are 4 or fewer pieces, then it uses 1 byte.

The target is the square the piece will move to. The target column is denoted with 3 bits, the target row with 3 bits, and the piece you want to move will require a number of bits to represent based on the size of the piecelist. It starts with 5 bits to represent the 32 starting pieces, and can go down to as few as 2 when there are 3 or 4 pieces on the board.

The other way to implement this is to choose a starting square with 6 bits and a target square with 6 bits. This always uses 2 bytes.

Some additional information is required. When a pawn reaches the end of the board, it promotes. A pawn can promote into one of four possible pieces (knight, bishop, rook, queen). This requires an additional 2 bits. It can fit into either scheme without increasing the number of bytes they require.
