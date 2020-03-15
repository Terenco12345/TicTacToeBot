package tictactoebot.game;

import java.util.List;

import tictactoebot.game.exception.InvalidPlacementException;
import tictactoebot.game.exception.PlayerNotPartOfGameException;
import tictactoebot.game.exception.PlayerWrongTurnException;

public class Board {

	public enum BoardState {
		PLAYING, PLAYER_ONE_WON, PLAYER_TWO_WON, TIED
	}
	private Piece[][] board = new Piece[3][3];
	private BoardState state;

	private Player playerOne;
	private Player playerTwo;

	private Player currentPlayer;

	/**
	 * The Board class represents a Tic-tac-toe board, and all the actions that can be perfomed on it by a player.
	 * @param playerOne
	 * @param playerTwo
	 */
	public Board(Player playerOne, Player playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		this.currentPlayer = playerOne;
		this.state = BoardState.PLAYING;

		initCleanBoard();
	}

	/**
	 * Wipe the board completely clean.
	 */
	public void initCleanBoard() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				board[i][j] = Piece.EMPTY;
			}
		}
	}

	/**
	 * Attempt to place a piece on the board at x, y, a nought or a cross.
	 * If the piece cannot be placed legally - i.e. spot is already taken, return false.
	 * @return false if illegal move, true if legal move
	 * @throws PlayerNotPartOfGameException
	 * @throws PlayerWrongTurnException
	 * @throws InvalidPlacementException 
	 */
	public boolean placePieceAsPlayer(String playerName, int row, int col) throws PlayerNotPartOfGameException, PlayerWrongTurnException, InvalidPlacementException{
		Player player = null;
		if(playerName.equals(playerOne.getName())){
			System.out.println("Player one is trying to place at "+row+", "+col);
			player = playerOne;
		} else if(playerName.equals(playerTwo.getName())) {
			System.out.println("Player two is trying to place at "+row+", "+col);
			player = playerTwo;
		} else {
			// This player is not authorised to make a move.
			throw new PlayerNotPartOfGameException();
		}

		if(player != currentPlayer) {
			throw new PlayerWrongTurnException();
		}

		if(board[row][col] != Piece.EMPTY) {
			throw new InvalidPlacementException();
		}

		// If x, y is empty and player is the current player, this is a legal move.
		if(state == BoardState.PLAYING) {
			board[row][col] = player.getPieceType();
			updateState();
			System.out.println("The game state is "+state.toString());
			// Switch the players
			if(currentPlayer == playerOne) {
				System.out.println("Swapping from player one to player two!");
				currentPlayer = playerTwo;
			} else if(currentPlayer == playerTwo) {
				System.out.println("Swapping from player two to player one!");
				currentPlayer = playerOne;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the game is still running, if a player has won or if the game tied.
	 * @return returns the current game's state
	 */
	public void updateState() {
		int[][][] winConfigs = {
				{{0, 0}, {0, 1}, {0, 2}}, // Top row 
				{{1, 0}, {1, 1}, {1, 2}}, // Middle row
				{{2, 0}, {2, 1}, {2, 2}}, // Bottom row
				{{0, 0}, {1, 0}, {2, 0}}, // Left column
				{{0, 1}, {1, 1}, {2, 1}}, // Middle column
				{{0, 2}, {1, 2}, {2, 2}}, // Right column
				{{0, 0}, {1, 1}, {2, 2}}, // Left to Right Diagonal
				{{2, 0}, {1, 1}, {0, 2}}, // Right to Left Diagonal
		};  

		// For each win configuration...
		for(int i = 0; i < winConfigs.length; i++) {
			boolean playerOneWin = true;
			boolean playerTwoWin = true;

			// Check if all the conditions in the configuration have been met by either player one or player two.
			for(int j = 0; j < winConfigs[i].length; j++) {
				if(playerOne.getPieceType() != board[winConfigs[i][j][0]][winConfigs[i][j][1]]) {
					playerOneWin = false;
				}
				if(playerTwo.getPieceType() != board[winConfigs[i][j][0]][winConfigs[i][j][1]]) {
					playerTwoWin = false;
				}
				if(board[winConfigs[i][j][0]][winConfigs[i][j][1]] == Piece.EMPTY) {
					playerOneWin = false;
					playerTwoWin = false;
				}
			}
			// Check if any players have fulfilled all the conditions
			if(playerOneWin) {
				System.out.println("Player one won by getting "+i);
				state = BoardState.PLAYER_ONE_WON;
				return;
			} else if (playerTwoWin) {
				System.out.println("Player two won by getting "+i);
				state = BoardState.PLAYER_TWO_WON;
				return;
			}
		}

		// Check if there are any empty spaces left on the board
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(board[i][j] == Piece.EMPTY) {
					state = BoardState.PLAYING;
					return;
				}
			}
		}

		// If the method hasn't returned by now, it must be a tie, since noone has won and there are no empty spaces.
		state = BoardState.TIED;
		return;
	}

	/**
	 * Prints the entire board state to the console.
	 */
	public String getBoardAsString() {
		String output = "";
		for(int i = 0; i < 3; i++) {
			output += "|";
			for(int j = 0; j < 3; j++) {
				output += (board[i][j].getStringRepresentation());
				output += "|";
			}
			output+= "\n";
		}
		output+="";
		return output;
	}

	public BoardState getBoardState() {
		return state;
	}
	
	public Piece[][] getBoardCopy(){
		return board.clone();
	}
	
	public boolean isBoardFull() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j <3; j++) {
				if(Piece.EMPTY == board[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
}
