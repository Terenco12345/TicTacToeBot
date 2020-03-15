package tictactoebot.game;

public class Player {
	private String name;
	private Piece pieceType;
	
	public Player(String name, Piece pieceType) {
		this.name = name;
		this.pieceType = pieceType;
	}
	
	public String getName() {
		return name;
	}
	
	public Piece getPieceType() {
		return pieceType;
	}
}
