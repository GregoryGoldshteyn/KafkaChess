# KafkaChess
A small app for learning more about Apache Kafka

### TODO:
- Refactor processing using processor.api classes
- Refactor processing using a seperate defined class instead of anonymous classes
- Finish client
- Finish server

### Notes:
#### Chessboard storage
Kafka stores data as byte arrays. To reduce message size, the minimal number of bytes required to represent a chess position should be used. The most popular minimal representation of the chess board is the bitboard + piece info. The implementation is as follows:
- The board is represented by 64 bits with a 1 indicating a piece is on the square and a 0 an empty square.
- A list of the pieces at each position with each peice type (and color) represented by 4 bits
With 32 pieces, 16 bytes are required on a full board. Every 2 captures, one fewer byte is required. At 3 pieces remaining (the minimal required for the game to continue) a game state only requires 10 bytes. Additionally, since 12 pieces use 4 bits, there are 4 open slots, which can be used for castling information on the rooks and kings.
