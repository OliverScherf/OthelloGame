package org.game.othello.connection.server;

import java.util.Scanner;

/**
 * Listens for Console Inputs.
 * @author Oliver Scherf
 */
public class ConsoleListener implements Runnable {

	private Scanner consoleReader;
	private OthelloServer srv;

	/**
	 * Instantiate a new ConsoleListener.
	 * @param srv 
	 */
	public ConsoleListener(OthelloServer srv) {
		this.srv = srv;
		this.consoleReader = new Scanner(System.in);
	}

	@Override
	public void run() {
		while (true) {
			String cmd = this.consoleReader.nextLine();
			if (cmd.equals("help")) {
				System.out.println("Commands are:");
				System.out.println("\thelp - printing this");
				System.out.println("\tlistlobbies - list all existing lobbies");
				System.out.println("\tkill <lobby number> - forces to close the connection of both players");
				System.out.println("\tquit - stops the server");
			} else if (cmd.equals("listlobbies")) {
				this.srv.listLobbies();
			} else if (cmd.startsWith("kill")) {
				this.srv.killLobby(cmd);
			} else if (cmd.equals("quit")) {
				System.exit(0);
			} else {
				System.out.println("Unknown command. Type help for available commands");
			}
		}
	}
}
