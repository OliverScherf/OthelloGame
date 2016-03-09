package org.game.othello.connection;

import java.io.Serializable;

import org.game.othello.ao.Coordinate;

/**
 * An Object of this class will be reveiced, when the game ends.
 * This means, no move is possible for BOTH players.
 * @author Oliver Scherf
 */
public class GameEndsCommando implements Serializable {

	private static final long serialVersionUID = -8997344565937593651L;

	private Coordinate lastIndex;
	
	/**
	 * @see GameEndsCommando
	 * @param lastIndex
	 */
	public GameEndsCommando(Coordinate lastIndex) {
		this.lastIndex = lastIndex;
	}
	
	@Override
	public String toString() {
		return "Game Ends, last Index was: " +  this.lastIndex.toString();
	}
	
	public Coordinate getLastIndex() {
		return this.lastIndex;
	}
	
	
}
