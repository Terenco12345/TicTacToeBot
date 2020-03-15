package tictactoebot.game;

import net.dv8tion.jda.api.entities.Member;
import tictactoebot.game.Board.BoardState;
import tictactoebot.game.exception.GameNotStartedException;
import tictactoebot.game.exception.InvalidPlacementException;
import tictactoebot.game.exception.PlayerNotPartOfGameException;
import tictactoebot.game.exception.PlayerWrongTurnException;
import tictactoebot.game.exception.UnauthorizedJoinException;

public class BoardHandler {
	
	private Board board;
	private Member playerOne;
	private Member playerTwo;
	
	public BoardHandler() {
		board = null;
		playerOne = null;
		playerTwo = null;
	}
	
	/**
	 * Attempt to join user to this game of Tic tac toe.
	 * @param user
	 * @return If no more spaces, return false. If joined successfully, return true.
	 * @throws UnauthorizedJoinException 
	 */
	public boolean joinGame(Member user) throws UnauthorizedJoinException {
		if(playerOne == null) {
			playerOne = user;
			return true;
		} else if(playerOne.equals(user)) {
			System.out.println(user.getEffectiveName()+" cannot join, since they are the same as player 1!");
			throw new UnauthorizedJoinException();
		} else if (playerTwo == null) {
			playerTwo = user;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Attempt to start the game of Tic tac toe.
	 */
	public boolean startGame() {
		if(playerOne == null || playerTwo == null) {
			return false;
		} else {
			board = new Board(new Player(playerOne.getEffectiveName(), Piece.NOUGHT), new Player(playerTwo.getEffectiveName(), Piece.CROSS));
			return true;
		}
	}
	
	/**
	 * Resets the board.
	 */
	public void reset() {
		board = null;
		playerOne = null;
		playerTwo = null;
	}
	
	public boolean play(Member member, int row, int col) throws GameNotStartedException, PlayerNotPartOfGameException, PlayerWrongTurnException, InvalidPlacementException {
		if(board == null) {
			throw new GameNotStartedException();
		}
		boolean success = board.placePieceAsPlayer(member.getEffectiveName(), row, col);
		return success;
	}
	
	public BoardState getBoardState() {
		return board.getBoardState();
	}
	
	public String getBoardAsString() {
		return board.getBoardAsString();
	}
	
	public Member getPlayerOne() {
		return playerOne;
	}
	
	public Member getPlayerTwo() {
		return playerTwo;
	}

	public boolean hasGameStarted() {
		if(board != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Piece[][] getBoardCopy() {
		return board.getBoardCopy();
	}
	
	public boolean isBoardFull() {
		return board.isBoardFull();
	}
}
