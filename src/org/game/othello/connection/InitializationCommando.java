package org.game.othello.connection;

import java.io.Serializable;

/**
 * Gives the initial infomation for both players: LobbyNumber and the starting player.
 * @author Oliver
 */
public class InitializationCommando implements Serializable{

	private static final long serialVersionUID = 3742816512911668042L;
	private Boolean startingPlayer;
	private Integer lobbyNumber;
	
	public InitializationCommando(Boolean startingPlayer, Integer lobbyNumer) {
		this.startingPlayer = startingPlayer;
		this.lobbyNumber = lobbyNumer;
	}
	
	public boolean getStartingPlayer() {
		return this.startingPlayer;
	}
	
	public int getLobbyNumer() {
		return this.lobbyNumber;
	}
}
