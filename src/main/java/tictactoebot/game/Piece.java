package tictactoebot.game;

public enum Piece {
	EMPTY("--"), NOUGHT("O"), CROSS("X");

	private String stringRepresentation;
	
	Piece(String string) {
		this.stringRepresentation = string;
	}
	
	public String getStringRepresentation(){
		return stringRepresentation;
	}
}
