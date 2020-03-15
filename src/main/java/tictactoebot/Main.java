package tictactoebot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import tictactoebot.game.Board;
import tictactoebot.game.Piece;
import tictactoebot.game.Player;
import tictactoebot.game.TicTacToeListener;

public class Main {
	
	public static void main(String[] args) {
		try {
			JDA jda = JDABuilder.createDefault("NDMzMTc4MjY5NjI2NDAwNzY4.Xmt0TQ.4_P3erilPUiQUtUJJ57iRy77O0g")
					.setAutoReconnect(true)
					.setActivity(EntityBuilder.createActivity("Tic Tac Toe", null, ActivityType.DEFAULT))
					.addEventListeners(new TicTacToeListener())
					.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
}
