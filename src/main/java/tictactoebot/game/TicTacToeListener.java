package tictactoebot.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tictactoebot.game.MiniMaxAI.Move;
import tictactoebot.game.exception.GameNotStartedException;
import tictactoebot.game.exception.InvalidPlacementException;
import tictactoebot.game.exception.PlayerNotPartOfGameException;
import tictactoebot.game.exception.PlayerWrongTurnException;
import tictactoebot.game.exception.UnauthorizedJoinException;

public class TicTacToeListener extends ListenerAdapter{
	
	public BoardHandler boardHandler;
	public boolean aiMode;
	
	public TicTacToeListener() {
		boardHandler = new BoardHandler();
		aiMode = false;
	}
	
	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
		Member member = event.getMember();
		MessageChannel channel = event.getChannel();
		String message = event.getMessage().getContentRaw();
		
		System.out.printf("[%s]: %s\n", event.getAuthor().getName(), event.getMessage().getContentDisplay());
		
		String[] commands = message.split(" ");
		
        // Try to join a game
		if(commands[0].equals("~join")) {
			try {
				boolean success = boardHandler.joinGame(member);
				channel.sendMessage(member.getEffectiveName()+" has joined tic tac toe!").queue();
			} catch (UnauthorizedJoinException e) {
				channel.sendMessage(member.getEffectiveName()+", you have already joined!").queue();
			}
			
			if(boardHandler.getPlayerOne() != null && boardHandler.getPlayerTwo() != null) {
				channel.sendMessage("There are now two players ready to play! Type ~start to begin the game!").queue();
			}
		}
		
		// Let the bot join (This enables AI mode, this can only happen if there is one player present.)
		if(commands[0].equals("~joinInari") && boardHandler.getPlayerOne() != null) {
			channel.sendMessage("~join").queue();
			aiMode = true;
		}
		
		// Start the game
		if(commands[0].equals("~start")) {
        	boolean success = boardHandler.startGame();
        	if(success == false) {
        		channel.sendMessage("You can't start a game right now!").queue();
        	} else {
        		channel.sendMessage("Game has started!").queue();
        		sendBoardToChannel(channel, boardHandler);
        	}
        }
		
		// Attempt to place a piece
		if(commands[0].equals("~play") && commands.length >= 3){
			try {
				int x = Integer.parseInt(commands[1]);
				int y = Integer.parseInt(commands[2]);
				System.out.println(member.getEffectiveName() +" is playing at "+x+" "+y+".");
				boardHandler.play(member, 3-y, x-1);
				sendBoardToChannel(channel, boardHandler);
				
				if(aiMode == true && !member.getUser().isBot() && !boardHandler.isBoardFull()) {
					Move move = MiniMaxAI.findBestMove(boardHandler.getBoardCopy());
					System.out.println("The best calculated move is "+move.col+" ,"+move.row);
					channel.sendMessage("~play "+(move.col+1)+" "+(3-move.row)).queue();
				}
			} catch(NumberFormatException e) {
				channel.sendMessage("Please put your moves in the format: !play x y, where x and y are the coordinates of your move.").queue();
			} catch(PlayerNotPartOfGameException e) {
				channel.sendMessage(member.getEffectiveName()+" cannot play as they are not part of the current game.").queue();
			} catch (PlayerWrongTurnException e) {
				channel.sendMessage(member.getEffectiveName()+", it is not your turn yet!").queue();
			} catch (ArrayIndexOutOfBoundsException e) {
				channel.sendMessage(member.getEffectiveName()+" please enter a number between 1 and 3 for both coordinates.").queue();
			} catch (GameNotStartedException e) {
				channel.sendMessage("The game has not started yet! Please type ~start to start.").queue();
			} catch (InvalidPlacementException e) {
				channel.sendMessage(member.getEffectiveName()+", you can't place a piece there! Try again.").queue();
			}
		}
		
		// Message the board to the chat
        if(commands[0].equals("~board")) {
        	try {
        		sendBoardToChannel(channel, boardHandler);
        	} catch (NullPointerException e) {
        		channel.sendMessage("The game has not started yet! There is no board to see.").queue();
        	}
        }
        
        if(boardHandler.hasGameStarted()) {
        	switch(boardHandler.getBoardState()) {
        	case TIED:
        		channel.sendMessage("The game has been tied! Game is now reset.").queue();
        		reset();
        		break;
        	case PLAYER_ONE_WON:
        		channel.sendMessage(boardHandler.getPlayerOne().getAsMention()+" has won! Game is now reset.").queue();
        		reset();
        		break;
        	case PLAYER_TWO_WON:
        		channel.sendMessage(boardHandler.getPlayerTwo().getAsMention()+" has won! Game is now reset.").queue();
        		reset();
        		break;
			case PLAYING:
				break;
        	}
        }
    }
	
	public void reset() {
		boardHandler.reset();
		aiMode = false;
	}
	
	public static void sendBoardToChannel(MessageChannel channel, BoardHandler boardHandler) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Tic-tac-toe Game");
		eb.addField("Player one:", boardHandler.getPlayerOne().getEffectiveName(), true);
		eb.addField("Player two:", boardHandler.getPlayerTwo().getEffectiveName(), true);
		eb.addField("Board", boardHandler.getBoardAsString(), false);
		channel.sendMessage(eb.build()).queue();
	}
}